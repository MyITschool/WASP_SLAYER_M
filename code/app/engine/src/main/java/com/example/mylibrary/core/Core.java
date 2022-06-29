package com.example.mylibrary.core;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylibrary.audio.AudioLoader;
import com.example.mylibrary.event.TouchListener;
import com.example.mylibrary.model.ModelLoader;
import com.example.mylibrary.physics.physics3D.Physics;
import com.example.mylibrary.render.Renderer;

public class Core extends AppCompatActivity {

    // активная сцена
    private Scene scene;
    // отрисовщик
    private Renderer renderer;
    // загрузчик 3Д моделий
    private final ModelLoader modelLoader = new ModelLoader(this);
    // физика
    private final Physics physics = new Physics(renderer);
    // слушатель нажатий
    private TouchListener touchListener;
    // загрузчик аудио
    private AudioLoader audioLoader;

    // получить отрисовщик
    public Renderer getRenderer(){return renderer;}
    // получить загрузчик 3Д моделей
    public ModelLoader getModelLoader(){return modelLoader;}
    // получить физику
    public Physics getPhysics(){return physics;}
    // получить слушатель нажатий
    public TouchListener getTouchListener(){return touchListener;}
    // получить загрузчик аудио
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

    @Override
    public void onResume(){
        super.onResume();
        scene.resume();
    }
    @Override
    public void onPause(){
        super.onPause();
        scene.pause();
    }

    // получить активную сцену
    public Scene getScene(){
        return scene;
    }

    // установить сцену
    public void setScene(Scene scene){
        renderer.clear();
        physics.clear();
        this.scene = scene;
        scene.start();
    }

}
