package com.quansoon.facecamera.view;

import com.quansoon.facecamera.model.FaceDeviceBean;
import com.quansoon.facecamera.model.VersionUpdateBean;

import java.util.List;

/**
 * @author: Caoy
 * @created on: 2018/9/1 11:37
 * @description:
 */
public interface LoginView {

   void onGetDeviceFailure(String errorMessage);
   void onGetDeviceSucceed(List<FaceDeviceBean> faceDeviceBeans);

   void onCheckUpdateFailure(String message);

   void onCheckUpdateSuccess(VersionUpdateBean data);
}
