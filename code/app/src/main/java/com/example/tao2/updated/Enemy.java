package com.example.tao2.updated;

import com.example.engine.core.Core;
import com.example.engine.core.Updated;
import com.example.engine.math.Vector2;
import com.example.engine.math.Vector3;
import com.example.engine.model.Model;
import com.example.engine.physics.CubeCollider;
import com.example.engine.physics.Physics;
import com.example.engine.render.RenderObject;
import com.example.engine.render.RendererGL;
import com.example.tao2.dunge.DungeGen;

public class Enemy extends Updated {
    public RenderObject enemy_ro;

    private final Core core;
    private final CubeCollider collider;
    private final Physics physics;
    private final DungeGen dungeGen;
    private final Player player;

    private float hp = 1;


    public Enemy(Model model, Vector3 pos, Player player, Core core, DungeGen dungeGen){
        this.core = core;
        RendererGL rendererGL = core.getRenderer();
        physics = core.getPhysics();
        this.player = player;
        this.dungeGen=dungeGen;

        enemy_ro = rendererGL.addObject(model);
        enemy_ro.setPosition(pos);
        enemy_ro.setUsNormal(true);

        enemy_ro.setRotate(new Vector3(0,(int) (Math.random() * 360),0));

        collider = core.getPhysics().addCubeCollider(new Vector3(pos.x-0.25f,0,pos.z-0.25f), new Vector3(0.5f, 2, 0.5f));
        collider.setType(1);
        collider.setColliderObj(this);

        core.getLoop().addUpdateObj(this);
    }

    public void damage(float damage){
        hp-=damage;
        if(hp==0){
            core.getRenderer().deleteObject(enemy_ro);
            physics.deleteCubeCollider(collider);
            dungeGen.enemydeath();
        }
    }

    float speed = 0.1f;
    float rot_speed = 3f*5;
    float max_depth = 8;
    float damage_depth = 2;
    float damage = 1;

    float[] anim_key = new float[]{1.5f, 1};
    float dy = 0.015f;
    boolean as = true;
    float del = 1000;
    long last_a = 0;

    public void update() {
        if (hp>0){
            Vector3 ep = enemy_ro.getPosition().clone();
            if(as){
                ep.y-=dy;
                enemy_ro.setPosition(ep);
                if(ep.y <= anim_key[1]){
                    as=!as;
                }
            }else {
                ep.y+=dy;
                enemy_ro.setPosition(ep);
                if(ep.y >= anim_key[0]){
                    as=!as;
                }
            }

            Vector3 pp = core.getRenderer().camera.getPosition().clone();
            pp.x=-pp.x;
            pp.z=-pp.z;


            Vector2 dir = new Vector2(pp.x-ep.x, pp.z-ep.z);
            if(dir.length() <= damage_depth && (System.currentTimeMillis() - last_a) >= del){
                player.damage(damage);
                last_a = System.currentTimeMillis();
            }

            if(dir.length() <= max_depth && dir.length() > 0.7f){
                dir.norm();
                Vector3 np = ep.clone();
                float a = (float) ((-enemy_ro.getRotate().y-90+180)*Math.PI/180);
                collider.pos.x+=(float)Math.cos(a)*speed;//dir.x*speed;
                if(!physics.testCollisionCube(collider)){
                    np.x+=(float)Math.cos(a)*speed;//dir.x*speed;
                }else {
                    collider.pos.x-=(float)Math.cos(a)*speed;//dir.x*speed;
                }
                collider.pos.z+=(float)Math.sin(a)*speed;//dir.y*speed;
                if(!physics.testCollisionCube(collider)){
                    np.z+=(float)Math.sin(a)*speed;//dir.y*speed;
                }else {
                    collider.pos.z-=(float)Math.sin(a)*speed;//dir.y*speed;
                }
                enemy_ro.setPosition(np);
            }

            if(dir.length() <= max_depth){
                float a = -(float) ((float) Math.atan2(pp.z-ep.z, pp.x-ep.x)*180/Math.PI)+90;
                float ry = enemy_ro.getRotate().y;
                //if(a>0){
                float da = (a-ry)/Math.abs(a-ry)*rot_speed;
//                }else {
//                    da = (ry-a)/Math.abs(a-ry)*rot_speed;
//                }

                if(Math.abs(a-ry)>10){
                    ry+=da;
                    enemy_ro.setRotate(new Vector3(0, ry, 0));
                }
            }
        }
    }
}
