package com.example.engine_demo.Scenes;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;
import com.example.mylibrary.render.Light;
import com.example.mylibrary.render.RenderObject;
import com.example.mylibrary.render.Renderer;

public final class Menu extends Scene implements Updated {
    public Menu(Core core) {
        super(core);
    }

    Renderer renderer = core.getRenderer();

    @Override
    public void preload() {
        renderer.loadTexture("icon.png", "test");
    }

    @Override
    public void start() {
        renderer.camera.setPosition(new Vector3(0,0,6));
        renderer.camera.setRotate(new Vector3(0,180,0));
        Model model = new Model(new float[]{
                0.0f,  0.622008459f, 0.0f,
                -0.5f, -0.311004243f, 0.0f,
                0.5f, -0.311004243f, 0.0f }, new float[]{
                0.0f,  0.622008459f,
                -0.5f, -0.311004243f,
                0.5f, -0.311004243f
        }, new float[]{
                0.0f,  0.622008459f,
                -0.5f, -0.311004243f,
                0.5f, -0.311004243f,
        }, core);
        RenderObject renderObject = new RenderObject(model, renderer.getTexture("test"));
        renderObject.setSize(new Vector3(0.5f));
        renderer.addRenderObject(renderObject);
        renderer.ambient = 0.01f;
        renderer.global_light_color = new Vector3(1);

        renderer.addLigth(new Light(new Vector3(0,0,-2), new Vector3(1), 1));
//        renderer.addLigth(new Light(new Vector3(6), new Vector3(1), 1));

        renderer.addUpdated(this);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

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

    float t = 0;
    @Override
    public void update(float dt) {
//        t+=0.1f;
//        renderer.global_light_dir.y = (float) Math.cos(t);
//        renderer.global_light_dir.z = (float) Math.sin(t);

    }
}
