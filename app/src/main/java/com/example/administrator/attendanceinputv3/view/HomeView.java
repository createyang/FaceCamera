package com.example.administrator.attendanceinputv3.view;

import android.content.Context;
import android.view.SurfaceHolder;

import com.example.administrator.attendanceinputv3.model.PersonModel;

import java.util.ArrayList;
import java.util.List;

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
