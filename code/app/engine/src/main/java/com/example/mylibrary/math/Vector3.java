package com.example.mylibrary.math;

public final class Vector3 implements Cloneable{
    public float x;
    public float y;
    public float z;

    public Vector3(float x,float y,float z){
        setXYZ(x, y, z);
    }
    public Vector3(float i){
        setXYZ(i,i,i);
    }
    public Vector3(Vector2 vec2, float z){
        setXYZ(vec2.x, vec2.y, z);
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
        try {
            return (Vector3)super.clone();
        }catch( CloneNotSupportedException ex ) {
            throw new InternalError();
        }
    }
}
