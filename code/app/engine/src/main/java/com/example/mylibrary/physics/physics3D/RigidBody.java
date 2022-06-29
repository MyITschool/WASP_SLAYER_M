package com.example.mylibrary.physics.physics3D;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.GameObject;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.render.Camera;
import com.example.mylibrary.render.Renderer;

public final class RigidBody implements Updated {

    private CubeCollider collider;

    public float mass = 1;
    public float drag = 0.05f;
    public float elasticity = 0.1f;
    public boolean usGravity = true;
    public boolean activity = true;

    private Vector3 velocity = new Vector3(0);

    private final Physics physics;
    private final GameObject gameObject;

    private final Camera camera;

    public RigidBody(CubeCollider collider, GameObject gameObject, Core core){
        physics = core.getPhysics();
        camera = core.getRenderer().camera;
        this.collider = collider;
        this.gameObject = gameObject;
        collider.setColliderRigidBody(this);

        physics.addCubeCollider(collider);

        core.getRenderer().addUpdated(this);
    }
    public RigidBody(GameObject gameObject, Core core){
        physics = core.getPhysics();
        this.gameObject = gameObject;
        camera = core.getRenderer().camera;
        core.getRenderer().addUpdated(this);
    }

    public void setColliders(CubeCollider collider){
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
    public void setForce(Vector3 force){
        this.velocity=Vector.div(force, mass);
    }
    public void addForce(Vector3 force){
        this.velocity=Vector.add(velocity, Vector.div(force, mass));
    }
    public Vector3 getForce(){return Vector.mul(velocity, mass);}

    private void gravityC(float dt){
        if (usGravity){
            velocity=Vector.add(velocity, Vector.mul(physics.g, dt));
        }
    }
    private void dragC(float dt){
        if (drag>0){
            Vector3 ddm = Vector.mul(Vector.mul(Vector.mul(velocity, velocity), drag*dt),
                    Vector.div(velocity, Vector.abs(velocity)));

            Vector3 v = Vector.abs(velocity);
            Vector3 df = Vector.abs(ddm);

            velocity.setXYZ((velocity.x == 0 && v.x < df.x) ? 0 : velocity.x-ddm.x,
                    (velocity.y == 0 && v.y < df.y) ? 0 : velocity.y-ddm.y,
                    (velocity.z == 0 && v.z < df.z) ? 0 : velocity.z-ddm.z);
        }
    }
    private Vector3 collisionC(Vector3 step){
        Collision collision = new Collision();
        if(physics.testCollisionCube(collider, collision)){
            collider.pos = Vector.sub(collider.pos, step);

            RigidBody rb = collision.collider.getColliderRigidBody();
            if(rb!=null&&rb.activity){
                rb.addForce( Vector.mul(Vector.mul(velocity, 1-elasticity), mass) );
            }
            ///////////////////////////////////////////////////////////
            Vector3 ls = new Vector3(0);
            Vector3 stepX = new Vector3(step.x,0,0);
            collider.pos = Vector.add(collider.pos, stepX);
            if(physics.testCollisionCube(collider, collision)){
                velocity.x = velocity.x*-elasticity;
                ls.x=step.x*-elasticity;
            }else {
                ls.x=step.x;
            }
            collider.pos = Vector.sub(collider.pos, stepX);
            ////////////////////////////////////////////////////////////
            Vector3 stepY = new Vector3(0,step.y,0);
            collider.pos = Vector.add(collider.pos, stepY);
            if(physics.testCollisionCube(collider, collision)){
                velocity.y = velocity.y*-elasticity;
                ls.y=step.y*-elasticity;
            }else {
                ls.y=step.y;
            }
            collider.pos = Vector.sub(collider.pos, stepY);
            ////////////////////////////////////////////////////////////
            Vector3 stepZ = new Vector3(0,0,step.z);
            collider.pos = Vector.add(collider.pos, stepZ);
            if(physics.testCollisionCube(collider, collision)){
                velocity.z = velocity.z*-elasticity;
                ls.z=step.z*-elasticity;
            }else {
                ls.z=step.z;
            }
            collider.pos = Vector.sub(collider.pos, stepZ);
            ////////////////////////////////////////////////////////////
            step=ls;
            collider.pos = Vector.add(collider.pos, step);
        }
        return step;
    }
    @Override
    public void update(float dt) {

        if(activity && gameObject.activity && Vector.sub(collider.pos, camera.getPosition()).length() < camera.getFar()){
            gravityC(dt);
            dragC(dt);
            Vector3 step = Vector.mul(velocity, dt);

            collider.pos = Vector.add(collider.pos, step);

            step = collisionC(step);

            gameObject.setPosition(Vector.add(step, gameObject.getPosition()));
        }
    }
}
