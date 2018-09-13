package com.quansoon.facecamera.presenter;

/**
 * @author: Caoy
 * @created on: 2018/9/1 11:47
 * @description:
 */
public interface LoginPresenter {
    /**
     * 选择考勤设备ip号
     * @param code
     */
    void checkDeviceCode(String code);

    /**
     * 检查应用更新
     */
    void checkAppUpdate();

}
