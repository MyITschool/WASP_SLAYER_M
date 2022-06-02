package com.example.mylibrary.render;

import static android.opengl.GLES10.GL_MULTISAMPLE;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearDepthf;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;

import android.opengl.GLSurfaceView;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector2Int;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public final class Renderer extends GLSurfaceView implements GLSurfaceView.Renderer {

    private final Core core;

    public final HashMap<String, ShaderProgram> shaderPrograms = new HashMap<>();

    private final ArrayList<Model> models = new ArrayList<>();
    private final ArrayList<ArrayList<RenderObject>> obj = new ArrayList<>();
    private final ArrayList<Updated> updateds = new ArrayList<>();

    public final ArrayList<Light> lights = new ArrayList<>();

    public Camera camera;

    public Vector4 fog_color = new Vector4(1);
    public float ambient = 0;
    public Vector3 global_light_dir = new Vector3(0,1,0);
    public Vector3 global_light_color = new Vector3(1);

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
        String[] uniforms = new String[252+10];
        System.arraycopy(new String[]{"uVPMatrix", "uModelMatrix", "color", "global_light_dir", "global_light_color", "uViewPos", "ambient", "fog_color", "specular", "far"},0,uniforms,0,10);
        for (int i = 0; i < 252; i++){
            uniforms[10+i] = "uLight["+i+"]";
        }
        shaderPrograms.put("color_normals", new ShaderProgram("color_normals","shaders/color_normals/vs.glsl","shaders/color_normals/fs.glsl",
                new String[]{"vPosition", "vNormal"},
                uniforms,
                core));

        glEnable(GL_MULTISAMPLE);
        glEnable(GL10.GL_BLEND);
        glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        core.getScene().preload();
        core.getScene().start();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        camera.setResolution(new Vector2Int(i, i1));
        glViewport(0, 0, i, i1);
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
                glUseProgram(model.shaderProgram.shaderProgram);
                model.setBuffers();
                model.setGeneralUniforms();
                for (int j = 0; j < obj.get(i).size(); j++){
                    obj.get(i).get(j).draw();
                }
                model.disableAttributs();
            }
        }
    }

    private void update(float l){
        for(int i = 0; i < updateds.size(); i++){
            updateds.get(i).update(l);
        }
    }

    public void addRenderObject(RenderObject renderObject){

        int im = models.lastIndexOf(renderObject.model);
        if(im != -1){
            obj.get(im).add(renderObject);
            return;
        }
        models.add(renderObject.model);
        obj.add(new ArrayList<RenderObject>());
        obj.get(obj.size()-1).add(renderObject);
    }

    public void deleteRenderObject(RenderObject renderObject){
        int im = models.lastIndexOf(renderObject.model);
        obj.get(im).remove(renderObject);
        if(obj.get(im).size()==0){
            obj.remove(im);
            models.remove(im);
        }
    }

    public void addUpdated(Updated updated){
        updateds.add(updated);
    }

    public void deleteUpdated(Updated updated){
        updateds.remove(updated);
    }

    public void addLigth(Light light){
        lights.add(light);
        sortLigth();
    }
    public void deleteLigth(Light light){
        lights.remove(light);
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


}