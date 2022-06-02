package com.example.mylibrary.core;

public abstract class Scene {

    protected final Core core;

    public Scene(Core core){
        this.core=core;
    }

    public abstract void preload();
    public abstract void start();
    public abstract void resume();
    public abstract void pause();

}
