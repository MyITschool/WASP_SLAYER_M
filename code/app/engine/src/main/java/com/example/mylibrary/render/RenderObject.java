package com.example.mylibrary.render;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform2fv;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

import android.opengl.Matrix;

import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector2;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;

import java.util.HashMap;

public class RenderObject {

    private final Model model;

    public Vector4 color = new Vector4(1);
    public int texture = 0;
    public int normalTexture = 0;

    protected Vector3 position = new Vector3(0);
    protected Vector3 rotate = new Vector3(0);
    protected Vector3 size = new Vector3(1);

    protected float[] modelMatrix = new float[16];

    protected final Renderer renderer;

    public boolean activity = true;

    public Vector2 specular = new Vector2(1, 32);

    public RenderObject(Model model){
        this.model = model;
        this.renderer = model.core.getRenderer();

        genModelMat();
    }
    public RenderObject(Model model, int texture){
        this.model = model;
        this.renderer = model.core.getRenderer();
        this.texture=texture;

        genModelMat();
    }
    public RenderObject(Model model, int texture, int normalTexture){
        this.model = model;
        this.renderer = model.core.getRenderer();
        this.texture=texture;
        this.normalTexture = normalTexture;

        genModelMat();
    }

    public Model getModel(){return model;}

    public void setPosition(Vector3 position){
        this.position = position;
        genModelMat();
    }
    public void setRotate(Vector3 rotate){
        this.rotate = rotate;
        genModelMat();
    }
    public void setSize(Vector3 size){
        this.size = size;
        genModelMat();
    }
    public Vector3 getPosition(){return position;}
    public Vector3 getRotate(){return rotate;}
    public Vector3 getSize(){return size;};

    protected void genModelMat(){
        float[] modelMatrix = new float[16];
        float[] rotateMatrix = new float[16];

        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.translateM(modelMatrix, 0, modelMatrix,0, position.x,position.y,position.z);
        ////////////////////////////////////////////////////////////////////////////////
        float[] rotateMatrixX = new float[16];
        Matrix.setRotateM(rotateMatrixX, 0, rotate.x, 1, 0, 0);

        float[] rotateMatrixY = new float[16];
        Matrix.setRotateM(rotateMatrixY, 0, rotate.y, 0, 1, 0);

        float[] rotateMatrixZ = new float[16];
        Matrix.setRotateM(rotateMatrixZ, 0, rotate.z, 0, 0, 1);

        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotateMatrixX, 0);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotateMatrixY, 0);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotateMatrixZ, 0);

        Matrix.scaleM(modelMatrix, 0, modelMatrix,0,size.x,size.y,size.z);

        /////////////////////////////////////////////////////////////////////////////////
        Matrix.setIdentityM(rotateMatrix, 0);
        Matrix.translateM(rotateMatrix, 0, rotateMatrix,0, 0,0,0);

        Matrix.multiplyMM(rotateMatrix, 0, rotateMatrix, 0, rotateMatrixX, 0);
        Matrix.multiplyMM(rotateMatrix, 0, rotateMatrix, 0, rotateMatrixY, 0);
        Matrix.multiplyMM(rotateMatrix, 0, rotateMatrix, 0, rotateMatrixZ, 0);

        Matrix.scaleM(rotateMatrix, 0, rotateMatrix,0,1,1,1);

        this.modelMatrix = modelMatrix;
    }

    public void setUniforms(){
        HashMap<String, Integer> uniforms = model.shaderProgram.getUniforms();

        glUniformMatrix4fv(uniforms.get("uModelMatrix"), 1, false, modelMatrix, 0);
        glUniform4fv(uniforms.get("color"), 1, color.getArray(), 0);

        if(model.shaderProgram.name == "color_normals" || model.shaderProgram.name == "texture_normals" || model.shaderProgram.name == "texture_normalMap"){
            glUniform2fv(uniforms.get("specular"), 1, specular.getArray(), 0);
        }
        if(model.shaderProgram.name == "texture" || model.shaderProgram.name == "texture_normals" || model.shaderProgram.name == "texture_normalMap"){
            glUniform1i(uniforms.get("uTexture"), texture);
        }
        if(model.shaderProgram.name == "texture_normalMap"){
            glUniform1i(uniforms.get("uNormalTexture"), normalTexture);
        }
    }

    protected boolean gp(Vector4 vm, float[] m){
        float[] vpos = vm.getArray();

        Matrix.multiplyMV(vpos, 0, m, 0, vpos, 0);

        Vector4 p = new Vector4(vpos);

        return (-p.w <= p.x && p.x <= p.w) &&
                (-p.w <= p.y && p.y <= p.w) &&
                (0 <= p.z && p.z <= p.w);
    }

    public boolean inCamera(){
        Vector3 cam_p = Vector.mul(renderer.camera.getPosition(), -1);
        if (Vector.sub(position, cam_p).length() > renderer.camera.getFar() || !activity){
            return false;
        }

        float[] cm = renderer.camera.getvPMatrix();
        float[] m = new float[16];
        Matrix.multiplyMM(m, 0, cm, 0, modelMatrix, 0);


        Vector3 min = model.minPoint;
        Vector3 max = model.maxPoint;

        Vector4 v2 = new Vector4(max.x, min.y, min.z, 1);
        Vector4 v3 = new Vector4(min.x, max.y, min.z, 1);
        Vector4 v4 = new Vector4(max.x, max.y, min.z, 1);

        Vector4 v5 = new Vector4(min.x, min.y, max.z, 1);
        Vector4 v6 = new Vector4(max.x, min.y, max.z, 1);
        Vector4 v7 = new Vector4(min.x, max.y, max.z, 1);

        float s = Math.max(Math.abs(size.z), Math.max(Math.abs(size.x), Math.abs(size.y)));

        Vector3 ml = new Vector3(
                Math.max(max.x, Math.abs(min.x)),
                Math.max(max.y, Math.abs(min.y)),
                Math.max(max.z, Math.abs(min.z))
        );
        float l = ml.length()*s*2;
        return gp(new Vector4(max.x, max.y, max.z, 1), m) || gp(new Vector4(min.x, min.y, min.z, 1), m) || gp(v2, m) || gp(v3, m) ||
                gp(v4, m) || gp(v5, m) || gp(v6, m) || gp(v7, m)
                || Vector.sub(position, cam_p).length() <= l;
    }

    public void draw(){
        if (!inCamera())return;
        setUniforms();

        glDrawArrays(GL_TRIANGLES, 0, model.vertexes.length/3);
    }
}
