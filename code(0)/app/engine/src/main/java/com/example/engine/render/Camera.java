package com.example.engine.render;

import android.opengl.Matrix;

import com.example.engine.math.Vector2Int;
import com.example.engine.math.Vector3;


public class Camera {

    private float[] projectionMatrix = new float[16];
    private float[] vPMatrix = new float[16];

    private Vector3 cameraPos, cameraRot;

    private Vector2Int res;

    private float FOV = 60;
    private float far = 100;

    public float getFar(){return far;}

    public void setFar(float far){
        this.far = far;
        cameraUpd=true;
        //genVPMatrix();
        rotate();
    }

    public boolean rotateModeView = true;

    private boolean cameraUpd = false;


    public void setResolution(Vector2Int res){
        this.res = res;
        cameraUpd=true;
        rotate();
    }
    public void setFOV(float fov){
        this.FOV = fov;
        cameraUpd=true;
        rotate();
    }
    public void setPosition(Vector3 cameraPos){
        this.cameraPos=cameraPos;
        cameraUpd=true;
        rotate();
    }
    public void setRotate(Vector3 cameraRot){
        this.cameraRot=cameraRot;
        cameraUpd=true;
        rotate();
    }

    private void rotate(){
        cameraUpd=false;

//        genVPMatrix();
//        genProjectionMatrix();
        float[] vPMatrix = new float[16];
        float[] projectionMatrix = new float[16];
        float ratio = (float) res.x / res.y;
        Matrix.perspectiveM(vPMatrix,0, FOV, ratio, 0.1f, far);
        Matrix.perspectiveM(projectionMatrix,0, FOV, ratio, 0.1f, far);

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

        this.vPMatrix = vPMatrix;
        this.projectionMatrix = projectionMatrix;
    }

    private void genVPMatrix(){
        float ratio = (float) res.x / res.y;
        Matrix.perspectiveM(vPMatrix,0, FOV, ratio, 0.1f, far);
    }
    private void genProjectionMatrix(){
        float ratio = (float) res.x / res.y;
        Matrix.perspectiveM(projectionMatrix,0, FOV, ratio, 0.1f, far);
    }


    public float[] getProjectionMatrix(){
        return projectionMatrix;
    }
    public float[] getvPMatrix(){
//        if(cameraUpd)
//            rotate();
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
