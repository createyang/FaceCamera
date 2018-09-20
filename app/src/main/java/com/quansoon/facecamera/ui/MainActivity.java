package com.quansoon.facecamera.ui;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.base.BaseActivity;
import com.quansoon.facecamera.constant.Constants;
import com.quansoon.facecamera.model.ConnectDeviceBean;
import com.quansoon.facecamera.utils.LogUtils;
import com.quansoon.facecamera.utils.ThreadPoolUtils;
import com.quansoon.facecamera.utils.UiUtil;

import java.util.ArrayList;

import sdk.facecamera.sdk.FaceCamera;

/**
 * @author caoyang
 */
public class MainActivity extends BaseActivity {
    public static final String KEY_BUNDLE_CONNECT_DEVICE = "connectDevice";

    private String ip;
    private HomeFragment homeFragment;
    private String title;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        //获取权限
        initPermission();
        //获取登录账号信息
        Intent intent = getIntent();
        ip = intent.getStringExtra(Constants.Extra.IP);
        title = intent.getStringExtra(Constants.Extra.TITLE);
        ConnectDeviceBean connectDeviceBean = new ConnectDeviceBean();
//        connectDeviceBean.setIp("172.21.2.201");
        connectDeviceBean.setIp(ip);
        connectDeviceBean.setUsername("admin");
        connectDeviceBean.setPassword("admin");
        connectDeviceBean.setTitle(title);

        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.KEY_BUNDLE_CONNECT_DEVICE, connectDeviceBean);

        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        FragmentManager fm = getSupportFragmentManager();
        homeFragment.setArguments(bundle);
        fm.beginTransaction().add(R.id.layout_fragment_home, homeFragment)
                .commit();
    }

    @Override
    public void netConnectedFailed() {

    }

    @Override
    public void netConnectedSucceed(int type) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                FaceCamera faceCamera = UiUtil.getFaceCamera();
                if (faceCamera != null) {
                    faceCamera.stopVideoPlay();
                    faceCamera.unInitialize();
                    faceCamera.disConnect();
                    LogUtils.i("注销成功");
                }
            }
        });
    }


    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        LogUtils.d("requestCode: " + requestCode + "  permissions: " + permissions + "  grantResults: " + grantResults);
    }
}
