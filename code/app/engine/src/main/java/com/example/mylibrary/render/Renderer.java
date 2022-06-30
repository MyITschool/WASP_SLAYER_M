package com.example.mylibrary.render;

import static android.opengl.GLES10.GL_MULTISAMPLE;
import static android.opengl.GLES10.glClearColor;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearDepthf;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLUtils.texImage2D;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector2;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;
import com.example.mylibrary.model.UIModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@SuppressLint("ViewConstructor")
public final class Renderer extends GLSurfaceView implements GLSurfaceView.Renderer {
    private Logger logger = Logger.getLogger(Renderer.class.getName());

    private final Core core;

    // программы
    private final HashMap<String, ShaderProgram> shaderPrograms = new HashMap<>();
    // текстуры
    private final HashMap<String, Integer> textures = new HashMap<>();
    // удалёные текстуры
    private final ArrayList<Integer> deleteTextures = new ArrayList<>();
    // мадели
    private final ArrayList<Model> models = new ArrayList<>();
    // объекты
    private final ArrayList<ArrayList<RenderObject>> obj = new ArrayList<>();
    // UI модели
    private final ArrayList<Model> UImodels = new ArrayList<>();
    // UI объекты
    private final ArrayList<ArrayList<RenderObject>> UIobj = new ArrayList<>();
    // обновляемые объекты
    private final ArrayList<Updated> updateds = new ArrayList<>();
    // свет
    private final ArrayList<Light> lights = new ArrayList<>();
    // камера
    public Camera camera;
    public UIModel UIModel;
    // цвет тумана в дали
    public Vector4 fog_color = new Vector4(1);
    // мин освещение
    public float ambient = 0;
    // направление глобального освещения
    public Vector3 global_light_dir = new Vector3(0,1,0);
    // цвет глобального освещения
    public Vector3 global_light_color = new Vector3(1);
    // уровень теней
    public int softShadow = 1;
    // получить программу
    public ShaderProgram getShaderProgram(String key){
        return shaderPrograms.get(key);
    }
    // удалить программу
    public void deleteShaderProgram(String key){
        shaderPrograms.remove(key);
    }
    // разрешение
    private Vector2 res = new Vector2(1);
    // получить рарешение
    public Vector2 getRes(){return res.clone();}
    // камера теней
    private Camera shadowCamera;
    // Z буфер
    private int fbo = -1;
    // погрешность теней
    public float bias = 0.005f;

    // писать в консоль ФПС
    public boolean logFPS = true;

    public Renderer(Core core){
        super(core);
        this.core = core;
        camera = new Camera(core);

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);

