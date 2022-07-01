package com.example.mylibrary.render;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.model.UIModel;

import java.util.HashMap;

public class RenderImg extends RenderObject{

    public RenderImg(String textureKey, UIModel uiModel){
        super(uiModel, textureKey);
    }
    // установка юниформ
    public void setUniforms(){
        HashMap<String, Integer> uniforms = model.shaderProgram.getUniforms();

        glUniformMatrix4fv(uniforms.get("uModelMatrix"), 1, false, modelMatrix, 0);
        glUniform4fv(uniforms.get("color"), 1, color.getArray(), 0);
        glUniform1i(uniforms.get("uTexture"), texture);
    }
    // отрисовка
    public void draw(){
        if(!activity)return;
        setUniforms();

        glDrawArrays(GL_TRIANGLES, 0, model.getNumberPolygons());
    }
}
