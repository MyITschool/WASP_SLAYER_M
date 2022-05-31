package com.example.tao2.scenes;

import com.example.engine.core.Core;
import com.example.engine.core.Scene;
import com.example.engine.core.Updated;
import com.example.engine.math.Vector3;
import com.example.engine.model.ModelLoader;
import com.example.engine.render.RenderModel;
import com.example.engine.render.RendererGL;

public class Test extends Scene implements Updated {
    public Test(Core core) {
        super(core);
    }
    RendererGL r = core.getRenderer();
    ModelLoader modelLoader = core.getModelLoader();
    @Override
    public void preload() {
        r.loadTextures(new String[]
                {"models/room_0.png"}
        );
        modelLoader.loadModels(new String[]{"models/room_0.obj"}, true);
        core.getLoop().setFPS(30);
        modelLoader.getModel(0).setUsNormal(true);
        modelLoader.getModel(0).setUsTexture(0);
    }

    @Override
    public void start() {
        RenderModel rm = r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));
        r.addObject(modelLoader.getModel(0));


        //rm.setUsTexture(0);
        //rm.setUsNormal(true);

       // r.camera.rotateModeView=false;
        r.camera.setPosition(new Vector3(0,0,-6));

        core.getLoop().addUpdateObj(this);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose() {

    }
    float a = 0;
    @Override
    public void update() {
        a+=0.1f;
        r.camera.setRotate(new Vector3(0,a,0));
    }
}
