package com.example.administrator.attendanceinputv3.presenter.impl;

import android.content.Intent;

import com.example.administrator.attendanceinputv3.model.FaceDeviceBean;
import com.example.administrator.attendanceinputv3.network.LoginRequest;
import com.example.administrator.attendanceinputv3.network.NetworkListener;
import com.example.administrator.attendanceinputv3.presenter.LoginPresenter;
import com.example.administrator.attendanceinputv3.utils.LogUtils;
import com.example.administrator.attendanceinputv3.view.LoginView;

import java.util.List;

/**
 * @author: Caoy
 * @created on: 2018/9/1 11:48
 * @description:
 */
public class LoginPresenterImpl implements LoginPresenter {
    private LoginView loginView;
    private LoginRequest loginRequest;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
    }

    /**
     * 检查项目编码
     *
     * @param codeStr
     */
    @Override
    public void checkProjectCode(String codeStr) {
        //getFaceDevice
        if (loginRequest == null) {
            loginRequest = new LoginRequest();
        }
        loginRequest.postRequest(codeStr, new NetworkListener<List<FaceDeviceBean>>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onError(String errorMessage) {
                LogUtils.e("onGetDevice:" + errorMessage);
                loginView.onGetDeviceFailure(errorMessage);
            }

            @Override
            public void onSuccess(List<FaceDeviceBean> faceDeviceBeans) {
                LogUtils.d("onGetDevice:" + faceDeviceBeans.toString());
                loginView.onGetDeviceSucceed(faceDeviceBeans);
            }
        });
    }

    @Override
    public void loginToAttendance(String ipAddress) {

    }


}
