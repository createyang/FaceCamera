package sdk.facecamera.sdk.pojos;

public final class ListFaceCriteria {
    private int role;
    private int pageNo;
    private int pageSize;
    private boolean getFeatureData;
    private boolean getImageData;

    public ListFaceCriteria() {
        role = -1;
        pageNo = 1;
        pageSize = 20;
        getFeatureData = false;
        getImageData = false;
    }

    /**
     * 获取需要获取的人员角色
     *
     * @return 人员角色 0：普通人员。 1：白名单人员。 2：黑名单人员。 -1：所有人员。
     */
    public int getRole() {
        return role;
    }
    /**
     * 设置需要获取的人员角色
     *
     * @param role 人员角色 0：普通人员。 1：白名单人员。 2：黑名单人员。 -1：所有人员。
     */
    public void setRole(int role) {
        this.role = role;
    }
    /**
     * 获取页码
     *
     * @return 页码，从1开始
     */
    public int getPageNo() {
        return pageNo;
    }
    /**
     * 设置页码
     *
     * @param pageNo 页码，从1开始
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
    /**
     * 获取页大小
     *
     * @return 页大小，需小于100
     */
    public int getPageSize() {
        return pageSize;
    }
    /**
     * 设置页大小
     *
     * @param pageSize 页大小，需小于100
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    /**
     * 获取是否回传特征数据
     *
     * @return 是否回传特征数据
     */
    public boolean isGetFeatureData() {
        return getFeatureData;
    }
    /**
     * 设置是否回传特征数据
     *
     * @param getFeatureData 回传特征数据
     */
    public void setGetFeatureData(boolean getFeatureData) {
        this.getFeatureData = getFeatureData;
    }
    /**
     * 获取是否回传特片数据
     *
     * @return 是否回传特片数据
     */
    public boolean isGetImageData() {
        return getImageData;
    }
    /**
     * 设置是否回传图片数据
     *
     * @param getImageData 是否回传图片数据
     */
    public void setGetImageData(boolean getImageData) {
        this.getImageData = getImageData;
    }
}
