package com.example.mylibrary.math;

public class Vector {

    public static Vector4 div(Vector4 a, Vector4 b){
        Vector4 c = new Vector4(0);
        if (a.x==0||b.x==0) {
            c.x = 0;
        }else {
            c.x = a.x / b.x;
        }
        if (a.y==0||b.y==0) {
            c.y = 0;
        }else {
            c.y = a.y / b.y;
        }
        if (a.z==0||b.z==0) {
            c.z = 0;
        }else {
            c.z = a.z / b.z;
        }
        if (a.w==0||b.w==0) {
            c.w = 0;
        }else {
            c.w = a.w / b.w;
        }
        return c;
    }
    public static Vector4 div(float a, Vector4 b){
        return div(new Vector4(a), b);
    }
    public static Vector4 div(Vector4 a, float b){
        return div(a, new Vector4(b));
    }

    public static Vector4 mul(Vector4 a, Vector4 b){
        return new Vector4(a.x*b.x,a.y*b.y,a.z*b.z,a.w*b.w);
    }
    public static Vector4 mul(Vector4 a, float b){
        return mul(new Vector4(b), a);
    }

    public static Vector4 abs(Vector4 a){
        return new Vector4(Math.abs(a.x),Math.abs(a.y),Math.abs(a.z),Math.abs(a.w));
    }

    public static Vector4 sub(Vector4 a, Vector4 b){
        return new Vector4(a.x-b.x,a.y-b.y,a.z-b.z,a.w-b.w);
    }
    public static Vector4 sub(float a, Vector4 b){
        return sub(new Vector4(a), b);
    }
    public static Vector4 sub(Vector4 a, float b){
        return sub(a, new Vector4(b));
    }

    public static Vector4 add(Vector4 a, Vector4 b){
        return new Vector4(a.x+b.x,a.y+b.y,a.z+b.z,a.w+b.w);
    }
    public static Vector4 add(Vector4 a, float b){
        return add(a, new Vector4(b));
    }

    public static float dot(Vector4 a, Vector4 b){
        return a.x*b.x+a.y*b.y+a.z*b.z+a.w*b.w;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static Vector3 div(Vector3 a, Vector3 b){
        Vector3 c = new Vector3(0);
        if (a.x==0||b.x==0) {
            c.x = 0;
        }else {
            c.x = a.x / b.x;
        }
        if (a.y==0||b.y==0) {
            c.y = 0;
        }else {
            c.y = a.y / b.y;
        }
        if (a.z==0||b.z==0) {
            c.z = 0;
        }else {
            c.z = a.z / b.z;
        }
        return c;
    }
    public static Vector3 div(float a, Vector3 b){
        return div(new Vector3(a), b);
    }
    public static Vector3 div(Vector3 a, float b){
        return div(a, new Vector3(b));
    }

    public static Vector3 mul(Vector3 a, Vector3 b){
        return new Vector3(a.x*b.x,a.y*b.y,a.z*b.z);
    }
    public static Vector3 mul(Vector3 a, float b){
        return mul(new Vector3(b), a);
    }

    public static Vector3 abs(Vector3 a){
        return new Vector3(Math.abs(a.x),Math.abs(a.y),Math.abs(a.z));
    }

    public static Vector3 sub(Vector3 a, Vector3 b){
        return new Vector3(a.x-b.x,a.y-b.y,a.z-b.z);
    }
    public static Vector3 sub(float a, Vector3 b){
        return sub(new Vector3(a), b);
    }
    public static Vector3 sub(Vector3 a, float b){
        return sub(a, new Vector3(b));
    }

    public static Vector3 add(Vector3 a, Vector3 b){
        return new Vector3(a.x+b.x,a.y+b.y,a.z+b.z);
    }
    public static Vector3 add(Vector3 a, float b){
        return add(a, new Vector3(b));
    }

    public static float dot(Vector3 a, Vector3 b){
        return a.x*b.x+a.y*b.y+a.z*b.z;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Vector2 div(Vector2 a, Vector2 b){
        Vector2 c = new Vector2(0);
        if (a.x==0||b.x==0) {
            c.x = 0;
        }else {
            c.x = a.x / b.x;
        }
        if (a.y==0||b.y==0) {
            c.y = 0;
        }else {
            c.y = a.y / b.y;
        }
        return c;
    }
    public static Vector2 div(float a, Vector2 b){
        return div(new Vector2(a), b);
    }
    public static Vector2 div(Vector2 a, float b){
        return div(a, new Vector2(b));
    }

    public static Vector2 mul(Vector2 a, Vector2 b){
        return new Vector2(a.x*b.x,a.y*b.y);
    }
    public static Vector2 mul(Vector2 a, float b){
        return mul(new Vector2(b), a);
    }

    public static Vector2 abs(Vector2 a){
        return new Vector2(Math.abs(a.x),Math.abs(a.y));
    }

    public static Vector2 sub(Vector2 a, Vector2 b){
        return new Vector2(a.x-b.x,a.y-b.y);
    }
    public static Vector2 sub(float a, Vector2 b){
        return sub(new Vector2(a), b);
    }
    public static Vector2 sub(Vector2 a, float b){
        return sub(a, new Vector2(b));
    }

    public static Vector2 add(Vector2 a, Vector2 b){
        return new Vector2(a.x+b.x,a.y+b.y);
    }
    public static Vector2 add(Vector2 a, float b){
        return add(a, new Vector2(b));
    }

    public static float dot(Vector2 a, Vector2 b){
        return a.x*b.x+a.y*b.y;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
