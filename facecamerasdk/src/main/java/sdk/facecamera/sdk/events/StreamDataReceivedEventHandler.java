package sdk.facecamera.sdk.events;

/**
 * 收到设备视频数据事件
 */
public interface StreamDataReceivedEventHandler {

    /**
     * 收到设备视频数据事件处理函数
     *
     * @param width 画面宽度（以像素计）
     * @param height 画面高度（以像素计）
     * @param h264 h264数据段
     */
    void onStreamDataReceived(int width, int height, byte[] h264);
}
