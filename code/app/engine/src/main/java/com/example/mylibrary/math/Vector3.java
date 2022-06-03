package com.example.mylibrary.math;

public class Vector3 extends Vector2 implements Cloneable{
    public float z;

    public Vector3(float x,float y,float z){
        super(x, y);
        this.z=z;
    }
    public Vector3(float i){
        super(i);
        this.z=i;
    }
    public Vector3(float[] arr){
        super(arr);
        z=arr[2];
    }

    public void setXYZ(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float length(){
        return (float) Math.sqrt(x*x+y*y+z*z);
    }

    public void norm(){
        float l = length();
        x/=l;
        y/=l;
        z/=l;
    }

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
