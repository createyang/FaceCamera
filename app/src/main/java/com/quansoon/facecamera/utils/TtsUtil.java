package com.quansoon.facecamera.utils;

import android.app.Service;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.Engine.DEFAULT_STREAM;

/**
 * @author: Caoy
 * @created on: 2018/9/17 16:13
 * @description:
 */
public class TtsUtil implements TextToSpeech.OnInitListener {
    private static HashMap<String, String> params;
    private static TextToSpeech mTextToSpeech;
    private Context mContext;
    private static TtsUtil instance;


    public static TtsUtil init(Context context) {
        if (instance == null) {
            synchronized (TtsUtil.class) {
                if (instance == null) {
                    instance = new TtsUtil(context);
                }
            }
        }
        return instance;
    }

    private TtsUtil(Context context) {
        this.mContext = context;
        //获取上下文
        //实例化队列
        mTextToSpeech = new TextToSpeech(this.mContext, this);
        //实例化TTS
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        mTextToSpeech.setPitch(1f);
        //设定语速 ，默认1.0正常语速
        mTextToSpeech.setSpeechRate(1f);
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //设置识别语音为英文或者中文
            if (mTextToSpeech != null) {
                int result = mTextToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    ToastUtils.shortShowStr(mContext, "数据丢失或不支持");
                }
            }
        }
    }


    /**
     * 停止
     */
    public void stop() {
        if (mTextToSpeech != null && mTextToSpeech.isSpeaking()) {
            mTextToSpeech.stop();
        }
    }

    /**
     * //释放资源
     */
    public void release() {
        synchronized (this) {
            if (mTextToSpeech != null) {
                mTextToSpeech.stop();
                mTextToSpeech.shutdown();
            }
        }
        instance = null;
    }

    /**
     * 更新消息队列，或者读语音
     *
     * @param mes
     */
    public void notifyNewMessage(String mes) {
        synchronized (TtsUtil.class) {
            speakText(mes);
        }
    }

    /**
     * 读语音处理
     *
     * @param message
     */
    private void speakText(String message) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(DEFAULT_STREAM));
        params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, String.valueOf(1));
        //设置播放类型（音频流类型）
        //朗读，注意这里三个参数的added in API level 4   四个参数的added in API level 21
        if (mTextToSpeech != null) {
            mTextToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, params);
        }
    }
}
