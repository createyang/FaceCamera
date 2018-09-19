package com.quansoon.facecamera.model;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Caoy
 */
@Entity
public class PersonModel {
    @Id
    private Long id;

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

    @Generated(hash = 117846192)
    public PersonModel(Long id, String personID, String name, byte age, byte sex,
            byte[] scanImg, String ioTimeStr, String verifyFacImgUrl,
            byte[] verifyFacImg, boolean result, String groupName, String job,
            String matchScore) {
        this.id = id;
        this.personID = personID;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.scanImg = scanImg;
        this.ioTimeStr = ioTimeStr;
        this.verifyFacImgUrl = verifyFacImgUrl;
        this.verifyFacImg = verifyFacImg;
        this.result = result;
        this.groupName = groupName;
        this.job = job;
        this.matchScore = matchScore;
    }

    @Generated(hash = 1012623646)
    public PersonModel() {
    }

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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getResult() {
        return this.result;
    }
}
