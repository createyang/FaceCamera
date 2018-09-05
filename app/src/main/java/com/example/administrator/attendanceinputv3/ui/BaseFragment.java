package com.example.administrator.attendanceinputv3.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.attendanceinputv3.base.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * Created by 云中双月 on 2018/3/30.
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
}
