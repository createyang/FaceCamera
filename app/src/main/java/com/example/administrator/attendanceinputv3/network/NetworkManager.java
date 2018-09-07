package com.example.administrator.attendanceinputv3.network;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.example.administrator.attendanceinputv3.R;
import com.example.administrator.attendanceinputv3.base.BaseApplication;
import com.example.administrator.attendanceinputv3.constant.Constants;
import com.example.administrator.attendanceinputv3.model.BaseResultBean;
import com.example.administrator.attendanceinputv3.utils.LogUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * @author: Caoy
 * @created on: 2018/9/1 13:54
 * @description: 管理网络请求
 */
public class NetworkManager {

    private final OkHttpClient okHttpClient;
    private final Gson gson;
    private final Handler handler;

    private NetworkManager() {
        //https
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        //构建OkHttpClient
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY)
                .build();

        handler = new Handler(Looper.getMainLooper());

        gson = new Gson();
    }

    private static NetworkManager networkManager = null;

    public static NetworkManager getInstance() {
        if (networkManager == null) {
            synchronized (NetworkManager.class) {
                if (networkManager == null) {
                    networkManager = new NetworkManager();
                }
            }
        }
        return networkManager;
    }


    public void asynRequest(final BaseRequest baseRequest) {
        baseRequest.getNetworkListener().onStart();
        String url = baseRequest.getUrl();
        if (url == null) {
            throw new NullPointerException("url is null");
        }

        if (baseRequest.getNetworkListener() == null) {
            throw new NullPointerException("callback is null") {
            };
        }

        Map params = baseRequest.getParams();
        if (params == null) {
            throw new NullPointerException("params is null");
        }

        okHttpClient.newCall(baseRequest.baseGetRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d("request base onFailure tag: " + call.request().tag());
                if (e instanceof SocketTimeoutException) {
                    //判断超时异常
                    callbackError(baseRequest.getNetworkListener(), BaseApplication.getContext().getString(R.string.exception_request_timeout));
                    return;
                }
                if (e instanceof ConnectException) {
                    ////判断连接异常，
                    callbackError(baseRequest.getNetworkListener(), BaseApplication.getContext().getString(R.string.exception_connect));
                    return;
                }
                callbackError(baseRequest.getNetworkListener(), e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.d("request base onResponse tag: " + call.request().tag());
                if (response.isSuccessful()) {
                    try {
                        String str = response.body().string();
                        if (!TextUtils.isEmpty(str)) {
                            LogUtils.e("result:" + str);
                            BaseResultBean bean = gson.fromJson(str, BaseResultBean.class);

                            if (bean.getRetCode().equals(Constants.CHECK_RESULTS_CODE)) {
                                Object result = bean.getResult();
//                                ArrayList resultList = bean.getResult();
                                if (result == null) {
                                    callbackError(baseRequest.getNetworkListener(), BaseApplication.getContext().getString(R.string.server_returns_empty_data));
                                    return;
                                }

                                String json = gson.toJson(result);
                                callbackSuccess(baseRequest.getNetworkListener(), baseRequest.parsedResponse(json));

//                                JSONObject jsonObject = new JSONObject()
                            } else {
                                callbackError(baseRequest.getNetworkListener(), str);
                            }
                        }
                    } catch (final Exception e) {
                        LogUtils.e("convert json failure");
                        callbackError(baseRequest.getNetworkListener(), e.getLocalizedMessage());
                    }
                } else {
                    LogUtils.e("error:" + response.body().string());
                    callbackError(baseRequest.getNetworkListener(), BaseApplication.getContext().getString(R.string.server_exception));
                }
            }
        });
    }

    private void callbackSuccess(final NetworkListener networkListener, final Object o) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                networkListener.onSuccess(o);
            }
        });
    }


    private void callbackError(final NetworkListener networkListener, final String e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                networkListener.onError(e);
            }
        });
    }
}
