/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tharg.jme.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.anim.Armature;
import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.light.Light;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.scene.shape.Box;

/**
 * debugMaterial: logs (as info) all the materials attached to the spacial
 * debugSkeleton: Provides one static debug skeleton so model bones are vidsible
 * as a wireframe drawBoundingVolume: draws the bounding volume as a wireframe
 * box.
 *
 * @author tharg
 */
public class DebugUtilities {

    public static final Logger LOGGER = Logger.getLogger(DebugUtilities.class.getName());
    private static SkeletonDebugger skeletonDebug;
    private static Material skeletonDebugMaterial;

    //Sets up the globally uses static LOGGER. Should be called once at startup.
    public static void debugSetupLogger() {
        LOGGER.setLevel(Level.INFO);
    }

    //Overloaded function for Node
    public static void debugMaterial(Node node) {
        LOGGER.info("Node: " + node.getName());
        Geometry geom = (Geometry) node.getChild(0);
        debugMaterial(geom);
    }

    //Overloaded function for Spatial
    public static void debugMaterial(Spatial spatial) {
        LOGGER.info("Node: " + spatial.getName());
        debugMaterial((Geometry) spatial);
    }

    //Iterates and prints all material details found in the geometry
    public static void debugMaterial(Geometry geom) {
        if (geom != null) {
            Material mat = geom.getMaterial();
            if (mat != null) {
                LOGGER.info("Geometry: " + geom.getName() + " mat name: " + mat.getName() + ", asset name: " + mat.getAssetName());
                Collection<MatParam> params = mat.getParams();
                Iterator<MatParam> iter = params.iterator();
                MatParam matParam;
                if (iter != null && iter.hasNext()) {
                    while (iter.hasNext()) {
                        matParam = iter.next();
                        String matParamName = matParam.getName();
                        try {
                            String matParamInfo = mat.getParam(matParamName).toString();
                            LOGGER.info("...MatParam " + matParamName + " = " + matParamInfo);
                        } catch (java.lang.UnsupportedOperationException e) {
                            LOGGER.info("...MatParam " + matParamName + ": " + e.getMessage());
                        }
                    }
                    LOGGER.info("");
                } else {
                    LOGGER.warning("MatParam collection empty or null ");
                }
            } else {
                LOGGER.warning("Material null");
            }
        } else {
            LOGGER.warning("Geometry null");
        }
    }

    //EXAMPLES:
    // Create a red debugSkeleton and attach to the model so it moves with the model:
    //      debugSkeleton(control, 1.0f, model.getLocalTranslation(), assetManager, model, ColorRGBA.Red);
    //
    // Create a green debugSkeleton and attach to the root node so it stays at the origin:
    //      debugSkeleton(control, 1.0f, model.getLocalTranslation(), assetManager, rootNode, ColorRGBA.Green);
    //
    // Optionally add axis arrows to each bone joint
    //    requires additional code in update to position the axes (see com.tharg.jme.utilities.Normals)
    //    private final int NUM_BONES = boneNames.length;
    //    private final Node[] arrowNodes = new Node[NUM_BONES];
    //    initArrowNodes(this, boneNames, arrowNodes, rootNode);


    // Add axis arrows to each bone joint
    //    requires additional code in update to position the axes (see com.tharg.jme.utilities.Normals)
    //    private final int NUM_BONES = boneNames.length;
    //    private final Node[] arrowNodes = new Node[NUM_BONES];
    //    initArrowNodes(this, boneNames, arrowNodes, rootNode);
    public static void debugSkeletonDAC(
            Node model, AssetManager assetManager, Node rootNode, ColorRGBA color) {
        SkinningControl skinningControl
                = model.getControl(SkinningControl.class);
        Armature armature = skinningControl.getArmature();
        List<Joint> jointList = armature.getJointList();
        System.out.println("Joints found in Model:");
        for (Joint j : jointList) {
            System.out.println("Joint = " + j.getName());
        }


//        skeletonDebug = new SkeletonDebugger("skeleton", skeleton);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat);
        rootNode.attachChild(skeletonDebug);
    }
    
    // Call from simpleUpdate to position the axis markers at their joint positions
    public void updateJointAxisMarkers(List<Joint> jointList){
        
    }

