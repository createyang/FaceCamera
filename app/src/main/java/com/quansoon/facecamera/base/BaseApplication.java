package com.quansoon.facecamera.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.quansoon.facecamera.network.NetStateReceiver;
import com.quansoon.facecamera.utils.Density;
import com.quansoon.facecamera.utils.LogUtils;

import sdk.facecamera.sdk.FaceCamera;

import static java.lang.System.exit;


/**
 * @author Caoy
 * @date 2018/5/18
 */

public class BaseApplication extends Application {
    private static BaseApplication app;
    private static Context mContext;

    private static FaceCamera faceCamera;
    private static Handler mHandler;
    private static int mainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        mContext = getApplicationContext();
        //UI提供设计图的宽为1280，进行适配
        Density.setDensity(this, 1920);

        //初始化摄像头
        faceCamera = new FaceCamera();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                LogUtils.d("msg.what: " + msg.what
                        + "msg.obj: " + msg.obj
                );
            }
        };
        mainThreadId = android.os.Process.myPid();

        //初始化网络监听
        NetStateReceiver.registerNetworkStateReceiver(this);

//        EZOpenSDK.showSDKLog(true);
        /** * 设置是否支持P2P取流,详见api */
//        EZOpenSDK.enableP2P(false);

        /** * APP_KEY请替换成自己申请的 */
//        EZOpenSDK.initLib(this, SyncStateContract.Constants.VIDEO_APPKEY);

    }

    public static BaseApplication getInstance() {
        return app;
    }

    /**
     * 得到上下文
     */
    public static Context getContext() {
        return mContext;
    }

    public static FaceCamera getFaceCamera() {
        return faceCamera;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        NetStateReceiver.unRegisterNetworkStateReceiver(this);
        android.os.Process.killProcess(android.os.Process.myPid());
        exit(1);
    }

}
