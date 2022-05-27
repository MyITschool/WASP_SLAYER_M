package com.example.tao2.scenes;

import com.example.engine.core.Config;
import com.example.engine.core.Core;
import com.example.engine.core.Scene;
import com.example.engine.math.Vector3;
import com.example.engine.math.Vector4;
import com.example.engine.model.Model;
import com.example.engine.model.ModelLoader;
import com.example.engine.render.RenderObject;
import com.example.engine.render.RendererGL;

public class NextLvlAnim extends Scene {
    public NextLvlAnim(Core core) {
        super(core);
    }

    private final ModelLoader modelLoader = core.getModelLoader();
    private final RendererGL renderer = core.getRenderer();
    private final Config config = core.getConfig();

    @Override
    public void preload() {
//        modelLoader.loadModels(new String[]{"models/room_0.obj","models/door_0.obj",
//                "models/room_1.obj","models/room_2.obj","models/bee.obj","models/pipe.obj"}, true);
//        renderer.loadTextures(new String[]
//                {"models/room_0.png","img/controlBox.png","img/controlBoxBarrier.png",
//                        "img/gun_0.png","img/gun_1.png","img/gun_2.png", "img/razjalovan.png",
//                        "img/dalishe.png", "img/menu.png"}
//        );
//
//        config.ambient = 0;
//
//        core.getLoop().setFPS(30);
    }

    private boolean es = false;

    private long s;

    @Override
    public void start() {
//        config.setGlobal_light_dir(new Vector3(0, 1, 0));
////        config.setGlobal_light_color(new float[]{0.5f,0.5f,0.5f});
//        config.setClear_color(new Vector4(0,0,0,1));
//        config.setGlobal_light_color(new Vector3(0.f,0.f,0.f));


        Model model = modelLoader.getModel(5);
        model.texture = 0;



        for (int i = 0; i < 10; i++){
            RenderObject wall = renderer.addObject(model);
            wall.setPosition(new Vector3(0,-i*5,0));
            wall.setScale(new Vector3(1,1,1));
            wall.setUsNormal(true);


        }
        renderer.addPointLight(new Vector4(0,0,0,0), new Vector4(1,1,1,10));

        renderer.camera.setPosition(new Vector3(0,-2, 0));
        renderer.camera.setRotate(new Vector3(0));

        es = true;

        s = System.currentTimeMillis() + 1400;
    }

    @Override
    public void resume() {
    }
    private float v = 0;
    private final float g = 9.8f/core.getLoop().getFPS();
    @Override
    public void update() {
        if (es){
            if ((System.currentTimeMillis() - s) < 0){
                Vector3 r = renderer.camera.getRotate();
                r.y+=0.8f;
                renderer.camera.setRotate(r);
                r = renderer.camera.getPosition();
                r.y+=v;
                v+=g;
                renderer.camera.setRotate(r);
            }else {
                core.setScene(new DungeScene(core), false);
            }
        }

    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose() {

    }
}
