package com.quansoon.facecamera.network;

import com.quansoon.facecamera.constant.Urls;
import com.quansoon.facecamera.model.TimeBean;

import java.util.HashMap;

/**
 * @author: Caoy
 * @created on: 2018/9/9 14:26
 * @description:
 */
public class TimeRequest extends BaseRequest<TimeBean>{

    public void getCurrentTimeRequest(NetworkListener<TimeBean> networkListener) {
        HashMap<String, String> parameterMap = new HashMap<>();
        basePostRequest(Urls.URL_CURRENT_TIME, parameterMap, networkListener);
    }

}
