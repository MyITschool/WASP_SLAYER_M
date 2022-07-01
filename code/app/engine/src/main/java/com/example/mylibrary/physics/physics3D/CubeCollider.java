package com.example.mylibrary.physics.physics3D;


import com.example.mylibrary.core.GameObject;
import com.example.mylibrary.math.Vector3;

public final class CubeCollider extends GameObject implements Cloneable{
    // тип
    private int type = 0;
    // тело к которому относится
    private RigidBody rigidBody;
    // получить тип
    public int getType(){
        return type;
    }
    // установить тип
    public void setType(int type){
        this.type=type;
    }
    // получить тело к которому относится
    public RigidBody getColliderRigidBody(){
        return rigidBody;
    }
    // установить тело
    public void setColliderRigidBody(RigidBody rigidBody){
        this.rigidBody=rigidBody;
    }

    public CubeCollider(Vector3 pos, Vector3 size){
        this.position = pos;
        this.scale = size;
    }

    public CubeCollider(GameObject obj){
        this.position = obj.getPosition();
        this.scale = obj.getScale();
    }

    @Override
    public String toString() {
        return "pos: "+position+", size: "+scale+", type: "+type+", obj: "+rigidBody;
    }

    @Override
    public CubeCollider clone() {
        try {
            CubeCollider newCC = (CubeCollider)super.clone();
            newCC.position = this.position.clone();
            newCC.scale = this.scale.clone();
            return newCC;
        }catch( CloneNotSupportedException ex ) {
            throw new InternalError();
        }
    }
}
