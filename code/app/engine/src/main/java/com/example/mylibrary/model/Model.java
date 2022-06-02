package com.example.mylibrary.model;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.render.Light;
import com.example.mylibrary.render.Renderer;
import com.example.mylibrary.render.ShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;


public class Model {

    class BufferData{
        public final FloatBuffer floatBuffer;
        public final int COORDS_PER_VERTEX;
        public BufferData(FloatBuffer floatBuffer, int COORDS_PER_VERTEX){
            this.floatBuffer = floatBuffer;
            this.COORDS_PER_VERTEX = COORDS_PER_VERTEX;
        }
    }

    public final float[] vertexes;
    public final float[] vertexes_normal;
    public final float[] vertexes_texture;
    public final float[] getVertexes_normalTexture;

    public Vector4 color;

    public Vector3 minPoint = new Vector3(-1);
    public Vector3 maxPoint = new Vector3(1);

    public final ShaderProgram shaderProgram;

    public final HashMap<String, BufferData> buffers = new HashMap<>();
    private final HashMap<String, Integer> attributs;
    private final HashMap<String, Integer> uniforms;

    public final Core core;
    public Model(float[] vertexes, Vector4 color, Core core){
        this.vertexes = vertexes;
        this.color = color;
        this.core = core;
        this.shaderProgram = core.getRenderer().shaderPrograms.get("color");

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        vertexes_normal = null;
        vertexes_texture = null;
        getVertexes_normalTexture = null;

        genBuffer();
    }
    public Model(float[] vertexes, float[] vertexes_normal, Vector4 color, Core core){
        this.vertexes = vertexes;
        this.vertexes_normal = vertexes_normal;
        this.color = color;
        this.core = core;
        this.shaderProgram = core.getRenderer().shaderPrograms.get("color_normals");

        attributs = shaderProgram.getAttributs();
        uniforms = shaderProgram.getUniforms();

        vertexes_texture = null;
        getVertexes_normalTexture = null;

        genBuffer();
    }

    private void genBuffer(){
        int BYTES_PER_FLOAT = 4;

        FloatBuffer mVertices = ByteBuffer.allocateDirect(vertexes.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(vertexes).position(0);

        buffers.put("vPosition", new BufferData(mVertices, 3));

        if(shaderProgram.name == "color_normals"){
            FloatBuffer mNormals = ByteBuffer.allocateDirect(vertexes_normal.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mNormals.put(vertexes_normal).position(0);

            buffers.put("vNormal", new BufferData(mNormals, 3));
        }
    }

    public void setGeneralUniforms(){
        Renderer r = core.getRenderer();
        if(shaderProgram.name == "color"){
            glUniformMatrix4fv(uniforms.get("uVPMatrix"), 1, false, r.camera.getvPMatrix(), 0);
            glUniform1f(uniforms.get("far"), r.camera.getFar());
            glUniform4fv(uniforms.get("fog_color"), 1, r.fog_color.getArray(), 0);
        }
        if(shaderProgram.name == "color_normals"){
            glUniformMatrix4fv(uniforms.get("uVPMatrix"), 1, false, r.camera.getvPMatrix(), 0);
            glUniform1f(uniforms.get("far"), r.camera.getFar());
            glUniform4fv(uniforms.get("fog_color"), 1, r.fog_color.getArray(), 0);
            glUniform1f(uniforms.get("ambient"), r.ambient);
            glUniform3fv(uniforms.get("uViewPos"), 1, r.camera.getPosition().getArray(), 0);
            glUniform3fv(uniforms.get("global_light_color"), 1, r.global_light_color.getArray(), 0);
            glUniform3fv(uniforms.get("global_light_dir"), 1, r.global_light_dir.getArray(), 0);

            int j = 0;
            for (int i = 0; i < r.lights.size(); i++){
                j += r.lights.get(i).draw(new int[]{
                        uniforms.get("uLight["+j+"]"),
                        uniforms.get("uLight["+(j+1)+"]"),
                        uniforms.get("uLight["+(j+2)+"]")
                });
                if(252-j+1<3)break;
            }
        }
    }

    public void setBuffers(){
        for (Map.Entry<String, Integer> entry : attributs.entrySet()) {
            int positionHandle = entry.getValue();

            BufferData bufferData = buffers.get(entry.getKey());

            glVertexAttribPointer(positionHandle, bufferData.COORDS_PER_VERTEX,
                    GL_FLOAT, false,
                    bufferData.COORDS_PER_VERTEX*4, bufferData.floatBuffer);
            glEnableVertexAttribArray(positionHandle);
        }
    }

    public void disableAttributs(){
        for (Map.Entry<String, Integer> entry : attributs.entrySet()) {
            glDisableVertexAttribArray(entry.getValue());
        }
    }

    /*@Override
    public String toString() {
        return getClass().getName()
                +'\n'+"material: "+ Arrays.toString(material)
                +'\n'+"color: "+ color
                +'\n'+"texture: "+ texture
                +'\n'+"scale: "+ scale
                +'\n'+"pos: "+ pos
                +'\n'+"rot: "+ rot
                +'\n'+"v: "+ Arrays.toString(v)
                +'\n'+"vn: "+ Arrays.toString(vn)
                +'\n'+"vt: "+ Arrays.toString(vt);
    }*/

}
