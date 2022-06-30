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
    // хранит информацию о буфере
    protected class BufferData{
        public FloatBuffer floatBuffer;
        public int COORDS_PER_VERTEX;
        public BufferData(FloatBuffer floatBuffer, int COORDS_PER_VERTEX){
            this.floatBuffer = floatBuffer;
            this.COORDS_PER_VERTEX = COORDS_PER_VERTEX;
        }
    }
    // мнформация о вершинах
    protected VertexesData vertexesData = new VertexesData();
    // количество полигонов
    public int getNumberPolygons(){return vertexesData.vertexes.length/3;}
    // мин и макс точки
    public Vector3 minPoint = new Vector3(-1);
    public Vector3 maxPoint = new Vector3(1);
    // используемая шейдерная программа
    public final ShaderProgram shaderProgram;
    // буферы
    protected final HashMap<String, BufferData> buffers = new HashMap<>();
    // атрибуты
    protected HashMap<String, Integer> attributs;
    // юниформы
    protected HashMap<String, Integer> uniforms;
    // текстура неба
    private int texture;
    // ядро
    public final Core core;

    private static final String colorS = "color";
    private static final String color_normalsS = "color_normals";
    private static final String textureS = "texture";
    private static final String texture_normalsS = "texture_normals";
    private static final String texture_normalMapS = "texture_normalMap";
    private static final String vPositionS = "vPosition";
    // объект с цветом
    public Model(float[] vertexes, Core core){
        vertexesData.vertexes = vertexes;
        this.core = core;
        this.shaderProgram = core.getRenderer().getShaderProgram(colorS);

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        genBuffer();
    }
    // с нормалями
    public Model(float[] vertexes, float[] vertexes_normal, Core core){
        vertexesData.vertexes = vertexes;
        vertexesData.vertexes_normal = vertexes_normal;
        this.core = core;
        this.shaderProgram = core.getRenderer().getShaderProgram(color_normalsS);

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        genBuffer();
    }
    // с текстрой без нормалей
    public Model(float[] vertexes, float[] vertexes_texture, Core core, int texture){
        vertexesData.vertexes = vertexes;
        vertexesData.vertexes_texture = vertexes_texture;
        this.core = core;
        this.shaderProgram = core.getRenderer().getShaderProgram(textureS);

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        genBuffer();
    }
    // текстура + нормали
    public Model(float[] vertexes, float[] vertexes_normal, float[] vertexes_texture, Core core){
        vertexesData.vertexes = vertexes;
        vertexesData.vertexes_normal = vertexes_normal;
        vertexesData.vertexes_texture = vertexes_texture;
        this.core = core;
        this.shaderProgram = core.getRenderer().getShaderProgram(texture_normalsS);

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        genBuffer();
    }
    // текстура + карта нормалей
    public Model(float[] vertexes, float[] vertexes_normal, float[] vertexes_texture, float[] vertexes_normalTexture, Core core){
        vertexesData.vertexes = vertexes;
        vertexesData.vertexes_normal = vertexes_normal;
        vertexesData.vertexes_texture = vertexes_texture;
        vertexesData.vertexes_normalTexture = vertexes_normalTexture;
        this.core = core;
        this.shaderProgram = core.getRenderer().getShaderProgram(texture_normalMapS);

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        genBuffer();
    }
    // авто
    public Model(VertexesData vertexesData, Core core){
        this.vertexesData = vertexesData;

        maxPoint = vertexesData.maxPoint;
        minPoint = vertexesData.minPoint;

        this.core = core;

        String spn = colorS;
        if(vertexesData.vertexes_normal!=null)
            spn = color_normalsS;
        if(vertexesData.vertexes_texture!=null&&vertexesData.vertexes_normal==null)
            spn = textureS;
        if(vertexesData.vertexes_texture!=null&&vertexesData.vertexes_normal!=null)
            spn = texture_normalsS;
        if(vertexesData.vertexes_normalTexture!=null)
            spn = texture_normalMapS;

        shaderProgram = core.getRenderer().getShaderProgram(spn);

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        genBuffer();
    }
    // небо
    public Model(String cubeMapKey, Core core){
        this.core = core;
        texture = core.getRenderer().getTexture(cubeMapKey);

        this.shaderProgram = core.getRenderer().getShaderProgram("sky");

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        vertexesData.vertexes = new float[]{
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

    // создание буферов
    protected void genBuffer(){
        final int BYTES_PER_FLOAT = 4;
        final int COORDS_PER_VERTEX = 3;
        final int COORDS_PER_VERTEX_T = 2;

        FloatBuffer mVertices = ByteBuffer.allocateDirect(vertexesData.vertexes.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(vertexesData.vertexes).position(0);

        buffers.put(vPositionS, new BufferData(mVertices, COORDS_PER_VERTEX));

        if(shaderProgram.name == color_normalsS || shaderProgram.name == texture_normalsS || shaderProgram.name == texture_normalMapS){
            FloatBuffer mNormals = ByteBuffer.allocateDirect(vertexesData.vertexes_normal.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mNormals.put(vertexesData.vertexes_normal).position(0);

            buffers.put("vNormal", new BufferData(mNormals, COORDS_PER_VERTEX));
        }

        if(shaderProgram.name == textureS || shaderProgram.name == texture_normalsS || shaderProgram.name == texture_normalMapS){
            FloatBuffer mTexture = ByteBuffer.allocateDirect(vertexesData.vertexes_texture.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTexture.put(vertexesData.vertexes_texture).position(0);

            buffers.put("vTexture", new BufferData(mTexture, COORDS_PER_VERTEX_T));
        }
        if(shaderProgram.name == texture_normalMapS){
            FloatBuffer mTextureNormal = ByteBuffer.allocateDirect(vertexesData.vertexes_normalTexture.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTextureNormal.put(vertexesData.vertexes_normalTexture).position(0);

            buffers.put("vNormalTextureCoord", new BufferData(mTextureNormal, COORDS_PER_VERTEX_T));

            float[] vertexes_tangent = genTangentData();

            FloatBuffer mTangent = ByteBuffer.allocateDirect(vertexes_tangent.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTangent.put(vertexes_tangent).position(0);

            buffers.put("vTangent", new BufferData(mTangent, COORDS_PER_VERTEX));
        }
    }
    // вочисление тан для карты нормалей
    protected float[] genTangentData(){
        float[] mTangentData = new float[vertexesData.vertexes.length];

        int b = 0;
        for (int i = 0; i < vertexesData.vertexes.length; i+=9){
            float[] pos1 = {vertexesData.vertexes[i],vertexesData.vertexes[i+1],vertexesData.vertexes[i+2]};
            float[] pos2 = {vertexesData.vertexes[i+3],vertexesData.vertexes[i+4],vertexesData.vertexes[i+5]};
            float[] pos3 = {vertexesData.vertexes[i+6],vertexesData.vertexes[i+7],vertexesData.vertexes[i+8]};

            float[] uv1 = {vertexesData.vertexes_normalTexture[b],vertexesData.vertexes_normalTexture[b+1]};
            float[] uv2 = {vertexesData.vertexes_normalTexture[b+2],vertexesData.vertexes_normalTexture[b+3]};
            float[] uv3 = {vertexesData.vertexes_normalTexture[b+4],vertexesData.vertexes_normalTexture[b+5]};
            b+=6;

            float[] edge1 = {pos2[0]-pos1[0], pos2[1]-pos1[1], pos2[2]-pos1[2]};
            float[] edge2 = {pos3[0]-pos1[0], pos3[1]-pos1[1], pos3[2]-pos1[2]};
            float[] deltaUV1 = {uv2[0]-uv1[0], uv2[1]-uv1[1]};
            float[] deltaUV2 = {uv3[0]-uv1[0], uv3[1]-uv1[1]};

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
    // установка общих юниформ для объектов этой модели
    protected void setGeneralUniforms(){
        Renderer r = core.getRenderer();
        if(shaderProgram.name == colorS || shaderProgram.name == color_normalsS || shaderProgram.name == textureS || shaderProgram.name == texture_normalsS || shaderProgram.name == texture_normalMapS){
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
        if(shaderProgram.name == color_normalsS || shaderProgram.name == texture_normalsS || shaderProgram.name == texture_normalMapS){
            glUniform3fv(uniforms.get("uViewPos"), 1, r.camera.getPosition().getArray(), 0);
            glUniform3fv(uniforms.get("global_light_color"), 1, r.global_light_color.getArray(), 0);
            glUniform3fv(uniforms.get("global_light_dir"), 1, r.global_light_dir.getArray(), 0);

            final String uLightS = "uLight[";
            int j = 0;
            for (int i = 0; i < r.getLightsArraySize(); i++){
                j += r.getLight(i).draw(new int[]{
                        uniforms.get(uLightS+j+"]"),
                        uniforms.get(uLightS+(j+1)+"]"),
                        uniforms.get(uLightS+(j+2)+"]")
                });
                if(252-j+1<3)break;
            }
            if(252-j+1>=2){
                glUniform4fv(uniforms.get(uLightS+j+"]"), 1, new float[]{0,0,0,0}, 0);
                glUniform4fv(uniforms.get(uLightS+(j+1)+"]"), 1, new float[]{0,0,0,0}, 0);
            }
        }
        if(shaderProgram.name == "sky"){
            glUniformMatrix4fv(uniforms.get("uVPMatrix"), 1, false, r.camera.getProjectionMatrix(), 0);
            glUniform1i(uniforms.get("skyBox"), texture);
        }
    }
    // установка буферов
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

    // старт установка буферов и юниформ
    public void putShaderVariables(){
        setBuffers();
        setGeneralUniforms();
    }
    // для Z буфера
    public void setZbufferAtr(HashMap<String, Integer> attributs){
        int positionHandle = attributs.get(vPositionS);

        BufferData bufferData = buffers.get(vPositionS);

        glVertexAttribPointer(positionHandle, bufferData.COORDS_PER_VERTEX,
                GL_FLOAT, false,
                bufferData.COORDS_PER_VERTEX*4, bufferData.floatBuffer);
        glEnableVertexAttribArray(positionHandle);
    }

    // очистка
    public void disableAttributs(){
        for (Map.Entry<String, Integer> entry : attributs.entrySet()) {
            glDisableVertexAttribArray(entry.getValue());
        }
    }

    @Override
    public String toString() {
        return getClass().getName()
                +'\n'+"vertexesData: "+ vertexesData
                +'\n'+"shaderProgram: " + shaderProgram.name;
    }

}
