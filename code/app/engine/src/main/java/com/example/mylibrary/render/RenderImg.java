package com.example.mylibrary.render;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector2;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.model.Model;

import java.util.HashMap;

public class RenderImg extends RenderObject{

    protected final Core core;
    public RenderImg(String textureKey, Core core){
        super(core.getRenderer().UIModel, textureKey);
        this.core=core;
    }

    public void setUniforms(){
        HashMap<String, Integer> uniforms = model.shaderProgram.getUniforms();

        glUniformMatrix4fv(uniforms.get("uModelMatrix"), 1, false, modelMatrix, 0);
        glUniform4fv(uniforms.get("color"), 1, color.getArray(), 0);
        glUniform1i(uniforms.get("uTexture"), texture);
    }
    public void draw(){
        setUniforms();

        glDrawArrays(GL_TRIANGLES, 0, model.getNumberPolygons());
    }
}
