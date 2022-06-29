package com.example.mylibrary.core;

public abstract class Scene {
    // ядро
    protected final Core core;

    public Scene(Core core){
        this.core=core;
    }

    // старт
    public abstract void start();
    // возобновить
    public abstract void resume();
    // пауза
    public abstract void pause();

}
