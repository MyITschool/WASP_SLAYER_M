package com.example.engine.audio;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;

import java.io.IOException;
import java.util.LinkedList;

public class AudioLoader {

    private final AssetManager mAssetManager;

    public AudioLoader(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mAssetManager = activity.getAssets();
    }

    private LinkedList<Audio> audios = new LinkedList<>();

    public Audio addAudio(String fileName) {
        try {
            AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(fileName);
            Audio a = new Audio(assetFileDescriptor);
            audios.add(a);
            return a;
        } catch (IOException e) {
            throw new RuntimeException("Не возможно загрузить музыку");
        }
    }

    public Audio[] addAudios(String[] fileNames) {
        try {
            Audio[] as = new Audio[fileNames.length];
            for (int i = 0; i < fileNames.length; i++){
                AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(fileNames[i]);
                Audio a = new Audio(assetFileDescriptor);
                audios.add(a);
                as[i] = a;
            }
            return as;
        } catch (IOException e) {
            throw new RuntimeException("Не возможно загрузить музыку");
        }
    }

    public Audio getAudio(int i){
        return audios.get(i);
    }

    public void deleteAudio(Audio audio){
        audios.remove(audio);
    }

    public void clear(){
        audios = new LinkedList<>();
    }

}
