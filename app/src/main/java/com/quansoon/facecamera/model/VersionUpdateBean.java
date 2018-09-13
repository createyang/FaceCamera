package com.quansoon.facecamera.model;

/**
 * @author: Caoy
 * @created on: 2018/9/10 15:57
 * @description:
 */
public class VersionUpdateBean {


    /**
     * versionNo : 11
     * description : -版本号： 1.0.0开发测试版
     * packageSize : 10.0
     * dloadUrl : http://download.zhiguanzhuang.com/11/zgz.apk
     * iosStatus : 1
     * versionName : 1.0.0
     */

    private int versionNo;
    private String description;
    private double packageSize;
    private String dloadUrl;
    private String iosStatus;
    private String versionName;

    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(double packageSize) {
        this.packageSize = packageSize;
    }

    public String getDloadUrl() {
        return dloadUrl;
    }

    public void setDloadUrl(String dloadUrl) {
        this.dloadUrl = dloadUrl;
    }

    public String getIosStatus() {
        return iosStatus;
    }

    public void setIosStatus(String iosStatus) {
        this.iosStatus = iosStatus;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
