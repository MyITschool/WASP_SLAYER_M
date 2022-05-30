package com.example.tao2.updated;

import com.example.engine.audio.Audio;
import com.example.engine.core.Core;
import com.example.engine.core.Updated;
import com.example.engine.event.TouchListener;
import com.example.engine.math.Func;
import com.example.engine.math.Vector2;
import com.example.engine.math.Vector2Int;
import com.example.engine.math.Vector3;
import com.example.engine.math.Vector4;
import com.example.engine.physics.CubeCollider;
import com.example.engine.physics.Hit;
import com.example.engine.physics.Physics;
import com.example.engine.render.RenderImg;
import com.example.engine.render.RendererGL;
import com.example.tao2.MainActivity;
import com.example.tao2.scenes.Menu;

public class Player implements Updated {

    private final Core core;
    private final RendererGL renderer;
    private final TouchListener touchListener;
    private final Physics physics;
    private final Func func;

    private final RenderImg hp_bar;
    private final RenderImg controlBox;
    private final RenderImg controlBoxBarrier;
    private float hp = 10;

    private final RenderImg gun;

    private final Audio shoot;

    private RenderImg bs;

    public Player(Core core){
        this.core = core;
        this.renderer = core.getRenderer();
        this.touchListener = core.getTouchListener();
        this.physics = core.getPhysics();
        this.func = core.getFunc();

        MainActivity ma = (MainActivity)core;

        shoot = core.getAudioLoader().getAudio(3);
        shoot.setVolume(new Vector2(ma.settings.getSoundsVolume()));

        renderer.camera.rotateModeView=false;
        renderer.camera.setPosition(new Vector3(0,-2f,0));
        renderer.camera.setRotate(new Vector3(0));

        hp_bar = renderer.addUIImg();
        hp_bar.setPosition(new Vector3(0f,1,0));
        hp_bar.setScale(new Vector2(0.5f,0.1f));
        hp_bar.setColor(new Vector4(1,0,0,1));

        core.getLoop().addUpdateObj(this);

        float ycor = (float) renderer.getResolution().x / (float)renderer.getResolution().y;

        controlBoxBarrier = renderer.addUIImg();
        controlBoxBarrier.setPosition(new Vector3(-0.6f,-0.6f, 0));
        controlBoxBarrier.setScale(new Vector2(0.4f,0.4f*ycor));
        controlBoxBarrier.setTexture(2);

        controlBox= renderer.addUIImg();
        controlBox.setPosition(new Vector3(-0.6f,-0.6f, 0));
        controlBox.setScale(new Vector2(0.05f,0.05f*ycor));
        controlBox.setTexture(1);

//        RenderUI curs = renderer.addUI();
//        curs.setScale(new Vector2(0.01f,0.006f));

        gun = renderer.addUIImg();
        gun.setScale(new Vector2(0.5f,0.5f));
        gun.setPosition(new Vector3(0.5f,-0.5f,0));
        gun.setTexture(3);


    }

    private long det = -1;
    private int dcd = 500;

    public void damage(float damage){
        if (hp>0){
            hp-=damage;
            hp_bar.setScale(new Vector2(0.5f*(hp/10),0.1f));
            if(hp <= 0){
                //core.setScene(new DungeScene(core), false);
                core.getAudioLoader().getAudio(1).play(true, new Vector2(1));
                renderer.deleteUI(gun);
                renderer.deleteUI(controlBox);
                renderer.deleteUI(controlBoxBarrier);

                RenderImg dt = renderer.addUIImg();
                dt.setTexture(6);
                dt.setScale(new Vector2(0.8f, 0.3f));

                bs = core.getRenderer().addUIImg();
                bs.setColor(new Vector4(1,0,0,0));

                core.getLoop().clear();
                core.getLoop().addUpdateObj(this);

                det = System.currentTimeMillis()+dcd;
            }
        }
    }



    private float camear_v = 0.1f*2;
    private float camera_rot_v = 0.1f*2;
    private Vector2 lastTouch = new Vector2(-1);

    private long last_fire = 0;
    private int bullets = 2;

    private boolean gun_s = true;

    private boolean nl = false;

    public void update() {
        if (hp > 0){
            upd();
        }else {
            Vector2 touch = touchListener.getTouchDown(new Vector2(0), new Vector2(0.8f, 0.3f));
            if (touch.x != -1 && (System.currentTimeMillis() - det) > 0){
               // core.setScene(new Menu(core), false);
                det = System.currentTimeMillis()+dcd;
                nl=true;
            }

            if (nl && (System.currentTimeMillis() - det) < 0){
                bs.setColor(new Vector4(1,0,0, 1+(System.currentTimeMillis() - det) / (float)dcd));
            }else if (nl && (System.currentTimeMillis() - det) > 0){
                core.setScene(new Menu(core, false), false);
            }
        }
    }

