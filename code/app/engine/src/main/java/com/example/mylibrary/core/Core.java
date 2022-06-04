package com.example.mylibrary.core;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylibrary.audio.AudioLoader;
import com.example.mylibrary.event.TouchListener;
import com.example.mylibrary.model.ModelLoader;
import com.example.mylibrary.physics.Physics;
import com.example.mylibrary.render.Renderer;

public class Core extends AppCompatActivity {

    private Scene scene;

    private Renderer renderer;
    private final ModelLoader modelLoader = new ModelLoader(this);
    private final Physics physics = new Physics(this);
    private TouchListener touchListener;
    private AudioLoader audioLoader;

    public Renderer getRenderer(){return renderer;}
    public ModelLoader getModelLoader(){return modelLoader;}
    public Physics getPhysics(){return physics;}
    public TouchListener getTouchListener(){return touchListener;}
    public AudioLoader getAudioLoader(){return audioLoader;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        scene = getScene();
        renderer = new Renderer(this);
        audioLoader = new AudioLoader(this);
        touchListener = new TouchListener(renderer);

        setContentView(renderer);
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
        scene.resume();
    }
    public void onPause(){
        super.onPause();
        scene.pause();
    }

    public Scene getScene(){
        return scene;
    }

    public void setScene(Scene scene){
        renderer.allClear();
        this.scene = scene;
        scene.start();
    }

}
