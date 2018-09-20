package com.quansoon.facecamera.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.network.NetChangeObserver;
import com.quansoon.facecamera.network.NetStateReceiver;
import com.quansoon.facecamera.utils.ToastUtils;

/**
 *
 * @author caoy
 * @date 2018/3/30
 */

public abstract class BaseFragment extends Fragment {
    public Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initData();
    }

    public abstract View initView();

    public abstract void initData();

    public abstract void netConnectedSucceed(int type);

    public abstract void netConnectedFailed();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
