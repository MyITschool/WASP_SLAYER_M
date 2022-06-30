package com.example.mylibrary.render;

import static android.opengl.GLES20.glUniform4fv;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.GameObject;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;

public final class Light extends GameObject {
    // направление
    public Vector3 direction;
    // сила
    public float strength;
    // угл
    public float theta;

    private final Renderer renderer;
    // точичный свет
    public Light(Vector3 position, Vector3 color, float strength, Renderer renderer){
        this.position = position;
        this.color = new Vector4(color.x,color.y,color.z,1);
        this.renderer = renderer;
        this.strength = strength;
    }
    // направленный
    public Light(Vector3 position, Vector3 color, Vector3 direction, float strength, float theta, Renderer renderer){
        this.position = position;
        this.color = new Vector4(color.x,color.y,color.z,1);
        this.direction = direction;
        this.renderer = renderer;

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

    // установить позицию
    public void setPosition(Vector3 position){
        this.position = position;
        renderer.sortLigth();
    }

    @Override
    public String toString() {
        return "Light: position: " + position + " color: " + color + " direction: " + direction + " strength: " + strength + " theta: " + theta + " activity: " + activity;
    }
}
