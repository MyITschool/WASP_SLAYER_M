package com.example.mylibrary.math;

public final class Vector4 extends Vector3 implements Cloneable{
    //      w
    public float w;
    //                  x,    y,      z,    w
    public Vector4(float x,float y,float z, float w){
        super(x,y,z);
        this.w = w;
    }
    //              xyzw
    public Vector4(float i){
        super(i);
        this.w = i;
    }
    //              {x,y,z,w}
    public Vector4(float[] arr){
        super(arr);
        this.w = arr[3];
    }

    // установить x y z w
    public void setXYZW(float x, float y, float z, float w){
        setXYZ(x,y,z);
        this.w = w;
    }
    // длина вектора
    public float length(){
        return (float) Math.sqrt(x*x+y*y+z*z+w*w);
    }

    // нормализация
    public void norm(){
        float l = length();
        x/=l;
        y/=l;
        z/=l;
        w/=l;
    }

    // получить массив {x,y,z,w}
    public float[] getArray(){
        return new float[]{x,y,z,w};
    }

    @Override
    public String toString() {
        return "Vector4"+"("+x+" "+y+" "+z+" "+w+")";
    }

    @Override
    public Vector4 clone() {
        return (Vector4)super.clone();
    }
}
