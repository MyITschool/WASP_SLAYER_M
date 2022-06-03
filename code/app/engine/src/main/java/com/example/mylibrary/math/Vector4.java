package com.example.mylibrary.math;

public final class Vector4 extends Vector3 implements Cloneable{
    public float w;

    public Vector4(float x,float y,float z, float w){
        super(x,y,z);
        this.w = w;
    }
    public Vector4(float i){
        super(i);
        this.w = i;
    }
    public Vector4(float[] arr){
        super(arr);
        this.w = arr[3];
    }

    public void setXYZW(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float length(){
        return (float) Math.sqrt(x*x+y*y+z*z+w*w);
    }

    public void norm(){
        float l = length();
        x/=l;
        y/=l;
        z/=l;
        w/=l;
    }

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
