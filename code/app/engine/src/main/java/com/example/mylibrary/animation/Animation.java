package com.example.mylibrary.animation;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.GameObject;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.render.Renderer;

public final class Animation implements Updated {

    // будет ли зациклена анимация
    public boolean loop = false;
    // ключи анимации
    private Key[] keys;
    // анимируемый объект
    public GameObject gameObject;
    // renderer
    private final Renderer renderer;
    // статус проигрывания анимации
    private boolean play = false;
    // индекс активного ключа
    private int activeKey = 0;
    // пройденое время
    private float playTime = 0;
    // активный ключ изменения (хранится информация изменения за 1 мл.с.)
    private Key key;

    //              ключи,          анимируемый объект, ядро
    public Animation(Key[] keys, GameObject gameObject, Core core){
        this.keys = keys;
        this.gameObject= gameObject;
        this.renderer= core.getRenderer();
    }
    //              ключи,          анимируемый объект, renderer
    public Animation(Key[] keys, GameObject gameObject, Renderer renderer){
        this.keys = keys;
        this.gameObject= gameObject;
        this.renderer= renderer;
    }
    // устонавливает новый массив ключей
    public void setKeys(Key[] keys){
        this.keys = keys;
        setKey(keys[0]);
    }
    // возращает массив ключей
    public Key[] getKeys(){return keys;}
    // перезапуск
    public void restart(){
        stop();
        play();
    }
    // начать проигрывание
    public void play(){
        setKey(keys[activeKey]);
        play=true;
        renderer.addUpdated(this);
    }
    // пауза
    public void pause(){
        renderer.deleteUpdated(this);
        play=false;
        renderer.deleteUpdated(this);
    }
    // остановка
    public void stop(){
        renderer.deleteUpdated(this);
        activeKey=0;
        play=false;
        renderer.deleteUpdated(this);
    }


    @Override
    public void update(float dt) {
        if(play){
            Key ak = keys[activeKey];
            playTime+=dt;

            if(playTime>=ak.time){
                setFrame(dt - (playTime - ak.time));
                //////////////////////////////////////////
                activeKey++;
                if(activeKey==keys.length){
                    if (loop)
                        activeKey=0;
                    else
                        stop();
                }
                playTime-=ak.time;

                setKey(keys[activeKey]);

                setFrame(playTime);
            }else {
                setFrame(dt);
            }
        }
    }

    // установка кадра анимации
    private void setFrame(float time){
        gameObject.setPosition( Vector.add(gameObject.getPosition(), Vector.mul(key.deltaPosition, time)) );
        gameObject.setRotation( Vector.add(gameObject.getRotation(), Vector.mul(key.deltaRotation, time)) );
        gameObject.setScale( Vector.add(gameObject.getScale(), Vector.mul(key.deltaScale, time)) );
        gameObject.color = Vector.add(gameObject.color, Vector.mul(key.deltaColor, time));
    }

    // установка активного ключа изменения
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
