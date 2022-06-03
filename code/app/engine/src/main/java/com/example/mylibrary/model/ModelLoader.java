package com.example.mylibrary.model;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.math.Vector3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public final class ModelLoader {
    private final Core core;

    private final HashMap<String, VertexesData> models = new HashMap<>();

    public ModelLoader(Core core){
        this.core=core;
    }

    public VertexesData loadModel(String src, String key) {
        ArrayList<Float> vn = new ArrayList<>();
        ArrayList<Float> vt = new ArrayList<>();
        ArrayList<Float> v = new ArrayList<>();

        ArrayList<Float> Ovn = new ArrayList<>();
        ArrayList<Float> Ovt = new ArrayList<>();
        ArrayList<Float> Ov = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(core.getAssets().open(src), StandardCharsets.UTF_8));

            String mLine;
            while ((mLine = reader.readLine()) != null) {

                if(mLine.startsWith("vn ")){
                    String[] vnA = mLine.split(" ");
                    vn.add(Float.parseFloat(vnA[1]));
                    vn.add(Float.parseFloat(vnA[2]));
                    vn.add(Float.parseFloat(vnA[3]));
                }else if(mLine.startsWith("vt ")){
                    String[] vtA = mLine.split(" ");
                    vt.add(Float.parseFloat(vtA[1]));
                    vt.add(Float.parseFloat(vtA[2]));
                }else if(mLine.startsWith("v ")){
                    String[] vA = mLine.split(" ");
                    v.add(Float.parseFloat(vA[1]));
                    v.add(Float.parseFloat(vA[2]));
                    v.add(Float.parseFloat(vA[3]));
                }else if(mLine.startsWith("f ")){
                    String[] fA = mLine.split(" ");
                    for (int i = 1; i < 4; i++){
                        String[] vA = fA[i].split("/");

                        int vi = Integer.parseInt(vA[0])-1;
                        Ov.add(v.get(vi*3));
                        Ov.add(v.get(vi*3+1));
                        Ov.add(v.get(vi*3+2));

                        int vni = Integer.parseInt(vA[2])-1;
                        Ovn.add(vn.get(vni*3));
                        Ovn.add(vn.get(vni*3+1));
                        Ovn.add(vn.get(vni*3+2));

                        int vti = Integer.parseInt(vA[1])-1;
                        Ovt.add(vt.get(vti*2));
                        Ovt.add(vt.get(vti*2+1));
                    }
                }

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        VertexesData m = new VertexesData();

        Vector3 min = new Vector3(0);
        Vector3 max = new Vector3(0);

        float[] arr = new float[Ov.size()];
        for (int i = 0;i<Ov.size();i++){
            arr[i]=Ov.get(i);

            if(i%3==0){
                if(arr[i] > max.x){
                    max.x=arr[i];
                }else if(arr[i] < min.x){
                    min.x=arr[i];
                }
            }else if(i%3-1==0){
                if(arr[i] > max.y){
                    max.y=arr[i];
                }else if(arr[i] < min.y){
                    min.y=arr[i];
                }
            }else if(i%3-2==0){
                if(arr[i] > max.z){
                    max.z=arr[i];
                }else if(arr[i] < min.z){
                    min.z=arr[i];
                }
            }
        }

        m.maxPoint = max;
        m.minPoint = min;

        m.vertexes=arr;
        ////////////////////////////////////////
        arr = new float[Ovn.size()];
        for (int i = 0;i<Ovn.size();i++){
            arr[i]=Ovn.get(i);
        }
        m.vertexes_normal=arr;
        ////////////////////////////////////////
        arr = new float[Ovt.size()];
        for (int i = 0;i<Ovt.size();i++){
            arr[i]=Ovt.get(i);
        }
        m.vertexes_texture=arr;

        addModel(m, key);

        return m;
    }
    public VertexesData getModel(String key){
        return models.get(key);
    }
    public void addModel(VertexesData model, String key){
        models.put(key, model);
    }
    public void deleteModel(String key){
        models.remove(key);
    }
    public void clearModels(){
        models.clear();
    }
}

