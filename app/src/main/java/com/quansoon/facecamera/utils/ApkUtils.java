package com.quansoon.facecamera.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;


import com.quansoon.facecamera.base.BaseApplication;

import java.io.File;

public class ApkUtils {

    //放在下载更新包的文件夹
    public static String updateDownloadDir = "updateApk";

    /**
     * 获取该软件版本
     *
     * @return
     */
    public static int getVersionNumber() {
        int version = 0;
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = BaseApplication.getContext().getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    BaseApplication.getContext().getPackageName(), 0);
            version = packInfo.versionCode;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }


    /**
     * 获取版本名字
     *
     * @return
     */
    public static String getVersionName() {
        String version;
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = BaseApplication.getContext().getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    BaseApplication.getContext().getPackageName(), 0);
            version = packInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName() {
        try {
            PackageManager packageManager = BaseApplication.getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    BaseApplication.getContext().getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return BaseApplication.getContext().getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断当前应用是否是debug状态
     */

    public static String isApkInDebug() {
        try {
            ApplicationInfo info = BaseApplication.getContext().getApplicationInfo();
            if (((info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0)) {
                return "debug";
            }else {
                return "release";
            }
        } catch (Exception e) {
            return "release";
        }
    }


    /**
     * 删除更新包
     */
    public static void clearUpateApk(String fileName) {
        File updateDir;
        File updateFile;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory(),
                    updateDownloadDir);
        } else {
            updateDir = BaseApplication.getContext().getFilesDir();
        }
        updateFile = new File(updateDir.getPath(), fileName);
        if (updateFile.exists()) {
            LogUtils.d("升级包存在，删除升级包");
            updateFile.delete();
        } else {
            LogUtils.d("升级包不存在，不用删除升级包");
        }
    }
}
 
