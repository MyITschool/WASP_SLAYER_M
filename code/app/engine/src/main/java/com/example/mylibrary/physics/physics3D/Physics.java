package com.example.mylibrary.physics.physics3D;

import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.render.Renderer;

import java.util.ArrayList;

public final class Physics {

    private final Renderer renderer;
    //ускорение свободного падения
    public Vector3 g = new Vector3(0,-9.8f,0);

    public Physics(Renderer renderer){
        this.renderer=renderer;
    }
    // все коллайдеры
    private final ArrayList<CubeCollider> cubeColliders = new ArrayList<>();
    // добавить коллайдер
    public void addCubeCollider(CubeCollider collider){
        cubeColliders.add(collider);
    }
    // удалить коллайдер
    public void deleteCubeCollider(CubeCollider collider){
        cubeColliders.remove(collider);
    }
    // удалить все коллайдеры
    public void clear(){
        cubeColliders.clear();
    }
    // проверить столкновение
    public boolean testCollisionCube(CubeCollider collider){
        return testCollisionCube(collider, new Collision());
    }
    public boolean testCollisionCube(CubeCollider collider, Collision collision){
        for (int i = 0; i < cubeColliders.size(); i++){
            CubeCollider ge = cubeColliders.get(i);
            if (ge.activity && ge != collider && CubeInters(collider, ge)){
                collision.collider=ge;
                return true;
            }
        }

        return false;
    }
    // проверка пересечения кубов
    public boolean CubeInters(CubeCollider collider, CubeCollider collider1) {
        float[] cube0 = new float[]{collider.getPosition().x-collider.getScale().x, collider.getPosition().y-collider.getScale().y, collider.getPosition().z-collider.getScale().z,
                collider.getPosition().x+collider.getScale().x, collider.getPosition().y+collider.getScale().y, collider.getPosition().z+collider.getScale().z};
        float[] cube1 = new float[]{collider1.getPosition().x-collider1.getScale().x, collider1.getPosition().y-collider1.getScale().y, collider1.getPosition().z-collider1.getScale().z,
                collider1.getPosition().x+collider1.getScale().x, collider1.getPosition().y+collider1.getScale().y, collider1.getPosition().z+collider1.getScale().z};
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

    // рэй каст ну понятно
    public boolean rayCastCamera(Hit hit, float minDepth, float maxDepth){
        Vector3 camr = renderer.camera.getRotation();
        Vector3 rd = new Vector3((float)Math.sin((camr.y)*Math.PI/180), (float)-Math.sin((camr.x)*Math.PI/180), (float)-Math.cos((camr.y)*Math.PI/180));
        Vector3 ro = renderer.camera.getPosition().clone();
        rd.norm();
        return rayCast(hit, ro, rd, minDepth, maxDepth);
    }
    public boolean rayCastCamera(Hit hit, float minDepth, float maxDepth, CubeCollider collider){
        Vector3 camr = renderer.camera.getRotation();
        Vector3 rd = new Vector3((float)Math.sin((camr.y)*Math.PI/180), (float)-Math.sin((camr.x)*Math.PI/180), (float)-Math.cos((camr.y)*Math.PI/180));
        Vector3 ro = renderer.camera.getPosition().clone();
        rd.norm();
        return rayCast(hit, ro, rd, minDepth, maxDepth, collider);
    }
    public boolean rayCast(Hit hit, Vector3 ro, Vector3 rd, float minDepth, float maxDepth){
        return rayCast(hit,ro,rd,minDepth,maxDepth,null);
    }
    public boolean rayCast(Hit hit, Vector3 ro, Vector3 rd, float minDepth, float maxDepth, CubeCollider collider){
        float min = maxDepth;
        Vector3 rdir = rd.clone();
        rdir.norm();
        Ray r = new Ray(ro, rdir);

        for(int i = 0; i < cubeColliders.size(); i++){
            CubeCollider c = cubeColliders.get(i);
            Brick b = new Brick(c.getPosition(), Vector.add(c.getPosition(), c.getScale()));
            boolean rc = IntersectRayBrick(r, b, minDepth, maxDepth);
            if(rc && collider != c){
                float dist = Vector.sub(ro, c.getPosition()).length();
                if(dist < min){
                    min=dist;
                    hit.distance=dist;
                    hit.collider=c;
                }
            }
        }

        if(min==maxDepth)
            return false;
        return true;
    }
    // проверка попадания луча
    public boolean IntersectRayBrick(Ray ray, Brick brick, double t_near, double t_far) {
        if ( ray.start[0] >= brick.min_point[0] && ray.start[0] <= brick.max_point[0] &&
                ray.start[1] >= brick.min_point[1] && ray.start[1] <= brick.max_point[1] &&
                ray.start[2] >= brick.min_point[2] && ray.start[2] <= brick.max_point[2] ) {
            return true;
        }

        double t1, t2;

        for (int i = 0; i < 3; i++) {
            if ( Math.abs(ray.direction[i]) >= Math.ulp(.0)){
                t1 = (brick.min_point[i] - ray.start[i]) / ray.direction[i];
                t2 = (brick.max_point[i] - ray.start[i]) / ray.direction[i];

                if (t1 > t2){
                    double tmp = t1;
                    t1 = t2;
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
            }
            else {
                if ( ray.start[i] < brick.min_point[i] || ray.start[i] > brick.max_point[i] )
                    return false;
            }
        }
        return (t_near <= t_far && t_far >=0);
    }
}
