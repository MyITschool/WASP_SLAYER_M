package com.example.mylibrary.physics;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.GameObject;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.render.RenderObject;

public class RigidBody implements Updated {

    protected CubeCollider collider;

    public float mass = 1;
    public float drag = 2.6f;
    public boolean usGravity = true;
    public boolean activity = true;

    protected Vector3 velocity = new Vector3(0);

    protected final Physics physics;
    protected final GameObject gameObject;

    public RigidBody(CubeCollider collider, GameObject gameObject, Core core){
        physics = core.getPhysics();
        this.collider = collider;
        this.gameObject = gameObject;
        collider.setColliderRigidBody(this);

        physics.addCubeCollider(collider);

        core.getRenderer().addUpdated(this);
    }

    public RigidBody(GameObject gameObject, Core core){
        physics = core.getPhysics();
        this.gameObject = gameObject;

        core.getRenderer().addUpdated(this);
    }

    protected void setColliders(CubeCollider collider){
        physics.deleteCubeCollider(this.collider);
        this.collider.setColliderRigidBody(null);

        collider.setColliderRigidBody(this);
        physics.addCubeCollider(collider);

        this.collider=collider;
    }

    public void setVelocity(Vector3 velocity){
        this.velocity = velocity;
    }
    public void addVelocity(Vector3 velocity){
        this.velocity = Vector.add(this.velocity,velocity);
    }
    public Vector3 getVelocity(){return velocity;}

    @Override
    public void update(float dt) {

        if(activity && gameObject.activity){
            if(usGravity){
                velocity=Vector.add(velocity, Vector.mul(physics.g, dt));
            }
            if (drag>0){
                Vector3 ddm = Vector.mul(Vector.mul(Vector.mul(velocity, velocity), drag/mass),
                        Vector.div(velocity, Vector.abs(velocity)));

                Vector3 v = Vector.abs(velocity);
                Vector3 df = Vector.abs(ddm);

                velocity.setXYZ((velocity.x == 0 && v.x < df.x) ? 0 : velocity.x-ddm.x,
                        velocity.y == 0 ? 0 : velocity.y-ddm.y,
                        velocity.z == 0 ? 0 : velocity.z-ddm.z);
            }

            Vector3 v_dir = velocity.clone();
            v_dir.norm();

            collider.pos=Vector.add(collider.pos,velocity);

            if(!physics.testCollisionCube(collider)){
                Hit hit = new Hit();
                if (physics.rayCast(hit,  gameObject.getPosition(), velocity, 0, velocity.length(), collider)) {
                    v_dir = Vector.mul(v_dir, hit.distance);
                    collider.pos=Vector.sub(collider.pos,velocity);
                    velocity=v_dir;
                    collider.pos=Vector.add(collider.pos,velocity);
                }
            }else {
                collider.pos=Vector.sub(collider.pos,velocity);
                velocity.setXYZ(0,0,0);
            }

            if(physics.testCollisionCube(collider)){
                velocity.setXYZ(0,dt,0);
                collider.pos=Vector.sub(collider.pos, velocity);
            }

            gameObject.setPosition(Vector.add(velocity, gameObject.getPosition()));

        }

    }
}