        setRenderer(this);
    }

    private final String uVPMatrixS = "uVPMatrix";
    private final String zBufferS = "zBuffer";
    private final String uModelMatrixS = "uModelMatrix";
    // создание стандартных программ
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        final String fog_colorS = "fog_color";
        final String shadowMapS = "shadowMap";
        final String softShadowS = "softShadow";
        final String depthMVPS = "depthMVP";
        final String ambientS = "ambient";
        final String uRotMatrixS = "uRotMatrix";
        final String global_light_dirS = "global_light_dir";
        final String global_light_colorS = "global_light_color";
        final String uViewPosS = "uViewPos";
        final String specularS = "specular";
        final String uLightS = "uLight[";
        final String vNormalS = "vNormal";
        final String vTextureS = "vTexture";
        final String uTextureS = "uTexture";
        final String colorS = "color";
        final String vPositionS = "vPosition";
        shaderPrograms.put(colorS, new ShaderProgram(colorS,"shaders/color/vs.glsl","shaders/color/fs.glsl",
                new String[]{vPositionS},
                new String[]{uVPMatrixS, uModelMatrixS, colorS, "far", fog_colorS, shadowMapS, softShadowS, "bias", depthMVPS, ambientS},
                core));
        //////////////////////////////////////////////////////////////////////////
        String[] uniforms = new String[252+15];
        System.arraycopy(new String[]{uRotMatrixS, uVPMatrixS, uModelMatrixS, colorS, global_light_dirS, global_light_colorS, uViewPosS, ambientS, fog_colorS, specularS, "far", shadowMapS, softShadowS, "bias", depthMVPS},0,uniforms,0,15);

        for (int i = 0; i < 252; i++){
            uniforms[15+i] = uLightS+i+"]";
        }
        shaderPrograms.put("color_normals", new ShaderProgram("color_normals","shaders/color_normals/vs.glsl","shaders/color_normals/fs.glsl",
                new String[]{vPositionS, vNormalS},
                uniforms,
                core));
        ///////////////////////////////////////////////////////////////////////////
        shaderPrograms.put("texture", new ShaderProgram("texture","shaders/texture/vs.glsl","shaders/texture/fs.glsl",
                new String[]{vPositionS, vTextureS},
                new String[]{uVPMatrixS, uModelMatrixS, colorS, "far", fog_colorS, uTextureS, shadowMapS, softShadowS, "bias", depthMVPS, ambientS},
                core));
        ///////////////////////////////////////////////////////////////////////////
        uniforms = new String[252+16];
        System.arraycopy(new String[]{uRotMatrixS, uVPMatrixS, uModelMatrixS, colorS, global_light_dirS, global_light_colorS, uViewPosS, ambientS, fog_colorS, specularS, "far", uTextureS, shadowMapS, softShadowS, "bias", depthMVPS},0,uniforms,0,16);
        for (int i = 0; i < 252; i++){
            uniforms[16+i] = uLightS+i+"]";
        }
        shaderPrograms.put("texture_normals", new ShaderProgram("texture_normals","shaders/texture_normals/vs.glsl","shaders/texture_normals/fs.glsl",
                new String[]{vPositionS, vNormalS, vTextureS},
                uniforms,
                core));
        ///////////////////////////////////////////////////////////////////////////
        uniforms = new String[252+17];
        System.arraycopy(new String[]{uRotMatrixS, uVPMatrixS, uModelMatrixS, colorS, global_light_dirS, global_light_colorS, uViewPosS, ambientS, fog_colorS, specularS, "far", "uNormalTexture", uTextureS, shadowMapS, softShadowS, "bias", depthMVPS},0,uniforms,0,17);
        for (int i = 0; i < 252; i++){
            uniforms[17+i] = uLightS+i+"]";
        }
        shaderPrograms.put("texture_normalMap", new ShaderProgram("texture_normalMap","shaders/texture_normalMap/vs.glsl","shaders/texture_normalMap/fs.glsl",
                new String[]{vPositionS, vNormalS, vTextureS, "vNormalTextureCoord", "vTangent"},
                uniforms,
                core));
        ///////////////////////////////////////////////////////////////////////////
        shaderPrograms.put("sky", new ShaderProgram("sky","shaders/sky/vs.glsl","shaders/sky/fs.glsl",
                new String[]{vPositionS},
                new String[]{uVPMatrixS, "skyBox"},
                core));
        ///////////////////////////////////////////////////////////////////////////
        shaderPrograms.put("UI", new ShaderProgram("UI","shaders/UI/vs.glsl","shaders/UI/fs.glsl",
                new String[]{vPositionS, vTextureS},
                new String[]{uTextureS, colorS, uModelMatrixS},
                core));
        UIModel = new UIModel(core);
        ///////////////////////////////////////////////////////////////////////////
        shaderPrograms.put(zBufferS, new ShaderProgram(zBufferS,"shaders/zBuffer/vs.glsl","shaders/zBuffer/fs.glsl",
                new String[]{vPositionS},
                new String[]{uVPMatrixS,uModelMatrixS,"far"},
                core));
        UIModel = new UIModel(core);
        ///////////////////////////////////////////////////////////////////////////
        glEnable(GL_MULTISAMPLE);
        glEnable(GL10.GL_BLEND);
        glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        glClearColor(0,0,0,1);

        core.getScene().start();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        res.x=i;
        res.y=i1;

        camera.setResolution(res);
        glViewport(0, 0, i, i1);

        core.getTouchListener().setResolution(res);
    }

    private int upd = 0;
    private long timer=System.currentTimeMillis();
    private int fps = 0;
    // получить ФПС
    public int getLastFPS(){
        return fps;
    }

    long lt = System.currentTimeMillis();
    @Override
    public void onDrawFrame(GL10 gl10) {
        if(logFPS){
            upd++;
            if(System.currentTimeMillis()-timer>1000){
                logger.info(upd+"FPS");
                fps=upd;
                upd=0;
                timer+=System.currentTimeMillis()-timer;
            }
        }

        long t = System.currentTimeMillis();
        update((float)(t-lt)/1000.f);
        lt=t;

        glClear(GL_COLOR_BUFFER_BIT);

        if(shadowCamera!=null){
            Vector2 sRes = shadowCamera.getResolution();
            glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glViewport(0, 0, (int) sRes.x, (int)sRes.y);
            drawZBuffer();
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, (int)res.x, (int)res.y);
        drawRenderObject();
    }
    // отрисовка в З буфер
    private void drawZBuffer(){
        if (models.size() != 0){
            ShaderProgram shaderProgram = shaderPrograms.get(zBufferS);
            glUseProgram(shaderProgram.shaderProgram);
            glClearDepthf(1.0f);
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LEQUAL);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            HashMap<String,Integer> u = shaderProgram.getUniforms();
            int adrMM = u.get(uModelMatrixS);
            glUniform1f(u.get("far"), shadowCamera.getFar());
            glUniformMatrix4fv(u.get(uVPMatrixS), 1, false, shadowCamera.getvPMatrix(), 0);

            HashMap<String, Integer> attributs = shaderProgram.getAttributs();

            for(int i = 0; i < models.size(); i++){
                Model model = models.get(i);
                model.setZbufferAtr(attributs);
                for (int j = 0; j < obj.get(i).size(); j++){
                    obj.get(i).get(j).drawInBuff(adrMM);
                }
            }
            for (Map.Entry<String, Integer> entry : attributs.entrySet()) {
                glDisableVertexAttribArray(entry.getValue());
            }
        }
    }
    // отрисовка объектов
    private void drawRenderObject(){
        glClearDepthf(1.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (models.size() != 0){
            for(int i = 0; i < models.size(); i++){
                Model model = models.get(i);
                ShaderProgram shaderProgram = model.shaderProgram;
                glUseProgram(shaderProgram.shaderProgram);
                model.putShaderVariables();
                for (int j = 0; j < obj.get(i).size(); j++){
                    obj.get(i).get(j).draw();
                }
                model.disableAttributs();
            }
        }
        if (UImodels.size() != 0) {
            glDisable(GL_DEPTH_TEST);
            for (int i = 0; i < UImodels.size(); i++) {
                Model model = UImodels.get(i);
                ShaderProgram shaderProgram = model.shaderProgram;
                glUseProgram(shaderProgram.shaderProgram);
                model.putShaderVariables();
                for (int j = 0; j < UIobj.get(i).size(); j++) {
                    UIobj.get(i).get(j).draw();
                }
                model.disableAttributs();
            }
            glEnable(GL_DEPTH_TEST);
        }
    }

    // обновление
    private void update(float l){
        for(int i = 0; i < updateds.size(); i++){
            updateds.get(i).update(l);
        }
    }
    // добавить обновляемый объект
    public void addUpdated(Updated updated){
        updateds.add(updated);
    }
    // удалить
    public void deleteUpdated(Updated updated){
        updateds.remove(updated);
    }

    // добавить 3Д объект
    public void addRenderObject(RenderObject renderObject){
        int im = models.lastIndexOf(renderObject.getModel());
        if(im != -1){
            obj.get(im).add(renderObject);
            return;
        }
        models.add(renderObject.getModel());
        obj.add(new ArrayList<RenderObject>());
        obj.get(obj.size()-1).add(renderObject);
    }
    // удалить
    public void deleteRenderObject(RenderObject renderObject){
        int im = models.lastIndexOf(renderObject.getModel());
        obj.get(im).remove(renderObject);
        if(obj.get(im).size()==0){
            obj.remove(im);
            models.remove(im);
        }
    }

    // добавить 2Д
    public void addUI(RenderObject renderObject){

        int im = UImodels.lastIndexOf(renderObject.getModel());
        if(im != -1){
            UIobj.get(im).add(renderObject);
            return;
        }
        UImodels.add(renderObject.getModel());
        UIobj.add(new ArrayList<RenderObject>());
        UIobj.get(UIobj.size()-1).add(renderObject);
    }
    // удалить
    public void deleteUI(RenderObject renderObject){
        int im = UImodels.lastIndexOf(renderObject.getModel());
        UIobj.get(im).remove(renderObject);
        if(UIobj.get(im).size()==0){
            UIobj.remove(im);
            UImodels.remove(im);
        }
    }

    // добавить свет
    public void addLigth(Light light){
        lights.add(light);
        sortLigth();
    }
    // удадить
    public void deleteLigth(Light light){
        lights.remove(light);
    }
    // получить
    public Light getLight(int i){
        return lights.get(i);
    }
    // получить количество света
    public int getLightsArraySize(){
        return lights.size();
    }
    // сортировка
    public void sortLigth(){
        Vector3 camera_position = camera.getPosition();
        Collections.sort(lights, new Comparator<Light>(){
            public int compare(Light l, Light l1){
                float d = Vector.sub(l.getPosition(), camera_position).length();
                float d1 = Vector.sub(l1.getPosition(), camera_position).length();
                if(d == d1)
                    return 0;
                return d < d1 ? -1 : 1;
            }
        });
    }

    // очистка
    public void clear(){
        models.clear();
        UImodels.clear();
        obj.clear();
        UIobj.clear();
        updateds.clear();
        lights.clear();
    }

    // получить номер текстуры
    public int getTexture(String key){
        return textures.get(key);
    }
    // удалить
    public void deleteTexture(String key) {
        deleteTextures.add(textures.get(key));
        textures.remove(key);
    }
    // загрузить
    public void loadTexture(String src, String key){
        AssetManager assetManager = core.getAssets();

        InputStream istr;

        Bitmap bitmap;
        try {
            istr = assetManager.open(src);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {

            logger.log(Level.WARNING,"не удалось загрузить bitmap: "+src, e);
            return;
        }

        if(deleteTextures.size()!=0){
            glActiveTexture(GL_TEXTURE0+deleteTextures.get(0));
        }else {
            glActiveTexture(GL_TEXTURE0+textures.size());
        }


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

        if(deleteTextures.size()!=0){
            this.textures.put(key, deleteTextures.get(0));
            deleteTextures.remove(0);
        }else {
            this.textures.put(key, this.textures.size());
        }

    }

    // загрузить Cubemap (небо)
    public void loadCubemap(String[] src, String key){
        if(deleteTextures.size()!=0){
            glActiveTexture(GL_TEXTURE0+deleteTextures.get(0));
        }else {
            glActiveTexture(GL_TEXTURE0+textures.size());
        }

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
                logger.log(Level.WARNING, "не удалось загрузить bitmap: "+i+" "+src[i], e);
                continue;
            }

            texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, bitmap, 0);

            bitmap.recycle();
        }
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);

        if(deleteTextures.size()!=0){
            this.textures.put(key, deleteTextures.get(0));
            deleteTextures.remove(0);
        }else {
            this.textures.put(key, this.textures.size());
        }
    }

    // добавить тени
    public void addShadow(Vector2 res, Camera shadowCamera){
        if(fbo!=-1){
            logger.log(Level.WARNING, "fbo уже есть");
            return;
        }
        this.shadowCamera=shadowCamera;
        fbo = GLUtil.createFrameBuffer((int)res.x, (int)res.y, textures.size() )[1];
        textures.put(zBufferS, textures.size());
    }
    // установка камеры теней
    public void setShadowCamera(Camera shadowCamera){
        if(fbo==-1){
            logger.log(Level.WARNING, "нет fbo");
            return;
        }
        this.shadowCamera=shadowCamera;
    }
    // получить камеру теней
    public Camera getShadowCamera(){
        return shadowCamera;
    }
    // получить номер текстуры З буфера
    public int getFBO(){
        return fbo;
    }
}
