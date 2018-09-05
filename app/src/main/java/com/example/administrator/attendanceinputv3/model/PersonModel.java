package com.example.administrator.attendanceinputv3.model;



/**
 * @author Caoy
 */
public class PersonModel {
    private String personID;
    public String name;
    public byte age;
    public byte sex;
    public byte[] img;
    public boolean result;
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

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
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

    /** 获取人员编号；如果未匹配到可能为null或者空值 */
    public String getPersonID() {
        return personID;
    }

    /** 设置人员编号 */
    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
