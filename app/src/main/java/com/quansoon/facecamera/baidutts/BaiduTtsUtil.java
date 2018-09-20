package com.quansoon.facecamera.baidutts;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.quansoon.facecamera.utils.LogUtils;
import com.quansoon.facecamera.utils.UiUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Caoy
 * @created on: 2018/9/20 13:45
 * @description:
 */
public class BaiduTtsUtil {

    // ================== 初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    protected String appId = "14242104";

    protected String appKey = "vGWPAdjckGvgqf6emn7NanNq";

    protected String secretKey = "c0s9Q1ZUXxzsjp9HvPUUveanei8NsOe3";

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode = TtsMode.MIX;

    protected String offlineVoice = OfflineResource.VOICE_MALE;

    private static BaiduTtsUtil
            instance;
    private Context context;
    private NonBlockSyntherizer synthesizer;

    private BaiduTtsUtil(Context context) {
        this.context = context;
        initialTts();
    }

    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    protected void initialTts() {
        // 日志打印在logcat中
        LoggerProxy.printable(true);
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new UiMessageListener(UiUtil.getHandler());
        Map<String, String> params = getParams();
        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
        // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
        // 上线时请删除AutoCheck的调用
//        AutoCheck.getInstance(context).check(initConfig, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == 100) {
//                    AutoCheck autoCheck = (AutoCheck) msg.obj;
//                    synchronized (autoCheck) {
//                        String message = autoCheck.obtainDebugMessage();
////                        toPrint(message); // 可以用下面一行替代，在logcat中查看代码
//                        LogUtils.w("AutoCheckMessage", message);
//                    }
//                }
//            }
//        });
        // 此处可以改为MySyntherizer 了解调用过程
        synthesizer = new NonBlockSyntherizer(context, initConfig, UiUtil.getHandler());
//        int result = synthesizer.speak("test");

    }

    /**
     * 合成并播放
     *
     * @param text 小于1024 GBK字节，即512个汉字或者字母数字
     * @return
     */
    public int speak(String text) {
        int code = synthesizer.speak(text);
        checkResult(code, "speak");
        return code;
    }

    public void stop() {
        synthesizer.stop();
    }

    public void pause() {
        synthesizer.pause();
    }

    public void release() {
        synthesizer.release();
        context = null;
        instance = null;
    }


    private void checkResult(int result, String method) {
        if (result != 0) {
            LogUtils.d("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;
    }

    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(context, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
            LogUtils.e("【error】:copy files from assets failed." + e.getMessage());
        }
        return offlineResource;
    }


    public static BaiduTtsUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (BaiduTtsUtil.class) {
                if (instance == null) {
                    instance = new BaiduTtsUtil(context);
                }
            }
        }
        return instance;
    }


}
