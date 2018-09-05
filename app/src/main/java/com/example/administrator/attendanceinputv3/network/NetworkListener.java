package com.example.administrator.attendanceinputv3.network;

/**
 * @author: Caoy
 * @created on: 2018/9/1 15:55
 * @description:
 */
public interface NetworkListener<T> {

    void onError(String localizedMessage);

    void onSuccess(T data);
}
