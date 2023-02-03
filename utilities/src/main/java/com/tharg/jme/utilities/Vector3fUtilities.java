/*
 *
 */
package com.tharg.jme.utilities;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.animation.Bone;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author tharg
 */
public class Vector3fUtilities {

    private static final Logger LOGGER = Logger.getLogger(Vector3fUtilities.class.getName());
    private static Random random = new Random();

    // orig
//    // Cheap and cheerful lookAt function. 
//    // Only useful for 'upright' nodes, since X and Z rotations are set to zero.
//    // The node is rotated around the Y axis to look at the ground location of the target node
//    // Replaces Quaternion myNode.lookAt(targetNode.getWorldTranslation(), Vector3f.UNIT_Y);
//    public static void lookAt(Node looker, Node target) {
//        Vector3f trajectory = target.getWorldTranslation().subtract(looker.getWorldTranslation());
//        trajectory = trajectory.normalize();
//        float angleY;
//        if (Math.abs(trajectory.getZ()) < 0.01) {
//            // Z value is almost zero. Set the angle to + or - 90 degrees depeding on value of X
//            if (trajectory.getX() > 0.0f) {
//                angleY = FastMath.HALF_PI;
//            } else {
//                angleY = -FastMath.HALF_PI;
//            }
//        } else if (trajectory.getZ() >= 0.01) {
//            // Calculate the angle based on tan=Opposite/Adjacent
//            angleY = FastMath.atan((trajectory.getX() / trajectory.getZ()));
//        } else {
//            // Correct the angle if Z is negative
//            angleY = FastMath.PI + FastMath.atan((trajectory.getX() / trajectory.getZ()));
//        }
//        //System.out.println("simpleUpdate trajectory = " + trajectory + ", angleY = " + angleY * FastMath.RAD_TO_DEG);
//        // Set the looker Y rotation, setting X and Z rotations to zero.  
//        float[] angles = {0,angleY,0};
//        Quaternion rot = new Quaternion(angles);
//        looker.setLocalRotation(rot);
//    }

    // Ignore the X and Z rotations, and return a normalized direction vector matching the Y rotation
    public static Vector3f getDirectionVectorFromYRotation(Quaternion q){
        float[] angles = new float[3];
        q.toAngles(angles);
        float x = FastMath.cos(angles[1]);
        float z = FastMath.sin(angles[1]);
        return new Vector3f(x,0,z).normalizeLocal();
    }

    // Cheap and cheerful lookAt function. 
    // Only useful for 'upright' nodes, since X and Z rotations are set to zero.
    // The node is rotated around the Y axis to look at the ground location of the target node
    // Replaces Quaternion myNode.lookAt(targetNode.getWorldTranslation(), Vector3f.UNIT_Y);
    public static void lookAt(Node looker, Node target) {
        looker.setLocalRotation(lookAtAngle(looker.getWorldTranslation(), target.getWorldTranslation()));
    }

    // Cheap and cheerful lookAt function, returning the angle from 2 points mapped to the Y plane.
    // Only useful for 'upright' nodes, since X and Z rotations are set to zero.
    public static Quaternion lookAtAngle(Vector3f from, Vector3f to) {
        float angleY = lookAtAngleY(from, to);
        //System.out.println("simpleUpdate trajectory = " + trajectory + ", angleY = " + angleY * FastMath.RAD_TO_DEG);
        // Set the looker Y rotation, setting X and Z rotations to zero.  
        float[] angles = {0, angleY, 0};
        Quaternion rot = new Quaternion(angles);
        return rot;
    }

    // Cheap and cheerful lookAt function, returning the angle from 2 points mapped to the Y plane.
    // Only useful for 'upright' nodes, since X and Z rotations are set to zero.
    public static float lookAtAngleY(Vector3f from, Vector3f to) {
        Vector3f trajectory = new Vector3f(to.subtract(from));
        trajectory.normalizeLocal();
        float angleY;
        if (Math.abs(trajectory.getZ()) < 0.01) {
            // Z value is almost zero. Set the angle to + or - 90 degrees depending on value of X
            if (trajectory.getX() > 0.0f) {
                angleY = FastMath.HALF_PI;
            } else {
                angleY = -FastMath.HALF_PI;
            }
        } else if (trajectory.getZ() >= 0.01) {
            // Calculate the angle based on tan=Opposite/Adjacent
            angleY = FastMath.atan((trajectory.getX() / trajectory.getZ()));
        } else {
            // Correct the angle if Z is negative
            angleY = FastMath.PI + FastMath.atan((trajectory.getX() / trajectory.getZ()));
        }
        return angleY;
    }

