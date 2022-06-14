package com.example.engine_demo.Scenes;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.event.TouchListener;
import com.example.mylibrary.math.Vector2;
import com.example.mylibrary.math.Vector2Int;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;
import com.example.mylibrary.model.ModelLoader;
import com.example.mylibrary.model.VertexesData;
import com.example.mylibrary.physics.physics3D.CubeCollider;
import com.example.mylibrary.physics.physics3D.RigidBody;
import com.example.mylibrary.render.Camera;
import com.example.mylibrary.render.RebdererText;
import com.example.mylibrary.render.RenderObject;
import com.example.mylibrary.render.Renderer;

public final class Tests3D extends Scene implements Updated{
    public Tests3D(Core core) {
        super(core);
    }

    Renderer renderer = core.getRenderer();
    ModelLoader modelLoader = core.getModelLoader();

    RebdererText rebdererText;

    RigidBody rigidBody23;
    @Override
    public void start() {
        renderer.ambient = 0.01f;
        renderer.global_light_color = new Vector3(1f);
        renderer.global_light_dir = new Vector3(-1,1,1);
        renderer.global_light_dir.norm();

        renderer.camera.setFar(100);

        VertexesData vertexesData = modelLoader.getVertexesData("cube");
        vertexesData.vertexes_texture=null;
        vertexesData.vertexes_normal=vertexesData.vertexes;
        VertexesData vertexesData2 = modelLoader.getVertexesData("sphere");
        vertexesData2.vertexes_texture=null;
        vertexesData2.vertexes_normal=vertexesData2.vertexes;

        VertexesData vertexesData1 = modelLoader.getVertexesData("plane");
        vertexesData1.vertexes_normalTexture=vertexesData1.vertexes_texture;

        Model modelS = new Model(vertexesData2, core);

        RenderObject renderObject = new RenderObject(modelS);
        renderObject.setScale(new Vector3(1f));
        renderObject.setPosition(new Vector3(0,10,0));
        renderer.addRenderObject(renderObject);
        renderObject.color=new Vector4(0.1f,0.1f,0.1f,1);
        rigidBody = new RigidBody(new CubeCollider(new Vector3(0,10,0), new Vector3(0.9f)), renderObject, core);
        rigidBody.elasticity=0.7f;
        rigidBody.activity=false;

        Model model = new Model(vertexesData, core);
        RenderObject renderObject3 = new RenderObject(model);
        renderObject3.setScale(new Vector3(1f));
        renderObject3.setPosition(new Vector3(0,5,0));
        renderObject3.color=new Vector4(0,0,1,1);
        renderer.addRenderObject(renderObject3);
        rigidBody23 = new RigidBody(new CubeCollider(new Vector3(0,5,0), new Vector3(1)), renderObject3, core);
        rigidBody23.elasticity=0.1f;
        rigidBody23.activity=false;


        Model model1 = new Model(vertexesData1, core);
        RenderObject renderObject1 = new RenderObject(model1,"floor", "floor_normalmap");
        renderObject1.setScale(new Vector3(10));
        renderObject1.setPosition(new Vector3(0));
        renderer.addRenderObject(renderObject1);
        RigidBody rigidBody1 = new RigidBody(new CubeCollider(new Vector3(0), new Vector3(10,0.1f,10)), renderObject1, core);
        rigidBody1.activity=false;

        rebdererText = new RebdererText("font", "00", core);
        rebdererText.setScale(new Vector3(0.1f,0.2f,1));
        rebdererText.setPosition(new Vector3(-0.8f,0.8f,0));
        renderer.addUI(rebdererText);


        Model skyM = new Model("sky", core);
        RenderObject skyRO = new RenderObject(skyM);
        renderer.addRenderObject(skyRO);

        renderer.camera.rotateModeView = false;
        renderer.camera.setPosition(new Vector3(0,4f,15));
        renderer.camera.setRotate(new Vector3(0,-45,0));


        renderer.softShadow=5;
        Camera shadowCamera = new Camera(core);
        shadowCamera.rotateModeView = false;
        Vector2Int res = new Vector2Int(800*2,600*2);
        shadowCamera.setResolution(res);
        shadowCamera.setPosition(new Vector3(0,8f,35));
        shadowCamera.setRotate(new Vector3(15,45,0));
        renderer.addShadow(res, shadowCamera);


        renderer.addUpdated(this);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }
    RigidBody rigidBody;
    TouchListener touchListener = core.getTouchListener();
    byte t = 0;
    @Override
    public void update(float dt) {

        if(t==0&&touchListener.getTouchDown(new Vector2(0), new Vector2(1), new Vector2(0))){
            rigidBody23.activity=true;
            rigidBody.activity=true;
            t=1;
        }
        if(t==1&&touchListener.getTouchDown(new Vector2(0), new Vector2(1), new Vector2(0))){
           rigidBody.addVelocity(new Vector3(0,30*dt,0));
        }

        int fps = renderer.getLastFPS();
        String fpsS;
        if(fps<10){
            fpsS = "0"+fps;
        }else {
            fpsS=fps+"";
        }
        rebdererText.text = fpsS;
    }
}
