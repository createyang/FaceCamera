package com.quansoon.facecamera.network;

import com.quansoon.facecamera.constant.Urls;
import com.quansoon.facecamera.model.FaceDeviceBean;

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
        basePostRequest(Urls.URL_FACE_DEVICE, parameterMap,networkListener);
    }
}
