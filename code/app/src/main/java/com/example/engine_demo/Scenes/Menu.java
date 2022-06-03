package com.example.engine_demo.Scenes;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;
import com.example.mylibrary.model.ModelLoader;
import com.example.mylibrary.model.VertexesData;
import com.example.mylibrary.render.Light;
import com.example.mylibrary.render.RebdererText;
import com.example.mylibrary.render.RenderImg;
import com.example.mylibrary.render.RenderObject;
import com.example.mylibrary.render.Renderer;

public final class Menu extends Scene implements Updated{
    public Menu(Core core) {
        super(core);
    }

    Renderer renderer = core.getRenderer();
    ModelLoader modelLoader = core.getModelLoader();

    @Override
    public void preload() {
        renderer.loadTexture("textures/floor.png", "floor");
        renderer.loadTexture("textures/floor_normalmap.png", "floor_normalmap");
        renderer.loadTexture("textures/ui.png", "ui");
        renderer.loadTexture("textures/font.png", "font");

        renderer.loadCubemap(new String[]{
                "textures/skybox/r.png",
                "textures/skybox/l.png",
                "textures/skybox/d.png",
                "textures/skybox/u.png",
                "textures/skybox/f.png",
                "textures/skybox/b.png"
        }, "sky");
    }

    RenderObject renderObject;

    RebdererText rebdererText;

    @Override
    public void start() {
        VertexesData vertexesData = modelLoader.loadModel("models/cube.obj", "cube");
        vertexesData.vertexes_normalTexture = vertexesData.vertexes_texture;
        Model model = new Model(vertexesData, core);

        renderObject = new RenderObject(model,"floor","floor_normalmap");
        renderObject.setSize(new Vector3(1f));
        renderer.addRenderObject(renderObject);

        RenderObject renderObjec1t = new RenderObject(model,"floor","floor_normalmap");
        renderObjec1t.setSize(new Vector3(1f));
        renderObjec1t.setPosition(new Vector3(3,0,0));
        renderer.addRenderObject(renderObjec1t);


        renderer.ambient = 0.01f;
        renderer.global_light_color = new Vector3(1f);

        renderer.addLigth(new Light(new Vector3(0,0,-2), new Vector3(1), 1));

        RenderImg ui = new RenderImg("ui", core);
        ui.setSize(new Vector3(0.3f));

        renderer.addRenderObject(ui);

        rebdererText = new RebdererText("font", "00", core);
        rebdererText.setSize(new Vector3(0.3f,0.6f,1));
        renderer.addRenderObject(rebdererText);

        Model model1 = new Model("sky", core);
        RenderObject renderObject1 = new RenderObject(model1);
        renderer.addRenderObject(renderObject1);

        renderer.addUpdated(this);

       // renderer.camera.rotateModeView = false;
        renderer.camera.setPosition(new Vector3(0,-2,-15));
        renderer.camera.setRotate(new Vector3(0,0,0));
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    float t = 0;
    @Override
    public void update(float dr) {
//        t+=0.01f;
//        renderer.global_light_dir.x = (float) Math.sin(t);
//        renderer.global_light_dir.y = (float) Math.cos(t);
//        renderObject.setRotate(Vector.add(renderObject.getRotate(), new Vector3(0.1f,0,0)));
//
        renderer.camera.setRotate(Vector.add(renderer.camera.getRotate(), new Vector3(0,0.1f,0)));

        rebdererText.text = renderer.getLastFPS()+"";
    }
}
