package com.quansoon.facecamera.model;

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

    public String getProjInfoName() {
        return projInfoName;
    }

    public void setProjInfoName(String projInfoName) {
        this.projInfoName = projInfoName;
    }

    private String projInfoName;

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

}
