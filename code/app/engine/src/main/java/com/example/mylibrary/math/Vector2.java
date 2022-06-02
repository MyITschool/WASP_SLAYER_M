package com.example.mylibrary.math;


public class Vector2 implements Cloneable{
    public float x;
    public float y;
    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }
    public Vector2(float i){
        this.x = i;
        this.y = i;
    }

    public float length(){
        return (float) Math.sqrt(x*x+y*y);
    }

    public void norm(){
        float l = length();
        x/=l;
        y/=l;
    }

    public float[] getArray(){
        return new float[]{x,y};
    }

    public void setXY(float x, float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector2"+"("+x+" "+y+")";
    }

    @Override
    public Vector2 clone() {
        try {
            return (Vector2)super.clone();
        }catch( CloneNotSupportedException ex ) {
            throw new InternalError();
        }
    }
}
