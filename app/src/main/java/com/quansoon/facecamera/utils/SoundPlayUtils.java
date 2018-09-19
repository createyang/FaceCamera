package com.quansoon.facecamera.utils;

import android.content.Context;
import android.media.SoundPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundPlayUtils {
    static Context mContext;

    public static SoundPlayUtils soundPlayUtils;
    private static SoundPool mSoundPlayer;

    private SoundPlayUtils() {
        mSoundPlayer = new SoundPool(10, 1, 5);
    }

    public static SoundPlayUtils init(Context paramContext, int paramInt) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
            mContext = paramContext;
            mSoundPlayer.load(mContext, paramInt, 1);
        }
        return soundPlayUtils;
    }

    public static void play(int paramInt) {
        if (mSoundPlayer != null) {
            mSoundPlayer.stop(1);
            mSoundPlayer.play(paramInt, 1.0F, 1.0F, 0, 0, 1.0F);
        }
    }

    public static void release() {
        if (mSoundPlayer != null) {
            mSoundPlayer.release();
            mSoundPlayer = null;
        }
        mContext = null;
    }

}