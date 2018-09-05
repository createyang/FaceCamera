package sdk.facecamera.sdk.events;

/**
 * 连接状态改变事件回调
 *
 */
public interface ConnectedStateChangedEventHandler {

    /**
     * 连接状态改变事件处理函数
     *
     * @param connected 当前连接状态（true表示设备重连成功，false表示设备已离线）
     */
    void onConnectedStateChanged(boolean connected);
}