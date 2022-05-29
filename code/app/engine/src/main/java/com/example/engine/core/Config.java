package com.example.engine.core;

import com.example.engine.math.Vector2Int;
import com.example.engine.math.Vector3;
import com.example.engine.math.Vector4;

public class Config {
    public Vector4 clear_color = new Vector4(0.5f,0.5f,0.5f,1.0f); //{0.5f,0.5f,0.5f,1.0f};
    public Vector3 global_light_dir = new Vector3(0.0f,1.0f,0.0f); //{0.0f,1.0f,0.0f};
    public Vector3 global_light_color = new Vector3(1.0f,1.0f,1.0f); //{1.0f,1.0f,1.0f};
    public float ambient = 0.1f;
    public boolean shadow_map = false;
    public float soft_shadow_cof = 10;
    public float bias = 0.005f;
    public int ultroSoftShadow = 0;
    public float max_render_depth = 100;

    public Vector4 fog_color = new Vector4(1);

    private final Core core;

    public void setClear_color(Vector4 clear_color){
        //if(clear_color.length==4){
            this.clear_color = clear_color;
            core.getRenderer().setClearColor=true;
        //}else {
          //  System.err.println("clear_color.length!=4   " + "clear_color.length: " + clear_color.length);
        //}
    }
    public Vector4 getClear_color(){
        return clear_color;
    }

    public void setGlobal_light_dir(Vector3 global_light_dir){
        this.global_light_dir=global_light_dir;
    }
    public void setGlobal_light_color(Vector3 global_light_color){
        this.global_light_color=global_light_color;
    }
    public Vector3 getGlobal_light_dir(){
        return global_light_dir;
    }
    public Vector3 getGlobal_light_color(){
        return global_light_color;
    }

    public Config(Core core){
        this.core=core;
    }

    public Vector2Int shadow_res;

    public float usRandL=0;

}
