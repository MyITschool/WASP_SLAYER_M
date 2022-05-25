package com.example.engine.render;

import android.opengl.Matrix;

import com.example.engine.math.Vector2Int;
import com.example.engine.math.Vector3;


public class Camera {

    private final float[] projectionMatrix = new float[16];
    private final float[] vPMatrix = new float[16];

    private Vector3 cameraPos, cameraRot;

    private Vector2Int res;

    private float FOV = 60;

    public boolean rotateModeView = true;

    private boolean cameraUpd = false;


    public void setResolution(Vector2Int res){
        this.res = res;

        cameraUpd=true;

        genProjectionMatrix();
        genVPMatrix();
    }
    public void setFOV(float fov){
        this.FOV = fov;
        cameraUpd=true;
    }
    public void setPosition(Vector3 cameraPos){
        this.cameraPos=cameraPos;
        cameraUpd=true;
    }
    public void setRotate(Vector3 cameraRot){
        this.cameraRot=cameraRot;
        cameraUpd=true;
    }

    private void rotate(){
        cameraUpd=false;

        genVPMatrix();
        genProjectionMatrix();
        if(rotateModeView){


            Matrix.translateM(vPMatrix,0,vPMatrix,0,cameraPos.x,cameraPos.y,cameraPos.z);

            float[] matRotX = new float[16];
            Matrix.setRotateM(matRotX, 0, cameraRot.x, 1,0,0);
            float[] matRotY = new float[16];
            Matrix.setRotateM(matRotY, 0, cameraRot.y, 0,1,0);
            float[] matRotZ = new float[16];
            Matrix.setRotateM(matRotZ, 0, cameraRot.z, 0,0,1);

            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotX,0);
            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotY,0);
            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotZ,0);

            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotX,0);
            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotY,0);
            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotZ,0);
        }else {

            float[] matRotX = new float[16];
            Matrix.setRotateM(matRotX, 0, cameraRot.x, 1,0,0);
            float[] matRotY = new float[16];
            Matrix.setRotateM(matRotY, 0, cameraRot.y, 0,1,0);
            float[] matRotZ = new float[16];
            Matrix.setRotateM(matRotZ, 0, cameraRot.z, 0,0,1);

            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotX,0);
            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotY,0);
            Matrix.multiplyMM(vPMatrix,0,vPMatrix,0,matRotZ,0);

            Matrix.translateM(vPMatrix,0,vPMatrix,0,cameraPos.x,cameraPos.y,cameraPos.z);

            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotX,0);
            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotY,0);
            Matrix.multiplyMM(projectionMatrix,0,projectionMatrix,0,matRotZ,0);
        }
    }

    private void genVPMatrix(){
        float ratio = (float) res.x / res.y;

        Matrix.perspectiveM(vPMatrix,0, FOV, ratio, 0.1f, 100);
    }
    private void genProjectionMatrix(){
        float ratio = (float) res.x / res.y;
        //Matrix.frustumM(projectionMatrix,0,-ratio,ratio,-1,1,1,100);
        Matrix.perspectiveM(projectionMatrix,0, FOV, ratio, 0.1f, 100);
    }


    public float[] getProjectionMatrix(){
        return projectionMatrix;
    }
    public float[] getvPMatrix(){
        rotate();
        return vPMatrix;
    }
    public Vector3 getPosition(){
        return cameraPos;
    }
    public Vector3 getRotate(){
        return cameraRot;
    }

    public Vector2Int getResolution(){
        return res;
    }

    public Camera(Vector2Int res){
        cameraPos = new Vector3(0);
        cameraRot = new Vector3(0);

        this.res = res;

        genVPMatrix();

        genProjectionMatrix();
    }

}
