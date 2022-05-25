package com.example.engine.physics;

import com.example.engine.math.Vector3;

public class CubeCollider implements Cloneable{
    public Vector3 pos;
    public Vector3 size;
    private int type = 0;
    private Object obj;

    public int getType(){
        return type;
    }
    public void setType(int type){
        this.type=type;
    }

    public Object getColliderObj(){
        return obj;
    }
    public void setColliderObj(Object obj){
        this.obj=obj;
    }

    public CubeCollider(Vector3 pos, Vector3 size){
        this.pos = pos;
        this.size = size;
    }

    @Override
    public String toString() {
        return "pos: "+pos+", size: "+size+", type: "+type+", obj: "+obj;
    }

    @Override
    public CubeCollider clone() {
        try {
            CubeCollider newCC = (CubeCollider)super.clone();
            newCC.pos = this.pos.clone();
            newCC.size = this.size.clone();
            return newCC;
        }catch( CloneNotSupportedException ex ) {
            throw new InternalError();
        }
    }
}
