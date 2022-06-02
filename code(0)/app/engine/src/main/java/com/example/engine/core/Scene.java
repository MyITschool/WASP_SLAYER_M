package com.example.engine.core;

public abstract class Scene {

    public Core core;

    public Scene(Core core){
        this.core=core;
    }

    public abstract void preload();
    public abstract void start();
    public abstract void resume();
    public abstract void pause();
    public abstract void dispose();
}
