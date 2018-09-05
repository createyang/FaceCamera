package sdk.facecamera.sdk.pojos;

public final class ListFaceResult {
    private int total;
    private Face[] faces;

    /**
     * 获取总共数量
     *
     * @return 总数
     */
    public int getTotal() {
        return total;
    }

    /**
     * 设置总数
     *
     * @param total
     *            总数
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * 获取特征值数组
     *
     * @return 当前页特征值数组
     */
    public Face[] getFaces() {
        return faces;
    }

    /**
     * 设置当前页特征值数组
     *
     * @param faces
     *            特征值数组
     */
    public void setFaces(Face[] faces) {
        this.faces = faces;
    }
}
