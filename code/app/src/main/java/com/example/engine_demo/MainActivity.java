package com.example.engine_demo;

import com.example.engine_demo.Scenes.Tests;
import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;

public final class MainActivity extends Core {

    public Scene getScene(){
        return new Tests(this);
    }

}