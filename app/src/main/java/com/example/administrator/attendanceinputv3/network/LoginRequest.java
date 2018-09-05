package com.example.administrator.attendanceinputv3.network;

import com.example.administrator.attendanceinputv3.constant.Urls;
import com.example.administrator.attendanceinputv3.model.FaceDeviceBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author: Caoy
 * @created on: 2018/9/1 16:17
 * @description:
 */
public class LoginRequest extends BaseRequest<List<FaceDeviceBean>> {

    public LoginRequest() {
        super();
    }

    public void postRequest(String code,NetworkListener networkListener) {
        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("code", code);
        postRequest(Urls.GET_FACE_DEVICE, parameterMap,networkListener);
    }
}
