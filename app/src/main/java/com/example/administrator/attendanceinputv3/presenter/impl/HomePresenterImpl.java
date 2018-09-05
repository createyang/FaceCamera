package com.example.administrator.attendanceinputv3.presenter.impl;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.administrator.attendanceinputv3.model.ConnectDeviceBean;
import com.example.administrator.attendanceinputv3.model.PersonModel;
import com.example.administrator.attendanceinputv3.presenter.HomePresenter;
import com.example.administrator.attendanceinputv3.utils.LogUtils;
import com.example.administrator.attendanceinputv3.utils.ThreadPoolUtils;
import com.example.administrator.attendanceinputv3.utils.UiUtil;
import com.example.administrator.attendanceinputv3.view.HomeView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sdk.facecamera.sdk.FaceCamera;
import sdk.facecamera.sdk.events.CaptureCompareDataReceivedEventHandler;
import sdk.facecamera.sdk.pojos.CaptureCompareData;

/**
 * @author: Caoy
 * @created on: 2018/9/4 14:58
 * @description:
 */
public class HomePresenterImpl implements HomePresenter {
    private FaceCamera faceCamera;
    private List<PersonModel> personList = new ArrayList<>();
    private HashMap<String, PersonModel> personMatchedHashMap = new HashMap<>();
    private final MyCallback callback;
    private final SurfaceHolder surfaceHolder;
    private HomeView homeView;
    private ConnectDeviceBean connectDeviceBean;
    private final HomeFragmentHandler homeFragmentHandler;

    private static final int scanStart = 0;
    private static final int scanPause = 1;
    private int mCurrentScanState = scanStart;

    private static final int requestInfoStart = 3;
    private static final int requestInfoPause = 4;
    private int mCurrentRequestInfoState = scanStart;


    private static class HomeFragmentHandler extends Handler {
        private final WeakReference<HomePresenterImpl> homePresenterWeakReference;

        private HomeFragmentHandler(HomePresenterImpl homePresenter) {
            homePresenterWeakReference = new WeakReference<>(homePresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            HomePresenterImpl homePresenter = homePresenterWeakReference.get();
            homePresenter.notifyDataSetChanged();
        }
    }

    public HomePresenterImpl(HomeView homeView, ConnectDeviceBean connectDeviceBean) {
        this.homeView = homeView;
        this.connectDeviceBean = connectDeviceBean;

        homeFragmentHandler = new HomeFragmentHandler(this);

        surfaceHolder = homeView.getSurfaceHolder();
        callback = new MyCallback();
        surfaceHolder.addCallback(callback);
    }

    @Override
    public FaceCamera startVideoPlay() {
        if (faceCamera != null) {
            faceCamera.startVideoPlay(surfaceHolder);
        }
        return null;
    }

    @Override
    public void removeRun() {
        if (callback != null) {
            surfaceHolder.removeCallback(callback);
        }
    }

    @Override
    public List<PersonModel> getPersonList() {
        return personList;
    }

    public void notifyDataSetChanged() {
        homeView.notifyDataSetChanged(getPersonList());
    }

    /**
     * 由于surface可能被销毁，它只在SurfaceHolder.Callback.surfaceCreated()
     * 和 SurfaceHolder.Callback.surfaceDestroyed()之间有效，
     * 所以要确保渲染线程访问的是合法有效的surface。
     */
    private class MyCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            //使用线程池创建线程，执行run任务
            ThreadPoolUtils.execute(faceCameraRunnable);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            faceCamera.stopVideoPlay();
        }

    }

    /**
     * 人脸识别任务
     */
    private Runnable faceCameraRunnable = new Runnable() {
        @Override
        public void run() {
            faceCamera = UiUtil.getFaceCamera();
            if (faceCamera.initialize(connectDeviceBean.getIp(), connectDeviceBean.getUsername(), connectDeviceBean.getPassword())) {
                //faceCamera.getState()可以获取连接状态
                Log.i("HomeFragment", "初始化成功 " + faceCamera.getState());
                if (faceCamera.connect()) {
                    Log.i("HomeFragment", "链接成功");
                    faceCamera.onCaptureCompareDataReceived(new CaptureCompareDataReceivedEventHandler() {
                        @Override
                        public void onCaptureCompareDataReceived(CaptureCompareData data) {
                            //TODO CaptureCompareData 捕获的对象
                            if (mCurrentScanState == scanPause) {
                                return;
                            }
                            mCurrentScanState = scanPause;
                            homeView.showScanFeatureImage(data.getFeatureImageData());
                            /**
                             * 1.得到捕获的数据对象
                             * 2.显示抓怕数据头像
                             * 3.请求对比信息
                             * 4.显示对比结果
                             */
                            PersonModel model = new PersonModel();
                            if (data.isPersonMatched()) {
                                model.setResult(true);
                                //如果匹配得到设备库中的人员
                                model.setName(data.getPersonName());
                                model.setAge(data.getAge());
                                model.setImg(data.getFeatureImageData());
                                model.setSex(data.getSex());
                                personMatchedHashMap.put(model.getPersonID(), model);

                            } else {
                                //匹配不到  但是数据可能不为空
                                model.setResult(false);
                                if (data.getPersonName() != null) {
                                    model.setName(data.getPersonName());
                                }
                                model.setAge(data.getAge());
                                model.setSex(data.getSex());
                                model.setImg(data.getFeatureImageData());
                            }
                            personList.add(model);


                            //通知更新
                            homeFragmentHandler.sendEmptyMessage(0);
                        }
                    });
                    //直接传一个holder进来就能播放，
                    // 如果需要自己处理数据，可以调用faceCamera.onStreamDataReceived
                    if (faceCamera.startVideoPlay(surfaceHolder)) {
                        LogUtils.i("成功播放");
                    } else {
                        LogUtils.i("播放失败");
                    }
                }
            }
        }
    };
}
