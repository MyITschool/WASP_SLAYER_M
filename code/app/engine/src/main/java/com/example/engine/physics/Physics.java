package com.example.engine.physics;

import com.example.engine.core.Core;
import com.example.engine.math.Vector;
import com.example.engine.math.Vector3;
import com.example.engine.render.RendererGL;

import java.util.ArrayList;

public class Physics {
    private final Core core;
    private final RendererGL renderer;
    public Physics(Core core){
        this.core=core;
        renderer=core.getRenderer();
    }

    private ArrayList<CubeCollider> cubeColliders = new ArrayList<>();

    public CubeCollider addCubeCollider(Vector3 pos, Vector3 size){
        cubeColliders.add(new CubeCollider(pos, size));
        return cubeColliders.get(cubeColliders.size()-1);
    }
    public CubeCollider addCubeCollider(CubeCollider collider){
        cubeColliders.add(collider);
        return cubeColliders.get(cubeColliders.size()-1);
    }
    public void deleteCubeCollider(CubeCollider collider){
        cubeColliders.remove(collider);
    }
    public CubeCollider getCubeCollider(int i){return cubeColliders.get(i);}
    public void clear(){
        cubeColliders = new ArrayList<>();
    }

    public boolean testCollisionCube(CubeCollider collider){
        float[] cube = new float[]{collider.pos.x-collider.size.x/2, collider.pos.y-collider.size.y/2, collider.pos.z-collider.size.z/2,
                collider.pos.x+collider.size.x/2, collider.pos.y+collider.size.y/2, collider.pos.z+collider.size.z/2,};

        for (int i = 0; i < cubeColliders.size(); i++){
            CubeCollider ge = cubeColliders.get(i);
            float[] cube1 = new float[]{ge.pos.x-ge.size.x/2, ge.pos.y-ge.size.y/2, ge.pos.z-ge.size.z/2,
                    ge.pos.x+ge.size.x/2, ge.pos.y+ge.size.y/2, ge.pos.z+ge.size.z/2,};
            if (ge != collider && CubeInters(cube, cube1))
                return true;
        }

        return false;
    }

    private boolean CubeInters(float[] cube0, float[] cube1 ) {
        for (int i = 0; i < 3; ++i)
            if (!Inters(cube0[i], cube0[i + 3], cube1[i], cube1[i + 3]))
                return false;

        return true;
    }
    private boolean Inters( float min1, float max1, float min2, float max2 ) {
        if (min1 > max2) return false;
        if (max1 < min2) return false;
        return true;
    }

    public boolean rayCastCamera(Hit hit, float minDepth, float maxDepth){
        Vector3 camr = renderer.camera.getRotate();
        Vector3 rd = new Vector3((float)Math.sin((camr.y)*Math.PI/180), (float)-Math.sin((camr.x)*Math.PI/180), (float)-Math.cos((camr.y)*Math.PI/180));
        Vector3 ro = renderer.camera.getPosition().clone();
        ro.setXYZ(-ro.x, -ro.y, -ro.z);
        rd.norm();
        Ray r = new Ray(ro, rd);

        float min = maxDepth;

        for(int i = 0; i < cubeColliders.size(); i++){
            CubeCollider c = cubeColliders.get(i);
            Brick b = new Brick(c.pos, Vector.add(c.pos, c.size));
            boolean rc = IntersectRayBrick(r, b, minDepth, maxDepth);
            if(rc){
                float dist = Vector.sub(ro, c.pos).length();
                if(dist < min){
                    min=dist;
                    hit.distance=dist;
                    hit.collider=c;
                    hit.index=i;
                }
            }
        }
        if(min==maxDepth)
            return false;
        return true;
    }

    public boolean rayCast(Hit hit, Vector3 ro, Vector3 rd, float minDepth, float maxDepth){
        float min = maxDepth;

        for(int i = 0; i < cubeColliders.size(); i++){
            CubeCollider c = cubeColliders.get(i);
            Ray r = new Ray(ro, rd);
            Brick b = new Brick(c.pos, Vector.add(c.pos, c.size));
            boolean rc = IntersectRayBrick(r, b, minDepth, maxDepth);
            if(rc){
                float dist = Vector.sub(ro, c.pos).length();
                if(dist < min){
                    min=dist;
                    hit=new Hit(dist, c, i);
                }
            }
        }

        if(min==maxDepth)
            return false;
        return true;
    }

    public boolean IntersectRayBrick(Ray ray, Brick brick, double t_near, double t_far) {

// check whether initial point is inside the parallelepiped
        if ( ray.start[0] >= brick.min_point[0] && ray.start[0] <= brick.max_point[0] &&
                ray.start[1] >= brick.min_point[1] && ray.start[1] <= brick.max_point[1] &&
                ray.start[2] >= brick.min_point[2] && ray.start[2] <= brick.max_point[2] ) {
            return true;
        }

// ray parameter
//        double t_near = std::numeric_limits::min(),
//                t_far = std::numeric_limits::max();
        double t1, t2;

// directions loop
        for (int i = 0; i < 3; i++) {
            if ( Math.abs(ray.direction[i]) >= Math.ulp(.0)){//std::numeric_limits::epsilon() ) {
                t1 = (brick.min_point[i] - ray.start[i]) / ray.direction[i];
                t2 = (brick.max_point[i] - ray.start[i]) / ray.direction[i];

                if (t1 > t2){
                    double tmp = t1;
                    t1 = t2;    //std::swap(t1, t2);
                    t2 = tmp;
                }
                if (t1 > t_near)
                    t_near = t1;
                if (t2 < t_far)
                    t_far = t2;

                if (t_near > t_far)
                    return false;
                if (t_far < 0.0)
                    return false;
            } // if
            else {
                if ( ray.start[i] < brick.min_point[i] || ray.start[i] > brick.max_point[i] )
                    return false;
            }
        } // for
        return (t_near <= t_far && t_far >=0);
    }

    public void printCubeColliders(){
        for (int i = 0; i < cubeColliders.size();i++){
            System.out.println(cubeColliders.get(i).pos.x+"|"+cubeColliders.get(i).pos.y+"|"+cubeColliders.get(i).pos.z+
                    " || "+cubeColliders.get(i).size.x+"|"+cubeColliders.get(i).size.y+"|"+cubeColliders.get(i).size.z);
        }
    }
}