    //Rotate looker towards target, but limit the rotation to a fraction of the full rotation required
    //NOT WORKING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public static Quaternion lookAtAngleNLERP(Vector3f lookerLocation, Vector3f targetLocation, Quaternion lookerRotation, float fraction) {

        float lookerRotationY = lookerRotation.toAngleAxis(Vector3f.UNIT_Y);
        float lookAtTargetAngleY = lookAtAngleY(lookerLocation, targetLocation);
        float scaledRotationY = lookerRotationY + (lookerRotationY - lookAtTargetAngleY) * fraction;

        float[] angles = {0, scaledRotationY, 0};
        Quaternion rot = new Quaternion(angles);
        return rot;
    }

    public static boolean tolerablyEqual(Vector3f a, Vector3f b, float tolerance) {
        return (Math.abs(a.x) - Math.abs(b.x) < tolerance
                && Math.abs(a.y) - Math.abs(b.y) < tolerance
                && Math.abs(a.z) - Math.abs(b.z) < tolerance);
    }

    public static boolean tolerablyEqualFloat(float[] a, float[] b, float tolerance) {
        //Assume float array is float[3] x,y,z,angles.
        if (a.length != 3 || b.length != 3) {
            return false;
        }
        Boolean xWithinTolerance = FastMath.abs(FastMath.abs(a[0]) - FastMath.abs(b[0])) < tolerance;
        Boolean yWithinTolerance = FastMath.abs(FastMath.abs(a[1]) - FastMath.abs(b[1])) < tolerance;
        Boolean zWithinTolerance = FastMath.abs(FastMath.abs(a[2]) - FastMath.abs(b[2])) < tolerance;

        return xWithinTolerance && yWithinTolerance && zWithinTolerance;
    }

    //Limit the x,y,z input array of floats to the values in limit array.
    //RESTRICTION: Limits apply to both positive and negative values.
    //Returns null if either of the input arrays are malformed.
    public static float[] limitAngles(float[] angles, float[] limits) {
        if (angles.length != 3 || limits.length != 3) {
            return null;
        }
        float[] returnArray = new float[3];
        for (int i = 0; i < 3; i++) {
            if (angles[i] > limits[i]) {
                returnArray[i] = limits[i];
            } else if (angles[i] < -limits[i]) {
                returnArray[i] = -limits[i];
            } else {
                returnArray[i] = angles[i];
            }
        }
        return returnArray;
    }

    //Returns the Vector3f values x,y,z as a comma seperated string
    public static String vector3fToString(Vector3f vec) {
        StringBuilder sb = new StringBuilder();
        if (vec == null) {
            return null;
        }
        sb.append(String.format("%.2f", vec.x)).append("f, ");
        sb.append(String.format("%.2f", vec.y)).append("f, ");
        sb.append(String.format("%.2f", vec.z)).append("f");
        return "("+sb.toString()+")";
    }

    //Converts radians to degrees then converts to string
    //Assumes [x,y,z] ordering
    public static String vector3fToStringDegrees(Vector3f angles) {
        float xAngle = FastMath.RAD_TO_DEG * angles.x;
        float yAngle = FastMath.RAD_TO_DEG * angles.y;
        float zAngle = FastMath.RAD_TO_DEG * angles.z;
        String result = "(" + xAngle + ", " + yAngle + ", " + zAngle + ")";
        return result;
    }

    //Converts radians to degrees and converts to floats then string
    //Assumes [x,y,z] ordering
    public static String floatArrayToFloatDegrees(float[] angles) {
        float xAngle = FastMath.RAD_TO_DEG * angles[0];
        float yAngle = FastMath.RAD_TO_DEG * angles[1];
        float zAngle = FastMath.RAD_TO_DEG * angles[2];
        String result = "(" + xAngle + ", " + yAngle + ", " + zAngle + ")";
        return result;
    }

    //Converts radians to degrees and converts to rounded int then string
    //Assumes [x,y,z] ordering
    public static String floatArrayToIntegerDegrees(float[] angles) {
        int xAngle = Math.round(FastMath.RAD_TO_DEG * angles[0]);
        int yAngle = Math.round(FastMath.RAD_TO_DEG * angles[1]);
        int zAngle = Math.round(FastMath.RAD_TO_DEG * angles[2]);
        String result = "(" + xAngle + ", " + yAngle + ", " + zAngle + ")";
        return result;
    }

    public static String toString(float[] floats) {
        return toString(floats, 1);
    }

    //Converts and array of floats to strings
    public static String toString(float[] floats, int decimalPlaces) {
        StringBuilder sb = new StringBuilder();
        if (floats == null) {
            return null;
        }
        sb.append("(");
        for (float aFloat : floats) {
            sb.append(String.format("%."+decimalPlaces+"f", aFloat)).append(", ");
        }
        String result = sb.toString();
        //Remove trailing comma
        result = result.substring(0, result.lastIndexOf(','));
        return result+")";
    }

    //Assumes the parameter is an array of radian floats
    //Converts to a degrees and returns the values as a comma seperated string
    public static String toDegrees(float[] floats) {
        StringBuilder sb = new StringBuilder();
        if (floats == null) {
            return null;
        }
        for (float aFloat : floats) {
            sb.append(String.format("%.2f", FastMath.RAD_TO_DEG * aFloat)).append(", ");
        }
        String result = sb.toString();
        //Remove trailing comma
        return result.substring(0, result.lastIndexOf(','));
    }

    //Returns the Quaternion values x,y,z,w as a comma seperated string
    public static String quaternionToString(Quaternion quat) {
        StringBuilder sb = new StringBuilder();
        if (quat == null) {
            return null;
        }
        sb.append(String.format("%.2f", quat.getX())).append(", ");
        sb.append(String.format("%.2f", quat.getY())).append(", ");
        sb.append(String.format("%.2f", quat.getZ())).append(", ");
        sb.append(String.format("%.2f", quat.getW()));
        return sb.toString();
    }

    public static Quaternion clampRotationsArray(Quaternion quat, float[] minMaxArray, int testCase) {
        return clampRotations(quat,
                minMaxArray[0], minMaxArray[1], minMaxArray[2], minMaxArray[3], minMaxArray[4], minMaxArray[5], testCase);
    }

    public static Quaternion clampRotations(Quaternion quat,
                                            float minX, float maxX, float minY, float maxY, float minZ, float maxZ, int testCase) {
        LOGGER.setLevel(Level.OFF);
        float[] angles = quat.toAngles(null);
        float xRot = FastMath.clamp(angles[0], minX, maxX);
        float yRot = FastMath.clamp(angles[1], minY, maxY);
        float zRot = FastMath.clamp(angles[2], minZ, maxZ);
        boolean xLimited = angles[0] < minX || angles[0] > maxX;
        boolean yLimited = angles[1] < minY || angles[1] > maxY;
        boolean zLimited = angles[2] < minZ || angles[2] > maxZ;
        if (xLimited) {
            LOGGER.fine(testCase
                    + " X angle[" + angles[0] * FastMath.RAD_TO_DEG
                    + "] is outside min[" + minX * FastMath.RAD_TO_DEG
                    + "] and max[" + maxX * FastMath.RAD_TO_DEG
                    + "] clamped to: " + xRot * FastMath.RAD_TO_DEG);
        }
        if (yLimited) {
            LOGGER.fine(testCase
                    + " Y angle[" + angles[1] * FastMath.RAD_TO_DEG
                    + "] is outside min[" + minY * FastMath.RAD_TO_DEG
                    + "] and max[" + maxY * FastMath.RAD_TO_DEG
                    + "] clamped to: " + yRot * FastMath.RAD_TO_DEG);
        }
        if (zLimited) {
            LOGGER.fine(testCase
                    + " Z angle[" + angles[2] * FastMath.RAD_TO_DEG
                    + "] is outside min[" + minZ * FastMath.RAD_TO_DEG
                    + "] and max[" + maxZ * FastMath.RAD_TO_DEG
                    + "] clamped to: " + zRot * FastMath.RAD_TO_DEG);
        }
        if (!(xLimited || yLimited || zLimited)) {
            LOGGER.fine(testCase
                    + " X angle[" + angles[0] * FastMath.RAD_TO_DEG
                    + " Y angle[" + angles[1] * FastMath.RAD_TO_DEG
                    + " Z angle[" + angles[2] * FastMath.RAD_TO_DEG
                    + " no limits applied");
        }
        float[] returnAngles = new float[]{xRot, yRot, zRot};
        return new Quaternion(returnAngles);
    }

