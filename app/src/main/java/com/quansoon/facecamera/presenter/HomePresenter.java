package com.quansoon.facecamera.presenter;

import com.quansoon.facecamera.model.PersonModel;

import java.util.List;

import sdk.facecamera.sdk.FaceCamera;

/**
 * @author: Caoy
 * @created on: 2018/9/4 14:54
 * @description:
 */
public interface HomePresenter {
   FaceCamera startVideoPlay();

    void removeRun();

    List<PersonModel> getPersonList();

}
