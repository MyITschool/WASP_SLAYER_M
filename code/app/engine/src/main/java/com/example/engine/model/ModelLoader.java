package com.example.engine.model;

import com.example.engine.core.Core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

public class ModelLoader {
    private final Core core;
    public ModelLoader(Core core){
        this.core=core;
    }

    public Model loadModel(String src) {
        ArrayList<Float> vn = new ArrayList<>();
        ArrayList<Float> vt = new ArrayList<>();
        ArrayList<Float> v = new ArrayList<>();

        ArrayList<Float> Ovn = new ArrayList<>();
        ArrayList<Float> Ovt = new ArrayList<>();
        ArrayList<Float> Ov = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(core.getAssets().open(src), "UTF-8"));

            // do reading, usually loop until end of file reading
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
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        //System.out.println(Ovt);
//        System.out.println(v.size());

        float[] arr = new float[Ov.size()];
        for (int i = 0;i<Ov.size();i++){
            arr[i]=Ov.get(i);
        }
        Model m = new Model();
        m.v=arr;
        ////////////////////////////////////////
        arr = new float[Ovn.size()];
        for (int i = 0;i<Ovn.size();i++){
            arr[i]=Ovn.get(i);
        }
        m.vn=arr;
        ////////////////////////////////////////
        arr = new float[Ovt.size()];
        for (int i = 0;i<Ovt.size();i++){
            arr[i]=Ovt.get(i);
        }
        m.vt=arr;


        return m;
    }

    public Model loadModel(String src, boolean save){
        Model m = loadModel(src);
        if (save){
            addModel(m);
        }

        return m;
    }

    public Model[] loadModels(String[] src){
        Model[] ms = new Model[src.length];
        for (int i = 0; i < src.length; i++){
            ms[i] = loadModel(src[i]);
        }
        return ms;
    }
    public Model[] loadModels(String[] src, boolean save){
        Model[] ms = new Model[src.length];
        for (int i = 0; i < src.length; i++){
            ms[i] = loadModel(src[i], save);
        }
        return ms;
    }

    private LinkedList<Model> models = new LinkedList<>();

    public Model getModel(int i){
        return models.get(i);
    }
    public Model addModel(Model model){
        models.add(model);
        return models.get(models.size()-1);
    }
    public void deleteModel(Model model){
        models.remove(model);
    }
    public void clearModels(){
        models = new LinkedList<>();
    }
}

