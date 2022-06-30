package com.example.engine_demo.Scenes;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;
import com.example.mylibrary.model.ModelLoader;
import com.example.mylibrary.render.Renderer;

public final class Loading extends Scene {
    public Loading(Core core) {
        super(core);
    }

    Renderer renderer = core.getRenderer();
    ModelLoader modelLoader = core.getModelLoader();

    @Override
    public void start() {
        // загрузка текстур
        renderer.loadTexture("textures/tests/floor.png", "floor");
        renderer.loadTexture("textures/tests/floor_normalmap.png", "floor_normalmap");
        renderer.loadTexture("textures/tests/wall.png", "wall");
        renderer.loadTexture("textures/tests/NormalMapWall.png", "wall_normalmap");
        renderer.loadTexture("textures/font.png", "font");
        // загрузка неба
        renderer.loadCubemap(new String[]{
                "textures/tests/skybox/r.png",
                "textures/tests/skybox/l.png",
                "textures/tests/skybox/d.png",
                "textures/tests/skybox/u.png",
                "textures/tests/skybox/f.png",
                "textures/tests/skybox/b.png"
        }, "sky");
        // загрузка моделей
        modelLoader.loadModel("models/cube.obj", "cube");
        modelLoader.loadModel("models/sphere.obj", "sphere");
        modelLoader.loadModel("models/plane.obj", "plane");
        // загрузка звука
        core.getAudioLoader().addAudio("audio/bg_m.mp3", "bg");

        core.setScene(new GraphicsTest(core));
    }

    @Override
    public void resume() {
        // TODO не нужен
    }

    @Override
    public void pause() {
        // TODO не нужен
    }
}
