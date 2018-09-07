package com.example.administrator.attendanceinputv3.presenter.impl;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.administrator.attendanceinputv3.R;
import com.example.administrator.attendanceinputv3.model.ConnectDeviceBean;
import com.example.administrator.attendanceinputv3.model.EmployeeInfoBean;
import com.example.administrator.attendanceinputv3.model.PersonModel;
import com.example.administrator.attendanceinputv3.network.EmployeeInfoRequest;
import com.example.administrator.attendanceinputv3.network.NetworkListener;
import com.example.administrator.attendanceinputv3.presenter.HomePresenter;
import com.example.administrator.attendanceinputv3.utils.LogUtils;
import com.example.administrator.attendanceinputv3.utils.StringUtils;
import com.example.administrator.attendanceinputv3.utils.ThreadPoolUtils;
import com.example.administrator.attendanceinputv3.utils.UiUtil;
import com.example.administrator.attendanceinputv3.view.HomeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;
import sdk.facecamera.sdk.FaceCamera;
import sdk.facecamera.sdk.events.CaptureCompareDataReceivedEventHandler;
import sdk.facecamera.sdk.pojos.CaptureCompareData;

/**
 * @author: Caoy
 * @created on: 2018/9/4 14:58
 * @description:
 */
public class HomePresenterImpl implements HomePresenter {
    private final Handler handler;
    private FaceCamera faceCamera;
    private HashMap<String, PersonModel> personMatchedHashMap = new HashMap<>();
    private ArrayList<PersonModel> personList = new ArrayList<>();

    private final MyCallback callback;
    private final SurfaceHolder surfaceHolder;
    private HomeView homeView;
    private ConnectDeviceBean connectDeviceBean;
    private Activity mActivity;

    private static final int scanStart = 0;
    private static final int scanPause = 1;
    private int mCurrentScanState = scanStart;

    private static final int requestInfoStart = 3;
    private static final int requestInfoPause = 4;
    private int mCurrentRequestInfoState = scanStart;


    public static final int HANDLE_WHAT_UPDATE_SCAN_INFO = 506;
    private EmployeeInfoRequest employeeInfoRequest;

    public HomePresenterImpl(HomeView homeView, ConnectDeviceBean connectDeviceBean, Activity mActivity) {
        this.homeView = homeView;
        this.connectDeviceBean = connectDeviceBean;
        this.mActivity = mActivity;
        handler = new Handler(Looper.getMainLooper());

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
//                            if (mCurrentScanState == scanPause) {
//                                return;
//                            }
//                            mCurrentScanState = scanPause;
                            //1.开始扫描
                            startScanEmployee();
                            LogUtils.d("onCaptureCompareDataReceived 扫描触发");
                            if (data.getFeatureImageData() == null) {
                                errorScanEmployee();
                                return;
                            }
                            final String personID = data.getPersonID();
                            //2.如果匹配得到设备库中的人员，扫描成功
                            if (data.isPersonMatched()) {
                                LogUtils.d("onCaptureCompareDataReceived 匹配成功" + data);
                                final PersonModel model = new PersonModel();
                                model.setResult(true);
                                model.setName(data.getPersonName());
                                model.setAge(data.getAge());
                                model.setScanImg(data.getFeatureImageData());
                                model.setSex(data.getSex());
                                if (!StringUtils.isEmpty(personID)) {
                                    //3.扫描并且匹配成功
                                    personList.add(model);
                                    successScanList(personList);
                                    //4.如果匹配得到设备库中的人员id,请求人员信息
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyRequestInfo(model, personList.size() - 1);
                                        }
                                    });
                                } else {
                                    //匹配失败
                                    errorMatched();
                                }
                            } else {
                                //如果不在匹配库中，扫描失败
                                errorScanEmployee();
                            }
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

    private void successScanList(final ArrayList<PersonModel> personList) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                homeView.successScanList(personList);
            }
        });
    }


    private void errorMatched() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                homeView.errorMatched();
            }
        });
    }

    private void startScanEmployee() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                homeView.startScan();
            }
        });
    }

    private void errorScanEmployee() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                homeView.errorScan();
            }
        });
    }


    /**
     * 主线程异步请求网络
     *
     * @param model
     */
    private void notifyRequestInfo(final PersonModel model, final int indexTag) {
        if (employeeInfoRequest == null) {
            employeeInfoRequest = new EmployeeInfoRequest();
        }

        employeeInfoRequest.getEmployeeInfoRequest(model.getPersonID(), new NetworkListener<EmployeeInfoBean>() {

            private PersonModel errorPersonModel;

            @Override
            public void onStart() {
                //请求开始
                Request request = employeeInfoRequest.baseGetRequest();
                int tag = (int) request.tag();
                LogUtils.d("request onStart tag: " + tag);
            }

            @Override
            public void onError(String localizedMessage) {
                //请求失败
                Request request = employeeInfoRequest.baseGetRequest();
                int tag = (int) request.tag();
                LogUtils.d("request onError tag: " + tag);

                PersonModel personModel = personList.get(tag);
                personModel.setVerifyFacImgUrl("");
                personModel.setName(mActivity.getString(R.string.str_));
                personModel.setIoTimeStr(mActivity.getString(R.string.str_));
                personModel.setGroupName(mActivity.getString(R.string.str_));
                personModel.setJob(mActivity.getString(R.string.str_));

                //1.存入到数据集
                personList.set(tag, personModel);
                //2.刷新UI
                homeView.notifyUiRefreshData(personList);
            }

            @Override
            public void onSuccess(EmployeeInfoBean data) {
                Request request = employeeInfoRequest.baseGetRequest();
                int tag = (int) request.tag();
                LogUtils.d("request onSuccess tag: " + tag);

                PersonModel personModel = personList.get(tag);
                personModel.setVerifyFacImgUrl(data.getVerifyFace());
                personModel.setName(data.getName());
                personModel.setIoTimeStr(data.getIoTimeStr());
                personModel.setGroupName(data.getGroupName());
                personModel.setJob(data.getJob());

                //1.存入到数据集
                personList.set(tag, personModel);
                //2.刷新UI
                homeView.notifyUiRefreshData(personList);
            }
        }, indexTag);
    }
}
