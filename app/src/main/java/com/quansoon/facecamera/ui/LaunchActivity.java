package com.quansoon.facecamera.ui;

import android.content.Intent;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.base.BaseActivity;

/**
 * @author Caoy
 */
public class LaunchActivity extends BaseActivity {


    @Override
    public int getLayoutResId() {
        return R.layout.activity_launch;
    }

    @Override
    public void intView() {
        startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void intData() {

    }
}
