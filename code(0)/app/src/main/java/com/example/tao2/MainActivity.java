package com.example.tao2;

import android.os.Bundle;

import com.example.engine.core.Core;
import com.example.engine.core.Scene;
import com.example.tao2.scenes.Menu;

public class MainActivity extends Core {

    public final Settings settings;

    public MainActivity(){
        settings = new Settings(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings.read();
    }

    protected Scene getStartScene(){
        return new Menu(this, true);
    }

}