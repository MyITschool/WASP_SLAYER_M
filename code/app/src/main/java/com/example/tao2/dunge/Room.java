package com.example.tao2.dunge;


import com.example.engine.core.Core;
import com.example.engine.math.Vector2;
import com.example.engine.math.Vector3;
import com.example.engine.math.Vector4;
import com.example.engine.model.Model;
import com.example.engine.physics.CubeCollider;
import com.example.engine.physics.Physics;
import com.example.engine.render.RenderObject;
import com.example.engine.render.RendererGL;
import com.example.tao2.updated.Enemy;
import com.example.tao2.updated.Player;

public class Room implements Cloneable{
    public float roomRes;
    public Vector2 doorRes;

    public RenderObject room;
    public RenderObject[] doors = new RenderObject[4];

    private final Core core;
    public Room(Core core){
        this.core = core;
    }

    public int enemys_count = 0;
    public Model enemys_models;

    private Enemy[] enemies;

    public void addLight(){
        Vector3 arrP = room.getPosition();
        core.getRenderer().addPointLight(new Vector4(arrP.x, arrP.y+3f, arrP.z, 0), new Vector4(1,1,1,3f));//new float[]{arrP.x, arrP.y-0.2f, arrP.z, 0}, new float[]{1,1,1,0.1f});

        RendererGL renderer = core.getRenderer();
        RenderObject l = renderer.addObject(renderer.getCubeVert());
        l.setScale(new Vector3(0.3f));
        l.setPosition(new Vector3(arrP.x, arrP.y+3f, arrP.z));
    }

    public int spawn_enemy(Player player, DungeGen dungeGen){
        int es = 0;
        if (enemys_count!=0){
            enemies = new Enemy[enemys_count];

            Vector3 rp = room.getPosition();
            es = getRandomNumber(1, enemys_count);
            for (int i = 0; i < es; i++){
                enemies[i] = new Enemy(enemys_models, new Vector3(rp.x + getRandomNumber(-4,4), 1.5f, rp.z + getRandomNumber(-4,4)), player, core, dungeGen);
            }
        }
        return es;
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public void addColliders(){
        if(room.colliders != null){
            //System.out.println(Arrays.toString(room.colliders));
            Physics physics = core.getPhysics();
            Vector3 rp = room.getPosition();
            for (int i = 0; i < room.colliders.length; i++){
                CubeCollider c = room.colliders[i].clone();
                c.pos = new Vector3(c.pos.x+rp.x,c.pos.y+rp.y,c.pos.z+rp.z);
                physics.addCubeCollider(c);
            }
        }

    }

    public void addDoorsColliders(){
        Physics physics = core.getPhysics();
        for (int i = 0; i < 4; i++){
            if (doors[i] != null && doors[i].getActive()){
               // physics.addCubeCollider(doors[i].getPosition(), new Vector3(0.1f));
                Vector3 pos = doors[i].getPosition().clone();
                pos.y = 0;
                if(i == 0) {
                    pos.z+=0.5f;
                    physics.addCubeCollider(pos, new Vector3(2f));
                }else if(i == 1){
                    pos.x+=0.5f;
                    physics.addCubeCollider(pos, new Vector3(2f));
                }else if(i == 2){
                    pos.z+=0.5f;
                    physics.addCubeCollider(pos, new Vector3(2f));
                }else {
                    pos.x+=0.5f;
                    physics.addCubeCollider(pos, new Vector3(2f));
                }
            }
        }
    }

    public void setDoorsPosition(){
        Vector3 pos = room.getPosition();
        for (int i = 0; i < 4; i++){
            if(doors[i] != null){
                if(i == 0){
                    doors[0].setPosition(new Vector3(pos.x, 0.2f, pos.z - roomRes/2));
                    doors[0].setRotate(new Vector3(0,0,0));
                }else if(i == 1){
                    doors[1].setPosition(new Vector3(pos.x + roomRes/2 - doorRes.x, 0.2f, pos.z));
                    doors[1].setRotate(new Vector3(0,90,0));
                }else if(i == 2){
                    doors[2].setPosition(new Vector3(pos.x, 0.2f, pos.z + roomRes/2 - doorRes.x));
                    doors[2].setRotate(new Vector3(0,0,0));
                }else {
                    doors[3].setPosition(new Vector3(pos.x - roomRes/2, 0.2f, pos.z) );
                    doors[3].setRotate(new Vector3(0,90,0));
                }
                doors[i].setActive(true);
            }

        }
    }

    public void delete(){
        core.getRenderer().deleteObject(room);
        for (int i = 0; i < doors.length; i++){
            core.getRenderer().deleteObject(doors[i]);
        }
//        for (int i = 0; i < cubeColliders.size(); i++){
//            core.getPhysics().deleteCubeCollider(cubeColliders.get(i));
//        }
    }

    @Override
    public Room clone() {
        try {
            RenderObject newRoomObj = core.getRenderer().addObject(room.getModel());

            RenderObject[] newDoors = new RenderObject[4];
            for(int i = 0; i < 4; i++){
                if(doors[i] != null){
                    newDoors[i] = core.getRenderer().addObject(doors[i].getModel());
                    newDoors[i].setActive(false);
                }
            }

//            SquareCollider[] newColliders = new SquareCollider[squareColliders.size()];
//            for(int i = 0; i < squareColliders.size(); i++){
//                    newColliders[i] = core.getPhysics().addSquareCollider(squareColliders.get(i).pos,squareColliders.get(i).size);//squareColliders[i].clone();
//            }

            Room newRoom = (Room)super.clone();
            newRoom.room = newRoomObj;
            newRoom.doors = newDoors;

            return newRoom;
        }
        catch( CloneNotSupportedException ex ) {
            throw new InternalError();
        }
    }
}
