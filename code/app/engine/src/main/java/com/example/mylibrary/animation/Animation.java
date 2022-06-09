package com.example.mylibrary.animation;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.GameObject;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.render.Renderer;

public final class Animation implements Updated {

    private Key[] keys;
    private GameObject gameObject;
    private final Renderer renderer;

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

    public void play(){

    }
    public void pause(){

    }
    public void stop(){

    }

    @Override
    public void update(float dt) {

    }
}
