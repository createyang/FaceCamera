package com.example.administrator.attendanceinputv3.view;

import android.view.SurfaceHolder;

import com.example.administrator.attendanceinputv3.model.PersonModel;

import java.util.List;

/**
 * @author: Caoy
 * @created on: 2018/9/4 15:05
 * @description:
 */
public interface HomeView {
    SurfaceHolder getSurfaceHolder();

    void notifyDataSetChanged(List<PersonModel> personList);

    void showScanFeatureImage(byte[] featureImageData);
}
