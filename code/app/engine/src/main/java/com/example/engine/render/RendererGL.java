package com.example.engine.render;

import static android.opengl.GLES10.GL_MULTISAMPLE;
import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glClearDepthf;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform2fv;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLUtils.texImage2D;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;

import com.example.engine.core.Config;
import com.example.engine.core.Core;
import com.example.engine.math.Vector2Int;
import com.example.engine.math.Vector3;
import com.example.engine.math.Vector4;
import com.example.engine.model.Model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RendererGL implements GLSurfaceView.Renderer  {


    private final Core core;

    public Camera camera, shadow_camera;

    //private int mWidth,mHeight;
    private Vector2Int res = new Vector2Int(1);

    private int mProgramObject, mShadowProgramObject, mSkyProgram, mUIPorgramObject;

    //              0       1           2               3                   4       5           6
    /*private int vPosition, vNormal, vTextureCoord, vNormalTextureCoord, vTangent, uVPMatrix, uModelMatrix,
//              7           8               9                   10              11      12          13
            uTexture, uNormalTexture, uGlobal_light_dir, uGlobal_light_color, uColor, uViewPos, uAmbient,
    //          14          15              16          17          18      19              20          21
            usVPMatrix, usModelMatrix, vsPosition, udepthMVP, shadowMap, utexturesize, usShadowMap, softShadow,
//          22      23          24          25          26          27          28          29          30
            bias, skyBox, ultroSoftShadow, usSkyBox, vlPosition, ulVPMatrix, lskyBox, vuPosition, uUIModelMatrix,
    //          31          32              33      34
            uUITexture, vuTextureCoord, uUIColor, rotateMatrix;*/

    private final int[] shader_vars = new int[40];

    private final int[] uMaterial = new int[6];
    private int[] uUIMaterial = new int[1];

    private final int[] uLight = new int[256];
    private final float[] mLight = new float[uLight.length*4];
    private int lastLightIndex = 0;
    private int lastTextureIndex = 0;

    public int skyboxN=-1;

    public final int[] mVBOIds = new int[6];

    public boolean setClearColor = false;

    private LinkedList<RenderModel> renderObjectList = new LinkedList<>();
    private LinkedList<RenderImg> renderUIList = new LinkedList<>();

    public int FBO = -1;
    public boolean usFBO_shadow = false;

    private final FloatBuffer mVerticesL;
    private final ShortBuffer mIndicesL;

    public Vector2Int getResolution(){
        return res;
    }

    private final Config config;

    public void clear_i(){
        FBO = -1;
        usFBO_shadow = false;
        skyboxN=-1;
        lastTextureIndex = 0;
        lastLightIndex = 0;
    }

    public RendererGL(Core core) {
        this.core = core;
        camera = new Camera(res);
        shadow_camera = new Camera(res);

        //////////////////////////////////////////////////////////////////

        mVerticesL = ByteBuffer.allocateDirect(getSkyBoxVert().length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesL.put(getSkyBoxVert()).position(0);

        mIndicesL = ByteBuffer.allocateDirect(getSkyBoxIndex().length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndicesL.put(getSkyBoxIndex()).position(0);
        
        
        config = core.getConfig();
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        String vShaderStr = GLUtil.readShader(core, "shaders/vs.glsl");
        String fShaderStr = GLUtil.readShader(core, "shaders/fs.glsl");
        mProgramObject = GLUtil.loadProgram(vShaderStr, fShaderStr);

        vShaderStr = GLUtil.readShader(core, "shaders/vs_UI.glsl");
        fShaderStr = GLUtil.readShader(core, "shaders/fs_UI.glsl");
        mUIPorgramObject = GLUtil.loadProgram(vShaderStr, fShaderStr);

        //vuPosition = glGetAttribLocation(mUIPorgramObject, "vPosition");
        shader_vars[29] = glGetAttribLocation(mUIPorgramObject, "vPosition");
        //vuTextureCoord = glGetAttribLocation(mUIPorgramObject, "vTextureCoord");
        shader_vars[32] = glGetAttribLocation(mUIPorgramObject, "vTextureCoord");
        //uUIModelMatrix = glGetUniformLocation(mUIPorgramObject, "uModelMatrix");
        shader_vars[30] = glGetUniformLocation(mUIPorgramObject, "uModelMatrix");



        //uUITexture = glGetUniformLocation(mUIPorgramObject, "uTexture");
        shader_vars[31] = glGetUniformLocation(mUIPorgramObject, "uTexture");
        //uUIColor = glGetUniformLocation(mUIPorgramObject, "uColor");
        shader_vars[33] = glGetUniformLocation(mUIPorgramObject, "uColor");

        for (int i = 0; i < uUIMaterial.length; i++){
            uUIMaterial[i] = glGetUniformLocation(mUIPorgramObject, "material["+i+"]");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        shader_vars[39] = glGetUniformLocation(mProgramObject, "fog_color");
        shader_vars[37] = glGetUniformLocation(mProgramObject, "far");
        //vPosition = glGetAttribLocation(mProgramObject, "vPosition");
        shader_vars[0] = glGetAttribLocation(mProgramObject, "vPosition");
        shader_vars[35] = glGetUniformLocation(mProgramObject, "random_seed");
        shader_vars[36] = glGetUniformLocation(mProgramObject, "usRandL");
        //vNormal = glGetAttribLocation(mProgramObject, "vNormal");
        shader_vars[1] = glGetAttribLocation(mProgramObject, "vNormal");
        //vTextureCoord = glGetAttribLocation(mProgramObject, "vTextureCoord");
        shader_vars[2] = glGetAttribLocation(mProgramObject, "vTextureCoord");
        //vNormalTextureCoord = glGetAttribLocation(mProgramObject, "vNormalTextureCoord");
        shader_vars[3] = glGetAttribLocation(mProgramObject, "vNormalTextureCoord");
        //vTangent = glGetAttribLocation(mProgramObject, "vTangent");
        shader_vars[4] = glGetAttribLocation(mProgramObject, "vTangent");
        shader_vars[34] = glGetUniformLocation(mProgramObject, "rotateMatrix");
        //uVPMatrix = glGetUniformLocation(mProgramObject, "uVPMatrix");
        shader_vars[5] = glGetUniformLocation(mProgramObject, "uVPMatrix");
        //uModelMatrix = glGetUniformLocation(mProgramObject, "uModelMatrix");
        shader_vars[6] = glGetUniformLocation(mProgramObject, "uModelMatrix");
        //udepthMVP = glGetUniformLocation(mProgramObject, "depthMVP");
        shader_vars[17] = glGetUniformLocation(mProgramObject, "depthMVP");
        //usShadowMap = glGetUniformLocation(mProgramObject, "usShadowMap");
        shader_vars[20] = glGetUniformLocation(mProgramObject, "usShadowMap");
        //softShadow = glGetUniformLocation(mProgramObject, "softShadow");
        shader_vars[21] = glGetUniformLocation(mProgramObject, "softShadow");
        //bias = glGetUniformLocation(mProgramObject, "bias");
        shader_vars[22] = glGetUniformLocation(mProgramObject, "bias");
        //skyBox = glGetUniformLocation(mProgramObject, "skyBox");
        shader_vars[23] = glGetUniformLocation(mProgramObject, "skyBox");
        //ultroSoftShadow = glGetUniformLocation(mProgramObject, "ultroSoftShadow");
        shader_vars[24] = glGetUniformLocation(mProgramObject, "ultroSoftShadow");
        //usSkyBox = glGetUniformLocation(mProgramObject, "usSkyBox");
        shader_vars[25] = glGetUniformLocation(mProgramObject, "usSkyBox");

        //uGlobal_light_dir = glGetUniformLocation(mProgramObject, "global_light_dir");
        shader_vars[9] = glGetUniformLocation(mProgramObject, "global_light_dir");
        //uGlobal_light_color = glGetUniformLocation(mProgramObject, "global_light_color");
        shader_vars[10] = glGetUniformLocation(mProgramObject, "global_light_color");

        //uViewPos = glGetUniformLocation(mProgramObject, "uViewPos");
        shader_vars[12] = glGetUniformLocation(mProgramObject, "uViewPos");

        //utexturesize = glGetUniformLocation(mProgramObject, "utexturesize");
        shader_vars[19] = glGetUniformLocation(mProgramObject, "utexturesize");

        for (int i = 0; i < uMaterial.length; i++){
            uMaterial[i] = glGetUniformLocation(mProgramObject, "material["+i+"]");
        }

        //uAmbient = glGetUniformLocation(mProgramObject, "ambient");
        shader_vars[13] = glGetUniformLocation(mProgramObject, "ambient");

        //uTexture = glGetUniformLocation(mProgramObject, "uTexture");
        shader_vars[7] = glGetUniformLocation(mProgramObject, "uTexture");
        //uNormalTexture = glGetUniformLocation(mProgramObject, "uNormalTexture");
        shader_vars[8] = glGetUniformLocation(mProgramObject, "uNormalTexture");
        //shadowMap = glGetUniformLocation(mProgramObject, "shadowMap");
        shader_vars[18] = glGetUniformLocation(mProgramObject, "shadowMap");

        //uColor = glGetUniformLocation(mProgramObject, "uColor");
        shader_vars[11] = glGetUniformLocation(mProgramObject, "uColor");

        for (int i = 0; i < uLight.length; i++){
            uLight[i] = glGetUniformLocation(mProgramObject, "uLight["+i+"]");
        }

        activ_preload();

        glGenBuffers(6, mVBOIds, 0);

        glClearColor(config.clear_color.x,config.clear_color.y,config.clear_color.z,config.clear_color.w);

        glClearDepthf(1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);
        glDepthFunc(GL_LEQUAL);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glEnable(GL10.GL_BLEND);
        glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        res = new Vector2Int(i, i1);

        camera.setResolution(res);

        core.getTouchListener().setResolution(i,i1);
    }

    public void activ_preload(){
        core.getScene().preload();

        if(FBO == -1 && usFBO_shadow){
            int[] result = GLUtil.createFrameBuffer(config.shadow_res.x, config.shadow_res.y, lastTextureIndex);
            FBO = result[1];

            shadow_camera.setResolution(config.shadow_res);
        }

        if(usFBO_shadow){
            String vShaderStr = GLUtil.readShader(core, "shaders/vs_s.glsl");
            String fShaderStr = GLUtil.readShader(core, "shaders/fs_s.glsl");
            mShadowProgramObject = GLUtil.loadProgram(vShaderStr, fShaderStr);

            //vsPosition = glGetAttribLocation(mShadowProgramObject, "vPosition");
            shader_vars[16] = glGetAttribLocation(mShadowProgramObject, "vPosition");

            //usVPMatrix = glGetUniformLocation(mShadowProgramObject, "uVPMatrix");
            shader_vars[14] = glGetUniformLocation(mShadowProgramObject, "uVPMatrix");
            //usModelMatrix = glGetUniformLocation(mShadowProgramObject, "uModelMatrix");
            shader_vars[15] = glGetUniformLocation(mShadowProgramObject, "uModelMatrix");

            shader_vars[38] = glGetUniformLocation(mShadowProgramObject, "far");
        }

        if(skyboxN>-1){
            String vShaderStr = GLUtil.readShader(core, "shaders/vs_sky.glsl");
            String fShaderStr = GLUtil.readShader(core, "shaders/fs_sky.glsl");
            mSkyProgram = GLUtil.loadProgram(vShaderStr, fShaderStr);

            //vlPosition = glGetAttribLocation(mSkyProgram, "vPosition");
            shader_vars[26] = glGetAttribLocation(mSkyProgram, "vPosition");

            //ulVPMatrix = glGetUniformLocation(mSkyProgram, "uVPMatrix");
            shader_vars[27] = glGetUniformLocation(mSkyProgram, "uVPMatrix");

            //lskyBox = glGetUniformLocation(mSkyProgram, "skyBox");
            shader_vars[28] = glGetUniformLocation(mSkyProgram, "skyBox");


        }
        core.getScene().start();
    }

    public boolean sns = false;

    @Override
    public void onDrawFrame(GL10 gl10) {
        //glDisable(GL10.GL_CULL_FACE);
        //glEnable(GL10.GL_ALPHA);

        if(sns){
            activ_preload();
            sns=false;
        }

        if(setClearColor){
            glClearColor(config.clear_color.x,config.clear_color.y,config.clear_color.z,config.clear_color.w);
            setClearColor=false;
        }

        if(usFBO_shadow){
            Vector2Int cr = shadow_camera.getResolution();
            glViewport(0, 0, cr.x, cr.y);
            drawing_shadow();
        }
        drawing();
        if(skyboxN>-1){
            drawSkyBox();
        }
        drawUI();
    }

    private void drawUI(){
        glUseProgram(mUIPorgramObject);

        glDisable(GL_DEPTH_TEST);

        for (int i = 0; i < renderUIList.size(); i++){
            renderUIList.get(i).draw();
        }
        glEnable(GL_DEPTH_TEST);
    }

    private void drawSkyBox(){
        glUseProgram(mSkyProgram);

        mVerticesL.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glBufferData(GL_ARRAY_BUFFER, 4 * getSkyBoxVert().length,
                mVerticesL, GL_STATIC_DRAW);

        mIndicesL.position(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[1]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, 2 * getSkyBoxIndex().length, mIndicesL, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glEnableVertexAttribArray(shader_vars[26]);
        glVertexAttribPointer(shader_vars[26], 3,
                GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[1]);


        glUniformMatrix4fv(shader_vars[27], 1, false, camera.getProjectionMatrix(), 0);
        glUniform1i(shader_vars[28], skyboxN);

        glDrawElements(GL_TRIANGLES, getSkyBoxIndex().length, GL_UNSIGNED_SHORT, 0);

        glDisableVertexAttribArray(shader_vars[26]);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void clear(){
        Arrays.fill(mLight, 0);
        lastLightIndex = 0;
        renderObjectList = new LinkedList<>();
        renderUIList = new LinkedList<>();
    }

    public void addSearchlight(Vector4 pos, Vector4 dir, Vector4 col){//float[] pos, float[] dir, float[] col){
        int freeI = 256*4 - lastLightIndex;

        if(freeI < 12){
            System.out.println("нет места");
            return;
        }

        System.arraycopy(new float[]{pos.x,pos.y,pos.z,pos.w}, 0, mLight, lastLightIndex, 4);
        lastLightIndex+=4;
        System.arraycopy(new float[]{dir.x,dir.y,dir.z,dir.w}, 0, mLight, lastLightIndex, 4);
        lastLightIndex+=4;
        System.arraycopy(new float[]{col.x,col.y,col.z,col.w}, 0, mLight, lastLightIndex, 4);
        lastLightIndex+=4;
    }
    public void addPointLight(Vector4 pos, Vector4 col){//float[] pos, float[] col){
        int freeI = 256*4 - lastLightIndex;

        if(freeI < 8){
            System.out.println("нет места");
            return;
        }

        System.arraycopy( new float[]{pos.x,pos.y,pos.z,pos.w}, 0, mLight, lastLightIndex, 4);
        lastLightIndex+=4;
        System.arraycopy(new float[]{col.x,col.y,col.z,col.w}, 0, mLight, lastLightIndex, 4);
        lastLightIndex+=4;
    }

    private void drawing(){
        glUseProgram(mProgramObject);

        glClear(GL_COLOR_BUFFER_BIT);

        glViewport(0, 0, res.x,res.y);

        setUniforms();

        glClearDepthf(1.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for (int i = 0; i < renderObjectList.size(); i++){
            renderObjectList.get(i).draw();
        }
    }
    private void setUniforms(){
        glUniformMatrix4fv(shader_vars[5], 1, false, camera.getvPMatrix(), 0);
        glUniformMatrix4fv(shader_vars[17], 1, false, shadow_camera.getvPMatrix(), 0);

        glUniform1i(shader_vars[18], lastTextureIndex);

        glUniform1f(shader_vars[35], (float) (Math.random()));
        glUniform1f(shader_vars[36], config.usRandL);

        glUniform1f(shader_vars[37], camera.getFar());

        glUniform1i(shader_vars[24], config.ultroSoftShadow);

        glUniform3fv(shader_vars[9], 1, new float[]{config.global_light_dir.x,config.global_light_dir.y,config.global_light_dir.z}, 0);
        glUniform3fv(shader_vars[10], 1, new float[]{config.global_light_color.x,config.global_light_color.y,config.global_light_color.z}, 0);

        Vector3 cam_pos = camera.getPosition();
        glUniform3fv(shader_vars[12],1, new float[]{cam_pos.x,cam_pos.y,cam_pos.z}, 0);

        glUniform4fv(shader_vars[39],1, config.fog_color.getArray(), 0);

        glUniform1f(shader_vars[13], config.ambient);
        glUniform1f(shader_vars[21], config.soft_shadow_cof);
        glUniform1f(shader_vars[22], config.bias);

        if(skyboxN>-1){
            glUniform1i(shader_vars[23], skyboxN);
            glUniform1i(shader_vars[25], skyboxN);
        }


        glUniform2fv(shader_vars[19], 1, new float[]{res.x,res.y}, 0);

        if (usFBO_shadow){
            glUniform1i(shader_vars[20], 1);

            glUniform1f(shader_vars[38], shadow_camera.getFar());
        }

        drawingLigth();
    }

    private void drawing_shadow(){

        glBindFramebuffer(GL_FRAMEBUFFER, FBO);

        glUseProgram(mShadowProgramObject);

        //glViewport(0, 0, mWidth, mHeight);
        glClear(GL_COLOR_BUFFER_BIT);


        glUniformMatrix4fv(shader_vars[14], 1, false, shadow_camera.getvPMatrix(), 0);

        glClearDepthf(1.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for (int i = 0; i < renderObjectList.size(); i++){
            renderObjectList.get(i).draw_shadow();
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void drawingLigth(){
        for (int i = 0; i < lastLightIndex; i+=4){
            float[] arr = new float[]{mLight[i],mLight[i+1],mLight[i+2],mLight[i+3]};
            glUniform4fv(uLight[i/4], 1, arr, 0);
        }
    }

    public RenderModel addObject(float[] vertex){
        renderObjectList.addLast(new RenderModel(vertex, shader_vars, mVBOIds, uMaterial,core));

        return renderObjectList.get(renderObjectList.size()-1);
    }

    public RenderModel addObject(Model model){
        renderObjectList.addLast(new RenderModel(model, shader_vars, mVBOIds, uMaterial,core));

        return renderObjectList.get(renderObjectList.size()-1);
    }

    public void deleteObject(RenderModel object){
        renderObjectList.remove(object);
    }

    public void deleteUI(RenderImg ui){
        renderUIList.remove(ui);
    }


    public RenderImg addUIImg(){
        renderUIList.addLast(new RenderImg(shader_vars, mVBOIds, uUIMaterial));

        return renderUIList.get(renderUIList.size()-1);
    }
    public RenderText addUIText(){
        RenderText rt = new RenderText(shader_vars, mVBOIds, uUIMaterial);
        renderUIList.addLast(rt);
        return rt;
    }


    public void loadTextures(String[] src){

        lastTextureIndex = src.length;

        AssetManager assetManager = core.getAssets();

        InputStream istr;

        for(int i = 0; i < src.length; i++){
            Bitmap bitmap;
            try {
                istr = assetManager.open(src[i]);
                bitmap = BitmapFactory.decodeStream(istr);
            } catch (IOException e) {
                System.out.println("не удалось загрузить bitmap: "+i+" "+src[i]);
                continue;
            }

            glActiveTexture(GL_TEXTURE0+i);

            int[] textures = new int[1];
            glGenTextures(1, textures, 0);

            glBindTexture(GL_TEXTURE_2D, textures[0]);

            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_WRAP_S,
                    GL_CLAMP_TO_EDGE);

            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_WRAP_T,
                    GL_CLAMP_TO_EDGE);

            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_MIN_FILTER,
                    GL_LINEAR);

            texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

            assert bitmap != null;
            bitmap.recycle();
        }

    }

    public void loadCubemap(String[] src){
        glActiveTexture(GL_TEXTURE0+lastTextureIndex);
        skyboxN=lastTextureIndex;
        lastTextureIndex++;

        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER,
                GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER,
                GL_LINEAR);

        AssetManager assetManager = core.getAssets();
        InputStream istr;

        for(int i = 0; i < src.length; i++){
            Bitmap bitmap;
            try {
                istr = assetManager.open(src[i]);
                bitmap = BitmapFactory.decodeStream(istr);
            } catch (IOException e) {
                System.out.println("не удалось загрузить bitmap: "+i+" "+src[i]);
                continue;
            }

            texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, bitmap, 0);

            bitmap.recycle();
        }
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
    }


    public float[] getCubeVert(){
        return new float[]{
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
    }
    private float[] getSkyBoxVert(){
        return new float[]{
                1,  1,  1,
                1,  1, -1,
                -1,  1, -1,
                -1,  1,  1,
                1, -1,  1,
                1, -1, -1,
                -1, -1, -1,
                -1, -1,  1
        };
    }
    private short[] getSkyBoxIndex(){
        return new short[]{
                0, 3, 7,    0, 7, 4,
                1, 2, 6,    1, 6, 5,
                2, 3, 7,    2, 7, 6,
                0, 4, 5,    0, 5, 1,
                0, 1, 2,    0, 2, 3,
                4, 5, 6,    4, 6, 7
        };
    }
}
