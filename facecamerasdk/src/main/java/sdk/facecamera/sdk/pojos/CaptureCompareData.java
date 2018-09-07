package sdk.facecamera.sdk.pojos;

import android.graphics.Rect;

import java.util.Arrays;
import java.util.Date;

/**
 * 抓拍对比数据
 */
public class CaptureCompareData {

    private long sequenceID;
    private String cameraID;
    private String addrID;
    private String addrName;
    private Date captureTime;
    private boolean isRealtime;
    private boolean isPersonMatched;
    private int matchScore;
    private String personID;
    private String personName;
    private int personRole;
    private byte[] environmentImageData;
    private Rect faceRegionInEnvironment;
    private byte[] featureImageData;
    private Rect faceRegionInFeature;
    private byte[] videoData;
    private Date videoStartTime;
    private Date videoEndTime;
    private byte sex;
    private byte age;
    private float[] featureData;
    private byte[] modelImageData;

    /** 获取数据包序列号 */
    public long getSequenceID() {
        return sequenceID;
    }

    /** 设置数据包序列号 */
    public void setSequenceID(long sequenceID) {
        this.sequenceID = sequenceID;
    }

    /** 获取设备编号 */
    public String getCameraID() {
        return cameraID;
    }

    /** 设置设备编号 */
    public void setCameraID(String cameraID) {
        this.cameraID = cameraID;
    }

    /** 获取地点编号 */
    public String getAddrID() {
        return addrID;
    }

    /** 设置地点编号 */
    public void setAddrID(String addrID) {
        this.addrID = addrID;
    }

    /** 获取地点名称 */
    public String getAddrName() {
        return addrName;
    }

    /** 设置地点名称 */
    public void setAddrName(String addrName) {
        this.addrName = addrName;
    }

    /** 获取抓拍时间 */
    public Date getCaptureTime() {
        return captureTime;
    }

    /** 设置抓拍时间 */
    public void setCaptureTime(Date captureTime) {
        this.captureTime = captureTime;
    }

    /** 是否实时抓拍数据 */
    public boolean isRealtime() {
        return isRealtime;
    }

    /** 设置实时抓拍数据 */
    public void setRealtime(boolean isRealtime) {
        this.isRealtime = isRealtime;
    }

    /** 是否匹配的设备库中的人员 */
    public boolean isPersonMatched() {
        return isPersonMatched;
    }

    /** 设置是否匹配成功 */
    public void setPersonMatched(boolean isPersonMatched) {
        this.isPersonMatched = isPersonMatched;
    }

    /** 获取匹配度1~100 */
    public int getMatchScore() {
        return matchScore;
    }

    /** 设置匹配度 */
    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    /** 获取人员编号；如果未匹配到可能为null或者空值 */
    public String getPersonID() {
        return personID;
    }

    /** 设置人员编号 */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /** 获取人员姓名；如果未匹配到可能为null或者空值 */
    public String getPersonName() {
        return personName;
    }

    /** 设置人员姓名 */
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    /** 获取人员角色 */
    public int getPersonRole() {
        return personRole;
    }

    /** 设置人员角色 */
    public void setPersonRole(int personRole) {
        this.personRole = personRole;
    }

    /** 获取环境图（大图）图片；可能为null表示前端未回传大图 */
    public byte[] getEnvironmentImageData() {
        return environmentImageData;
    }

    /** 设置环境图（大图）图片 */
    public void setEnvironmentImageData(byte[] environmentImageData) {
        this.environmentImageData = environmentImageData;
    }

    /** 获取人脸在环境图（大图）中的坐标 */
    public Rect getFaceRegionInEnvironment() {
        return faceRegionInEnvironment;
    }

    /** 设置人脸在环境图（大图）中的坐标 */
    public void setFaceRegionInEnvironment(Rect faceRegionInEnvironment) {
        this.faceRegionInEnvironment = faceRegionInEnvironment;
    }

    /** 获取特写图数据；可能为null表示未回传特写图 */
    public byte[] getFeatureImageData() {
        return featureImageData;
    }

    /** 设置特写图数据 */
    public void setFeatureImageData(byte[] featureImageData) {
        this.featureImageData = featureImageData;
    }

    /** 获取人脸在特写图中的区域 */
    public Rect getFaceRegionInFeature() {
        return faceRegionInFeature;
    }

    /** 设置人脸在特写图中的区域 */
    public void setFaceRegionInFeature(Rect faceRegionInFeature) {
        this.faceRegionInFeature = faceRegionInFeature;
    }

    /** 获取视频数据 */
    public byte[] getVideoData() {
        return videoData;
    }

    /** 设置视频数据 */
    public void setVideoData(byte[] videoData) {
        this.videoData = videoData;
    }

    /** 获取视频开始时间 */
    public Date getVideoStartTime() {
        return videoStartTime;
    }

    /** 设置视频开始时间 */
    public void setVideoStartTime(Date videoStartTime) {
        this.videoStartTime = videoStartTime;
    }

    /** 获取视频结束时间 */
    public Date getVideoEndTime() {
        return videoEndTime;
    }

    /** 设置视频结束时间 */
    public void setVideoEndTime(Date videoEndTime) {
        this.videoEndTime = videoEndTime;
    }

    /**
     * 获取性别
     *
     * @return 0: 无此信息 1：男 2：女
     */
    public byte getSex() {
        return sex;
    }
    /**
     * 设置性别
     *
     * @param sex 0: 无此信息 1：男 2：女
     */
    public void setSex(byte sex) {
        this.sex = sex;
    }
    /**
     * 获取年龄
     *
     * @return 0: 无此信息 其它值：年龄
     */
    public byte getAge() {
        return age;
    }
    /**
     * 设置年龄
     *
     * @param age 0: 无此信息 其它值：年龄
     */
    public void setAge(byte age) {
        this.age = age;
    }

    /** 获取（抓拍到）人脸特征值数据 */
    public float[] getFeatureData() {
        return featureData;
    }

    /** 设置（抓拍到）人脸特征值数据 */
    public void setFeatureData(float[] featureData) {
        this.featureData = featureData;
    }

    /** 获取匹配到的人脸模板图 */
    public byte[] getModelImageData() {
        return modelImageData;
    }

    /** 设置匹配到的人脸模板图 */
    public void setModelImageData(byte[] modelImageData) {
        this.modelImageData = modelImageData;
    }


    @Override
    public String toString() {
        return "CaptureCompareData{" +
                "sequenceID=" + sequenceID +
                ", cameraID='" + cameraID + '\'' +
                ", addrID='" + addrID + '\'' +
                ", addrName='" + addrName + '\'' +
                ", captureTime=" + captureTime +
                ", isRealtime=" + isRealtime +
                ", isPersonMatched=" + isPersonMatched +
                ", matchScore=" + matchScore +
                ", personID='" + personID + '\'' +
                ", personName='" + personName + '\'' +
                ", personRole=" + personRole +
                ", environmentImageData=" + Arrays.toString(environmentImageData) +
                ", faceRegionInEnvironment=" + faceRegionInEnvironment +
                ", featureImageData=" + Arrays.toString(featureImageData) +
                ", faceRegionInFeature=" + faceRegionInFeature +
                ", videoData=" + Arrays.toString(videoData) +
                ", videoStartTime=" + videoStartTime +
                ", videoEndTime=" + videoEndTime +
                ", sex=" + sex +
                ", age=" + age +
                ", featureData=" + Arrays.toString(featureData) +
                ", modelImageData=" + Arrays.toString(modelImageData) +
                '}';
    }
}