package com.quansoon.facecamera.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.quansoon.facecamera.utils.LogUtils;
import com.quansoon.facecamera.utils.NetworkUtils;
import com.quansoon.facecamera.utils.ToastUtils;
import com.quansoon.facecamera.utils.UiUtil;

import java.util.ArrayList;

/**
 * @author Caoy
 */
public class NetStateReceiver extends BroadcastReceiver {

    private final static String ANDROID_NET_CHANGE_ACTION = ConnectivityManager.CONNECTIVITY_ACTION;

    private static boolean isNetAvailable = true;
    private static int mNetType;
    private static ArrayList<NetChangeObserver> mNetChangeObservers = new ArrayList<NetChangeObserver>();
    private static BroadcastReceiver mBroadcastReceiver;
    private static NetChangeObserver observer;

    private NetStateReceiver() {
    }

    private static BroadcastReceiver getReceiver() {
        if (null == mBroadcastReceiver) {
            synchronized (NetStateReceiver.class) {
                if (null == mBroadcastReceiver) {
                    mBroadcastReceiver = new NetStateReceiver();
                }
            }
        }
        return mBroadcastReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mBroadcastReceiver = NetStateReceiver.this;
        if (ANDROID_NET_CHANGE_ACTION.equalsIgnoreCase(intent.getAction())) {
            if (!NetworkUtils.isNetworkAvailable()) {
                LogUtils.d(this.getClass().getName(), "<--- network disconnected --->");
                isNetAvailable = false;
            } else {
                LogUtils.d(this.getClass().getName(), "<--- network connected --->");
                isNetAvailable = true;
            }
            notifyObserver();
        }
    }

    /**
     * 注册
     *
     * @param mContext
     */
    public static void registerNetworkStateReceiver(Context mContext) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ANDROID_NET_CHANGE_ACTION);
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        mContext.getApplicationContext().registerReceiver(getReceiver(), filter);
    }

    /**
     * 清除
     *
     * @param mContext
     */
    public static void checkNetworkState(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(ANDROID_NET_CHANGE_ACTION);
        mContext.sendBroadcast(intent);
    }

    /**
     * 反注册
     *
     * @param mContext
     */
    public static void unRegisterNetworkStateReceiver(Context mContext) {
        if (mBroadcastReceiver != null) {
            mContext.getApplicationContext().unregisterReceiver(mBroadcastReceiver);
        }
    }

    public static boolean isNetworkAvailable() {
        return isNetAvailable;
    }

    public static int getAPNType() {
        return mNetType;
    }

    private static void notifyObserver() {
        if (!mNetChangeObservers.isEmpty()) {
            int size = mNetChangeObservers.size();
            for (int i = 0; i < size; i++) {
                observer = mNetChangeObservers.get(i);
                if (observer != null) {
                    if (isNetworkAvailable() && NetworkUtils.isNetworkAvailable()) {
                        mNetType = NetworkUtils.getAPNType();
                        observer.onNetConnected(mNetType);
                    } else {
                        observer.onNetDisConnect();
                    }
                }
            }
        }
    }


    /**
     * 添加网络监听
     *
     * @param observer
     */
    public static void addObserver(NetChangeObserver observer) {
        if (mNetChangeObservers == null) {
            mNetChangeObservers = new ArrayList<NetChangeObserver>();
        }
        if (!mNetChangeObservers.contains(observer)) {
            mNetChangeObservers.add(observer);
        }
        notifyObserver();
    }

    /**
     * 移除网络监听
     *
     * @param observer
     */
    public static void removeRegisterObserver(NetChangeObserver observer) {
        if (mNetChangeObservers != null) {
            if (mNetChangeObservers.contains(observer)) {
                mNetChangeObservers.remove(observer);
            }
        }
    }


}