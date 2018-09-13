package com.quansoon.facecamera.presenter.impl;

import com.quansoon.facecamera.model.FaceDeviceBean;
import com.quansoon.facecamera.model.VersionUpdateBean;
import com.quansoon.facecamera.network.CheckUpdateRequest;
import com.quansoon.facecamera.network.LoginRequest;
import com.quansoon.facecamera.network.NetworkListener;
import com.quansoon.facecamera.presenter.LoginPresenter;
import com.quansoon.facecamera.ui.LoginActivity;
import com.quansoon.facecamera.utils.LogUtils;
import com.quansoon.facecamera.utils.ToastUtils;
import com.quansoon.facecamera.view.LoginView;

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
    public void checkDeviceCode(String codeStr) {
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

    CheckUpdateRequest checkUpdateRequest;

    @Override
    public void checkAppUpdate() {
        if (checkUpdateRequest == null) {
            checkUpdateRequest = new CheckUpdateRequest();
        }
        checkUpdateRequest.postRequest(new NetworkListener<VersionUpdateBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onError(String localizedMessage) {
                loginView.onCheckUpdateFailure(localizedMessage);
            }

            @Override
            public void onSuccess(VersionUpdateBean data) {
                loginView.onCheckUpdateSuccess(data);
            }
        });

    }


}
