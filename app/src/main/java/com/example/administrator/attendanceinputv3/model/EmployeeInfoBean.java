package com.example.administrator.attendanceinputv3.model;

/**
 * @author: Caoy
 * @created on: 2018/9/5 16:27
 * @description:
 */
public class EmployeeInfoBean extends BaseResultBean<EmployeeInfoBean>{
    /**
     * id : 1582
     * name : 方建辉
     * groupName : 全容公司
     * job : JAVA工程师
     * verifyFace :
     * installType :
     * installTypeStr :
     * ioTimeStr :
     */

    private int id;
    private String name;
    private String groupName;
    private String job;
    private String verifyFace;
    private String installType;
    private String installTypeStr;
    private String ioTimeStr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getVerifyFace() {
        return verifyFace;
    }

    public void setVerifyFace(String verifyFace) {
        this.verifyFace = verifyFace;
    }

    public String getInstallType() {
        return installType;
    }

    public void setInstallType(String installType) {
        this.installType = installType;
    }

    public String getInstallTypeStr() {
        return installTypeStr;
    }

    public void setInstallTypeStr(String installTypeStr) {
        this.installTypeStr = installTypeStr;
    }

    public String getIoTimeStr() {
        return ioTimeStr;
    }

    public void setIoTimeStr(String ioTimeStr) {
        this.ioTimeStr = ioTimeStr;
    }
}
