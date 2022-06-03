package com.example.mylibrary.physics;

public final class Hit {
    public float distance;
    public CubeCollider collider;
    public int index;

    public Hit(float distance, CubeCollider collider, int index){
        this.distance=distance;
        this.collider=collider;
        this.index=index;
    }
    public Hit(){}

    @Override
    public String toString() {
        return "Hit"+"distance: "+distance+", collider: {"+collider.toString()+"}, index: "+index;
    }
}
