package com.example.engine_demo.Scenes;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.Scene;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.event.TouchListener;
import com.example.mylibrary.math.Vector2;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.model.Model;
import com.example.mylibrary.model.ModelLoader;
import com.example.mylibrary.model.VertexesData;
import com.example.mylibrary.render.Light;
import com.example.mylibrary.render.RebdererText;
import com.example.mylibrary.render.RenderObject;
import com.example.mylibrary.render.Renderer;

public final class GraphicsTest extends Scene implements Updated {
    public GraphicsTest(Core core) {
        super(core);
    }

    private final Renderer renderer = core.getRenderer();
    private final ModelLoader modelLoader = core.getModelLoader();
    private final TouchListener touchListener = core.getTouchListener();

    private RenderObject lightRO;
    private Light light;

    private RenderObject[] testsRO = new RenderObject[5];

    private RebdererText fpsRT;

    @Override
    public void start() {
        renderer.ambient = 0.05f;
        renderer.global_light_color = new Vector3(0f);
        renderer.global_light_dir = new Vector3(-1,1,1);
        renderer.global_light_dir.norm();
        renderer.camera.setFar(100);

        VertexesData vertexesDataCube = modelLoader.getVertexesData("cube");
        VertexesData vertexesDataSphere = modelLoader.getVertexesData("sphere");

        Model modelSphereColor = new Model(vertexesDataSphere.vertexes, core);
        lightRO = new RenderObject(modelSphereColor);
        lightRO.setScale(new Vector3(0.1f));
        lightRO.setPosition(new Vector3(0,0,4));
        renderer.addRenderObject(lightRO);

        Model modelPlaneColor = new Model(vertexesDataCube.vertexes, core);
        RenderObject cubeColorRO = new RenderObject(modelPlaneColor);
       // cubeColorRO.activity = false;
        renderer.addRenderObject(cubeColorRO);
        testsRO[0] = cubeColorRO;

        Model modelPlaneColorNormal = new Model(vertexesDataCube.vertexes, vertexesDataCube.vertexes, core);
        RenderObject cubeColorNormalRO = new RenderObject(modelPlaneColorNormal);
        cubeColorNormalRO.activity=false;
        renderer.addRenderObject(cubeColorNormalRO);
        testsRO[1] = cubeColorNormalRO;

        Model modelPlaneTextureNormal = new Model(vertexesDataCube.vertexes, vertexesDataCube.vertexes, vertexesDataCube.vertexes_texture, core);
        RenderObject cubeTextureNormalRO = new RenderObject(modelPlaneTextureNormal, "wall");
        cubeTextureNormalRO.activity=false;
        renderer.addRenderObject(cubeTextureNormalRO);
        testsRO[2] = cubeTextureNormalRO;

        Model modelPlaneTextureNormalMap = new Model(vertexesDataCube.vertexes, vertexesDataCube.vertexes, vertexesDataCube.vertexes_texture, vertexesDataCube.vertexes_texture, core);
        RenderObject cubeTextureNormalMapRO = new RenderObject(modelPlaneTextureNormalMap, "wall", "wall_normalmap");
        cubeTextureNormalMapRO.activity=false;
        renderer.addRenderObject(cubeTextureNormalMapRO);
        testsRO[3] = cubeTextureNormalMapRO;

        Model skyM = new Model("sky", core);
        RenderObject skyRO = new RenderObject(skyM);
        skyRO.activity=false;
        renderer.addRenderObject(skyRO);
        testsRO[4] = skyRO;

        light = new Light(new Vector3(0,0,4), new Vector3(1), 4);
        renderer.addLigth(light);

        fpsRT = new RebdererText("font", "00", core);
        fpsRT.setScale(new Vector3(0.2f,0.2f,1));
        fpsRT.setPosition(new Vector3(-0.8f,0.8f,0));
        renderer.addUI(fpsRT);

        renderer.camera.rotateModeView = false;
        renderer.camera.setPosition(new Vector3(4,0f,5));
        renderer.camera.setRotate(new Vector3(0,-35,0));

        renderer.addUpdated(this);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    float t = 0;
    int i = 0;
    float cd = 0.5f;
    @Override
    public void update(float dt) {
        t+=dt;
        Vector3 pos = new Vector3((float) Math.sin(t)*4,0,(float) Math.cos(t)*4);
        light.position = pos;
        lightRO.setPosition(pos);
        cd-=dt;
        if(touchListener.getTouchDown(new Vector2(0), new Vector2(1), new Vector2(0)) && cd <= 0){
            if (i!=3) testsRO[i].activity=false;
            i++;
            if(i>=5){core.setScene(new PhysicsTest(core));return;}
            testsRO[i].activity=true;
            cd=0.5f;
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