    private void printJoints(Node model) {
        SkinningControl skinningControl
                = model.getControl(SkinningControl.class);
        Armature armature = skinningControl.getArmature();
        List<Joint> jointList = armature.getJointList();
        System.out.println("Joints found in Model:");
        for (Joint j : jointList) {
            System.out.println("Joint = " + j.getName());
        }
    }

//    private void printLinks(DynamicAnimControl dac) {
//        System.out.println("Number of linked bones = " + dac.countLinkedBones());
//        System.out.println("Number of links = " + dac.countLinks());
//        System.out.println("listLinkedBoneNames:");
//        for (String name : dac.listLinkedBoneNames()) {
//            System.out.println("Link Name = " + name);
//        }
//        System.out.println();
//    }

    public static void printBoneDetails(Bone bone) {
        System.out.println("Bone " + bone.getName() + " model position = " + bone.getModelSpacePosition()
                + " model rotation = " + Quaternions.QuatToEulerDegrees(bone.getModelSpaceRotation()));
        System.out.println("Bone " + bone.getName() + " local position = " + bone.getLocalPosition()
                + " local rotation = " + Quaternions.QuatToEulerDegrees(bone.getLocalRotation()));
    }

    public static void printBoneDetails(String name, Skeleton skeleton) {
        System.out.println(" ");
        System.out.println("BONE DETAILS for skeleton: " + name);
        Bone b;
        for (int i = 0; i < skeleton.getBoneCount(); i++) {
            b = skeleton.getBone(i);
            printBoneDetails(b);
        }
    }

    public static void printBoneRootsDetails(Skeleton skeleton) {
        System.out.println(" ");
        System.out.println("BONE ROOTS:");
        Bone[] boneRoots = skeleton.getRoots();
        for (Bone b : boneRoots) {
            System.out.println("Root " + b.getName() + " model position = " + b.getModelSpacePosition()
                    + " model rotation = " + Quaternions.QuatToEulerDegrees(b.getModelSpaceRotation()));
            ArrayList<Bone> children = b.getChildren();
            if (children == null || children.isEmpty()) {
                System.out.println("   NO Children");
            } else {
                for (Bone c : children) {
                    System.out.println("   Child " + c.getName() + " model position = " + c.getModelSpacePosition()
                            + " model rotation = " + Quaternions.QuatToEulerDegrees(c.getModelSpaceRotation()));
                }
            }
        }
    }

    public static void debugSkeletonSetLocalTranslation(Vector3f localTranslation) {
        skeletonDebug.setLocalTranslation(localTranslation);
    }

    public static void debugSkeletonSetLocalRotation(Quaternion localRotation) {
        skeletonDebug.setLocalRotation(localRotation);
    }

    public static void debugSkeletonSetLocalScale(float scale) {
        skeletonDebug.setLocalScale(scale);
    }

    public static void debugSkeletonSetColor(ColorRGBA color) {
        skeletonDebugMaterial.setColor("Color", color);
        skeletonDebug.setMaterial(skeletonDebugMaterial); //needed?
    }

    public static void debugListLights(Node node) {
        final String nodeName = node.getName();
        Iterator<Light> iter = node.getLocalLightList().iterator();
        if (iter != null && iter.hasNext()) {
            while (iter.hasNext()) {
                LOGGER.log(Level.INFO, "{0} has light: {1}", new Object[]{nodeName, iter.next().getName()});
            }
        } else {
            LOGGER.log(Level.WARNING, "{0}.getLocalLightList() empty or null", nodeName);
        }
    }

    public static Spatial debugDrawBoundingVolume(String name, Geometry geo, AssetManager assetManager, Node rootNode) {
        BoundingVolume bv = geo.getWorldBound();
        if (bv.getType() == BoundingVolume.Type.AABB) {
            BoundingBox boundingBox = (BoundingBox) bv;
            Box boundingMesh = new Box(boundingBox.getCenter(), boundingBox.getXExtent(), boundingBox.getYExtent(), boundingBox.getZExtent());
            Geometry boundingGeo = new Geometry(name, boundingMesh);
            Material boundingMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            boundingMat.setColor("Color", ColorRGBA.Green);
            boundingMat.getAdditionalRenderState().setWireframe(true);
            boundingGeo.setMaterial(boundingMat);
            rootNode.attachChild(boundingGeo);
            return boundingGeo;
        } else {
            LOGGER.warning("Only Bounding Volumes of type AABB are supported");
            return null;
        }
    }
}
