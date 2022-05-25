package com.example.engine.model;

import com.example.engine.math.Vector3;
import com.example.engine.physics.CubeCollider;

import java.util.Arrays;

public class Model {
    public float[] v;
    public float[] vn;
    public float[] vt;
    public float[] material = new float[]{-9, -1, -1, 0.5f, 32, 1};
    public Vector3 color=new Vector3(1);
    public int texture = -1;
    public CubeCollider[] colliders;
    public Vector3 scale=new Vector3(1);
    public Vector3 pos=new Vector3(0);
    public Vector3 rot=new Vector3(0);


    @Override
    public String toString() {
        return getClass().getName()
                +'\n'+"material: "+ Arrays.toString(material)
                +'\n'+"color: "+ color
                +'\n'+"texture: "+ texture
                +'\n'+"scale: "+ scale
                +'\n'+"pos: "+ pos
                +'\n'+"rot: "+ rot
                +'\n'+"v: "+ Arrays.toString(v)
                +'\n'+"vn: "+ Arrays.toString(vn)
                +'\n'+"vt: "+ Arrays.toString(vt);
    }
}
