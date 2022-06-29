package com.example.mylibrary.render;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;

import com.example.mylibrary.core.Core;

import java.util.HashMap;

public final class ShaderProgram {

    public final int shaderProgram;

    private final HashMap<String, Integer> attributs = new HashMap<>();
    private final HashMap<String, Integer> uniforms = new HashMap<>();

    public final String name;

    public ShaderProgram(String name, String vs_src, String fs_src, String[] attributs, String[] uniforms, Core core){
        this.name = name;

        String vShaderStr = GLUtil.readShader(core, vs_src);
        String fShaderStr = GLUtil.readShader(core, fs_src);
        shaderProgram = GLUtil.loadProgram(vShaderStr, fShaderStr);

        genAttributsUniforms(attributs, uniforms);
    }

    private void genAttributsUniforms(String[] attributs, String[] uniforms){

        for (String attribut : attributs) {
            this.attributs.put(attribut, glGetAttribLocation(shaderProgram, attribut));
        }
        for (String uniform : uniforms) {
            this.uniforms.put(uniform, glGetUniformLocation(shaderProgram, uniform));
        }

    }

    public HashMap<String, Integer> getAttributs(){
        return attributs;
    }
    public HashMap<String, Integer> getUniforms(){
        return uniforms;
    }

}
