package com.example.engine_demo.Scenes;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.event.TouchListener;
import com.example.mylibrary.math.Func;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector2;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;
import com.example.mylibrary.model.ModelLoader;
import com.example.mylibrary.model.VertexesData;
import com.example.mylibrary.physics.physics3D.CubeCollider;
import com.example.mylibrary.physics.physics3D.Physics;
import com.example.mylibrary.physics.physics3D.RigidBody;
import com.example.mylibrary.render.Camera;
import com.example.mylibrary.render.RebdererText;
import com.example.mylibrary.render.RenderObject;
import com.example.mylibrary.render.Renderer;

public final class PhysicsTest extends Scene implements Updated {
    public PhysicsTest(Core core) {
        super(core);
    }

    private final Renderer renderer = core.getRenderer();
    private final ModelLoader modelLoader = core.getModelLoader();
    private final TouchListener touchListener = core.getTouchListener();

    private RebdererText fpsRT;
    private RebdererText sphereCountRT;
    private Model modelSphere;

    @Override
    public void start() {
        renderer.ambient = 0.01f;
        renderer.global_light_color = new Vector3(1f);
        renderer.global_light_dir = new Vector3(0,1,0);
        renderer.global_light_dir.norm();

        core.getAudioLoader().getAudio("bg").play(true, new Vector2(1));

        VertexesData vertexesDataPlane = modelLoader.getVertexesData("plane");
        vertexesDataPlane.vertexes_normalTexture=vertexesDataPlane.vertexes_texture;

        VertexesData vertexesDataSphere = modelLoader.getVertexesData("sphere");
        vertexesDataSphere.vertexes_texture=null;
        vertexesDataSphere.vertexes_normal=vertexesDataSphere.vertexes;

        Model modelPlane = new Model(vertexesDataPlane, core);
        modelSphere = new Model(vertexesDataSphere, core);



        for(int x = -2; x < 2; x++){
            for (int z = -2; z < 2; z++){
                Vector3 pos = new Vector3(x*10,0,z*10);
                RenderObject planeRO = new RenderObject(modelPlane,"floor", "floor_normalmap");
                planeRO.setScale(new Vector3(10));
                planeRO.setPosition(pos);
                renderer.addRenderObject(planeRO);
                RigidBody planeRB = new RigidBody(new CubeCollider(pos, new Vector3(10,0.1f,10)), planeRO, core);
                planeRB.activity=false;
            }
        }

        Model modelSky = new Model("sky", core);
        RenderObject skyRO = new RenderObject(modelSky);
        renderer.addRenderObject(skyRO);

        fpsRT = new RebdererText("font", "00", core);
        fpsRT.setScale(new Vector3(0.2f,0.2f,1));
        fpsRT.setPosition(new Vector3(-0.8f,0.8f,0));
        renderer.addUI(fpsRT);

        sphereCountRT = new RebdererText("font", "spheres: 0", core);
        sphereCountRT.setScale(new Vector3(0.3f,0.2f,1));
        sphereCountRT.setPosition(new Vector3(0.5f,0.8f,0));
        renderer.addUI(sphereCountRT);

        renderer.camera.rotateModeView = false;
        renderer.camera.setPosition(new Vector3(0,15f,25));//0,15f,25
        renderer.camera.setRotate(new Vector3(20,0,0));//20,0,0

        renderer.softShadow=5;
        Camera shadowCamera = new Camera(core);
        shadowCamera.rotateModeView = false;
        Vector2 res = new Vector2(800*2,600*2);
        shadowCamera.setResolution(res);
        shadowCamera.setPosition(new Vector3(0,100f,0));
        shadowCamera.setRotate(new Vector3(90,0,0));
        renderer.addShadow(res, shadowCamera);
        shadowCamera.setFar(200);
        renderer.camera.setFar(200);

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

    private final Physics physics = core.getPhysics();

    int spheresCount = 0;
    @Override
    public void update(float dt) {
        if(touchListener.getTouchDown(new Vector2(0), new Vector2(1), new Vector2(0))){
            RenderObject sphereRO = new RenderObject(modelSphere);
            sphereRO.setScale(new Vector3(1f));
            Vector3 pos = new Vector3(Func.randomInt(-10,15), Func.randomInt(3,10), Func.randomInt(-10,15));
            sphereRO.setPosition(pos);

            if(physics.testCollisionCube(new CubeCollider(pos,new Vector3(1)))){
                return;
            }
            spheresCount++;
            sphereCountRT.text = "spheres: "+spheresCount;

            renderer.addRenderObject(sphereRO);
            sphereRO.color=new Vector4(0.1f,0.1f,0.1f,1);
            RigidBody sphereRB = new RigidBody(new CubeCollider(pos, new Vector3(0.8f)), sphereRO, core);

            pos = pos.clone();
            pos.norm();
            sphereRO.color = new Vector4((float) Math.random(),(float) Math.random(), (float) Math.random(), 1);
            sphereRB.setVelocity(Vector.mul(pos, Func.randomInt(1,10)));
            sphereRB.elasticity=0.7f;
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
