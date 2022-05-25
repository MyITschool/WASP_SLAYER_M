package com.example.engine.math;

public class Vector4 implements Cloneable{
    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4(float x,float y,float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public Vector4(float i){
        this.x = i;
        this.y = i;
        this.z = i;
        this.w = i;
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
        try {
            return (Vector4)super.clone();
        }catch( CloneNotSupportedException ex ) {
            throw new InternalError();
        }
    }
}
