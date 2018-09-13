package com.quansoon.facecamera.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.base.BaseActivity;
import com.quansoon.facecamera.model.ConnectDeviceBean;
import com.quansoon.facecamera.utils.LogUtils;
import com.quansoon.facecamera.utils.ThreadPoolUtils;
import com.quansoon.facecamera.utils.UiUtil;

import sdk.facecamera.sdk.FaceCamera;

/**
 * @author caoyang
 */
public class MainActivity extends BaseActivity {
    public static final String KEY_BUNDLE_CONNECT_DEVICE = "connectDevice";

    private String ip;
    private HomeFragment homeFragment;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void intView() {
    }

    @Override
    public void intData() {
        //获取登录账号信息
        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
        ConnectDeviceBean connectDeviceBean = new ConnectDeviceBean();
//        connectDeviceBean.setIp("172.21.2.201");
        connectDeviceBean.setIp(ip);
        connectDeviceBean.setUsername("admin");
        connectDeviceBean.setPassword("admin");
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.KEY_BUNDLE_CONNECT_DEVICE,connectDeviceBean);

        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        FragmentManager fm = getSupportFragmentManager();
        homeFragment.setArguments(bundle);
        fm.beginTransaction().add(R.id.layout_fragment_home, homeFragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                FaceCamera faceCamera = UiUtil.getFaceCamera();
                if (faceCamera != null){
                    faceCamera.stopVideoPlay();
                    faceCamera.unInitialize();
                    faceCamera.disConnect();
                    LogUtils.i("注销成功");
                }
            }
        });
    }
}
