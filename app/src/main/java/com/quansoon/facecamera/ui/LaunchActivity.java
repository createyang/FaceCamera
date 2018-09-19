package com.quansoon.facecamera.ui;

import android.content.Intent;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.base.BaseActivity;
import com.quansoon.facecamera.constant.Constants;
import com.quansoon.facecamera.utils.SharedPreferencesUtils;
import com.quansoon.facecamera.utils.StringUtils;

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
}
