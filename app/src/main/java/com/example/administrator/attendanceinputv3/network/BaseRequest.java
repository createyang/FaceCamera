package com.example.administrator.attendanceinputv3.network;

import com.example.administrator.attendanceinputv3.R;
import com.example.administrator.attendanceinputv3.base.BaseApplication;
import com.example.administrator.attendanceinputv3.model.BaseResultBean;
import com.example.administrator.attendanceinputv3.utils.NetworkUtils;
import com.example.administrator.attendanceinputv3.utils.StringUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author: Caoy
 * @created on: 2018/9/1 15:23
 * @description:
 */
public abstract class BaseRequest<T> {
    enum HttpMethodType {
        /**
         * get/post请求
         */
        GET,
        POST,
    }

    private String url;
    private Map<String, String> params;
    private Request request;
    private Gson gson;
    private NetworkListener<T> networkListener;

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Request getRequest() {
        return request;
    }


    public BaseRequest() {
        gson = new Gson();
    }

    /**
     * @return
     */
    public  NetworkListener getNetworkListener(){
        return networkListener;
    };

    void getRequest(String url, Map<String, String> params,NetworkListener networkListener) {
        if (!NetworkUtils.isNetworkAvailable()) {
            networkListener.onError(BaseApplication.getContext().getString(R.string.network_available));
            return;
        }
        this.url = url;
        this.params = params;
        this.networkListener = networkListener;
        request = buildRequest(url, BaseRequest.HttpMethodType.GET, params);
        NetworkManager.getInstance().request(this);
    }

    void postRequest(String url, Map<String, String> params,NetworkListener networkListener) {
        if (!NetworkUtils.isNetworkAvailable()) {
            networkListener.onError(BaseApplication.getContext().getString(R.string.network_available));
            return;
        }
        this.url = url;
        this.params = params;
        this.networkListener = networkListener;
        request = buildRequest(url, HttpMethodType.POST, params);
        NetworkManager.getInstance().request(this);
    }


    private Request buildRequest(String url, BaseRequest.HttpMethodType methodType, Map<String, String> params) {
        Request.Builder builder = new Request.Builder();
        if (methodType == BaseRequest.HttpMethodType.POST) {
            builder.url(url)
                    .post(builderFormData(params));
        } else if (methodType == BaseRequest.HttpMethodType.GET) {
            builder.url(builderGetUrl(url, params))
                    .get();
        }
        return builder.build();
    }

    private String builderGetUrl(String url, Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        if (params.size() > 0) {
            for (String key : params.keySet()) {
                sb.append(key + "=");
                if (StringUtils.isEmpty(params.get(key))) {
                    sb.append("&");
                } else {
                    String value = params.get(key);
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    sb.append(value + "&");
                }
            }
        }
        return url + "/" + sb.toString();
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private RequestBody builderFormData(Map<String, String> params) {
//        FormBody.Builder builder = new FormBody.Builder();
//        if (params != null) {
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                builder.add(entry.getKey(), entry.getValue());
//            }
//        }

        JSONObject jsonObject = new JSONObject(params);
        return RequestBody.create(JSON, jsonObject.toString());
    }


    public T parsedResponse(String str) {
        Class<? extends BaseRequest> aClass = this.getClass();
        ParameterizedType parameterizedType = (ParameterizedType) aClass.getGenericSuperclass();
        Type type = parameterizedType.getActualTypeArguments()[0];
        return gson.fromJson(str, type);
    }

}
