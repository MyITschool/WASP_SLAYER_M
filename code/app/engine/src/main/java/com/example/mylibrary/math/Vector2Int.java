package com.example.mylibrary.math;

public final class Vector2Int implements Cloneable{
    public int x;
    public int y;
    public Vector2Int(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Vector2Int(int i){
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
    public float dot(Vector2Int a, Vector2Int b){
        return a.x*b.x+a.y*b.y;
    }
    @Override
    public String toString() {
        return "Vector2int"+"("+x+" "+y+")";
    }

    @Override
    public Vector2Int clone() {
        try {
            return (Vector2Int)super.clone();
        }catch( CloneNotSupportedException ex ) {
            throw new InternalError();
        }
    }
}