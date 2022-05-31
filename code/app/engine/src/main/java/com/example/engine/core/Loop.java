package com.example.engine.core;

import android.opengl.GLSurfaceView;

import java.util.LinkedList;


public class Loop extends GLSurfaceView implements Runnable{

    private boolean running = false;

    private Thread gameThread;
    private final Core core;

    int upd = 0;

    private int UPDATE_TIME = 1000 / 60;

    public Loop(Core core){
        super(core);
        this.core = core;

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);

        setRenderer(this.core.getRenderer());
       // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setFPS(int fps){
        UPDATE_TIME = 1000/fps;
    }
    public int getFPS(){
        return UPDATE_TIME * 1000;
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
            //long t = System.currentTimeMillis();

            updateGame();
            //requestRender();
//            t = System.currentTimeMillis() - t;
//            if(t>=UPDATE_TIME){
//                t=0;
//            }else {
//                t = UPDATE_TIME-t;
//            }
            try {
                gameThread.sleep(UPDATE_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void updateGame(){
        upd++;
       // core.getScene().update();
        for (int i = 0; i < updateds.size(); i++){
            updateds.get(i).update();
        }
    }

    private LinkedList<Updated> updateds = new LinkedList<>();

    public void addUpdateObj(Updated updated){
        updateds.add(updated);
    }
    public void deleteUpdateObj(Updated updated){
        updateds.remove(updated);
    }
    public void clear(){
        updateds.clear();
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
