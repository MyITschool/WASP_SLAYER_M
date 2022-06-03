package com.example.mylibrary.render;

import static android.opengl.GLES10.GL_MULTISAMPLE;
import static android.opengl.GLES10.glClearColor;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
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
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearDepthf;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLUtils.texImage2D;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector2Int;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;
import com.example.mylibrary.model.UIModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public final class Renderer extends GLSurfaceView implements GLSurfaceView.Renderer {

    private final Core core;

    private final HashMap<String, ShaderProgram> shaderPrograms = new HashMap<>();
    private final HashMap<String, Integer> textures = new HashMap<>();

    private final ArrayList<Model> models = new ArrayList<>();
    private final ArrayList<ArrayList<RenderObject>> obj = new ArrayList<>();
    private final ArrayList<Updated> updateds = new ArrayList<>();
    private final ArrayList<Light> lights = new ArrayList<>();

    public Camera camera;
    public Model UIModel;

    public Vector4 fog_color = new Vector4(1);
    public float ambient = 0;
    public Vector3 global_light_dir = new Vector3(0,1,0);
    public Vector3 global_light_color = new Vector3(1);

    public ShaderProgram getShaderProgram(String key){
        return shaderPrograms.get(key);
    }
    public void deleteShaderProgram(String key){
        shaderPrograms.remove(key);
    }

    private Vector2Int res = new Vector2Int(1);

    public Renderer(Core core){
        super(core);
        this.core = core;
        camera = new Camera(core);

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);

        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        shaderPrograms.put("color", new ShaderProgram("color","shaders/color/vs.glsl","shaders/color/fs.glsl",
                new String[]{"vPosition"},
                new String[]{"uVPMatrix", "uModelMatrix", "color", "far", "fog_color"},
                core));
        //////////////////////////////////////////////////////////////////////////
        String[] uniforms = new String[252+10];
        System.arraycopy(new String[]{"uVPMatrix", "uModelMatrix", "color", "global_light_dir", "global_light_color", "uViewPos", "ambient", "fog_color", "specular", "far"},0,uniforms,0,10);

        for (int i = 0; i < 252; i++){
            uniforms[10+i] = "uLight["+i+"]";
        }
        shaderPrograms.put("color_normals", new ShaderProgram("color_normals","shaders/color_normals/vs.glsl","shaders/color_normals/fs.glsl",
                new String[]{"vPosition", "vNormal"},
                uniforms,
                core));
        ///////////////////////////////////////////////////////////////////////////
        shaderPrograms.put("texture", new ShaderProgram("texture","shaders/texture/vs.glsl","shaders/texture/fs.glsl",
                new String[]{"vPosition", "vTexture"},
                new String[]{"uVPMatrix", "uModelMatrix", "color", "far", "fog_color", "uTexture"},
                core));
        ///////////////////////////////////////////////////////////////////////////
        uniforms = new String[252+11];
        System.arraycopy(new String[]{"uVPMatrix", "uModelMatrix", "color", "global_light_dir", "global_light_color", "uViewPos", "ambient", "fog_color", "specular", "far", "uTexture"},0,uniforms,0,11);
        for (int i = 0; i < 252; i++){
            uniforms[11+i] = "uLight["+i+"]";
        }
        shaderPrograms.put("texture_normals", new ShaderProgram("texture_normals","shaders/texture_normals/vs.glsl","shaders/texture_normals/fs.glsl",
                new String[]{"vPosition", "vNormal", "vTexture"},
                uniforms,
                core));
        ///////////////////////////////////////////////////////////////////////////
        uniforms = new String[252+12];
        System.arraycopy(new String[]{"uVPMatrix", "uModelMatrix", "color", "global_light_dir", "global_light_color", "uViewPos", "ambient", "fog_color", "specular", "far", "uNormalTexture", "uTexture"},0,uniforms,0,12);
        for (int i = 0; i < 252; i++){
            uniforms[12+i] = "uLight["+i+"]";
        }
        shaderPrograms.put("texture_normalMap", new ShaderProgram("texture_normalMap","shaders/texture_normalMap/vs.glsl","shaders/texture_normalMap/fs.glsl",
                new String[]{"vPosition", "vNormal", "vTexture", "vNormalTextureCoord", "vTangent"},
                uniforms,
                core));
        ///////////////////////////////////////////////////////////////////////////
        shaderPrograms.put("sky", new ShaderProgram("sky","shaders/sky/vs.glsl","shaders/sky/fs.glsl",
                new String[]{"vPosition"},
                new String[]{"uVPMatrix", "skyBox"},
                core));
        ///////////////////////////////////////////////////////////////////////////
        shaderPrograms.put("UI", new ShaderProgram("UI","shaders/UI/vs.glsl","shaders/UI/fs.glsl",
                new String[]{"vPosition", "vTexture"},
                new String[]{"uTexture", "color", "uModelMatrix"},
                core));
        UIModel = new UIModel(core);
        ///////////////////////////////////////////////////////////////////////////
        glEnable(GL_MULTISAMPLE);
        glEnable(GL10.GL_BLEND);
        glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        glClearColor(0,0,0,1);


        core.getScene().preload();
        core.getScene().start();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        res.x=i;
        res.y=i1;

        camera.setResolution(res);
        glViewport(0, 0, res.x, res.y);
    }

    private int upd = 0;
    private long timer=System.currentTimeMillis();
    private int fps = 0;
    public int getLastFPS(){
        return fps;
    }

    long lt = System.currentTimeMillis();
    @Override
    public void onDrawFrame(GL10 gl10) {
        upd++;
        if(System.currentTimeMillis()-timer>1000){
            System.out.println("Render: "+upd+"FPS");
            fps=upd;
            upd=0;
            timer+=System.currentTimeMillis()-timer;
        }
        long t = System.currentTimeMillis();
        update((float)(t-lt)/1000.f);
        lt=t;

        glClear(GL_COLOR_BUFFER_BIT);
        drawRenderObject();
    }
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
               // if(shaderProgram.name == "UI")glDisable(GL_DEPTH_TEST);
                for (int j = 0; j < obj.get(i).size(); j++){
                    obj.get(i).get(j).draw();
                }
                model.disableAttributs();
               // if(shaderProgram.name == "UI")glEnable(GL_DEPTH_TEST);
            }
        }
    }
    private void update(float l){
        for(int i = 0; i < updateds.size(); i++){
            updateds.get(i).update(l);
        }
    }
    public void addUpdated(Updated updated){
        updateds.add(updated);
    }
    public void deleteUpdated(Updated updated){
        updateds.remove(updated);
    }


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
    public void deleteRenderObject(RenderObject renderObject){
        int im = models.lastIndexOf(renderObject.getModel());
        obj.get(im).remove(renderObject);
        if(obj.get(im).size()==0){
            obj.remove(im);
            models.remove(im);
        }
    }

    public void addLigth(Light light){
        lights.add(light);
        sortLigth();
    }
    public void deleteLigth(Light light){
        lights.remove(light);
    }
    public Light getLight(int i){
        return lights.get(i);
    }
    public int getLightsArraySize(){
        return lights.size();
    }
    public void sortLigth(){
        Vector3 camera_position = camera.getPosition();
        Collections.sort(lights, new Comparator<Light>(){
            public int compare(Light l, Light l1){
                float d = Vector.sub(l.position, camera_position).length();
                float d1 = Vector.sub(l1.position, camera_position).length();
                if(d == d1)
                    return 0;
                return d < d1 ? -1 : 1;
            }
        });
    }

    public void allClear(){
        models.clear();
        obj.clear();
        updateds.clear();
        lights.clear();
    }

    public int getTexture(String key){
        return textures.get(key);
    }
    public void loadTexture(String src, String key){
        AssetManager assetManager = core.getAssets();

        InputStream istr;

        Bitmap bitmap;
        try {
            istr = assetManager.open(src);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("не удалось загрузить bitmap: "+src);
            return;
        }

        glActiveTexture(GL_TEXTURE0+textures.size());

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

        this.textures.put(key, this.textures.size());
    }
    public void loadCubemap(String[] src, String key){
        glActiveTexture(GL_TEXTURE0+textures.size());

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
                System.out.println(e.getMessage());
                System.out.println("не удалось загрузить bitmap: "+i+" "+src[i]);
                continue;
            }

            texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, bitmap, 0);

            bitmap.recycle();
        }
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);

        this.textures.put(key, this.textures.size());
    }
}
