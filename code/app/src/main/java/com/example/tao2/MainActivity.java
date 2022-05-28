package com.example.tao2;

import com.example.engine.core.Core;
import com.example.engine.core.Scene;
import com.example.tao2.scenes.Menu;

public class MainActivity extends Core {

    public final Settings settings;

    {
        settings = new Settings();
    }

    protected Scene getStartScene(){
        return new Menu(this, true);
    }

}