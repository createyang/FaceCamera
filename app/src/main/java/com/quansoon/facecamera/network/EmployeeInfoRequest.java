package com.quansoon.facecamera.network;

import com.quansoon.facecamera.constant.Urls;
import com.quansoon.facecamera.model.EmployeeInfoBean;

import java.util.HashMap;

/**
 * @author: Caoy
 * @created on: 2018/9/5 16:27
 * @description:
 */
public class EmployeeInfoRequest extends BaseRequest<EmployeeInfoBean> {

    public void getEmployeeInfoRequest(String code, NetworkListener networkListener, Object tag) {
        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("id", code);
        basePostRequest(Urls.URL_EMPLOYEE_INFO, parameterMap, networkListener,tag);
    }

}
