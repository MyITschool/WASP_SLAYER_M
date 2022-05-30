package com.example.tao2.scenes;

import com.example.engine.audio.AudioLoader;
import com.example.engine.core.Config;
import com.example.engine.core.Core;
import com.example.engine.core.Scene;
import com.example.engine.event.TouchListener;
import com.example.engine.math.Vector2;
import com.example.engine.math.Vector2Int;
import com.example.engine.math.Vector3;
import com.example.engine.math.Vector4;
import com.example.engine.model.Model;
import com.example.engine.model.ModelLoader;
import com.example.engine.physics.CubeCollider;
import com.example.engine.render.RenderImg;
import com.example.engine.render.RenderModel;
import com.example.engine.render.RendererGL;
import com.example.tao2.MainActivity;

public class Menu extends Scene {
    private final boolean usp;
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
                            "img/dalishe.png", "img/button_start.png", "img/font.png", "img/button_settings.png",
                            "img/game_name.png", "img/settings.png"}
            );
            config.usRandL = 0.0f;
            config.ambient = 0.04f;
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
//            roomsModel[0].vnt = roomsModel[0].vt;
//            roomsModel[0].setUsTextureNormals(13);

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
            config.setGlobal_light_color(new Vector3(0.f,0.f,0.f));

            config.max_render_depth = 15;
            renderer.camera.setFar(15);

            Vector4 cc = new Vector4((float)44/256*2,(float)26/256*2, (float)2/256*2, 1);
            config.fog_color = cc;
            config.setClear_color(cc);
        }

        System.out.println("Menu preload");

    }

    private RenderImg bs;

    private RenderModel bee;

    boolean es = false;

    RenderImg sm;
    RenderImg[] bsm = new RenderImg[2];

    MainActivity ma = (MainActivity)core;

    @Override
    public void start() {
        ma.settings.read();

        RenderImg gn = renderer.addUIImg();
        gn.setTexture(11);
        gn.setScale(new Vector2(0.6f,0.3f));
        gn.setPosition(new Vector3(0, 0.5f,0));
        gn.setColor(new Vector4(1,1,1,0.7f));
        ////////////////////////////////////////////////
        RenderImg sb = renderer.addUIImg();
        sb.setTexture(8);
        sb.setScale(new Vector2(0.3f,0.15f));
        sb.setPosition(new Vector3(0, 0f,0));
        sb.setColor(new Vector4(1,1,1,0.7f));
        ////////////////////////////////////////////////
        RenderImg seb = renderer.addUIImg();
        seb.setTexture(10);
        seb.setScale(new Vector2(0.3f,0.15f));
        seb.setPosition(new Vector3(0, -0.4f,0));
        seb.setColor(new Vector4(1,1,1,0.7f));

        sm = renderer.addUIImg();
        sm.setScale(new Vector2(0.75f));
        sm.setTexture(12);
        sm.setPosition(new Vector3(0, -0.15f, 0));
        sm.setColor(new Vector4(1,1,1,0f));
        sm.actively = false;
        //0.95
        bsm[0] = renderer.addUIImg();
        bsm[0].actively=false;
        bsm[0].setPosition(new Vector3(ma.settings.getMusicVolume()*0.6f-0.3f,-0.05f,0));
        bsm[0].setColor(new Vector4(1,1,1,0));
        bsm[1] = renderer.addUIImg();
        bsm[1].setColor(new Vector4(1,1,1,0));
        bsm[1].actively=false;
        bsm[1].setPosition(new Vector3(ma.settings.getSoundsVolume()*0.6f-0.3f,-0.525f,0));


        audioLoader.getAudio(0).play(true, new Vector2(ma.settings.getMusicVolume()));

        bs = renderer.addUIImg();
        bs.setColor(new Vector4(0));

        renderer.camera.rotateModeView=false;
        renderer.camera.setPosition(new Vector3(0,-2f,0));
        renderer.camera.setRotate(new Vector3(0, 45, 0));

        RenderModel r = renderer.addObject(modelLoader.getModel(0));
        r.setUsNormal(true);
        r.setScale(new Vector3(1,1.4f,1));

        RenderModel d = renderer.addObject(modelLoader.getModel(1));
        d.setUsNormal(true);
        d.setScale(new Vector3(10,17,10));
        d.setPosition(new Vector3(0,0.1f,-5));

        RenderModel d1 = renderer.addObject(modelLoader.getModel(1));
        d1.setUsNormal(true);
        d1.setScale(new Vector3(10,17,10));
        d1.setPosition(new Vector3(4f, 0.2f, 0));
        d1.setRotate(new Vector3(0,90,0));

        renderer.addPointLight(new Vector4(0,2,0,0), new Vector4(1,1,1,2));

        bee = renderer.addObject(modelLoader.getModel(4));
        bee.setUsNormal(true);
        bee.setPosition(new Vector3(2, 1.5f, -2));

        es = true;
    }

    @Override
    public void resume() {
        audioLoader.getAudio(0).play(true, new Vector2(1));
    }

    private boolean nl = false;
    private long det;
    private final int dcd = 500;


    float iu = 0;
    byte m = 0;
    float r;

    @Override
    public void update() {
        Vector2 t = touchListener.getTouchDown(new Vector2(0), new Vector2(0.3f,0.15f));
        if (t.x != -1 && m==0){
            //core.setScene(new NextLvlAnim(core), false);
            m=1;
            nl = true;
            det = System.currentTimeMillis()+dcd;
        }

        t = touchListener.getTouchDown(new Vector2(0, -0.4f), new Vector2(0.3f,0.15f));
        if (t.x != -1 && m==0){
            m=2;
            sm.actively=true;
            bsm[0].actively=true;
            bsm[1].actively=true;
            Vector2Int res = renderer.getResolution();
            r = (float)res.y/(float)res.x;
            bsm[0].setScale(new Vector2(0.1f*r,0.1f));
            bsm[1].setScale(new Vector2(0.1f*r,0.1f));
        }
        if(m==2){
            Vector4 c = sm.getColor();
            c.w+=0.05f;
            c = bsm[0].getColor();
            c.w+=0.05f;
            c = bsm[1].getColor();
            c.w+=0.05f;
            if(c.w>=0.95f){
                m=3;
            }
        }
        if(m==3){
            t = touchListener.getTouchDown(new Vector2(0,-0.05f), new Vector2(0.4f,0.1f));
            if(t.x != -1){
                t = core.getFunc().canvasToGlCoord(t);
                t.y=-0.05f;
                if(t.x<-0.3f)
                    t.x=-0.3f;
                if(t.x>0.3f)
                    t.x=0.3f;
                ma.settings.setMusicVolume((t.x+0.3f)/0.6f);;
                audioLoader.getAudio(0).setVolume(new Vector2(ma.settings.getMusicVolume()));
                bsm[0].setPosition(new Vector3(t,0));
            }
            //-0.525f
            t = touchListener.getTouchDown(new Vector2(0,-0.525f), new Vector2(0.4f,0.1f));
            if(t.x != -1){
                t = core.getFunc().canvasToGlCoord(t);
                t.y=-0.525f;
                if(t.x<-0.3f)
                    t.x=-0.3f;
                if(t.x>0.3f)
                    t.x=0.3f;
                ma.settings.setSoundsVolume((t.x+0.3f)/0.6f);

                bsm[1].setPosition(new Vector3(t,0));
            }

            t = touchListener.getTouchDown(new Vector2(0.6f,0.4f), new Vector2(0.2f*r,0.2f));
            if(t.x!=-1){
                m=4;
            }
        }
        if(m==4){
            Vector4 c = sm.getColor();
            c.w-=0.05f;
            c = bsm[0].getColor();
            c.w-=0.05f;
            c = bsm[1].getColor();
            c.w-=0.05f;
            if(c.w<0){
                m=0;
                sm.actively=false;
                bsm[0].actively=false;
                bsm[1].actively=false;
            }
        }


        s();
    }

    private void s(){
        if (nl && (System.currentTimeMillis() - det) < 0){
            bs.setColor(new Vector4(0,0,0, 1+(System.currentTimeMillis() - det) / (float)dcd));
        }else if (nl && (System.currentTimeMillis() - det) > 0){
            core.setScene(new NextLvlAnim(core), false);
        }
        if (es){
            bee.setPosition(new Vector3(2, (float) (1.5f+Math.sin(iu)*0.25f), -2));
            bee.setRotate(new Vector3(0, (float) (Math.sin(iu)*15f), 0));

            renderer.camera.setRotate(new Vector3((float) (Math.sin(iu)*5), 45+(float) (Math.cos(iu)*5), 0));

            iu+=0.01f;
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
