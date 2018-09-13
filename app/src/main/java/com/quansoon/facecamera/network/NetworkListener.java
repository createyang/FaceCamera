package com.quansoon.facecamera.network;

/**
 * @author: Caoy
 * @created on: 2018/9/1 15:55
 * @description:
 */
public interface NetworkListener<T> {
    void onStart();

    void onError(String localizedMessage);

    void onSuccess(T data);
}
