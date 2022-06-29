package com.example.mylibrary.model;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.render.Renderer;
import com.example.mylibrary.render.ShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Model {

    protected class BufferData{
        public FloatBuffer floatBuffer;
        public int COORDS_PER_VERTEX;
        public BufferData(FloatBuffer floatBuffer, int COORDS_PER_VERTEX){
            this.floatBuffer = floatBuffer;
            this.COORDS_PER_VERTEX = COORDS_PER_VERTEX;
        }
    }

    protected float[] vertexes = null;
    protected float[] vertexes_normal = null;
    protected float[] vertexes_texture = null;
    protected float[] vertexes_normalTexture = null;

    public int getNumberPolygons(){return vertexes.length/3;}

    public Vector3 minPoint = new Vector3(-1);
    public Vector3 maxPoint = new Vector3(1);

    public final ShaderProgram shaderProgram;

    protected final HashMap<String, BufferData> buffers = new HashMap<>();
    protected HashMap<String, Integer> attributs;
    protected HashMap<String, Integer> uniforms;
    protected int texture;
    public final Core core;

    public Model(float[] vertexes, Core core){
        this.vertexes = vertexes;
        this.core = core;
        this.shaderProgram = core.getRenderer().getShaderProgram("color");

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        vertexes_normal = null;
        vertexes_texture = null;
        vertexes_normalTexture = null;

        genBuffer();
    }
    public Model(float[] vertexes, float[] vertexes_normal, Core core){
        this.vertexes = vertexes;
        this.vertexes_normal = vertexes_normal;
        this.core = core;
        this.shaderProgram = core.getRenderer().getShaderProgram("color_normals");

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        vertexes_texture = null;
        vertexes_normalTexture = null;

        genBuffer();
    }
    public Model(float[] vertexes, float[] vertexes_texture, Core core, int texture){
        this.vertexes = vertexes;
        this.vertexes_texture = vertexes_texture;
        this.core = core;
        this.shaderProgram = core.getRenderer().getShaderProgram("texture");

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        vertexes_normal = null;
        vertexes_normalTexture = null;

        genBuffer();
    }
    public Model(float[] vertexes, float[] vertexes_normal, float[] vertexes_texture, Core core){
        this.vertexes = vertexes;
        this.vertexes_normal = vertexes_normal;
        this.vertexes_texture = vertexes_texture;
        this.core = core;
        this.shaderProgram = core.getRenderer().getShaderProgram("texture_normals");

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        vertexes_normalTexture = null;

        genBuffer();
    }
    public Model(float[] vertexes, float[] vertexes_normal, float[] vertexes_texture, float[] vertexes_normalTexture, Core core){
        this.vertexes = vertexes;
        this.vertexes_normal = vertexes_normal;
        this.vertexes_texture = vertexes_texture;
        this.vertexes_normalTexture = vertexes_normalTexture;
        this.core = core;
        this.shaderProgram = core.getRenderer().getShaderProgram("texture_normalMap");

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        genBuffer();
    }
    public Model(VertexesData vertexesData, Core core){
        vertexes = vertexesData.vertexes;
        vertexes_normal = vertexesData.vertexes_normal;
        vertexes_texture = vertexesData.vertexes_texture;
        vertexes_normalTexture = vertexesData.vertexes_normalTexture;

        maxPoint = vertexesData.maxPoint;
        minPoint = vertexesData.minPoint;

        this.core = core;

        String spn = "color";
        if(vertexes_normal!=null)
            spn = "color_normals";
        if(vertexes_texture!=null&&vertexes_normal==null)
            spn = "texture";
        if(vertexes_texture!=null&&vertexes_normal!=null)
            spn = "texture_normals";
        if(vertexes_normalTexture!=null)
            spn = "texture_normalMap";

        shaderProgram = core.getRenderer().getShaderProgram(spn);

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        genBuffer();
    }
    public Model(String cubeMapKey, Core core){
        this.core = core;
        texture = core.getRenderer().getTexture(cubeMapKey);

        this.shaderProgram = core.getRenderer().getShaderProgram("sky");

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        vertexes_normal = null;
        vertexes_texture = null;
        vertexes_normalTexture = null;

        vertexes = new float[]{
                -1.0f, -1.0f,  1.0f,
                1.0f, -1.0f,  1.0f,
                1.0f,  1.0f,  1.0f,

                -1.0f, -1.0f,  1.0f,
                1.0f,  1.0f,  1.0f,
                -1.0f,  1.0f,  1.0f,

                // Задняя грань
                -1.0f, -1.0f, -1.0f,
                -1.0f,  1.0f, -1.0f,
                1.0f,  1.0f, -1.0f,

                1.0f,  1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,

                // Верхняя грань
                -1.0f,  1.0f, -1.0f,
                -1.0f,  1.0f,  1.0f,
                1.0f,  1.0f,  1.0f,

                -1.0f,  1.0f, -1.0f,
                1.0f,  1.0f,  1.0f,
                1.0f,  1.0f, -1.0f,

                // Нижняя грань
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f,  1.0f,

                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f,  1.0f,
                -1.0f, -1.0f,  1.0f,

                // Правая грань
                1.0f, -1.0f, -1.0f,
                1.0f,  1.0f, -1.0f,
                1.0f,  1.0f,  1.0f,

                1.0f, -1.0f, -1.0f,
                1.0f,  1.0f,  1.0f,
                1.0f, -1.0f,  1.0f,

                // Левая грань
                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f,  1.0f,
                -1.0f,  1.0f,  1.0f,

                -1.0f, -1.0f, -1.0f,
                -1.0f,  1.0f,  1.0f,
                -1.0f,  1.0f, -1.0f
        };

        genBuffer();
    }
    public Model(ShaderProgram shaderProgram, Core core){
        this.shaderProgram=shaderProgram;
        this.core=core;

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();
    }

    protected void genBuffer(){
        int BYTES_PER_FLOAT = 4;

        FloatBuffer mVertices = ByteBuffer.allocateDirect(vertexes.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(vertexes).position(0);

        buffers.put("vPosition", new BufferData(mVertices, 3));

        if(shaderProgram.name == "color_normals" || shaderProgram.name == "texture_normals" || shaderProgram.name == "texture_normalMap"){
            FloatBuffer mNormals = ByteBuffer.allocateDirect(vertexes_normal.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mNormals.put(vertexes_normal).position(0);

            buffers.put("vNormal", new BufferData(mNormals, 3));
        }

        if(shaderProgram.name == "texture" || shaderProgram.name == "texture_normals" || shaderProgram.name == "texture_normalMap"){
            FloatBuffer mTexture = ByteBuffer.allocateDirect(vertexes_texture.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTexture.put(vertexes_texture).position(0);

            buffers.put("vTexture", new BufferData(mTexture, 2));
        }
        if(shaderProgram.name == "texture_normalMap"){
            FloatBuffer mTextureNormal = ByteBuffer.allocateDirect(vertexes_normalTexture.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTextureNormal.put(vertexes_normalTexture).position(0);

            buffers.put("vNormalTextureCoord", new BufferData(mTextureNormal, 2));

            float[] vertexes_tangent = genTangentData();

            FloatBuffer mTangent = ByteBuffer.allocateDirect(vertexes_tangent.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTangent.put(vertexes_tangent).position(0);

            buffers.put("vTangent", new BufferData(mTangent, 3));
        }
    }
    protected float[] genTangentData(){
        float[] mTangentData = new float[vertexes.length];

        int b = 0;
        for (int i = 0; i < vertexes.length; i+=9){
            float[] pos1 = {vertexes[i],vertexes[i+1],vertexes[i+2]};
            float[] pos2 = {vertexes[i+3],vertexes[i+4],vertexes[i+5]};
            float[] pos3 = {vertexes[i+6],vertexes[i+7],vertexes[i+8]};

            float[] uv1 = {vertexes_normalTexture[b],vertexes_normalTexture[b+1]};
            float[] uv2 = {vertexes_normalTexture[b+2],vertexes_normalTexture[b+3]};
            float[] uv3 = {vertexes_normalTexture[b+4],vertexes_normalTexture[b+5]};
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

        return mTangentData;
    }


    protected void setGeneralUniforms(){
        Renderer r = core.getRenderer();
        if(shaderProgram.name == "color" || shaderProgram.name == "color_normals" || shaderProgram.name == "texture" || shaderProgram.name == "texture_normals" || shaderProgram.name == "texture_normalMap"){
            glUniformMatrix4fv(uniforms.get("uVPMatrix"), 1, false, r.camera.getvPMatrix(), 0);
            glUniform1f(uniforms.get("far"), r.camera.getFar());
            glUniform4fv(uniforms.get("fog_color"), 1, r.fog_color.getArray(), 0);
            glUniform1f(uniforms.get("ambient"), r.ambient);

            if(r.getShadowCamera()!=null){
                glUniform1i(uniforms.get("softShadow"), r.softShadow);
                glUniform1f(uniforms.get("bias"), r.bias);
                glUniform1i(uniforms.get("shadowMap"), r.getTexture("zBuffer"));
                glUniformMatrix4fv(uniforms.get("depthMVP"), 1, false, r.getShadowCamera().getvPMatrix(), 0);
            }
        }
        if(shaderProgram.name == "color_normals" || shaderProgram.name == "texture_normals" || shaderProgram.name == "texture_normalMap"){
            glUniform3fv(uniforms.get("uViewPos"), 1, r.camera.getPosition().getArray(), 0);
            glUniform3fv(uniforms.get("global_light_color"), 1, r.global_light_color.getArray(), 0);
            glUniform3fv(uniforms.get("global_light_dir"), 1, r.global_light_dir.getArray(), 0);

            int j = 0;
            for (int i = 0; i < r.getLightsArraySize(); i++){
                j += r.getLight(i).draw(new int[]{
                        uniforms.get("uLight["+j+"]"),
                        uniforms.get("uLight["+(j+1)+"]"),
                        uniforms.get("uLight["+(j+2)+"]")
                });
                if(252-j+1<3)break;
            }
            if(r.getLightsArraySize() == 0){
                    glUniform4fv(uniforms.get("uLight["+0+"]"), 1, new float[]{0,0,0,0}, 0);
                glUniform4fv(uniforms.get("uLight["+1+"]"), 1, new float[]{0,0,0,0}, 0);
            }
        }
        if(shaderProgram.name == "sky"){
            glUniformMatrix4fv(uniforms.get("uVPMatrix"), 1, false, r.camera.getProjectionMatrix(), 0);
            glUniform1i(uniforms.get("skyBox"), texture);
        }
    }
    protected void setBuffers(){
        for (Map.Entry<String, Integer> entry : attributs.entrySet()) {
            int positionHandle = entry.getValue();

            BufferData bufferData = buffers.get(entry.getKey());

            glVertexAttribPointer(positionHandle, bufferData.COORDS_PER_VERTEX,
                    GL_FLOAT, false,
                    bufferData.COORDS_PER_VERTEX*4, bufferData.floatBuffer);
            glEnableVertexAttribArray(positionHandle);
        }
    }

    public void putShaderVariables(){
        setBuffers();
        setGeneralUniforms();
    }

    public void setZbufferAtr(HashMap<String, Integer> attributs){
        int positionHandle = attributs.get("vPosition");

        BufferData bufferData = buffers.get("vPosition");

        glVertexAttribPointer(positionHandle, bufferData.COORDS_PER_VERTEX,
                GL_FLOAT, false,
                bufferData.COORDS_PER_VERTEX*4, bufferData.floatBuffer);
        glEnableVertexAttribArray(positionHandle);
    }

    public void disableAttributs(){
        for (Map.Entry<String, Integer> entry : attributs.entrySet()) {
            glDisableVertexAttribArray(entry.getValue());
        }
    }

    @Override
    public String toString() {
        return getClass().getName()
                +'\n'+"vertexes: "+ Arrays.toString(vertexes)
                +'\n'+"vertexes_normal: "+ Arrays.toString(vertexes_normal)
                +'\n'+"vertexes_normalTexture: "+ Arrays.toString(vertexes_normalTexture)
                +'\n'+"shaderProgram: " + shaderProgram.name;
    }

}
