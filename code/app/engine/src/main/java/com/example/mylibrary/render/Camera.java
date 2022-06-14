package com.example.mylibrary.render;

import android.opengl.Matrix;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.GameObject;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector2Int;
import com.example.mylibrary.math.Vector3;

public class Camera extends GameObject {

    protected float[] projectionMatrix = new float[16];
    protected float[] vPMatrix = new float[16];

    protected Vector2Int res;

    protected float FOV = 60;
    protected float far = 100;

    public float getFar(){return far;}

    public void setFar(float far){
        this.far = far;
        rotate();
    }


    public void setResolution(Vector2Int res){
        this.res = res;
        rotate();
    }
    public void setFOV(float fov){
        this.FOV = fov;
        rotate();
    }
    public void setPosition(Vector3 position){
        this.position= Vector.mul(position, -1);
        core.getRenderer().sortLigth();
        rotate();
    }
    public Vector3 getPosition(){
        return Vector.mul(position, -1);
    }
    public void setRotate(Vector3 rotation){
        this.rotation=rotation;
        rotate();
    }
    public boolean rotateModeView = true;
    protected void rotate(){

        float[] vPMatrix = new float[16];
        float[] projectionMatrix = new float[16];
        float ratio = (float) res.x / res.y;
        Matrix.perspectiveM(vPMatrix,0, FOV, ratio, 0.1f, far);
        Matrix.perspectiveM(projectionMatrix,0, FOV, ratio, 0.1f, far);

        if(rotateModeView){


            //Matrix.translateM(vPMatrix,0,vPMatrix,0,position.x,position.y,position.z);

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

    protected void genVPMatrix(){
        float ratio = (float) res.x / res.y;
        Matrix.perspectiveM(vPMatrix,0, FOV, ratio, 0.1f, far);
    }
    protected void genProjectionMatrix(){
        float ratio = (float) res.x / res.y;
        Matrix.perspectiveM(projectionMatrix,0, FOV, ratio, 0.1f, far);
    }


    public float[] getProjectionMatrix(){
        return projectionMatrix;
    }
    public float[] getvPMatrix(){
        return vPMatrix;
    }

    public Vector2Int getResolution(){
        return res;
    }

    protected final Core core;
    public Camera(Core core){
        this.core = core;

        this.res = new Vector2Int(1);

        genVPMatrix();
        genProjectionMatrix();
    }

}
