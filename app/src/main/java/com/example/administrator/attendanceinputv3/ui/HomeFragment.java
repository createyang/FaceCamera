package com.example.administrator.attendanceinputv3.ui;

import android.media.MediaCodec;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.administrator.attendanceinputv3.R;
import com.example.administrator.attendanceinputv3.adapter.HomeRecyAdapter;
import com.example.administrator.attendanceinputv3.model.ConnectDeviceBean;
import com.example.administrator.attendanceinputv3.model.PersonModel;
import com.example.administrator.attendanceinputv3.presenter.HomePresenter;
import com.example.administrator.attendanceinputv3.presenter.impl.HomePresenterImpl;
import com.example.administrator.attendanceinputv3.view.HomeView;
import java.util.List;


/**
 * @author Caoy
 */
public class HomeFragment extends BaseFragment implements HomeView {


    private SurfaceView mSurfaceView;
    private RecyclerView mRecyclerView;
    private HomePresenter homePresenter;
    public HomeRecyAdapter recyAdapter;
    private MediaCodec mCodec;

    public ConnectDeviceBean getConnectDeviceBean() {
        return connectDeviceBean;
    }

    private ConnectDeviceBean connectDeviceBean;

    @Override
    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceView.getHolder();
    }

    @Override
    public void notifyDataSetChanged(List<PersonModel> personList) {
        recyAdapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(personList.size() - 1);
    }

    @Override
    public void showScanFeatureImage(byte[] featureImageData) {

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            homePresenter.startVideoPlay();
        }
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_home, null);
        mSurfaceView = view.findViewById(R.id.sueface_view);
        mRecyclerView = view.findViewById(R.id.recy_view);
        return view;
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        connectDeviceBean = (ConnectDeviceBean) bundle.getSerializable(MainActivity.KEY_BUNDLE_CONNECT_DEVICE);
        homePresenter = new HomePresenterImpl(this,connectDeviceBean);

        recyAdapter = new HomeRecyAdapter(homePresenter.getPersonList());
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(recyAdapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        homePresenter.removeRun();
    }


}
