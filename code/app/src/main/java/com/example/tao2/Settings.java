package com.example.tao2;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Settings {
    private float musicVolume = 0.5f;
    private float soundsVolume = 1.0f;

    private final String settingsFileName = "game_settings.txt";

    public float getMusicVolume(){return musicVolume;}
    public float getSoundsVolume(){return soundsVolume;}
    public void setMusicVolume(float musicVolume){
        this.musicVolume=musicVolume;
        save();
    }
    public void setSoundsVolume(float soundsVolume){
        this.soundsVolume=soundsVolume;
        save();
    }

    public void save(){
        String s = ""+musicVolume+'\n'+soundsVolume;
        try {
            FileOutputStream fos = mainActivity.openFileOutput(settingsFileName, Context.MODE_PRIVATE);
            fos.write(s.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read(){
        try {
            FileInputStream fis = mainActivity.openFileInput(settingsFileName);
            InputStreamReader reader = new InputStreamReader(fis);
            BufferedReader buffere = new BufferedReader(reader);
            String l;
            int ln = 0;
            while ((l = buffere.readLine()) != null){
                if (ln==0){
                    musicVolume = Float.parseFloat(l);
                    ln++;
                }else {
                    soundsVolume = Float.parseFloat(l);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final MainActivity mainActivity;
    public Settings(MainActivity mainActivity){
        this.mainActivity=mainActivity;
    }

    @Override
    public String toString() {
        return "Settings: "+"musicVolume: "+musicVolume+" soundsVolume: "+soundsVolume;
    }
}
