package com.example.administrator.attendanceinputv3.constant;

/**
 * @author: Caoy
 * @created on: 2018/9/1 14:40
 * @description:
 */
public class Urls {
    /**
     * 基础接口
     */
    private static String HOST_BASE_CLIENT = Constants.IP_TEST;
    /**
     * 获取设备
     * http://172.21.2.113:8080/client/face/getFaceDevice
     */
    public static String URL_FACE_DEVICE = HOST_BASE_CLIENT + "/getFaceDevice";
    /**
     * 获取员工信息
     * http://172.21.2.113:8080/client/face/getWorkerById
     */
    public static String URL_EMPLOYEE_INFO = HOST_BASE_CLIENT + "/getWorkerById";

}
