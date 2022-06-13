package com.example.mylibrary.physics.physics3D;


import com.example.mylibrary.math.Vector3;

public final class Brick {
    public float[] min_point;
    public float[] max_point;

    public Brick(Vector3 min_point, Vector3 max_point){
        this.min_point=min_point.getArray();
        this.max_point=max_point.getArray();
    }
}
