package com.example.administrator.attendanceinputv3.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.administrator.attendanceinputv3.R;
import com.example.administrator.attendanceinputv3.base.BaseApplication;

/**
 * @author: Caoy
 * @created on: 2018/9/3 13:51
 * @description:
 */
public class NetworkUtils {

    /**
     * 检查是否有网络
     */
    public static boolean isNetworkAvailable() {
        NetworkInfo info = getNetworkInfo();
        if (info != null) {
            return info.isAvailable();
        }
        ToastUtils.shortShowStr(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.network_available));
        return false;
    }

    private static NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) BaseApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

}
