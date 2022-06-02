package com.example.engine_demo;

import com.example.engine_demo.Scenes.Menu;
import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;

public class MainActivity extends Core {

    public Scene getScene(){
        return new Menu(this);
    }

}