package com.example.mylibrary.physics.physics3D;

public final class Hit {
    // растояние
    public float distance;
    // коллайдер
    public CubeCollider collider;

    public Hit(float distance, CubeCollider collider){
        this.distance=distance;
        this.collider=collider;
    }
    public Hit(){}

    @Override
    public String toString() {
        return "Hit"+"distance: "+distance+", collider: {"+collider.toString()+"}";
    }
}
