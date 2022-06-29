package com.example.mylibrary.animation;

import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;

public final class Key {
    public final Vector3 deltaPosition;
    public final Vector3 deltaRotation;
    public final Vector3 deltaScale;
    public final Vector4 deltaColor;
    public final float time;

    //         изменение позиции,       изменение поворота,  изменение масштаба, изменение цвета,    время в с
    public Key(Vector3 deltaPosition, Vector3 deltaRotation, Vector3 deltaScale, Vector4 deltaColor, float time){
        this.deltaPosition = deltaPosition;
        this.deltaRotation = deltaRotation;
        this.deltaScale = deltaScale;
        this.deltaColor = deltaColor;
        this.time = time;
    }
}
