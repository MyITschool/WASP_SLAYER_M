package com.example.engine.core;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engine.audio.AudioLoader;
import com.example.engine.event.TouchListener;
import com.example.engine.math.Func;
import com.example.engine.model.ModelLoader;
import com.example.engine.physics.Physics;
import com.example.engine.render.RendererGL;

public class Core  extends AppCompatActivity {

    private Loop loop;
    private Scene scene;


    private final Config config = new Config(this);
    private final RendererGL renderer = new RendererGL(this);
    private final ModelLoader modelLoader = new ModelLoader(this);
    private TouchListener touchListener;
    private final Physics physics = new Physics(this);
    private final Func func = new Func(this);
    private AudioLoader audioLoader;

    public Config getConfig(){
        return config;
    }
    public RendererGL getRenderer(){
        return renderer;
    }
    public ModelLoader getModelLoader(){
        return modelLoader;
    }
    public TouchListener getTouchListener(){
        return touchListener;
    }
    public Loop getLoop(){
        return loop;
    }
    public Physics getPhysics() { return physics; }
    public Func getFunc() { return func; }
    public AudioLoader getAudioLoader(){ return audioLoader; }

    private boolean startScene = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        // w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        audioLoader = new AudioLoader(this);

        loop = new Loop(this);
        touchListener = new TouchListener(loop);

        scene = getStartScene();

        setContentView(loop);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void onResume(){
        super.onResume();
        loop.startGame();
        if(!startScene){
           // scene.start();
            startScene=true;
        }else {
            scene.resume();
        }
    }
    public void onPause(){
        super.onPause();
        scene.pause();
        loop.stopGame();
        if (isFinishing()){
            scene.dispose();
        }
    }

    public void setScene(Scene scene, boolean clear_last_last_i){
        if (scene==null){
            throw new IllegalArgumentException("не возможно загрузить сцену");
        }

        //this.scene.dispose();
       // loop.stopGame();
        this.scene.pause();

        loop.clear();
        physics.clear();
        renderer.clear();

        if(clear_last_last_i){
            renderer.clear_i();
        }

        this.scene=scene;
        renderer.sns=true;
       // this.scene.start();
        //renderer.set_scene();
    }

    public Scene getScene(){
        return scene;
    }
    protected Scene getStartScene(){
        return scene;
    }
}
