package com.example.engine.core;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;

import java.util.LinkedList;

@SuppressLint("ViewConstructor")
public class Loop extends GLSurfaceView implements Runnable{

    private boolean running = false;

    private Thread gameThread;
    private final Core core;

    float upd = 0;

    private int UPDATE_TIME = 1000 / 60;

    public Loop(Core core){
        super(core);
        this.core = core;

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);

        setRenderer(this.core.getRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setFPS(int fps){
        UPDATE_TIME = 1000/fps;
    }

    @Override
    public void run() {
        long timer=System.currentTimeMillis();

        while (running){

            if(System.currentTimeMillis()-timer>1000){
                System.out.println("Loop: "+upd+"FPS");
                upd=0;
                timer+=System.currentTimeMillis()-timer;
            }
            long t = System.currentTimeMillis();
            updateGame();
            requestRender();

            t = System.currentTimeMillis() - t;
            if(t>=UPDATE_TIME){
                t=0;
            }else {
                t = UPDATE_TIME-t;
            }
            try {
                Thread.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void updateGame(){
        upd++;
        core.getScene().update();
        for (int i = 0; i < updateds.size(); i++){
            updateds.get(i).update();
        }
    }

    private LinkedList<Updated> updateds = new LinkedList<>();

    public Updated addUpdateObj(Updated updated){
        updateds.add(updated);
        return updateds.get(updateds.size()-1);
    }
    public void deleteUpdateObj(Updated updated){
        updateds.remove(updated);
    }
    public void clear(){
        updateds = new LinkedList<>();
    }

    public void startGame(){
        if(running){
            return;
        }
        running=true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGame(){
        if(!running){
            return;
        }
        running=false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
