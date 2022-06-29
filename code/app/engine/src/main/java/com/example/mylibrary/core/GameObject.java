package com.example.mylibrary.core;

import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;

public class GameObject {
    // цвет
    public Vector4 color = new Vector4(1);
    // позиция
    protected Vector3 position = new Vector3(0);
    // повоот
    protected Vector3 rotation = new Vector3(0);
    // масштаб
    protected Vector3 scale = new Vector3(1);
    // статус отрисовки
    public boolean activity = true;

    // установить позицию
    public void setPosition(Vector3 position){
        this.position = position;
    }
    // установить поворот
    public void setRotation(Vector3 rotation){
        this.rotation = rotation;
    }
    // установить масштаб
    public void setScale(Vector3 scale){ this.scale = scale; }

    // получить позицию
    public Vector3 getPosition(){ return position; }
    // получить поворот
    public Vector3 getRotation(){ return rotation; }
    // получить масштаб
    public Vector3 getScale(){ return scale; }
}
