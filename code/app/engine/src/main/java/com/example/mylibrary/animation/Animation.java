package com.example.mylibrary.animation;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.GameObject;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.render.Renderer;

public final class Animation implements Updated {

    private Key[] keys;
    private GameObject gameObject;
    private final Renderer renderer;

    private boolean play = false;

    private int activeKey = 0;
    private float playTime = 0;
    private Key key;

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
        activeKey=0;
        setKey(keys[activeKey]);
        play=true;
        renderer.addUpdated(this);
    }
    public void play(){
        setKey(keys[activeKey]);

        play=true;
        renderer.addUpdated(this);
    }
    public void pause(){
        renderer.deleteUpdated(this);
        play=false;
    }
    public void stop(){
        renderer.deleteUpdated(this);
        activeKey=0;
        play=false;
    }

    @Override
    public void update(float dt) {
        if(play){
            Key ak = keys[activeKey];
            playTime+=dt;

            if(playTime>=ak.time){
                // playTime - ak.time
                // dt - (playTime - ak.time)
                setFrame(dt - (playTime - ak.time));
                //////////////////////////////////////////
                activeKey++;
                if(activeKey==keys.length)activeKey=0;
                playTime-=ak.time;

                setKey(keys[activeKey]);

                setFrame(playTime);
            }else {
                setFrame(dt);
            }
        }
    }

    private void setFrame(float time){
        gameObject.setPosition( Vector.add(gameObject.getPosition(), Vector.mul(key.deltaPosition, time)) );
        gameObject.setRotation( Vector.add(gameObject.getRotation(), Vector.mul(key.deltaRotation, time)) );
        gameObject.setScale( Vector.add(gameObject.getScale(), Vector.mul(key.deltaScale, time)) );
        gameObject.color = Vector.add(gameObject.color, Vector.mul(key.deltaColor, time));
    }

    private void setKey(Key ak){
        key = new Key(
                Vector.div(ak.deltaPosition, ak.time),
                Vector.div(ak.deltaRotation, ak.time),
                Vector.div(ak.deltaScale, ak.time),
                Vector.div(ak.deltaColor, ak.time),
                0.001f
        );
    }
}
