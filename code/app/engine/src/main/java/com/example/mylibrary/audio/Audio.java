package com.example.mylibrary.audio;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;


import com.example.mylibrary.core.Core;
import com.example.mylibrary.math.Vector2;

import java.io.IOException;

public final class Audio {

    // плеер
    private final MediaPlayer mMediaPlayer;
    // статус проигрывания
    private boolean mIsPrepared;
    // громкость
    private Vector2 volume = new Vector2(0);

    public Audio(AssetFileDescriptor assetFileDescriptor) {
        mIsPrepared = false;
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mIsPrepared = true;
    }

    // начать проигрывать
    public void play(boolean looping, Vector2 volume) {
        if (mMediaPlayer.isPlaying()) {
            return;
        }
        synchronized (this) {
            if (!mIsPrepared) {
                try {
                    mMediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.volume=volume;
            mMediaPlayer.setLooping(looping);
            mMediaPlayer.setVolume(volume.x, volume.y);
            mMediaPlayer.start();
        }
    }
    // остановить проигрывание
    public void stop() {
        mMediaPlayer.stop();
        synchronized (this) {
            mIsPrepared = false;
        }
    }
    // установить громкость
    public void setVolume(Vector2 volume){
        this.volume=volume;
        mMediaPlayer.setVolume(volume.x, volume.y);
    }
    // получить громкость
    public Vector2 getVolume(){
        return volume;
    }

    // уничтожить
    public void dispose() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

}
