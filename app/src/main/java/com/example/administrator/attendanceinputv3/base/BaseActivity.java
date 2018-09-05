package com.example.administrator.attendanceinputv3.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.administrator.attendanceinputv3.utils.Density;

/**
 *
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
        intView();
        intData();
    }

    public abstract int getLayoutResId();
    public abstract void intView();
    public abstract void intData();



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
