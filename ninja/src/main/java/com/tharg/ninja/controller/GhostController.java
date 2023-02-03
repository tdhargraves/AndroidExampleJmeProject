package com.tharg.ninja.controller;

/**
 *
 * @author tharg
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MultiSphere;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.tharg.jme.utilities.DebugDisplayActions;

import com.jme3.bullet.animation.DynamicAnimControl;
import com.jme3.bullet.animation.PhysicsLink;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Creates GhostObject limbs to match the Kinematic Ragdoll. 
 * Provides function to move the the ghost limbs with the Kinematic Ragdoll
 *
 * @author tharg
 */
public class GhostController{
    // A couple of lists to make life easy...
    private Map<String, PhysicsGhostObject> boneNameGhostObjectList = new HashMap<>();
    private HashMap<Long, String> collisionShapeIdBoneNameList = new HashMap<>();
    public GhostController(DynamicAnimControl dac, PhysicsSpace physicsSpace, int collisionGroup, int collideWith) {
        DebugDisplayActions.setIsDebugEnabled(true);
        String[] boneList = dac.listLinkedBoneNames();
        for (String boneName : boneList) {
            PhysicsLink physicsLink = dac.findBoneLink(boneName);
            PhysicsRigidBody physicsRigidBody = physicsLink.getRigidBody();
            CollisionShape collisionShape = physicsRigidBody.getCollisionShape();
            CollisionShape ghostCollisionShape;
            if (collisionShape instanceof MultiSphere) {
                MultiSphere torsoMultiSphere = (MultiSphere) collisionShape;
                ArrayList<Float> radii = new ArrayList<Float>();
                for (int i = 0; i < torsoMultiSphere.countSpheres(); i++) {
                    radii.add(torsoMultiSphere.getRadius(0));
                }
                ArrayList<Vector3f> centers = new ArrayList<Vector3f>();
                for (int j = 0; j < torsoMultiSphere.countSpheres(); j++) {
                    centers.add(torsoMultiSphere.copyCenter(j, null));
                }
                ghostCollisionShape = new MultiSphere(centers, radii);
            } else {
                HullCollisionShape hullCollisionShape = (HullCollisionShape) collisionShape;
                ghostCollisionShape = new HullCollisionShape(hullCollisionShape.copyHullVertices());
            }
            Vector3f location = physicsRigidBody.getPhysicsLocation();
            Quaternion rotation = physicsRigidBody.getPhysicsRotation();
            PhysicsGhostObject physicsGhostObject = createPhysicsGhostObject(
                    ghostCollisionShape, location, rotation, collisionGroup, collideWith);
            boneNameGhostObjectList.put(boneName, physicsGhostObject);
            collisionShapeIdBoneNameList.put(ghostCollisionShape.nativeId(), boneName);
            System.out.println("collisionShapeIdBoneNameList: "+ghostCollisionShape.nativeId()+", boneName: "+boneName);
        }

        // dac treats Torso link as a special case and is not found by dac.findBoneLink()
        PhysicsRigidBody physicsRigidBody = dac.getTorsoLink().getRigidBody();
        String torsoName = dac.getTorsoLink().name();
        CollisionShape torsoCollisionShape = physicsRigidBody.getCollisionShape();

        // ASSUMES TORSO SHAPE IS HULL COLLISION SHAPE
        HullCollisionShape torsoHullCollisionShape = (HullCollisionShape) torsoCollisionShape;
        HullCollisionShape ghostTorsoCollisionShape = new HullCollisionShape(torsoHullCollisionShape.copyHullVertices());

        Vector3f location = physicsRigidBody.getPhysicsLocation();
        Quaternion rotation = physicsRigidBody.getPhysicsRotation();
        PhysicsGhostObject torsoPhysicsGhostObject = createPhysicsGhostObject(
                ghostTorsoCollisionShape, location, rotation, collisionGroup, collideWith);
        boneNameGhostObjectList.put(torsoName, torsoPhysicsGhostObject);
        collisionShapeIdBoneNameList.put(ghostTorsoCollisionShape.nativeId(), "Torso");
    }

    public Map<String, PhysicsGhostObject> getBoneNameGhostObjectList(){
        return boneNameGhostObjectList;
    }

    private PhysicsGhostObject createPhysicsGhostObject(
            CollisionShape collisionShape, Vector3f location,
            Quaternion rotation, int collisionGroup, int collideWith) {
        PhysicsGhostObject physicsGhostObject = new PhysicsGhostObject(collisionShape);
        physicsGhostObject.setCollisionGroup(collisionGroup);
        physicsGhostObject.setCollideWithGroups(collideWith);
        physicsGhostObject.setPhysicsLocation(location);
        physicsGhostObject.setPhysicsRotation(rotation);
        return physicsGhostObject;
    }

    public void matchGhostToRagdoll(DynamicAnimControl dac) {
        for (String boneName : boneNameGhostObjectList.keySet()) {
            PhysicsLink physicsLink = dac.findBoneLink(boneName);
            if (boneName.contains("Torso")){
                boneNameGhostObjectList.get(boneName).setPhysicsLocation(dac.getTorsoLink().getRigidBody().getPhysicsLocation());
                boneNameGhostObjectList.get(boneName).setPhysicsRotation(dac.getTorsoLink().getRigidBody().getPhysicsRotation());
            } else {
                boneNameGhostObjectList.get(boneName).setPhysicsLocation(physicsLink.getRigidBody().getPhysicsLocation());
                boneNameGhostObjectList.get(boneName).setPhysicsRotation(physicsLink.getRigidBody().getPhysicsRotation());
            }
        }
    }

    public String getBoneName(PhysicsGhostObject physicsGhostObject) {
        for (String boneName : boneNameGhostObjectList.keySet()){
            PhysicsGhostObject listObject = boneNameGhostObjectList.get(boneName);
            if (listObject.getObjectId() == physicsGhostObject.getObjectId()){
                return boneName;
            }
        }
        return null;
    }

    public boolean isGhostObjectInList(PhysicsGhostObject physicsGhostObject){
        return (physicsGhostObject == null) ?
                false :
                collisionShapeIdBoneNameList.containsKey(physicsGhostObject.nativeId());
    }
}
