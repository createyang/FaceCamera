package sdk.facecamera.sdk.pojos;

/**
 * 人脸模板数据
 */
public final class Face {
    private String id;
    private String name;
    private int role;
    private long wiegandNo;
    private long expireDate; // 0表示未启用 0xFFFFFFFF表示永不过期
    private short featureCount;
    private short featureSize;
    private float[][] featureData;
    private byte[][] imageData;
    private byte[][] twisBgrs;

    /**
     * 获取人员编号
     *
     * @return 编号
     */
    public String getId() {
        return id;
    }
    /**
     * 设置人员编号
     *
     * @param id 编号
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * 获取人员姓名
     *
     * @return 姓名
     */
    public String getName() {
        return name;
    }
    /**
     * 设置人员姓名
     *
     * @param name 姓名
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * 获取人员角色
     *
     * @return 角色
     */
    public int getRole() {
        return role;
    }
    /**
     * 设置人员角色
     *
     * @param role 角色
     */
    public void setRole(int role) {
        this.role = role;
    }

    /**
     * 获取人员韦根卡号
     *
     * @return 韦根卡号，其实类型是unsigned int
     */
    public long getWiegandNo() {
        return wiegandNo;
    }
    /**
     * 设置人员韦根卡号
     *
     * @param wiegandNo 韦根卡号，其实类型是unsigned int
     */
    public void setWiegandNo(long wiegandNo) {
        this.wiegandNo = wiegandNo;
    }

    /**
     * 获取过期(截止)时间
     *
     * @return 过期时间
     */
    public long getExpireDate() {
        return expireDate;
    }
    /**
     * 设置过期(截止)时间
     *
     * @param expireDate 过期时间
     */
    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }
    /**
     * 获取特征值数量
     *
     * @return 特征值数量
     */
    public short getFeatureCount() {
        return featureCount;
    }
    /**
     * 设置特征值数量
     *
     * @param featureCount 特征值数量
     */
    public void setFeatureCount(short featureCount) {
        this.featureCount = featureCount;
    }
    /**
     * 获取每组特征值元素数量
     *
     * @return 特征值组内元素个数
     */
    public short getFeatureSize() {
        return featureSize;
    }
    /**
     * 设置每组特征值元素数量
     *
     * @param featureSize 特征值组内元素个数
     */
    public void setFeatureSize(short featureSize) {
        this.featureSize = featureSize;
    }
    /**
     * 获取特征值数据
     *
     * @return 特征值数据
     */
    public float[][] getFeatureData() {
        return featureData;
    }
    /**
     * 设置特征值数据
     *
     * @param featureData 特征值数据
     */
    public void setFeatureData(float[][] featureData) {
        this.featureData = featureData;
    }
    /**
     * 获取图像数据
     *
     * @return 图像数据
     */
    public byte[][] getImageData() {
        return imageData;
    }
    /**
     * 设置图像数据
     *
     * @param imageData 图像数据
     */
    public void setImageData(byte[][] imageData) {
        this.imageData = imageData;
    }
    /**
     * 获取人脸归一化图像数据
     *
     * @return 归一化图像数据
     */
    public byte[][] getTwisBgrs() { return twisBgrs; }
    /**
     * 设置人脸归一化图像数据
     *
     * @param twisBgrs 归一化图像数据
     */
    public void setTwisBgrs(byte[][] twisBgrs) { this.twisBgrs = twisBgrs; }
}
