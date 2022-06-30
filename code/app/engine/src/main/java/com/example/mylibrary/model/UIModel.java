package com.example.mylibrary.model;

import com.example.mylibrary.core.Core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class UIModel extends Model{
    // количество полигонов
    public int getNumberPolygons(){return vertexesData.vertexes.length/2;}

    public UIModel(Core core){
        super(core.getRenderer().getShaderProgram("UI"), core);

        vertexesData.vertexes = new float[]{
                -1,-1,
                1,-1,
                1,1,

                -1,-1,
                1,1,
                -1,1
        };

        vertexesData.vertexes_texture = new float[]{
                0,1,
                1,1,
                1,0,

                0,1,
                1,0,
                0,0
        };

        genBuffer();
    }

    // создание буферов
    protected void genBuffer() {
        final int BYTES_PER_FLOAT = 4;
        final int COORDS_PER_VERTEX = 2;

        FloatBuffer mVertices = ByteBuffer.allocateDirect(vertexesData.vertexes.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(vertexesData.vertexes).position(0);

        buffers.put("vPosition", new BufferData(mVertices, COORDS_PER_VERTEX));

        FloatBuffer mTexture = ByteBuffer.allocateDirect(vertexesData.vertexes_texture.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexture.put(vertexesData.vertexes_texture).position(0);

        buffers.put("vTexture", new BufferData(mTexture, COORDS_PER_VERTEX));
    }
    // установить новые координаты текстуры для вершин
    public void setVertexesTexture(float[] vertexes_texture){
        vertexesData.vertexes_texture=vertexes_texture;

        FloatBuffer mTexture = ByteBuffer.allocateDirect(vertexes_texture.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexture.put(vertexes_texture).position(0);

        buffers.get("vTexture").floatBuffer = mTexture;
    }
    // установка буферов
    public void putShaderVariables(){
        setBuffers();
    }
}