    private void upd(){
        Vector2Int res = renderer.getResolution();

        float xcor = (float)res.y/(float)res.x;
        controlBoxBarrier.setScale(new Vector2(0.6f*xcor,0.5f));
        controlBox.setScale(new Vector2(0.07f*xcor,0.07f));
        controlBoxBarrier.setPosition(new Vector3(-1+0.6f*xcor, -0.5f, 0));

        Vector2 newTouch = touchListener.getTouchDown(new Vector2(-1+0.6f*xcor,-0.5f), new Vector2(0.6f*xcor,0.5f));//-0.6f,-0.6f,0.4f,0.4f);
        if (newTouch.x != -1){

            Vector2 touchLocal = new Vector2(newTouch.x/((float)res.x*0.6f*xcor)*2-1, ((float)res.y-newTouch.y)/((float)res.y*0.5f)*2-1);

            controlBox.setPosition(new Vector3(func.canvasToGlCoord(newTouch),0));

            Vector3 arr = renderer.camera.getPosition().clone();
            Vector3 cr = renderer.camera.getRotate().clone();
            float ya = (float) (cr.y*Math.PI/180+Math.PI/2);

            Vector3 delta_m = new Vector3(0);
            if(touchLocal.x>=0){
                delta_m.z+=Math.cos(ya)*Math.abs(touchLocal.x*camear_v);
                delta_m.x-=Math.sin(ya)*Math.abs(touchLocal.x*camear_v);
            }else {
                delta_m.z-=Math.cos(ya)*Math.abs(touchLocal.x*camear_v);
                delta_m.x+=Math.sin(ya)*Math.abs(touchLocal.x*camear_v);
            }
            if(touchLocal.y>=0){
                delta_m.z+=Math.sin(ya)*Math.abs(touchLocal.y*camear_v);
                delta_m.x+=Math.cos(ya)*Math.abs(touchLocal.y*camear_v);
            }else {
                delta_m.z-=Math.sin(ya)*Math.abs(touchLocal.y*camear_v);
                delta_m.x-=Math.cos(ya)*Math.abs(touchLocal.y*camear_v);
            }
            Vector3 posX = new Vector3(0);
            Vector3 posZ = new Vector3(0);

            posX.x = -arr.x-delta_m.x - 0.25f;
            posZ.x = -arr.x - 0.25f;

            posX.z = -arr.z - 0.25f;
            posZ.z = -arr.z-delta_m.z - 0.25f;

            Vector3 lp = arr.clone();


            if(!physics.testCollisionCube(new CubeCollider(posX, new Vector3(0.5f)))){
                lp.x+=delta_m.x;
            }
            if(!physics.testCollisionCube(new CubeCollider(posZ, new Vector3(0.5f)))){
                lp.z+=delta_m.z;
            }
            renderer.camera.setPosition(lp);
        }else {
            controlBox.setPosition(new Vector3(-1+0.6f*xcor,-0.5f, 0));
        }

        newTouch = touchListener.getTouchDown(new Vector2(0.6f,0), new Vector2(0.5f,1f));//0.5f,0,0.5f,1f);

        // System.out.println(Arrays.toString(newTouch));
        int cd = 500;
        int reload = 1500;
        int gun_texture = 3;
        if(newTouch.x != -1){
            if(lastTouch.x==-1){
                lastTouch = newTouch.clone();
            }else {
                float deltaX = newTouch.x-lastTouch.x;
                float deltaY = newTouch.y-lastTouch.y;
                lastTouch=newTouch.clone();

                Vector3 arr = renderer.camera.getRotate().clone();

                arr.y+=deltaX*camera_rot_v;
                arr.x+=deltaY*camera_rot_v;

                renderer.camera.setRotate(arr);
                // System.out.println("a");

                Hit hit = new Hit();
                if(physics.rayCastCamera(hit, 0, 100)){
                    if(hit.collider.getType()==1 && (System.currentTimeMillis() - last_fire) >= cd){
                        Enemy enemy = (Enemy)hit.collider.getColliderObj();
                        enemy.damage(1);
                        shoot.play(false, new Vector2(1));
                        gun_texture =4;
                        gun.setTexture(gun_texture);
                        bullets--;
                        if(bullets<=0){
                            last_fire=System.currentTimeMillis()+ reload;
                            gun_s=false;
                        }else {
                            last_fire=System.currentTimeMillis()+ cd;
                        }
                    }
                }
            }
        }else {
            lastTouch=new Vector2(-1);
        }

        int fire_time = 100;
        if((gun_s && (System.currentTimeMillis() - last_fire) >= -(cd - fire_time) && (System.currentTimeMillis() - last_fire) < 0)){
            gun_texture =3;
            gun.setTexture(gun_texture);
        }else if(!gun_s && (System.currentTimeMillis() - last_fire) >= -(reload - fire_time) && (System.currentTimeMillis() - last_fire) < 0){
            gun_texture = 5;
            gun.setTexture(gun_texture);
        }else if((!gun_s && (System.currentTimeMillis() - last_fire) >= 0)){
            gun_s=true;
            bullets=2;
            gun_texture =3;
            gun.setTexture(gun_texture);
        }
    }
}
