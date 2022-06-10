package com.example.engine_demo.Scenes;

import static com.example.mylibrary.math.Func.randomInt;

import com.example.mylibrary.animation.Animation;
import com.example.mylibrary.animation.Key;
import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.event.TouchListener;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector2;
import com.example.mylibrary.math.Vector2Int;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;
import com.example.mylibrary.model.ModelLoader;
import com.example.mylibrary.model.VertexesData;
import com.example.mylibrary.physics.CubeCollider;
import com.example.mylibrary.physics.RigidBody;
import com.example.mylibrary.render.Camera;
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

    RebdererText rebdererText;

    RigidBody rigidBody23;
    @Override
    public void start() {
        renderer.loadTexture("textures/floor.png", "floor");
        renderer.loadTexture("textures/floor_normalmap.png", "floor_normalmap");
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
        renderer.global_light_dir = new Vector3(-1,1,1);
        renderer.global_light_dir.norm();

        VertexesData vertexesData = modelLoader.loadModel("models/cube.obj", "cube");
        vertexesData.vertexes_texture=null;
        vertexesData.vertexes_normal=vertexesData.vertexes;
        VertexesData vertexesData2 = modelLoader.loadModel("models/sphere.obj", "sphere");
        vertexesData2.vertexes_texture=null;
        vertexesData2.vertexes_normal=vertexesData2.vertexes;

        VertexesData vertexesData1 = modelLoader.loadModel("models/plane.obj", "plane");
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
        renderObject1.setScale(new Vector3(30));
        renderObject1.setPosition(new Vector3(-15,0,-15));
        renderer.addRenderObject(renderObject1);
        RigidBody rigidBody1 = new RigidBody(new CubeCollider(new Vector3(-15,0,-15), new Vector3(30,0.1f,30)), renderObject1, core);
        rigidBody1.activity=false;

        /*Key[] keys = new Key[]{
                new Key(new Vector3(0,0,0),
                        new Vector3(180,360,0),
                        new Vector3(1,0,0),
                        new Vector4(1,0.5f,0,0),
                        1),
                new Key(new Vector3(0,0,0),
                        new Vector3(-180,360,0),
                        new Vector3(-1,0,0),
                        new Vector4(-1,-0.5f,0,0),
                        1)
        };
        for(int i = 0; i < 200; i++){
            RenderObject nros = new RenderObject(modelS);
            nros.color=new Vector4(randomInt(0,100)/100.f,randomInt(0,100)/100.f,randomInt(0,100)/100.f, 1);
            Vector3 pos = new Vector3(randomInt(-30,30),randomInt(1,30),randomInt(-30,30));
            nros.setPosition(pos);
            renderer.addRenderObject(nros);
            RigidBody nrb = new RigidBody(new CubeCollider(pos, new Vector3(1)), nros, core);
            Animation animation = new Animation(keys, nros, core);
            animation.play();
        }*/

        rebdererText = new RebdererText("font", "00", core);
        rebdererText.setScale(new Vector3(0.1f,0.2f,1));
        rebdererText.setPosition(new Vector3(-0.8f,0.8f,0));
        renderer.addUI(rebdererText);


        Model skyM = new Model("sky", core);
        RenderObject skyRO = new RenderObject(skyM);
        renderer.addRenderObject(skyRO);

        //renderer.camera.rotateModeView = false;
        renderer.camera.setPosition(new Vector3(-15,4f,15));//0,4f,15
        renderer.camera.setRotate(new Vector3(0,0,0));//0,-45,0


        renderer.softShadow=5;
        Camera shadowCamera = new Camera(core);
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
    float t = 0;
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

        rebdererText.text = renderer.getLastFPS()+"";
    }
}
