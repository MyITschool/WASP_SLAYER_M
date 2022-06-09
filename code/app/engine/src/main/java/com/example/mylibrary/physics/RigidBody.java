package com.example.mylibrary.physics;

import com.example.mylibrary.core.Core;
import com.example.mylibrary.core.GameObject;
import com.example.mylibrary.core.Updated;
import com.example.mylibrary.math.Vector;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.render.RenderObject;

public final class RigidBody implements Updated {

    private CubeCollider collider;

    public float mass = 1;
    public float drag = 0.01f;
    public float elasticity = 0.1f;
    public boolean usGravity = true;
    public boolean activity = true;

    private Vector3 velocity = new Vector3(0);

    private final Physics physics;
    private final GameObject gameObject;

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

    @Override
    public void update(float dt) {

        if(activity && gameObject.activity){
            if (usGravity){
                velocity=Vector.add(velocity, Vector.mul(physics.g, dt));
            }
            if (drag>0){
                Vector3 ddm = Vector.mul(Vector.mul(Vector.mul(velocity, velocity), drag),
                        Vector.div(velocity, Vector.abs(velocity)));

                Vector3 v = Vector.abs(velocity);
                Vector3 df = Vector.abs(ddm);

                velocity.setXYZ((velocity.x == 0 && v.x < df.x) ? 0 : velocity.x-ddm.x,
                        (velocity.y == 0 && v.y < df.y) ? 0 : velocity.y-ddm.y,
                        (velocity.z == 0 && v.z < df.z) ? 0 : velocity.z-ddm.z);
            }
            Vector3 step = Vector.mul(velocity, dt);

            collider.pos = Vector.add(collider.pos, step);

            Collision collision = new Collision();
            if(physics.testCollisionCube(collider, collision)){
                collider.pos = Vector.sub(collider.pos, step);
                RigidBody rb = collision.collider.getColliderRigidBody();
                if(rb!=null&&rb.activity){
                    rb.addForce( Vector.mul(Vector.mul(velocity, 1-elasticity), mass) );
                }
                velocity = Vector.mul(velocity, -elasticity);
            }else {
                gameObject.setPosition(Vector.add(step, gameObject.getPosition()));
            }
        }
    }
}
