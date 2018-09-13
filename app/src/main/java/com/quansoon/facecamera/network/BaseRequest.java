package com.quansoon.facecamera.network;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.base.BaseApplication;
import com.quansoon.facecamera.utils.NetworkUtils;
import com.quansoon.facecamera.utils.StringUtils;
import com.google.gson.Gson;
import com.quansoon.facecamera.utils.ToastUtils;

import org.json.JSONObject;

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
    private Object tag;

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

    public Request baseGetRequest() {
        return request;
    }


    public BaseRequest() {
        gson = new Gson();
    }

    /**
     * @return
     */
    public NetworkListener getNetworkListener() {
        return networkListener;
    }

    void baseGetRequest(String url, Map<String, String> params, NetworkListener networkListener) {
        networkListener.onStart();
        if (!NetworkUtils.isNetworkAvailable()) {
            ToastUtils.shortShowStr(BaseApplication.getContext(),"isNetworkAvailable 111");
            networkListener.onError(BaseApplication.getContext().getString(R.string.network_available));
            return;
        }
        this.url = url;
        this.params = params;
        this.networkListener = networkListener;
        request = buildRequest(url, BaseRequest.HttpMethodType.GET, params, null);
        NetworkManager.getInstance().asynRequest(this);
    }


    void basePostRequest(String url, Map<String, String> params, NetworkListener networkListener) {
        basePostRequest(url, params, networkListener, null);
    }

    void basePostRequest(String url, Map<String, String> params, NetworkListener networkListener, Object tag) {
        networkListener.onStart();

        if (!NetworkUtils.isNetworkAvailable()) {
            ToastUtils.shortShowStr(BaseApplication.getContext(),"isNetworkAvailable 222");
            networkListener.onError(BaseApplication.getContext().getString(R.string.network_available));
            return;
        }
        this.url = url;
        this.params = params;
        this.networkListener = networkListener;
        if (tag != null) {
            this.tag = tag;
        }
        request = buildRequest(url, HttpMethodType.POST, params, tag);
        NetworkManager.getInstance().asynRequest(this);
    }


    private Request buildRequest(String url, BaseRequest.HttpMethodType methodType, Map<String, String> params, Object tag) {
        Request.Builder builder = new Request.Builder();
        if (methodType == BaseRequest.HttpMethodType.POST) {
            builder.url(url)
                    .post(builderFormData(params));
        } else if (methodType == BaseRequest.HttpMethodType.GET) {
            builder.url(builderGetUrl(url, params))
                    .get();
        }
        if (tag != null) {
            builder.tag(tag);
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
        String content = "";
        if (params != null) {
            JSONObject jsonObject = new JSONObject(params);
            content = jsonObject.toString();
        }
        return RequestBody.create(JSON, content);
    }


    public T parsedResponse(String str) {
        Class<? extends BaseRequest> aClass = this.getClass();
        ParameterizedType parameterizedType = (ParameterizedType) aClass.getGenericSuperclass();
        Type type = parameterizedType.getActualTypeArguments()[0];
        return gson.fromJson(str, type);
    }

}
