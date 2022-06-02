package com.example.engine.math;

import com.example.engine.core.Core;

public class Func {
    private final Core core;
    public Func(Core core){
        this.core = core;
    }
    public int randomInt(int min, int max){
        return min + (int) (Math.random() * max);
    }
    public float betwenAngle(Vector2 a, Vector2 b){
        return (float) Math.atan2(b.y-a.y, b.x-a.x);
    }
    public Vector2 canvasToGlCoord(Vector2 coord){
        Vector2Int res = core.getRenderer().getResolution();
        return new Vector2(coord.x/((float)res.x)*2-1, ((float)res.y-coord.y)/((float)res.y)*2-1);
    }
}
