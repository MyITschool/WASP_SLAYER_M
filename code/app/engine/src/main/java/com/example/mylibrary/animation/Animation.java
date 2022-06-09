package com.example.mylibrary.animation;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.GameObject;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.render.Renderer;

public final class Animation implements Updated {

    private Key[] keys;
    private GameObject gameObject;
    private final Renderer renderer;

    private boolean play = false;

    private int activeKey = 0;
    private float playTime = 0;

    public Animation(Key[] keys, GameObject gameObject, Core core){
        this.keys = keys;
        this.gameObject= gameObject;
        this.renderer= core.getRenderer();
    }
    public Animation(Key[] keys, GameObject gameObject, Renderer renderer){
        this.keys = keys;
        this.gameObject= gameObject;
        this.renderer= renderer;
    }

    public void restart(){
        play=true;
    }
    public void play(){
        play=true;
    }
    public void pause(){
        play=false;
    }
    public void stop(){
        play=false;
    }

    @Override
    public void update(float dt) {

    }
}
