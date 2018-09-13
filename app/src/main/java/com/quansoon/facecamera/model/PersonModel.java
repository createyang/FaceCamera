package com.quansoon.facecamera.model;


/**
 * @author Caoy
 */
public class PersonModel {

    private String personID;
    public String name;
    public byte age;
    public byte sex;
    public byte[] scanImg;
    private String ioTimeStr;
    private String verifyFacImgUrl;
    public byte[] verifyFacImg;
    public boolean result;
    private String groupName;
    private String job;
    private String matchScore;


    public String getIoTimeStr() {
        return ioTimeStr;
    }

    public String getVerifyFacImgUrl() {
        return verifyFacImgUrl;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getJob() {
        return job;
    }


    public byte[] getVerifyFacImg() {
        return verifyFacImg;
    }

    public void setVerifyFacImg(byte[] verifyFacImg) {
        this.verifyFacImg = verifyFacImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getAge() {
        return age;
    }

    public void setAge(byte age) {
        this.age = age;
    }

    public byte[] getScanImg() {
        return scanImg;
    }

    public void setScanImg(byte[] scanImg) {
        this.scanImg = scanImg;
    }

    public byte getSex() {
        return sex;
    }

    public void setSex(byte sex) {
        this.sex = sex;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    /**
     * 获取人员编号；如果未匹配到可能为null或者空值
     */
    public String getPersonID() {
        return personID;
    }

    /**
     * 设置人员编号
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setIoTimeStr(String ioTimeStr) {
        this.ioTimeStr = ioTimeStr;
    }

    public void setVerifyFacImgUrl(String verifyFacImgUrl) {
        this.verifyFacImgUrl = verifyFacImgUrl;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(String matchScore) {
        this.matchScore = matchScore;
    }
}
