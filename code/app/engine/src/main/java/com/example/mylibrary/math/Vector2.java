package com.example.mylibrary.math;


public class Vector2 implements Cloneable{
    // x
    public float x;
    // y
    public float y;

    //                  x,  y
    public Vector2(float x, float y){
        setXY(x,y);
    }
    //              xy
    public Vector2(float i){
        setXY(i,i);
    }
    //             {x, y}
    public Vector2(float[] arr){
        setXY(arr[0],arr[1]);
    }
    //      длина вектора
    public float length(){
        return (float) Math.sqrt(x*x+y*y);
    }

    // нормализация
    public void norm(){
        float l = length();
        x/=l;
        y/=l;
    }
    // получить массив {x,y}
    public float[] getArray(){
        return new float[]{x,y};
    }
    // устоновить х у
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
