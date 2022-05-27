package com.example.tao2.dunge;

import com.example.engine.core.Core;
import com.example.engine.core.Updated;
import com.example.engine.math.Vector2;
import com.example.engine.math.Vector2Int;
import com.example.engine.math.Vector3;
import com.example.engine.math.Vector4;
import com.example.engine.render.RenderUI;
import com.example.tao2.scenes.DungeScene;
import com.example.tao2.scenes.NextLvlAnim;
import com.example.tao2.updated.Player;

import java.util.ArrayList;
import java.util.HashSet;

public class DungeGen extends Updated {
    public Room[] rooms;
    public Room startRoom;
    public Vector2Int res;
    public float roomRes;
    public int maxRooms;
    private RenderUI bs;
    private Room[][] spawnedRooms;
    private final Core core;
    private final Player player;
    public DungeGen(Player player, Core core){
        this.core = core;
        this.player = player;
    }

    private int es;



    public void start(){
        spawnedRooms = new Room[res.x][res.y];
        spawnedRooms[res.x/2][res.y/2] = startRoom;
        //startRoom.room.setPosition(new float[]{(res.x/2) * roomRes, 0, (res.y / 2) * roomRes});

        for(int i = 0; i < maxRooms; i++)
        {
            placeRoom();
        }

        for (int x = 0; x < res.y; x++){
            for (int y = 0; y < res.y; y++){
                if(spawnedRooms[x][y] != null){
                    spawnedRooms[x][y].room.setActive(true);
                    spawnedRooms[x][y].addLight();
                    spawnedRooms[x][y].addDoorsColliders();

                    spawnedRooms[x][y].addColliders();

                    es+=spawnedRooms[x][y].spawn_enemy(player, this);
                }
            }
        }
        if (es==0){
            core.setScene(new DungeScene(core), false);
        }
    }

    private long det = -1;
    private int dcd = 500;

    public void enemydeath(){
        es--;
        if (es==0){
            core.getLoop().clear();
            core.getLoop().addUpdateObj(this);

            RenderUI dt = core.getRenderer().addUI();
            dt.setTexture(7);
            dt.setScale(new Vector2(0.8f, -0.3f));
            dt.setPosition(new Vector3(0,0,0));

            bs = core.getRenderer().addUI();
            bs.setColor(new Vector4(0));

            det = System.currentTimeMillis()+dcd;
        }
    }

    private void placeRoom(){
        HashSet<Vector2Int> vacant = new HashSet<Vector2Int>();

        for(int x = 0; x < res.x; x++)
        {
            for (int y = 0; y < res.y; y++)
            {
                if (spawnedRooms[x][y] == null) continue;

                if (x > 0 && spawnedRooms[x - 1][y] == null) vacant.add(new Vector2Int(x - 1, y));
                if (y > 0 && spawnedRooms[x][y - 1] == null) vacant.add(new Vector2Int(x, y - 1));

                if (x < res.x - 1 && spawnedRooms[x + 1][y] == null) vacant.add(new Vector2Int(x + 1, y));
                if (y < res.y - 1 && spawnedRooms[x][y + 1] == null) vacant.add(new Vector2Int(x, y + 1));

            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        Room newRoom =  rooms[ (int) (Math.random() * rooms.length) ].clone();

        newRoom.roomRes = roomRes;

        Vector2Int position = vacant.toArray(new Vector2Int[vacant.size()])[ (int)(Math.random() * vacant.size()) ];
        newRoom.room.setPosition(new Vector3((position.x - res.x/2) * roomRes, 0, (position.y - res.y/2) * roomRes));

        //newRoom.setRotate((int) (Math.random() * 4));
        //newRoom.addLight();

        newRoom.setDoorsPosition();

        int i = 0;
        while (!connectDoor(newRoom, position) && i < 16){
            i++;
            //newRoom.setRotate((int) (Math.random() * 4));
            position = vacant.toArray(new Vector2Int[vacant.size()])[ (int)(Math.random() * vacant.size()) ];
            newRoom.room.setPosition(new Vector3((position.x - res.x/2) * roomRes, 0, (position.y - res.y/2) * roomRes));
            newRoom.setDoorsPosition();
        }
        if(i < 16){
//            newRoom.room.setActive(true);
//            newRoom.addLight();
//            newRoom.addDoorsColliders();
            spawnedRooms[position.x][position.y] = newRoom;
        }else {
            newRoom.delete();
        }

        /*if(!connectDoor(newRoom, position)) {
            for (int i = 0; i < 4; i++){
                if(newRoom.doors[i]!=null) newRoom.doors[i].setActive(false);
            }
            return;
        }else {
            newRoom.room.setActive(true);
            spawnedRooms[position.x][position.y] = newRoom;
        }*/

    }

    private boolean connectDoor(Room room, Vector2Int pos){
        try {
            ArrayList<Integer> neigh = new ArrayList<Integer>();

        if (spawnedRooms[pos.x][pos.y-1] != null) if(room.doors[0] != null && pos.y < res.y - 1 && spawnedRooms[pos.x][pos.y-1].doors[2] != null) neigh.add(0); // up

        if (spawnedRooms[pos.x][pos.y+1] != null) if(room.doors[2] != null && pos.y > 0 && spawnedRooms[pos.x][pos.y+1].doors[0] != null) neigh.add(2); // down

        if (spawnedRooms[pos.x+1][pos.y] != null) if(room.doors[1] != null && pos.x < res.x - 1 && spawnedRooms[pos.x+1][pos.y].doors[3] != null) neigh.add(1); // right

        if (spawnedRooms[pos.x-1][pos.y] != null) if(room.doors[3] != null && pos.x > 0 && spawnedRooms[pos.x-1][pos.y].doors[1] != null) neigh.add(3); // left

        if(neigh.size() == 0){
            return false;
        }

        int selectedDirection = neigh.get( (int) (Math.random() * neigh.size()) );


            if (selectedDirection == 0){
                room.doors[0].setActive(false);
                Room selectedRoom = spawnedRooms[pos.x][pos.y-1];
                selectedRoom.doors[2].setActive(false);
            }else if(selectedDirection == 2){
                room.doors[2].setActive(false);
                Room selectedRoom = spawnedRooms[pos.x][pos.y+1];
                selectedRoom.doors[0].setActive(false);
            }else if(selectedDirection == 1){
                room.doors[1].setActive(false);
                Room selectedRoom = spawnedRooms[pos.x+1][pos.y];
                selectedRoom.doors[3].setActive(false);
            }else if(selectedDirection == 3){
                room.doors[3].setActive(false);
                Room selectedRoom = spawnedRooms[pos.x-1][pos.y];
                selectedRoom.doors[1].setActive(false);
            }
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            return false;
        }


        return true;
    }


    private boolean nl = false;

    @Override
    public void update() {
        Vector2 touch = core.getTouchListener().getTouchDown(new Vector2(0), new Vector2(0.8f, 0.3f));
        if (touch.x != -1 && (System.currentTimeMillis() - det) > 0){
            //core.setScene(new DungeScene(core), false);
            nl = true;
            det = System.currentTimeMillis()+dcd;
        }

        if (nl && (System.currentTimeMillis() - det) < 0){
            bs.setColor(new Vector4(0,0,0, 1+(System.currentTimeMillis() - det) / (float)dcd));
        }else if (nl && (System.currentTimeMillis() - det) > 0){
            core.setScene(new NextLvlAnim(core), false);
        }


    }
}
