package com.example.tao2.scenes;


import com.example.engine.audio.AudioLoader;
import com.example.engine.core.Config;
import com.example.engine.core.Core;
import com.example.engine.core.Scene;
import com.example.engine.math.Vector2;
import com.example.engine.math.Vector2Int;
import com.example.engine.math.Vector3;
import com.example.engine.model.Model;
import com.example.engine.model.ModelLoader;
import com.example.engine.render.RendererGL;
import com.example.tao2.dunge.DungeGen;
import com.example.tao2.dunge.Room;
import com.example.tao2.updated.Player;

public class DungeScene extends Scene {
    public DungeScene(Core core) {
        super(core);
    }

    private final Config config = core.getConfig();
    private final RendererGL renderer = core.getRenderer();
    private final ModelLoader modelLoader = core.getModelLoader();
    private final AudioLoader audioLoader = core.getAudioLoader();

    @Override
    public void preload() {

    }

    @Override
    public void start() {

        audioLoader.getAudio(2).play(true, new Vector2(0.7f));
        audioLoader.getAudio(1).play(true, new Vector2(0.1f));

        Player player = new Player(core);

        /*RenderObject c = renderer.addObject(renderer.getCubeVert());
        c.setColor(new Vector3(0.5f,0.5f,0.5f));
        c.setPosition(new Vector3(5f, 0.001f, 5));
        core.getPhysics().addCubeCollider(new Vector3(5f, 0.001f, 5), new Vector3(3));
         */
        Model[] roomsModel = new Model[]{
                modelLoader.getModel(0),
                modelLoader.getModel(1),
                modelLoader.getModel(2),
                modelLoader.getModel(3),
        };
        Model bee = modelLoader.getModel(4);


        ////////////////////////////////////////////////////////////////////////////////////////////
        Room startRoom = new Room(core);
        Vector2 doorRes = new Vector2(1, 1);

        createRoom(startRoom, roomsModel[0], 10, doorRes, new Vector3(1,1.4f,1));
        createDoor(startRoom, roomsModel[1], new int[]{0,1,2,3}, new Vector3(10,17,10));

        startRoom.roomRes = 10;
        startRoom.setDoorsPosition();

        //////////////////////////////////////////////////////////////////////////////
        Room[] rooms = new Room[]{
                new Room(core)
                ,new Room(core)
                , new Room(core)
//                new Room(core)
        };
        createRoom(rooms[0], roomsModel[0], 10, doorRes, new Vector3(1,1.4f,1));
        createDoor(rooms[0], roomsModel[1], new int[]{0,1,2,3}, new Vector3(10,17,10));

        createRoom(rooms[1], roomsModel[2], 10, doorRes, new Vector3(1,1.4f,1));
        createDoor(rooms[1], roomsModel[1], new int[]{2}, new Vector3(10,17,10));

        rooms[0].enemys_count = 5;
        rooms[0].enemys_models = bee;
        rooms[1].enemys_count = 5;
        rooms[1].enemys_models = bee;

        createRoom(rooms[2], roomsModel[3], 1, doorRes, new Vector3(1,1.4f,1));
        createDoor(rooms[2], roomsModel[1], new int[]{0,2}, new Vector3(10,17,10));


        for (Room room : rooms) {
            room.room.setActive(false);
        }

        /////////////////////////////////////////////////////////////



        DungeGen dungeGen = new DungeGen(player, core);
        dungeGen.startRoom = startRoom;
        dungeGen.rooms = rooms;
        dungeGen.res = new Vector2Int(10,10);
        dungeGen.roomRes = 10;
        dungeGen.maxRooms = 10;

        dungeGen.start();

        for (Room room : rooms) {
            room.delete();
        }

    }

    private void createRoom(Room room, Model model, float roomRes, Vector2 doorRes, Vector3 scale){
        room.room = renderer.addObject(model);
        room.room.setUsNormal(true);
        room.room.setUsTexture(model.texture);
        room.roomRes = roomRes;
        room.doorRes = doorRes.clone();
        room.room.setScale(scale);

//        room.enemys_models = bee;
//        room.enemys_count = 3;
    }

    private void createDoor(Room room, Model model, int[] activeDoors, Vector3 scale){
        room.doors[activeDoors[0]] = renderer.addObject(model.v);
        room.doors[activeDoors[0]].setNormals(model.vn);
        room.doors[activeDoors[0]].setTextureCoords(model.vt);
        room.doors[activeDoors[0]].setActive(true);
        room.doors[activeDoors[0]].setUsNormal(true);
        room.doors[activeDoors[0]].setUsTexture(model.texture);
        room.doors[activeDoors[0]].setScale(scale);

        for(int i = 1; i < activeDoors.length; i++){
            room.doors[activeDoors[i]] = renderer.addObject(room.doors[activeDoors[0]].getModel());
        }
    }

    @Override
    public void resume() {
        audioLoader.getAudio(2).play(true, new Vector2(0.7f));
        audioLoader.getAudio(1).play(true, new Vector2(0.1f));
    }
    @Override
    public void update() {
    }

    @Override
    public void pause() {
        //audioLoader.getAudio(3).stop();
        audioLoader.getAudio(2).stop();
        audioLoader.getAudio(1).stop();
    }

    @Override
    public void dispose() {
//        audioLoader.getAudio(3).dispose();
//        audioLoader.getAudio(2).dispose();
//        audioLoader.getAudio(1).dispose();
    }
}
