package com.example.mylibrary.animation;

import com.example.mylibrary.math.Vector3;

public final class Key {
    public final Vector3 deltaPosition;
    public final Vector3 deltaRotation;
    public final Vector3 deltaScale;
    public final float time;

    public Key(Vector3 deltaPosition, Vector3 deltaRotation, Vector3 deltaScale, float time){
        this.deltaPosition = deltaPosition;
        this.deltaRotation = deltaRotation;
        this.deltaScale = deltaScale;
        this.time = time;
    }
}
