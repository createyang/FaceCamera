package com.quansoon.facecamera.presenter.impl;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.constant.Constants;
import com.quansoon.facecamera.model.ConnectDeviceBean;
import com.quansoon.facecamera.model.EmployeeInfoBean;
import com.quansoon.facecamera.model.PersonModel;
import com.quansoon.facecamera.network.EmployeeInfoRequest;
import com.quansoon.facecamera.network.NetworkListener;
import com.quansoon.facecamera.presenter.HomePresenter;
import com.quansoon.facecamera.utils.DateTimeUtil;
import com.quansoon.facecamera.utils.LogUtils;
import com.quansoon.facecamera.utils.StringUtils;
import com.quansoon.facecamera.utils.ThreadPoolUtils;
import com.quansoon.facecamera.utils.UiUtil;
import com.quansoon.facecamera.view.HomeView;

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
    private ArrayList<PersonModel> personList = new ArrayList<>(50);
    private ArrayList<PersonModel> completePersonList = new ArrayList<>();

    private final MyCallback callback;
    private final SurfaceHolder surfaceHolder;
    private HomeView homeView;
    private ConnectDeviceBean connectDeviceBean;
    private Activity mActivity;

    public static final int HANDLE_WHAT_UPDATE_SCAN_INFO = 506;
    private EmployeeInfoRequest employeeInfoRequest;

    private static final int IntervalTime = 15000;
    private String recordLast = "";

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
            handler.removeCallbacks(lastRun);
        }
    }

    @Override
    public List<PersonModel> getPersonList() {
        return personList;
    }

    @Override
    public void removeDataList() {
        personList.clear();
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
                LogUtils.i("HomeFragment", "初始化成功 " + faceCamera.getState());
                if (faceCamera.connect()) {
                    LogUtils.i("HomeFragment", "链接成功");
                    faceCamera.onCaptureCompareDataReceived(new CaptureCompareDataReceivedEventHandler() {
                        @Override
                        public void onCaptureCompareDataReceived(CaptureCompareData data) {
                            //TODO CaptureCompareData 捕获的对象
                            //1.开始扫描
                            LogUtils.d("onCaptureCompareDataReceived 扫描触发");
                            startScanEmployee();
                            if (data.getFeatureImageData() == null) {
                                errorScanEmployee();
                                return;
                            }
                            final String personID = data.getPersonID();
                            //2.如果匹配得到设备库中的人员，扫描成功
                            if (data.isPersonMatched()) {
                                LogUtils.d("onCaptureCompareDataReceived 匹配成功");
                                if (!StringUtils.isEmpty(personID)) {

                                    //如果最后打卡的人员在10秒内重复打卡，不执行匹配操作
                                    if (!Constants.IS_DEBUG) {
                                        if (recordLast.equals(personID)) {
                                            return;
                                        } else {
                                            recordLast = personID;
                                            handler.removeCallbacks(lastRun);
                                            handler.postDelayed(lastRun, IntervalTime);
                                        }
                                    }

                                    final PersonModel model = new PersonModel();
                                    model.setResult(true);
                                    model.setName(data.getPersonName());
                                    model.setIoTimeStr(DateTimeUtil.getDateToString(data.getCaptureTime().getTime(), "HH:mm:ss"));
                                    model.setAge(data.getAge());
                                    model.setScanImg(data.getFeatureImageData());
                                    model.setSex(data.getSex());
                                    model.setMatchScore(data.getMatchScore() + "");
                                    model.setPersonID(data.getPersonID());
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

    Runnable lastRun = new Runnable() {
        @Override
        public void run() {
            recordLast = "";
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
            }

            @Override
            public void onError(String localizedMessage) {
                //请求失败
                Request request = employeeInfoRequest.baseGetRequest();
                int tag = (int) request.tag();
                LogUtils.d("request onError tag: " + tag);
                PersonModel personModel = personList.get(tag);
                personModel = setModelValue(personModel, new EmployeeInfoBean());
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
                personModel = setModelValue(personModel, data);
                //1.存入到数据集
                personList.set(tag, personModel);
                //2.刷新UI
                homeView.notifyUiRefreshData(personList);
            }
        }, indexTag);
    }

    private PersonModel setModelValue(PersonModel personModel, EmployeeInfoBean data) {
        if (personModel != null) {
            if (!StringUtils.isEmpty(data.getVerifyFace())) {
                personModel.setVerifyFacImgUrl(data.getVerifyFace());
            } else {
                personModel.setVerifyFacImgUrl("");
            }

            if (!StringUtils.isEmpty(data.getName())) {
                personModel.setName(data.getName());
            } else {
                if (!StringUtils.isEmpty(personModel.getName())) {
                    personModel.setName(personModel.getName());
                } else {
                    personModel.setName(mActivity.getString(R.string.str_));
                }
            }

            if (!StringUtils.isEmpty(data.getIoTimeStr())) {
                personModel.setIoTimeStr(data.getIoTimeStr());
            } else {
                if (!StringUtils.isEmpty(personModel.getIoTimeStr())) {
                    personModel.setIoTimeStr(personModel.getIoTimeStr());
                } else {
                    personModel.setIoTimeStr(mActivity.getString(R.string.str_));
                }
            }

            if (!StringUtils.isEmpty(data.getGroupName())) {
                personModel.setGroupName(data.getGroupName());
            } else {
                if (!StringUtils.isEmpty(personModel.getGroupName())) {
                    personModel.setGroupName(personModel.getGroupName());
                } else {
                    personModel.setGroupName(mActivity.getString(R.string.str_));
                }
            }

            if (!StringUtils.isEmpty(data.getJob())) {
                personModel.setJob(data.getJob());
            } else {
                if (!StringUtils.isEmpty(personModel.getJob())) {
                    personModel.setJob(personModel.getJob());
                } else {
                    personModel.setJob(mActivity.getString(R.string.str_));
                }
            }
        }
        return personModel;
    }

}
