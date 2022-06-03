package com.example.mylibrary.model;

import com.example.mylibrary.core.Core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class UIModel extends Model{

    public int getNumberPolygons(){return vertexes.length/2;}

    public UIModel(Core core){
        super(core.getRenderer().getShaderProgram("UI"), core);

        vertexes = new float[]{
                -1,-1,
                1,-1,
                1,1,

                -1,-1,
                1,1,
                -1,1
        };

        vertexes_texture = new float[]{
                0,1,
                1,1,
                1,0,

                0,1,
                1,0,
                0,0
        };

        genBuffer();
    }

    protected void genBuffer() {
        int BYTES_PER_FLOAT = 4;

        FloatBuffer mVertices = ByteBuffer.allocateDirect(vertexes.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(vertexes).position(0);

        buffers.put("vPosition", new BufferData(mVertices, 2));

        FloatBuffer mTexture = ByteBuffer.allocateDirect(vertexes_texture.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexture.put(vertexes_texture).position(0);

        buffers.put("vTexture", new BufferData(mTexture, 2));
    }

    public void genTextureBuffer(float[] vertexes_texture){
        this.vertexes_texture=vertexes_texture;
        int BYTES_PER_FLOAT = 4;

        FloatBuffer mTexture = ByteBuffer.allocateDirect(vertexes_texture.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexture.put(vertexes_texture).position(0);

        buffers.get("vTexture").floatBuffer = mTexture;
    }

    public void putShaderVariables(){
        setBuffers();
    }
}
