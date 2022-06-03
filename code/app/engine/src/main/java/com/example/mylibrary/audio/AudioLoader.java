package com.example.mylibrary.audio;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public final class AudioLoader {

    private final AssetManager mAssetManager;

    public AudioLoader(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mAssetManager = activity.getAssets();
    }

    private final HashMap<String, Audio> audios = new HashMap<>();

    public Audio addAudio(String src, String key) {
        try {
            AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(src);
            Audio a = new Audio(assetFileDescriptor);
            audios.put(key, a);
            return a;
        } catch (IOException e) {
            throw new RuntimeException("Не возможно загрузить музыку");
        }
    }

    public Audio getAudio(String key){
        return audios.get(key);
    }

    public void deleteAudio(String key){
        audios.remove(key);
    }

    public void clear(){
        audios.clear();
    }

}
