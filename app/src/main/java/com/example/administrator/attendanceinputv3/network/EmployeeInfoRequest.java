package com.example.administrator.attendanceinputv3.network;

import com.example.administrator.attendanceinputv3.constant.Urls;
import com.example.administrator.attendanceinputv3.model.EmployeeInfoBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author: Caoy
 * @created on: 2018/9/5 16:27
 * @description:
 */
public class EmployeeInfoRequest extends BaseRequest<EmployeeInfoBean> {

    public void getEmployeeInfoRequest(String code, NetworkListener networkListener, Object tag) {
        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("id", "1582");
        basePostRequest(Urls.URL_EMPLOYEE_INFO, parameterMap, networkListener,tag);
    }

}
