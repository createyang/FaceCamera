package com.quansoon.facecamera.network;

import com.quansoon.facecamera.constant.Urls;
import com.quansoon.facecamera.model.VersionUpdateBean;

import java.util.HashMap;

/**
 * @author: Caoy
 * @created on: 2018/9/1 16:17
 * @description:
 */
public class CheckUpdateRequest extends BaseRequest<VersionUpdateBean> {

    public CheckUpdateRequest() {
        super();
    }

    public void postRequest(NetworkListener<VersionUpdateBean> networkListener) {
        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("clientType", "HA_TV");
        basePostRequest(Urls.URL_VERSION_UPDATE, parameterMap,networkListener);
    }
}
