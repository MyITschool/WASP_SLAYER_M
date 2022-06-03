package com.example.mylibrary.audio;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;


import com.example.mylibrary.math.Vector2;

import java.io.IOException;

public final class Audio {

    private final MediaPlayer mMediaPlayer;
    private boolean mIsPrepared;
    private Vector2 volume = new Vector2(0);
    private final AssetFileDescriptor assetFileDescriptor;

    public Audio(AssetFileDescriptor assetFileDescriptor) {
        this.assetFileDescriptor = assetFileDescriptor;
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
    public void setVolume(Vector2 volume){
        synchronized (this){
            this.volume=volume;
            mMediaPlayer.setVolume(volume.x, volume.y);
        }
    }
    public Vector2 getVolume(){
        return volume;
    }
    public void stop() {
        mMediaPlayer.stop();
        synchronized (this) {
            mIsPrepared = false;
        }
    }
    public AssetFileDescriptor getAssetFileDescriptor(){
        return assetFileDescriptor;
    }

    public void dispose() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

}
