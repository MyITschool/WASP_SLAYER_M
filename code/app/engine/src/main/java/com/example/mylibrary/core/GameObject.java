package com.example.mylibrary.core;

import com.example.mylibrary.math.Vector3;

public class GameObject {
    protected Vector3 position = new Vector3(0);
    protected Vector3 rotation = new Vector3(0);
    protected Vector3 scale = new Vector3(1);

    public boolean activity = true;

    public void setPosition(Vector3 position){
        this.position = position;
    }
    public void setRotation(Vector3 rotation){
        this.rotation = rotation;
    }
    public void setScale(Vector3 scale){ this.scale = scale; }
    public Vector3 getPosition(){ return position; }
    public Vector3 getRotate(){ return rotation; }
    public Vector3 getScale(){ return scale; }
}
