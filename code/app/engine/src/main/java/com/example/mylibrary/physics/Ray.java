package com.example.mylibrary.physics;

import com.example.mylibrary.math.Vector3;

public final class Ray {
    public float[] start = new float[3];
    public float[] direction = new float[3];

    public Ray(Vector3 start, Vector3 direction){
        this.start=start.getArray();
        this.direction=direction.getArray();
    }
}
