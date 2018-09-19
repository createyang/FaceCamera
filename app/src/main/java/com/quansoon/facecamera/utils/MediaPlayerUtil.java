package com.quansoon.facecamera.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import java.io.IOException;

/**
 * @author: Caoy
 * @created on: 2018/9/18 10:28
 * @description:
 */
public class MediaPlayerUtil implements MediaPlayer.OnPreparedListener {
    private static
    MediaPlayerUtil instance;
    private MediaPlayer mediaPlayer;
    private Context context;

    private MediaPlayerUtil(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
    }

    public static MediaPlayerUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (MediaPlayerUtil.class) {
                if (instance == null) {
                    instance = new MediaPlayerUtil(context);
                }
            }
        }
        return instance;
    }


    public void playMusic(AssetFileDescriptor assetFileDescriptor, MediaPlayer.OnPreparedListener listener) {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.prepareAsync();
            mediaPlayer.prepare();
//            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0.6f, 0.4f);
            mediaPlayer.setOnPreparedListener(listener);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        instance = null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(context, "onPrepared 完成", Toast.LENGTH_SHORT).show();
        mp.start();
    }

}
