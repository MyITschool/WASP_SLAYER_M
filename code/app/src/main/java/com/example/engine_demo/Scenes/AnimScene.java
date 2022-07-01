package com.example.engine_demo.Scenes;

import com.example.mylibrary.animation.Animation;
import com.example.mylibrary.animation.Key;
import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.event.TouchListener;
import com.example.mylibrary.math.Vector2;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;
import com.example.mylibrary.model.ModelLoader;
import com.example.mylibrary.model.UIModel;
import com.example.mylibrary.model.VertexesData;
import com.example.mylibrary.render.RebdererText;
import com.example.mylibrary.render.RenderObject;
import com.example.mylibrary.render.Renderer;

public final class AnimScene extends Scene implements Updated {
    public AnimScene(Core core) {
        super(core);
    }
    private final Renderer renderer = core.getRenderer();
    private final ModelLoader modelLoader = core.getModelLoader();
    private final TouchListener touchListener = core.getTouchListener();

    private RebdererText fpsRT;
    @Override
    public void start() {
        renderer.ambient = 0.05f;
        renderer.global_light_color = new Vector3(1f);
        renderer.global_light_dir = new Vector3(-1,1,1);
        renderer.global_light_dir.norm();
        renderer.camera.setFar(100);

        VertexesData vertexesDataCube = modelLoader.getVertexesData("cube");

        Model modelPlaneColorNormal = new Model(vertexesDataCube.vertexes, vertexesDataCube.vertexes, core);
        RenderObject cubeColorNormalRO = new RenderObject(modelPlaneColorNormal);
        renderer.addRenderObject(cubeColorNormalRO);
        RenderObject ROb = cubeColorNormalRO;

        // массив ключей анимации
        Key[] animKeys = new Key[]{
                new Key(new Vector3(2), new Vector3(360,360,360), new Vector3(2), new Vector4(-0.4f,-0.1f,-0.8f,0), 1),
                new Key(new Vector3(-2), new Vector3(-360,-360,-360), new Vector3(-2), new Vector4(0.4f,0.1f,0.8f,0), 1)
        };
        // создание анимации
        Animation animation = new Animation(animKeys, ROb, renderer);
        animation.loop = true;
        // включение
        animation.play();


        fpsRT = new RebdererText("font", "00", new UIModel(core));
        fpsRT.setScale(new Vector3(0.2f,0.2f,1));
        fpsRT.setPosition(new Vector3(-0.8f,0.8f,0));
        renderer.addUI(fpsRT);

        renderer.camera.rotateModeView = false;
        renderer.camera.setPosition(new Vector3(0,0f,14));
        renderer.camera.setRotate(new Vector3(0,0,0));

        renderer.addUpdated(this);
    }

    @Override
    public void resume() {
        // TODO не нужен
    }

    @Override
    public void pause() {
        // TODO не нужен
    }
    float cd = 1.5f;
    @Override
    public void update(float dt) {
        cd-=dt;
        if(touchListener.getTouchDown(new Vector2(0), new Vector2(1), new Vector2(0)) && cd <= 0){
            core.setScene(new PhysicsTest(core));
        }
        int fps = renderer.getLastFPS();
        String fpsS;
        if(fps<10){
            fpsS = "fps: 0"+fps;
        }else {
            fpsS="fps: "+fps;
        }
        fpsRT.text = fpsS;
    }
}