//    public static String quaternionToRadiansString(Quaternion quat) {
//        float[] angles = quat.toAngles(null);
//        return (angles[0] + ", " + angles[1] + ", " + angles[2]);
//    }
//
//    public static String quaternionToDegreesString(Quaternion quat) {
//        float[] angles = quat.toAngles(null);
//        return (angles[0] * FastMath.RAD_TO_DEG +
//                ", " + angles[1] * FastMath.RAD_TO_DEG +
//                ", " + angles[2] * FastMath.RAD_TO_DEG);
//    }

    /**
     * Returns the world position of a bone in a model.
     * This is the bone model position, plus the world model position.
     * <p>
     * Usage:
     * Vector3f leftHandBoxer1BoneWorldPosition =
     * Vector3fUtilities.getBoneWorldTranslation(
     * boxer1Control.getSkeleton().getBone("hand.L"), boxer1Node);
     *
     * @param bone
     * @param modelNode
     * @return BoneWorldPosition
     */
    public static Vector3f getBoneWorldTranslation(Bone bone, Node modelNode) {
        Vector3f returnWorldTranslation;
        // The bone model position is the position relative to the model's root position.
        // Since the model is rotated, we have to rotate the bone position around the model axis.
        returnWorldTranslation
                = Quaternions.RotatePointAroundPivot(
                        bone.getModelSpacePosition(),
                        Vector3f.ZERO,
                        modelNode.getLocalRotation())
                .clone();
        // Now add the model world position to our rotated bone position to get the world bone position.
        returnWorldTranslation = returnWorldTranslation.add(modelNode.getWorldTranslation());
        return returnWorldTranslation;

    }

    // The world rotation is simply the bone rotation multiplied by the model rotation.
    // NOT SURE THIS IS CORRECT. ARE BONE ROTATIONS ARE RELATIVE TO THE PARENT BONE???
    // DO WE HAVE TO MULTIPLY ALL THE ROTATIONS FROM ROOT TO BONE AND MODEL WORLD ROTATION?
    public static Quaternion getBoneWorldRotation(Bone bone, Node modelNode) {
        Quaternion returnWorldRotation;
        returnWorldRotation = bone.getModelSpaceRotation().mult(modelNode.getLocalRotation()).clone();
        return returnWorldRotation;
    }

    // Makes the spatial is upright
    public static void clearRotationsXZ(Spatial spatial) {
        Quaternion localRotation = spatial.getLocalRotation();
        float [] angles = new float[3];
        localRotation.toAngles(angles);
        angles[0] = 0;
        angles[2] = 0;
        localRotation.fromAngles(angles);
        spatial.setLocalRotation(localRotation);
    }

    // Set the Y axis value to zero
    // The spatial will then be 'on the ground'
    public static void groundTheSpatial(Spatial spatial) {
        Vector3f spatialGroundPosition = new Vector3f(spatial.getLocalTranslation());
        spatialGroundPosition.y = 0;
        spatial.setLocalTranslation(spatialGroundPosition);
    }

    public static Vector3f getRandomVector(){
        float randomF1 = random.nextFloat();
        float randomF2 = random.nextFloat();
        float randomF3 = random.nextFloat();
        return (new Vector3f(randomF1, randomF2, randomF3)).normalizeLocal();
    }

    // Returns a direction vector at 90 degrees clockwise around Y axis.
    // The 'rotation' is in the 2D x-z plane (Y=0).
    // Returns the direction vector with Y=0.
    public static Vector3f directionVectorAt90Degrees(Vector3f directionVector){
        // Y value will be set to zero
        // Simple technique is to multiply x by -1 and swap x and z
        // Simple but it works
        float z = directionVector.x * -1;
        float x = directionVector.z;
        return new Vector3f(x,0,z);
    }
}
