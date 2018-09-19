package com.quansoon.facecamera.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.quansoon.facecamera.utils.UiUtil;

/**
 * @author caoyang
 * @date 2018/8/31
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getLayoutResId());
        initView();
        initData();
    }


    public abstract int getLayoutResId();

    public abstract void initView();

    public abstract void initData();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        UiUtil.getHandler().removeCallbacks(null);
    }
}
