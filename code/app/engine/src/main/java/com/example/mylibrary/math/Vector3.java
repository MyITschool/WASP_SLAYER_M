package com.example.mylibrary.math;

public class Vector3 extends Vector2 implements Cloneable{
    // z
    public float z;

    //                  x,    y,      z
    public Vector3(float x,float y,float z){
        super(x, y);
        this.z=z;
    }
    //              xyz
    public Vector3(float i){
        super(i);
        this.z=i;
    }
    //              {x,y,z}
    public Vector3(float[] arr){
        super(arr);
        z=arr[2];
    }
    // установить xyz
    public void setXYZ(float x, float y, float z){
        setXY(x,y);
        this.z = z;
    }
    // длина вектора
    public float length(){
        return (float) Math.sqrt(x*x+y*y+z*z);
    }
    // нормализация
    public void norm(){
        float l = length();
        x/=l;
        y/=l;
        z/=l;
    }
    // получить массив {x,y,z}
    public float[] getArray(){
        return new float[]{x,y,z};
    }

    @Override
    public String toString() {
        return "Vector3"+"("+x+" "+y+" "+z+")";
    }

    @Override
    public Vector3 clone() {
        return (Vector3)super.clone();
    }
}
