package com.quansoon.facecamera.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.network.NetChangeObserver;
import com.quansoon.facecamera.network.NetStateReceiver;
import com.quansoon.facecamera.utils.ToastUtils;
import com.quansoon.facecamera.utils.UiUtil;

/**
 * @author caoyang
 * @date 2018/8/31
 */

public abstract class BaseActivity extends AppCompatActivity implements NetChangeObserver {

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
        NetStateReceiver.addObserver(this);
    }


    public abstract int getLayoutResId();

    public abstract void initView();

    public abstract void initData();

    public abstract void netConnectedFailed();

    public abstract void netConnectedSucceed(int type);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UiUtil.getHandler().removeCallbacks(null);
        NetStateReceiver.removeRegisterObserver(this);
    }


    @Override
    public void onNetConnected(int type) {
        UiUtil.getHandler().removeCallbacks(netDisRun);
        netConnectedSucceed(type);
    }

    @Override
    public void onNetDisConnect() {
        netConnectedFailed();
        ToastUtils.shortShowStr(this, getString(R.string.network_available));
        UiUtil.getHandler().postDelayed(netDisRun, 10000);
    }

    Runnable netDisRun = new Runnable() {
        @Override
        public void run() {
            onNetDisConnect();
        }
    };

}
