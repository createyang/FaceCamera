package sdk.facecamera.sdk.events;

import sdk.facecamera.sdk.pojos.CaptureCompareData;

/**
 * 收到设备抓拍对比数据事件
 */
public interface CaptureCompareDataReceivedEventHandler {

    /**
     * 收到设备抓拍对比数据事件处理函数
     *
     * @param data
     *            抓拍对比数据
     */
    void onCaptureCompareDataReceived(CaptureCompareData data);
}
