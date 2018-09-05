package com.example.administrator.attendanceinputv3.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.provider.SyncStateContract;

import com.example.administrator.attendanceinputv3.utils.Density;

import sdk.facecamera.sdk.FaceCamera;


/**
 *
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
        Density.setDensity(this, 1280);

        //初始化摄像头
        faceCamera = new FaceCamera();
        mHandler = new Handler();
        mainThreadId = android.os.Process.myPid();


//        EZOpenSDK.showSDKLog(true);
        /** * 设置是否支持P2P取流,详见api */
//        EZOpenSDK.enableP2P(false);

        /** * APP_KEY请替换成自己申请的 */
//        EZOpenSDK.initLib(this, SyncStateContract.Constants.VIDEO_APPKEY);

    }

    public static BaseApplication getInstance() {
        return app;
    }

    /**得到上下文*/
    public static Context getContext() {
        return mContext;
    }

    public static FaceCamera getFaceCamera(){
        return faceCamera;
    }
    public static Handler getHandler(){
        return mHandler;
    }
    public static int getMainThreadId(){
        return mainThreadId;
    }

}
