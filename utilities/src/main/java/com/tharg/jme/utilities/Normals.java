/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tharg.jme.utilities;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.debug.Arrow;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;

/**
 *
 * @author tharg
 */
public class Normals {

    public static Node createAxisMarker(SimpleApplication application, float arrowSize) {
        return createAxisMarker(application, arrowSize, 3.0f);
    }
        
    public static Node createAxisMarker(SimpleApplication application, float arrowSize, float arrowLineWidth) {

        Material xAxisMat = new Material(application.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        xAxisMat.getAdditionalRenderState().setWireframe(true);
        xAxisMat.getAdditionalRenderState().setLineWidth(arrowLineWidth);
        xAxisMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        xAxisMat.getAdditionalRenderState().setDepthTest(false);
        xAxisMat.setColor("Color", ColorRGBA.Red);

        Material yAxisMat = new Material(application.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        yAxisMat.getAdditionalRenderState().setWireframe(true);
        yAxisMat.getAdditionalRenderState().setLineWidth(arrowLineWidth);
        yAxisMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        yAxisMat.getAdditionalRenderState().setDepthTest(false);
        yAxisMat.setColor("Color", ColorRGBA.Green);

        Material zAxisMat = new Material(application.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        zAxisMat.getAdditionalRenderState().setWireframe(true);
        zAxisMat.getAdditionalRenderState().setLineWidth(arrowLineWidth);
        zAxisMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        zAxisMat.getAdditionalRenderState().setDepthTest(false);
        zAxisMat.setColor("Color", ColorRGBA.Blue);

        Node axis = new Node();

        // create arrows
        Geometry arrowX = new Geometry("arrowX", new Arrow(new Vector3f(arrowSize, 0, 0)));
        arrowX.setMaterial(xAxisMat);
        arrowX.setQueueBucket(RenderQueue.Bucket.Translucent);

        Geometry arrowY = new Geometry("arrowY", new Arrow(new Vector3f(0, arrowSize, 0)));
        arrowY.setMaterial(yAxisMat);
        arrowY.setQueueBucket(RenderQueue.Bucket.Translucent);

        Geometry arrowZ = new Geometry("arrowZ", new Arrow(new Vector3f(0, 0, arrowSize)));
        arrowZ.setMaterial(zAxisMat);
        arrowZ.setQueueBucket(RenderQueue.Bucket.Translucent);

        axis.attachChild(arrowX);
        axis.attachChild(arrowY);
        axis.attachChild(arrowZ);

        //axis.setModelBound(new BoundingBox());
        return axis;
    }

    //XYZ axis arrows are displayed for each bone
    //arrowNodes contains one Node for each bone
    //HOWTO use:
    //The returned arrowNodes array requires updating in simpleUpdate as follows:
    //Display bone axis arrows
    //    for (int i =0; i<boneNames.length;i++){
    //      Bone b = ragdoll.getBone(boneNames[i]);
    //      arrowNodes[i].setLocalTranslation(b.getModelSpacePosition());
    //      arrowNodes[i].setLocalRotation(b.getModelSpaceRotation());
    //    }  
    public static Node[] initArrowNodes(SimpleApplication application, String[] boneNames, Node rootNode, float size) {
        Node[] arrowNodes = new Node[boneNames.length];
        for (int i = 0; i < boneNames.length; i++) {
            arrowNodes[i] = createAxisMarker(application, size);
            rootNode.attachChild(arrowNodes[i]);
        }
        return arrowNodes;
    }

    //Use: Returns the appropriate color for the normal vector parameter
    //e.g. Normals.getNormalColor(Vector3f.UNIT_X); returns Red (X-axis)

    public static ColorRGBA getNormalColor(Vector3f vec) {
        //Assumes normal vectors of the form:
        //{0.0, 0.0, 1.0} or {-1.0, 0.0, 0.0}
        if (vec.x > 0.5 || vec.x < -0.5) {
            return ColorRGBA.Red;
        }
        if (vec.y > 0.5 || vec.y < -0.5) {
            return ColorRGBA.Green;
        }
        if (vec.z > 0.5 || vec.z < -0.5) {
            return ColorRGBA.Blue;
        }
        return ColorRGBA.Yellow;
    }

    //For each vertex in the geometry create a normal axis marker (X,Y,Z)
    //Note this arrow is tied to the geometry and will be static
    public static void showNormals(SimpleApplication application, Geometry geometry) {
        VertexBuffer position = geometry.getMesh().getBuffer(Type.Position);
        Vector3f[] positionVertexes = BufferUtils.getVector3Array((FloatBuffer) position.getData());
        VertexBuffer normal = geometry.getMesh().getBuffer(Type.Normal);
        Vector3f[] normalsVectors = BufferUtils.getVector3Array((FloatBuffer) normal.getData());
        for (int arrowIndex = 0; arrowIndex < normalsVectors.length; arrowIndex++) {
            createArrow(application, positionVertexes[arrowIndex], normalsVectors[arrowIndex]);
        }
    }

    //Create a single arrow at location. Colour the arrow depending on its direction.
    public static Geometry createArrow(SimpleApplication application, Vector3f location, Vector3f direction) {
        Arrow arrow = new Arrow(direction);
        Geometry g = new Geometry("arrow", arrow);
        Material mat = new Material(application.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", getNormalColor(direction));
        g.setMaterial(mat);
        g.setLocalTranslation(location);
        application.getRootNode().attachChild(g);
        return g;
    }


    //Create a single arrow at location with Colour parameter.
    public static Geometry createArrow(SimpleApplication application, Vector3f location, Vector3f direction, ColorRGBA colour) {
        Arrow arrow = new Arrow(direction);
        Geometry g = new Geometry("arrow", arrow);
        Material mat = new Material(application.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", colour);
        g.setMaterial(mat);
        g.setLocalTranslation(location);
        application.getRootNode().attachChild(g);
        return g;
    }

    //Create a single arrow attached to a node.
    public static void createArrow(SimpleApplication application, Node node, float arrowSize) {
        Geometry arrow = new Geometry(node.getName()+"Arrow", new Arrow(new Vector3f(0, 0, arrowSize)));
        Material mat = new Material(application.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Magenta);
        mat.getAdditionalRenderState().setLineWidth(3.0f);
        arrow.setMaterial(mat);
        arrow.setLocalTranslation(node.getLocalTranslation());
        node.attachChild(arrow);
    }
}
