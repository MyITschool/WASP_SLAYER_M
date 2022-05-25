package com.example.engine.render;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

import android.opengl.Matrix;

import com.example.engine.math.Vector3;
import com.example.engine.model.Model;
import com.example.engine.physics.CubeCollider;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class RenderObject{
    private final int BYTES_PER_FLOAT = 4;
    private final int BYTES_PER_SHORT = 2;

    private final int POSITION_COMPONENT_SIZE = 3;
    private final int NORMAL_COMPONENT_SIZE = 3;
    private final int TEXTURE_COMPONENT_SIZE = 2;

    private FloatBuffer mVertices, mNormals, mTextureCoords, mNormalTextureCoords, mTangents;
    private ShortBuffer mIndices;

    private float[] mVerticesData, mNormalsData, mTextureCoordsData, mNormalTextureCoordsData, mTangentData;

    private Vector3 mColor, objectPos, objectRot,objectSize;

    public float[] getmVerticesData(){ return mVerticesData; }
    public float[] getmNormalsData(){ return mNormalsData; }
    public float[] getmTextureCoordsData(){ return mTextureCoordsData; }

    public CubeCollider[] colliders;

    private final float[] modelMatrix = new float[16];
    private final float[] rotateMatrix = new float[16];

    //использовать нормали, использовать текстуру(номер текстуры), использовать текстуру нормалей(номер текстуры), specularStrength, specularPow
    private float[] material = new float[]{0, -1, -1, 0.5f, 32, 1};

    public void setUsNormal(boolean i){if(i){material[0]=1;}else{material[0]=0;} setMaterial(material);}
    public void setUsTexture(float i){material[1]=i; setMaterial(material);}
    public void setUsTextureNormals(float i){material[2]=i; setMaterial(material);}
    public void setSpecularStrength(float i){material[3]=i; setMaterial(material);}
    public void setSpecularPow(float i){material[4]=i; setMaterial(material);}
    public void setActive(boolean active){if(active){material[5]=1;}else{material[5]=0;} setMaterial(material);}
    public boolean getActive(){if(material[5]==1){return true;}else {return false;}}

    private boolean objUpd = false;
    
    private final int[] shader_vars;
    public final int[] mVBOIds;
    private final int[] uMaterial;

    public RenderObject(float[] vertex, int[] shader_vars, int[] mVBOIds, int[] uMaterial) {
        this.shader_vars = shader_vars;
        this.mVBOIds = mVBOIds;
        this.uMaterial = uMaterial;
        
        mVerticesData=vertex;
        //mIndicesData=index;


        objectPos=new Vector3(0);
        objectRot=new Vector3(0);
        objectSize=new Vector3(1);

        mColor = new Vector3(1);

        genBuffer();

        genModelMatrix();
    }
    public RenderObject(Model model, int[] shader_vars, int[] mVBOIds, int[] uMaterial){
        this.shader_vars = shader_vars;
        this.mVBOIds = mVBOIds;
        this.uMaterial = uMaterial;

        mVerticesData = model.v;
        mNormalsData = model.vn;
        mTextureCoordsData = model.vt;
        mColor = model.color.clone();

        objectPos=model.pos.clone();
        objectRot=model.rot.clone();
        objectSize=model.scale.clone();

        if(model.texture != -1){
            setUsTexture(model.texture);
        }

            if(model.colliders != null && model.colliders.length != 0){
                colliders = new CubeCollider[model.colliders.length];
                for (int i = 0; i < colliders.length; i++){
                    colliders[i] = model.colliders[i].clone();
                }
            }



        if(model.material[0] != -9) setMaterial(model.material.clone());

        genBuffer();
        genModelMatrix();
    }
    public Model getModel(){
        Model model = new Model();
        model.v = mVerticesData;
        model.vn = mNormalsData;
        model.vt = mTextureCoordsData;

        //model.material =  material;
        model.texture = (int) material[1];
       // model.color = mColor;
        System.arraycopy(material, 0, model.material, 0, material.length);
        //System.arraycopy(mColor, 0, model.color, 0, 3);
        model.color = mColor.clone();

        model.scale = objectSize.clone();
        model.pos = objectPos.clone();
        model.rot = objectRot.clone();

        if(colliders != null && colliders.length > 0){
            model.colliders = new CubeCollider[colliders.length];
            for (int i = 0; i < colliders.length; i++){
                model.colliders[i] = colliders[i].clone();
            }
        }


        return model;
    }

    private void genBuffer(){
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        //////////////////////////////////////////////////////////////////////////////
//        mIndices = ByteBuffer.allocateDirect(mIndicesData.length * BYTES_PER_SHORT)
//                .order(ByteOrder.nativeOrder()).asShortBuffer();
//        mIndices.put(mIndicesData).position(0);
        /////////////////////////////////////////////////////////////////////////////////
        if(material[0]==1){
            mNormals = ByteBuffer.allocateDirect(mNormalsData.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mNormals.put(mNormalsData).position(0);
        }
        if(material[1]>-1){
            mTextureCoords = ByteBuffer.allocateDirect(mTextureCoordsData.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTextureCoords.put(mTextureCoordsData).position(0);
        }
        if (material[2]>-1){
            mNormalTextureCoords = ByteBuffer.allocateDirect(mNormalTextureCoordsData.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mNormalTextureCoords.put(mNormalTextureCoordsData).position(0);
            ////////////////////////////////////////////////////////////////////////////////////////////////////
            mTangents = ByteBuffer.allocateDirect(mTangentData.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTangents.put(mTangentData).position(0);
        }
    }

    private void setBuffers(){
        mVertices.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mVerticesData.length,
                mVertices, GL_STATIC_DRAW);

//        mIndices.position(0);
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[1]);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, BYTES_PER_SHORT * mIndicesData.length, mIndices, GL_STATIC_DRAW);

        if(material[0]==1){
            mNormals.position(0);
            glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[2]);
            glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mNormalsData.length,
                    mNormals, GL_STATIC_DRAW);
        }
        if(material[1]>-1){
            mTextureCoords.position(0);
            glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[3]);
            glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mTextureCoordsData.length,
                    mTextureCoords, GL_STATIC_DRAW);
        }
        if(material[2]>-1){
            mNormalTextureCoords.position(0);
            glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[4]);
            glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mNormalTextureCoordsData.length,
                    mNormalTextureCoords, GL_STATIC_DRAW);
            /////////////////////////////////////////////////////////////////////////////////////
            mTangents.position(0);
            glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[5]);
            glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mTangentData.length,
                    mTangents, GL_STATIC_DRAW);
        }
    }

    private void putBuffers(){
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glEnableVertexAttribArray(shader_vars[0]);
        glVertexAttribPointer(shader_vars[0], POSITION_COMPONENT_SIZE,
                GL_FLOAT, false, 0, 0);

        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[1]);

        if (material[0]==1){
            glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[2]);
            glEnableVertexAttribArray(shader_vars[1]);
            glVertexAttribPointer(shader_vars[1], NORMAL_COMPONENT_SIZE,
                    GL_FLOAT, false, 0, 0);
        }
        if(material[1]>-1){
            glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[3]);
            glEnableVertexAttribArray(shader_vars[2]);
            glVertexAttribPointer(shader_vars[2], TEXTURE_COMPONENT_SIZE,
                    GL_FLOAT, false, 0, 0);
        }
        if(material[2]>-1){
            glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[4]);
            glEnableVertexAttribArray(shader_vars[3]);
            glVertexAttribPointer(shader_vars[3], TEXTURE_COMPONENT_SIZE,
                    GL_FLOAT, false, 0, 0);
            ///////////////////////////////////////////////////////////////////////////
            glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[5]);
            glEnableVertexAttribArray(shader_vars[4]);
            glVertexAttribPointer(shader_vars[4], NORMAL_COMPONENT_SIZE,
                    GL_FLOAT, false, 0, 0);
        }
    }

    private void genModelMatrix(){
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

        Matrix.scaleM(modelMatrix, 0, modelMatrix,0,objectSize.x,objectSize.y,objectSize.z);

        /////////////////////////////////////////////////////////////////////////////////
        Matrix.setIdentityM(rotateMatrix, 0);
        Matrix.translateM(rotateMatrix, 0, rotateMatrix,0, 0,0,0);

        Matrix.multiplyMM(rotateMatrix, 0, rotateMatrix, 0, rotateMatrixX, 0);
        Matrix.multiplyMM(rotateMatrix, 0, rotateMatrix, 0, rotateMatrixY, 0);
        Matrix.multiplyMM(rotateMatrix, 0, rotateMatrix, 0, rotateMatrixZ, 0);

        Matrix.scaleM(rotateMatrix, 0, rotateMatrix,0,1,1,1);
        ///////////////////////////////////////////////////////////////////////////////
    }

    private void genTangentData(){
        mTangentData = new float[mVerticesData.length];

        int b = 0;
        for (int i = 0; i < mVerticesData.length; i+=9){
            float[] pos1 = {mVerticesData[i],mVerticesData[i+1],mVerticesData[i+2]};
            float[] pos2 = {mVerticesData[i+3],mVerticesData[i+4],mVerticesData[i+5]};
            float[] pos3 = {mVerticesData[i+6],mVerticesData[i+7],mVerticesData[i+8]};

            float[] uv1 = {mNormalTextureCoordsData[b],mNormalTextureCoordsData[b+1]};
            float[] uv2 = {mNormalTextureCoordsData[b+2],mNormalTextureCoordsData[b+3]};
            float[] uv3 = {mNormalTextureCoordsData[b+4],mNormalTextureCoordsData[b+5]};
            b+=6;

            float[] edge1 = {pos2[0]-pos1[0], pos2[1]-pos1[1], pos2[2]-pos1[2]};//pos2 - pos1;
            float[] edge2 = {pos3[0]-pos1[0], pos3[1]-pos1[1], pos3[2]-pos1[2]};//pos3 - pos1;
            float[] deltaUV1 = {uv2[0]-uv1[0], uv2[1]-uv1[1]};//uv2 - uv1;
            float[] deltaUV2 = {uv3[0]-uv1[0], uv3[1]-uv1[1]};//uv3 - uv1;

            float f = 1.0f / (deltaUV1[0] * deltaUV2[1] - deltaUV2[0] * deltaUV1[1]);

            float[] tangent = new float[3];

            tangent[0] = f * (deltaUV2[1] * edge1[0] - deltaUV1[1] * edge2[0]);
            tangent[1] = f * (deltaUV2[1] * edge1[1] - deltaUV1[1] * edge2[1]);
            tangent[2] = f * (deltaUV2[1] * edge1[2] - deltaUV1[1] * edge2[2]);

            for (int j = 0; j < 3; j++){
                mTangentData[i+j*3]=tangent[0];
                mTangentData[i+j*3+1]=tangent[1];
                mTangentData[i+j*3+2]=tangent[2];
            }
        }
    }

    public void setScale(Vector3 size){
        objectSize=size;
        objUpd=true;
    }
    public void setRotate(Vector3 rotate){
        objectRot=rotate;
        objUpd=true;
    }
    public void setPosition(Vector3 position){
        objectPos=position;
        objUpd=true;
    }
    public void setVertices(float[] mVerticesData){
        this.mVerticesData=mVerticesData;
        genBuffer();
    }
    public void setNormals(float[] mNormalsData){
        this.mNormalsData=mNormalsData;
        genBuffer();
    }
    public void setTextureCoords(float[] mTextureCoordsData){
        this.mTextureCoordsData=mTextureCoordsData;
        genBuffer();
    }
    public void setNormalTextureCoords(float[] mNormalTextureCoordsData){
        this.mNormalTextureCoordsData=mNormalTextureCoordsData;
        genBuffer();
    }
    public void setMaterial(float[] material){
        this.material=material;
        if(material[2]>-1){
            genTangentData();
        }
        genBuffer();
    }
    public void setColor(Vector3 mColor){
        this.mColor=mColor;
    }
