package com.quansoon.facecamera.view;

import android.view.SurfaceHolder;

import com.quansoon.facecamera.model.PersonModel;

import java.util.ArrayList;

/**
 * @author: Caoy
 * @created on: 2018/9/4 15:05
 * @description:
 */
public interface HomeView {

    SurfaceHolder getSurfaceHolder();

    void startScan();

    void errorScan();


    void errorMatched();

    void successScanList(ArrayList<PersonModel> personList);

    void notifyUiRefreshData(ArrayList<PersonModel> personList);
}
