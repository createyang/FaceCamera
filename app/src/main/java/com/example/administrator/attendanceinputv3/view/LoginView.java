package com.example.administrator.attendanceinputv3.view;

import com.example.administrator.attendanceinputv3.model.FaceDeviceBean;

import java.util.List;

/**
 * @author: Caoy
 * @created on: 2018/9/1 11:37
 * @description:
 */
public interface LoginView {

   void onGetDeviceFailure(String errorMessage);
   void onGetDeviceSucceed(List<FaceDeviceBean> faceDeviceBeans);
}
