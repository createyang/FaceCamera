package com.example.administrator.attendanceinputv3.model;

import java.util.List;

/**
 * @author: Caoy
 * @created on: 2018/9/1 16:18
 * @description:
 */
public class FaceDeviceBean {

        /**
         * deviceId : 0216
         * deviceAddress : 大厅
         * status : 0
         * faceIp : 172.21 .2 .203
         */

        private String deviceId;
        private String deviceAddress;
        private String status;
        private String faceIp;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceAddress() {
            return deviceAddress;
        }

        public void setDeviceAddress(String deviceAddress) {
            this.deviceAddress = deviceAddress;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFaceIp() {
            return faceIp;
        }

        public void setFaceIp(String faceIp) {
            this.faceIp = faceIp;
        }

    @Override
    public String toString() {
        return "FaceDeviceBean{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceAddress='" + deviceAddress + '\'' +
                ", status='" + status + '\'' +
                ", faceIp='" + faceIp + '\'' +
                '}';
    }
}
