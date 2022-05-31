package com.example.engine.render;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

import android.opengl.Matrix;

import com.example.engine.math.Vector2;
import com.example.engine.math.Vector2Int;
import com.example.engine.math.Vector3;
import com.example.engine.math.Vector4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class RenderImg {

    private final int BYTES_PER_FLOAT = 4;
    private final int BYTES_PER_SHORT = 2;

    private FloatBuffer mVertices, mTextureCoords;
    private ShortBuffer mIndices;

    private final float[] mVerticesData = new float[]{
//            -1, -1, 0,
//            -1, 1, 0,
//            1, 1, 0,
//            1, -1, 0

            -1, -1, 0,
            1, -1, 0,
            1, 1, 0,
            -1, 1, 0
    };
    protected float[] mTextureCoordsData = new float[]{
//            0, 0,
//            0, 1,
//            1, 1,
//            1, 0
            0,1,
            1,1,
            1,0,
            0,0
    };
//    private float[] objectPos = new float[]{0,0,0};
    private Vector3 objectPos = new Vector3(0);
    //private float[] objectPixelPos = new float[]{0,0};
//    private float[] objectRot=new float[]{0,0,0};
    private Vector3 objectRot=new Vector3(0);
//    private float[] objectSize=new float[]{1,1,1};
    private Vector2 objectSize=new Vector2(1);
    protected Vector4 mColor=new Vector4(1);
//    private float[] mColor=new float[]{1,1,1,1};
//    private int[] res = new int[]{0,0};
    private Vector2Int res = new Vector2Int(0);
    protected final short[] mIndicesData = new short[]{
            0, 1, 2,    0, 2, 3
    };

    protected float[] modelMatrix = new float[16];

    protected float[] material = new float[]{-1};

    protected boolean objUpd = false;

    protected final int[] shader_vars;
    private final int[] mVBOIds;
    protected final int[] uUIMaterial;
    
    public RenderImg(int[] shader_vars, int[] mVBOIds, int[] uUIMaterial) {
        this.shader_vars = shader_vars;
        this.mVBOIds = mVBOIds;
        this.uUIMaterial = uUIMaterial;

        genBuffers();
        genModelMatrix();
    }

    private void genBuffers(){
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        //////////////////////////////////////////////////////////////////////////////
        mIndices = ByteBuffer.allocateDirect(mIndicesData.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(mIndicesData).position(0);
        /////////////////////////////////////////////////////////////////////////////////
        if(material[0]>-1){
            mTextureCoords = ByteBuffer.allocateDirect(mTextureCoordsData.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTextureCoords.put(mTextureCoordsData).position(0);
        }
    }
    protected void setBuffers(){
        mVertices.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mVerticesData.length,
                mVertices, GL_STATIC_DRAW);

        mIndices.position(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[1]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, BYTES_PER_SHORT * mIndicesData.length, mIndices, GL_STATIC_DRAW);

        if(material[0]>-1){
            mTextureCoords.position(0);
            glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[3]);
            glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mTextureCoordsData.length,
                    mTextureCoords, GL_STATIC_DRAW);
        }
    }
    protected void putBuffers(){
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glEnableVertexAttribArray(shader_vars[29]);
        int POSITION_COMPONENT_SIZE = 3;
        glVertexAttribPointer(shader_vars[29], POSITION_COMPONENT_SIZE,
                GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[1]);

        if(material[0]>-1){
            glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[3]);
            glEnableVertexAttribArray(shader_vars[32]);
            int TEXTURE_COMPONENT_SIZE = 2;
            glVertexAttribPointer(shader_vars[32], TEXTURE_COMPONENT_SIZE,
                    GL_FLOAT, false, 0, 0);
        }
    }

    public void setTextureCoords(float[] mTextureCoordsData){
        this.mTextureCoordsData=mTextureCoordsData;
        genBuffers();
    }
    public void setMaterial(float[] material){
        this.material=material;
        genBuffers();
    }
    public void setTexture(int id){
        material[0]=id;
        genBuffers();
    }
    public void setScale(Vector2 size){
        objectSize=size;
        objUpd=true;
        genModelMatrix();
    }
    public void setRotate(Vector3 rotate){
        objectRot=rotate;
        objUpd=true;
        genModelMatrix();
    }
    public void setPosition(Vector3 position){
        //int[] res = renderer.getResolution();
        //System.out.println(Arrays.toString(res));
//        position[0]=position[0]/res[0]*2-1;
//        position[1]=position[1]/res[0]*2-1;
        objectPos=position;
        objUpd=true;
        genModelMatrix();
    }
    public void setColor(Vector4 mColor){
        this.mColor=mColor;
    }


    protected void genModelMatrix(){
        float[] modelMatrix = new float[16];

        Matrix.setIdentityM(modelMatrix, 0);

        //////////////////////////////////////////////////////////////////////////////

        Matrix.translateM(modelMatrix, 0, modelMatrix,0, objectPos.x,objectPos.y,objectPos.z);
        ////////////////////////////////////////////////////////////////////////////////
        float[] rotateMatrixX = new float[16];
        Matrix.setRotateM(rotateMatrixX, 0, objectRot.x, 1, 0, 0);

        float[] rotateMatrixY = new float[16];
        Matrix.setRotateM(rotateMatrixY, 0, objectRot.y, 0, 1, 0);

        float[] rotateMatrixZ = new float[16];
        Matrix.setRotateM(rotateMatrixZ, 0, objectRot.z, 0, 0, 1);

        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotateMatrixX, 0);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotateMatrixY, 0);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotateMatrixZ, 0);

        ///////////////////////////////////////////////////////////////////////////////

        Matrix.scaleM(modelMatrix, 0, modelMatrix,0,objectSize.x,objectSize.y, 1);

        this.modelMatrix=modelMatrix;
    }


    public Vector2 getScale(){
        return objectSize;
    }
    public Vector3 getRotate(){
        return objectRot;
    }
    public Vector3 getPosition(){
        return objectPos;
    }
    public Vector4 getColor(){
        return mColor;
    }

    public boolean actively = true;

    public void draw() {
        if(!actively)
            return;
        setBuffers();
        putBuffers();

//        if (objUpd){
//            objUpd=false;
//            genModelMatrix();
//        }
        if (material[0]>-1){
            glUniform1i(shader_vars[31], (int) material[0]);
        }

        for (int i = 0; i < uUIMaterial.length; i++){
            glUniform1f(uUIMaterial[i], material[i]);
        }

        glUniform4fv(shader_vars[33], 1, new float[]{mColor.x, mColor.y, mColor.z, mColor.w}, 0);

        glUniformMatrix4fv(shader_vars[30], 1, false, modelMatrix, 0);

        glDrawElements(GL_TRIANGLES, mIndicesData.length, GL_UNSIGNED_SHORT, 0);

        glDisableVertexAttribArray(shader_vars[29]);
        glDisableVertexAttribArray(shader_vars[32]);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
