package com.example.tao2.scenes;

import com.example.engine.audio.AudioLoader;
import com.example.engine.core.Config;
import com.example.engine.core.Core;
import com.example.engine.core.Scene;
import com.example.engine.event.TouchListener;
import com.example.engine.math.Vector2;
import com.example.engine.model.ModelLoader;
import com.example.engine.render.RenderUI;
import com.example.engine.render.RendererGL;

public class Menu extends Scene {
    public Menu(Core core) {
        super(core);
    }
    private final Config config = core.getConfig();
    private final RendererGL renderer = core.getRenderer();
    private final ModelLoader modelLoader = core.getModelLoader();
    private final AudioLoader audioLoader = core.getAudioLoader();
    private final TouchListener touchListener = core.getTouchListener();
    @Override
    public void preload() {
        renderer.loadTextures(new String[]
                {"models/room_0.png","img/controlBox.png","img/controlBoxBarrier.png",
                        "img/gun_0.png","img/gun_1.png","img/gun_2.png", "img/razjalovan.png",
                        "img/dalishe.png", "img/menu.png"}
        );
        config.usRandL = 0;
        config.ambient = 0;
        modelLoader.loadModels(new String[]{"models/room_0.obj","models/door_0.obj",
                "models/room_1.obj","models/room_2.obj","models/bee.obj"}, true);

        audioLoader.addAudios(new String[]{"mp3/menu.mp3", "mp3/bee.mp3", "mp3/bg_m.mp3", "mp3/shoot_0.mp3"});
        core.getLoop().setFPS(30);
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
            core.setScene(new DungeScene(core), false);
        }
    }

    @Override
    public void pause() {
        audioLoader.getAudio(0).stop();
    }

    @Override
    public void dispose() {
//        audioLoader.getAudio(0).dispose();
    }
}
