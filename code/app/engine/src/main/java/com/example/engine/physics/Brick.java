package com.example.engine.physics;

import com.example.engine.math.Vector3;

public class Brick {
    public float[] min_point = new float[3];
    public float[] max_point = new float[3];

    public Brick(Vector3 min_point, Vector3 max_point){
        this.min_point=min_point.getArray();
        this.max_point=max_point.getArray();
    }
}
