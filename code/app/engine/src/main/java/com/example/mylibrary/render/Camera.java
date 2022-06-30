package com.example.mylibrary.render;

import android.opengl.Matrix;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.GameObject;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector2;
import com.example.mylibrary.math.Vector3;

public class Camera extends GameObject {

    protected float[] projectionMatrix = new float[16];
    protected float[] vPMatrix = new float[16];

    // разрешение
    protected Vector2 res;

    // угл обзора
    protected float FOV = 60;
    // дальность видимости
    protected float far = 100;
    // получить дальность видимости
    public float getFar(){return far;}
    // установить дальность видимости
    public void setFar(float far){
        this.far = far;
        rotate();
    }
    // установить разрешение
    public void setResolution(Vector2 res){
        this.res = res;
        rotate();
    }
    // установить угл обзора
    public void setFOV(float fov){
        this.FOV = fov;
        rotate();
    }
    // установить позицию
    public void setPosition(Vector3 position){
        this.position= Vector.mul(position, -1);
        core.getRenderer().sortLigth();
        rotate();
    }
    // получить позицию
    public Vector3 getPosition(){
        return Vector.mul(position, -1);
    }
    // установить поворот
    public void setRotate(Vector3 rotation){
        this.rotation=rotation;
        rotate();
    }
    // тип вращения
    public boolean rotateModeView = true;
    // поворот
    protected void rotate(){

        float[] vPMatrix = new float[16];
        float[] projectionMatrix = new float[16];
        float ratio = (float) res.x / res.y;
        Matrix.perspectiveM(vPMatrix,0, FOV, ratio, 0.1f, far);
        Matrix.perspectiveM(projectionMatrix,0, FOV, ratio, 0.1f, far);

        if(rotateModeView){


            Matrix.translateM(vPMatrix,0,vPMatrix,0,position.x,position.y,position.z);

            float[] matRotX = new float[16];
            Matrix.setRotateM(matRotX, 0, rotation.x, 1,0,0);
            float[] matRotY = new float[16];
            Matrix.setRotateM(matRotY, 0, rotation.y, 0,1,0);
            float[] matRotZ = new float[16];
            Matrix.setRotateM(matRotZ, 0, rotation.z, 0,0,1);

            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotX,0);
            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotY,0);
            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotZ,0);

            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotX,0);
            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotY,0);
            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotZ,0);
        }else {

            float[] matRotX = new float[16];
            Matrix.setRotateM(matRotX, 0, rotation.x, 1,0,0);
            float[] matRotY = new float[16];
            Matrix.setRotateM(matRotY, 0, rotation.y, 0,1,0);
            float[] matRotZ = new float[16];
            Matrix.setRotateM(matRotZ, 0, rotation.z, 0,0,1);

            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotX,0);
            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotY,0);
            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotZ,0);

            Matrix.translateM(vPMatrix,0,vPMatrix,0,position.x,position.y,position.z);

            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotX,0);
            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotY,0);
            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotZ,0);
        }

        this.vPMatrix = vPMatrix;
        this.projectionMatrix = projectionMatrix;
    }
    // создание матрици вида
    protected void genVPMatrix(){
        float ratio = (float) res.x / res.y;
        Matrix.perspectiveM(vPMatrix,0, FOV, ratio, 0.1f, far);
    }
    // создание матрици проекции
    protected void genProjectionMatrix(){
        float ratio = (float) res.x / res.y;
        Matrix.perspectiveM(projectionMatrix,0, FOV, ratio, 0.1f, far);
    }

    // получить матрици
    public float[] getProjectionMatrix(){
        return projectionMatrix;
    }
    public float[] getvPMatrix(){
        return vPMatrix;
    }
    // получить разрешение
    public Vector2 getResolution(){
        return res;
    }

    protected final Core core;
    public Camera(Core core){
        this.core = core;

        this.res = new Vector2(1);

        genVPMatrix();
        genProjectionMatrix();
    }

}
