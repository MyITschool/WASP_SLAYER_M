package com.example.mylibrary.audio;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;

import java.io.IOException;
import java.util.HashMap;

public final class AudioLoader {

    private final AssetManager mAssetManager;
    // загруженные аудио
    private final HashMap<String, Audio> audios = new HashMap<>();

    public AudioLoader(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mAssetManager = activity.getAssets();
    }
    // загрузить аудио
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
    // получить аудио
    public Audio getAudio(String key){
        return audios.get(key);
    }
    // удалить аудио
    public void deleteAudio(String key){
        audios.remove(key);
    }
    // удалить все аудио
    public void clear(){
        audios.clear();
    }

}