/////////////////////////////////////////////////////////////////
    public Vector3 getScale(){
        return objectSize;
    }
    public Vector3 getRotate(){
        return objectRot;
    }
    public Vector3 getPosition(){
        return objectPos;
    }
    public Vector3 getColor(){
        return mColor;
    }
    public float[] getMaterial() { return material; }

    private void setUniforms(){
        glUniformMatrix4fv(shader_vars[6], 1, false, modelMatrix, 0);

        glUniformMatrix4fv(shader_vars[34], 1, false, rotateMatrix, 0);

        glUniform3fv(shader_vars[11], 1, new float[]{mColor.x, mColor.y, mColor.z}, 0);


        for (int i = 0; i < uMaterial.length; i++){
            glUniform1f(uMaterial[i], material[i]);
        }

        if(material[1]>-1){
            glUniform1i(shader_vars[7], (int)material[1]);
        }
        if (material[2]>-1){
            glUniform1i(shader_vars[8], (int)material[2]);
        }
        //if(material[5]>-1.f){
       //     glUniform1i(shader_vars[23], (int)material[5]);

           // glUniformMatrix4fv(renderer.uVPMatrix, 1, false, renderer.camera.getProjectionMatrix(), 0);
       // }

    }

    private void putShadowBuffers(){
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glEnableVertexAttribArray(shader_vars[16]);
        glVertexAttribPointer(shader_vars[16], POSITION_COMPONENT_SIZE,
                GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[1]);
    }

    public void draw() {

        if(objUpd){
            objUpd=false;
            genModelMatrix();
        }

        if(material[5]==1){
            setBuffers();
            putBuffers();

            setUniforms();

            // glDrawElements(GL_TRIANGLES, mIndicesData.length, GL_UNSIGNED_SHORT, 0);
            glDrawArrays(GL_TRIANGLES, 0, mVerticesData.length);

//        if(material[5]>-1.f) {
//            //glUniformMatrix4fv(renderer.uVPMatrix, 1, false, renderer.camera.getvPMatrix(), 0);
//        }

            glDisableVertexAttribArray(shader_vars[0]);
            glDisableVertexAttribArray(shader_vars[1]);
            glDisableVertexAttribArray(shader_vars[2]);
            glDisableVertexAttribArray(shader_vars[3]);
            glDisableVertexAttribArray(shader_vars[4]);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }

        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void draw_shadow() {
        if(material[0]!=0&&material[5]==1){
            setBuffers();
            putShadowBuffers();

            glUniformMatrix4fv(shader_vars[15], 1, false, modelMatrix, 0);

            //glDrawElements(GL_TRIANGLES, mIndicesData.length, GL_UNSIGNED_SHORT, 0);
            glDrawArrays(GL_TRIANGLES, 0, mVerticesData.length);

            glDisableVertexAttribArray(shader_vars[16]);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }
}
