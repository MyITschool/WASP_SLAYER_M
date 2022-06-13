package com.example.engine_demo;

import com.example.engine_demo.Scenes.Loading;
import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;

public final class MainActivity extends Core {

    public Scene getScene(){
        return new Loading(this);
    }

}