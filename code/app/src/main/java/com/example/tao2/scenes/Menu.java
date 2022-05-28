package com.example.tao2.scenes;

import com.example.engine.audio.AudioLoader;
import com.example.engine.core.Config;
import com.example.engine.core.Core;
import com.example.engine.core.Scene;
import com.example.engine.event.TouchListener;
import com.example.engine.math.Vector2;
import com.example.engine.math.Vector3;
import com.example.engine.math.Vector4;
import com.example.engine.model.Model;
import com.example.engine.model.ModelLoader;
import com.example.engine.physics.CubeCollider;
import com.example.engine.render.RenderUI;
import com.example.engine.render.RendererGL;

public class Menu extends Scene {
    private boolean usp = false;
    public Menu(Core core, boolean usp) {
        super(core);
        this.usp=usp;
    }
    private final Config config = core.getConfig();
    private final RendererGL renderer = core.getRenderer();
    private final ModelLoader modelLoader = core.getModelLoader();
    private final AudioLoader audioLoader = core.getAudioLoader();
    private final TouchListener touchListener = core.getTouchListener();
    @Override
    public void preload() {
        if (usp){
            renderer.loadTextures(new String[]
                    {"models/room_0.png","img/controlBox.png","img/controlBoxBarrier.png",
                            "img/gun_0.png","img/gun_1.png","img/gun_2.png", "img/razjalovan.png",
                            "img/dalishe.png", "img/menu.png"}
            );
            config.usRandL = 0.09f;
            config.ambient = 0;
            modelLoader.loadModels(new String[]{"models/room_0.obj","models/door_0.obj",
                    "models/room_1.obj","models/room_2.obj","models/bee.obj","models/pipe.obj"}, true);

            audioLoader.addAudios(new String[]{"mp3/menu.mp3", "mp3/bee.mp3", "mp3/bg_m.mp3", "mp3/shoot_0.mp3"});
            core.getLoop().setFPS(30);
            ///////////////////////////////////////////////////////////////////////////////
            Model[] roomsModel = new Model[]{
                    modelLoader.getModel(0),
                    modelLoader.getModel(1),
                    modelLoader.getModel(2),
                    modelLoader.getModel(3),
            };
            Model bee = modelLoader.getModel(4);
            bee.texture = 0;

            roomsModel[0].texture = 0;
            roomsModel[1].texture = 0;
            roomsModel[2].texture = 0;
            roomsModel[3].texture = 0;

            roomsModel[0].colliders = new CubeCollider[]{
                    new CubeCollider(new Vector3(-4.5f, 0, -3), new Vector3(2,2,3.25f)),
                    new CubeCollider(new Vector3(-2.5f, 0, -4.5f), new Vector3(2.5f,2,1.25f)),

                    new CubeCollider(new Vector3(2, 0, -4.5f), new Vector3(3f,2,1.25f)),
                    new CubeCollider(new Vector3(4.5f, 0, -3), new Vector3(1,2,3.25f)),

                    ////////////////////////////////////////////////////////////////////
                    new CubeCollider(new Vector3(-4.5f, 0, 2), new Vector3(1.25f,2,3.25f)),
                    new CubeCollider(new Vector3(-2.5f, 0, 4f), new Vector3(2.5f,2,1.25f)),

                    new CubeCollider(new Vector3(2, 0, 4f), new Vector3(3f,2,1.25f)),
                    new CubeCollider(new Vector3(4f, 0, 2), new Vector3(1.25f,2,3.25f))
            };
            roomsModel[2].colliders = new CubeCollider[]{
                    new CubeCollider(new Vector3(-2.5f, 0, 4f), new Vector3(2.5f,2,1.25f)),
                    new CubeCollider(new Vector3(2, 0, 4f), new Vector3(3f,2,1.25f)),

                    new CubeCollider(new Vector3(-4.5f, 0, 0), new Vector3(1,2,10)),
                    new CubeCollider(new Vector3(4f, 0, 0), new Vector3(1,2,10)),

                    new CubeCollider(new Vector3(0, 0, -4.5f), new Vector3(10,2,1.25f)),
            };
            roomsModel[3].colliders = new CubeCollider[]{
                    new CubeCollider(new Vector3(-1.5f, 0, 0), new Vector3(1,2,10)),
                    new CubeCollider(new Vector3(1f, 0, 0), new Vector3(1,2,10)),
            };


            config.setGlobal_light_dir(new Vector3(0, 1, 0));
            config.setClear_color(new Vector4(0,0,0,1));
            config.setGlobal_light_color(new Vector3(0.f,0.f,0.f));
        }

        System.out.println("Menu preload");
    }

    @Override
    public void start() {
        RenderUI m = renderer.addUI();
        m.setTexture(8);
        m.setScale(new Vector2(1, -1));
        audioLoader.getAudio(0).play(true, new Vector2(1));
    }

    @Override
    public void resume() {
        audioLoader.getAudio(0).play(true, new Vector2(1));
    }

    @Override
    public void update() {
        Vector2 t = touchListener.getTouchDown(new Vector2(0), new Vector2(0.4f));
        if (t.x != -1){
            core.setScene(new NextLvlAnim(core), false);
        }
    }

    @Override
    public void pause() {
        audioLoader.getAudio(0).stop();
    }

    @Override
    public void dispose() {
        audioLoader.getAudio(0).dispose();
    }
}
