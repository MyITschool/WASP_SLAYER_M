package com.example.mylibrary.math;

public final class Func {
    private Func(){}
    public static int randomInt(int min, int max){
        return min + (int) (Math.random() * max);
    }
    public static float betwenAngle(Vector2 a, Vector2 b){
        return (float) Math.atan2(b.y-a.y, b.x-a.x);
    }
    public static Vector2 canvasToGlCoord(Vector2 coord, Vector2Int res){
        return new Vector2(coord.x/((float)res.x)*2-1, ((float)res.y-coord.y)/((float)res.y)*2-1);
    }
}
