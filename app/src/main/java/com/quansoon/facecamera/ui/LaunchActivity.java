package com.quansoon.facecamera.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.quansoon.facecamera.R;
import com.quansoon.facecamera.baidutts.AutoCheck;
import com.quansoon.facecamera.baidutts.NonBlockSyntherizer;
import com.quansoon.facecamera.baidutts.OfflineResource;
import com.quansoon.facecamera.baidutts.UiMessageListener;
import com.quansoon.facecamera.base.BaseActivity;
import com.quansoon.facecamera.constant.Constants;
import com.quansoon.facecamera.baidutts.InitConfig;
import com.quansoon.facecamera.utils.LogUtils;
import com.quansoon.facecamera.utils.SharedPreferencesUtils;
import com.quansoon.facecamera.utils.StringUtils;
import com.quansoon.facecamera.utils.UiUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Caoy
 */
public class LaunchActivity extends BaseActivity {

    @Override
    public int getLayoutResId() {
        return R.layout.activity_launch;
    }

    @Override
    public void initView() {

        SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance();
        sharedPreferencesUtils.init(this, Constants.SP.NAME_LOGIN_STATE);
        if (sharedPreferencesUtils.getValue(Constants.SP.KEY_LOGIN_STATE, false) && !StringUtils.isEmpty(sharedPreferencesUtils.getValue(Constants.SP.KEY_LOGIN_IP, ""))) {
            Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
            intent.putExtra(Constants.Extra.IP, sharedPreferencesUtils.getValue(Constants.SP.KEY_LOGIN_IP, ""));
            intent.putExtra(Constants.Extra.TITLE, sharedPreferencesUtils.getValue(Constants.SP.KEY_LOGIN_TITLE, ""));
            startActivity(intent);
        } else {
            startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
        }
        finish();
    }

    @Override
    public void initData() {
    }

    @Override
    public void netConnectedFailed() {

    }

    @Override
    public void netConnectedSucceed(int type) {

    }
}
