package com.example.mylibrary.render;

import static android.opengl.GLES20.glUniform4fv;

import com.example.mylibrary.math.Vector3;

public final class Light {
    public Vector3 position;
    public Vector3 direction = null;
    public Vector3 color;

    public float strength;
    public float theta;

    public boolean activity = true;

    public Light(Vector3 position, Vector3 color, float strength){
        this.position = position;
        this.color = color;

        this.strength = strength;
    }
    public Light(Vector3 position, Vector3 color, Vector3 direction, float strength, float theta){
        this.position = position;
        this.color = color;
        this.direction = direction;

        this.theta = theta;
        this.strength = strength;
    }

    public int draw(int[] adr){
        int t = 0;
        if(direction != null) t = 1;
        glUniform4fv(adr[0], 1, new float[]{position.x,position.y,position.z,t}, 0);
        if(t==1){
            glUniform4fv(adr[1], 1, new float[]{direction.x,direction.y,direction.z,theta}, 0);
        }else {
            glUniform4fv(adr[1], 1, new float[]{color.x,color.y,color.z,strength}, 0);
            return 2;
        }
        glUniform4fv(adr[2], 1, new float[]{color.x,color.y,color.z,strength}, 0);
        return 3;
    }

    @Override
    public String toString() {
        return "Light: position: " + position + " color: " + color + " direction: " + direction + " strength: " + strength + " theta: " + theta + " activity: " + activity;
    }
}
