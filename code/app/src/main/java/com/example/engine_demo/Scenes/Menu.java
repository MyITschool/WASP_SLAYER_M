package com.example.engine_demo.Scenes;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.event.TouchListener;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector2;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;
import com.example.mylibrary.model.ModelLoader;
import com.example.mylibrary.model.VertexesData;
import com.example.mylibrary.physics.CubeCollider;
import com.example.mylibrary.physics.RigidBody;
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

    RenderObject renderObject;

    RebdererText rebdererText;

    RigidBody rigidBody23;
    @Override
    public void start() {
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
        renderer.ambient = 0.01f;
        renderer.global_light_color = new Vector3(1f);

        VertexesData vertexesData = modelLoader.loadModel("models/cube.obj", "cube");
        vertexesData.vertexes_texture=null;
        VertexesData vertexesData1 = modelLoader.loadModel("models/plane.obj", "plane");

        Model model = new Model(vertexesData, core);

        renderObject = new RenderObject(model);
        renderObject.setSize(new Vector3(1f));
        renderObject.setPosition(new Vector3(0,10,0));
        renderer.addRenderObject(renderObject);


        RenderObject renderObject3 = new RenderObject(model);
        renderObject3.setSize(new Vector3(1f));
        renderObject3.setPosition(new Vector3(0,5,0));
        renderer.addRenderObject(renderObject3);
        rigidBody23 = new RigidBody(new CubeCollider(new Vector3(0,5,0), new Vector3(1)), renderObject3, core);
        rigidBody23.activity=false;


        Model model1 = new Model(vertexesData1, core);
        RenderObject renderObject1 = new RenderObject(model1,"floor");
        renderObject1.setSize(new Vector3(5));
        renderObject1.setRotate(new Vector3(0,0,0));
        renderer.addRenderObject(renderObject1);
        RigidBody rigidBody1 = new RigidBody(new CubeCollider(new Vector3(0,0,0), new Vector3(5,0.1f,5)), renderObject1, core);
        rigidBody1.activity=false;

        rebdererText = new RebdererText("font", "00", core);
        rebdererText.setSize(new Vector3(0.1f,0.2f,1));
        rebdererText.setPosition(new Vector3(-0.8f,0.8f,0));
        renderer.addRenderObject(rebdererText);


        renderer.addUpdated(this);

        //renderer.camera.rotateModeView = false;
        renderer.camera.setPosition(new Vector3(0,4f,15));
        renderer.camera.setRotate(new Vector3(0,0,0));
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }
    RigidBody rigidBody;
    TouchListener touchListener = core.getTouchListener();
    float t = 0;
    @Override
    public void update(float dt) {

        if(t==0&&touchListener.getTouchDown(new Vector2(0), new Vector2(1), new Vector2(0))){
            rigidBody = new RigidBody(new CubeCollider(new Vector3(0,10,0), new Vector3(0.9f)), renderObject, core);
            rigidBody23.activity=true;
            t=1;
        }
        if(t==1&&touchListener.getTouchDown(new Vector2(0), new Vector2(1), new Vector2(0))){
           rigidBody.addVelocity(new Vector3(0,10*dt,0));
        }

        rebdererText.text = renderer.getLastFPS()+"";
    }
}
