package com.example.mylibrary.physics.physics3D;

import com.example.mylibrary.math.Vector3;

public final class Ray {
    // начало
    public float[] start;
    // напровление
    public float[] direction;

    public Ray(Vector3 start, Vector3 direction){
        this.start=start.getArray();
        this.direction=direction.getArray();
    }
}
