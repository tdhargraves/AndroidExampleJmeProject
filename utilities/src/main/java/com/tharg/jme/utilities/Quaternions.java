/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tharg.jme.utilities;

import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author tharg
 */
public class Quaternions {

    public static final Quaternion zeroRotation(){
        Quaternion q = new Quaternion();
        q.fromAngles(0, 0, 0);
        return q;
    }

    /*
     * Returns the rotation around the Y axis needed to 'lookAt' a location.
     */
    public static float yRotationToLookAtDeg(Vector3f myLocation, Vector3f lookAtLocation, Quaternion myRotation){
        Vector3f myLocationGrounded = new Vector3f(myLocation);
        myLocationGrounded.y = 0;
        Vector3f lookAtLocationGrounded = new Vector3f(lookAtLocation);
        lookAtLocationGrounded.y = 0;

        if (myLocationGrounded.isSimilar(lookAtLocation, 0.001f)){
            return 0;
        }

        float [] myRotationAngles = Quaternions.quaternionToAngles(myRotation);

        Vector3f vectorToLookAtLocation =
                lookAtLocationGrounded.subtract(myLocationGrounded).normalizeLocal();
        Quaternion rotationToLookAtLocation = new Quaternion();
        rotationToLookAtLocation.lookAt(vectorToLookAtLocation, Vector3f.UNIT_Y);
        float[] rotationToLookAtLocationAngles = Quaternions.quaternionToAngles(rotationToLookAtLocation);
        float rotationToLookAtLocationYDeg = rotationToLookAtLocationAngles[1] * FastMath.RAD_TO_DEG;

        // Rotate at RECOVERY_ROTATION_RATE_DEG_PER_SEC
        float rotationRequiredDeg =
                Quaternions.shortestRotationDeg(
                        myRotationAngles[1] * FastMath.RAD_TO_DEG, rotationToLookAtLocationYDeg);
        return rotationRequiredDeg;
    }

    // Return the minimum angle to rotate between two degree values.
    // If positive, rotate clockwise.
    public static float shortestRotationDeg(float fromAngleDeg, float toAngleDeg) {

        float a = toAngleDeg - fromAngleDeg;
        float b = toAngleDeg - fromAngleDeg + 360;
        float c = toAngleDeg - fromAngleDeg - 360;

        // Whichever of |α|, |b|, and |c| is the smallest,
        // tells us which of a, b, and c is relevant,
        // and if the one with the smallest absolute value
        // is positive,
        // go clockwise,
        // and if it's negative,
        // go counterclockwise.

        float absA = FastMath.abs(a);
        float absB = FastMath.abs(b);
        float absC = FastMath.abs(c);

        float returnAngle = a; // Just in case they are all equal!

        if(absA < absB && absA < absC){
            returnAngle = a;
        } else if (absB < absA && absB < absC){
            returnAngle = b;
        } else if (absC < absA && absC < absB){
            returnAngle = c;
        }
        return returnAngle;
    }

    /*
    Creates a Vector that tells where is the forward vector of this object, relative to the object location.
    To move the node in the forward direction, do something like this:
        location = geomNode.getLocalTranslation();
        direction = quaternions.getForward(geomNode).subtract(location).normalizeLocal();
        geomNode.move(direction.mult(tpf));
    
        node.localToWorld() converts local coordinates to world coordinates so :
        (0,0,1) is forward.
        (0,0,-1) is backward.
        (1,0,0) is right.
        (0,1,0) is upward.
     */
    public static final Vector3f getForwardDirection(Node node) {
        Vector3f forward = new Vector3f(0, 0, 1);
        node.localToWorld(forward, forward);
        return forward;
    }

    public static final Vector3f getRightDirection(Node node) {
        Vector3f forward = new Vector3f(1, 0, 0);
        node.localToWorld(forward, forward);
        return forward;
    }

    public static Quaternion quaternionFromAngles(float x, float y, float z) {
        Quaternion q = new Quaternion();
        return q.fromAngles(x, y, z);
    }


    public static Quaternion quaternionFromAnglesArray(float[] angles) {
        Quaternion q = new Quaternion();
        return q.fromAngles(angles[0], angles[1], angles[2]);
    }
    public static float[] quaternionToAngles(Quaternion q) {
        float[] angles = new float[3];
        return q.toAngles(angles);
    }

    public static String quaternionInDegrees(Quaternion q){
        float[] angles = new float[3];
        int[] iAngles = new int[3];
        q.toAngles(angles);
        angles[0] *= FastMath.RAD_TO_DEG;
        angles[1] *= FastMath.RAD_TO_DEG;
        angles[2] *= FastMath.RAD_TO_DEG;
        iAngles[0] = Math.round(angles[0]);
        iAngles[1] = Math.round(angles[1]);
        iAngles[2] = Math.round(angles[2]);

        return Arrays.toString(iAngles);
    }

    public static float[] convertDegreesToRadians(float[] angles){
        float[] ret = new float[3];
        ret[0] = angles[0] * FastMath.DEG_TO_RAD;
        ret[1] = angles[1] * FastMath.DEG_TO_RAD;
        ret[2] = angles[2] * FastMath.DEG_TO_RAD;
        return (ret);
    }
    public static float[] convertRadiansToDegrees(float[] angles){
        float[] ret = new float[3];
        ret[0] = angles[0] * FastMath.RAD_TO_DEG;
        ret[1] = angles[1] * FastMath.RAD_TO_DEG;
        ret[2] = angles[2] * FastMath.RAD_TO_DEG;
        return (ret);
    }
    public static float[] convertRadiansToPositiveDegrees(float[] angles){
        float[] ret = new float[3];
        ret[0] = angles[0] * FastMath.RAD_TO_DEG;
        if (ret[0] < 0) {
            ret[0] += 360;
        }
        ret[1] = angles[1] * FastMath.RAD_TO_DEG;
        if (ret[1] < 0) {
            ret[1] += 360;
        }
        ret[2] = angles[2] * FastMath.RAD_TO_DEG;
        if (ret[2] < 0) {
            ret[2] += 360;
        }
        return (ret);
    }

    public static String anglesInDegrees(float[] angles){
        if (angles.length != 3){
            return "ERROR: angles does not contain x,y,z";
        }
        angles[0] *= FastMath.RAD_TO_DEG;
        angles[1] *= FastMath.RAD_TO_DEG;
        angles[2] *= FastMath.RAD_TO_DEG;
        return Arrays.toString(angles);
    }
    
    public static void printRotationInDegrees(String message, Quaternion q) {
        System.out.println(message + quaternionInDegrees(q));
    }

    public static String transformToStringDegrees(Transform t) {
        String locationString = Vector3fUtilities.vector3fToString(t.getTranslation());
        String rotationString = quaternionInDegrees(t.getRotation());
        String scaleString = Vector3fUtilities.vector3fToString(t.getScale());
        return "L"+locationString+" Rdeg"+ rotationString +" S"+ scaleString;
    }

    /**
     * /* Copyright (c) 2013, Brandon Jones, Colin MacKenzie IV. All rights
     * reserved. .
     */
///**
// * @class Quaternion
// * @name quat
// */
//var quat = {};
//
///**
// * Creates a new identity quat
// *
// * @returns {quat} a new quaternion
// */
//quat.create = function() {
//    var out = new GLMAT_ARRAY_TYPE(4);
//    out[0] = 0;
//    out[1] = 0;
//    out[2] = 0;
//    out[3] = 1;
//    return out;
//};

    /// <summary>
    ///   <para>Returns the angle in radians between two rotations a and b.</para>
    /// </summary>
    /// <param name="a"></param>
    /// <param name="b"></param>
    // https://github.com/jamesjlinden/unity-decompiled/blob/master/UnityEngine/UnityEngine/Quaternion.cs
    //
    public static float Angle(Quaternion a, Quaternion b) {
        float dotProduct = Math.min(FastMath.abs(a.dot(b)), 1f);
        return FastMath.acos(dotProduct);
    }

    /**
     * Sets a quaternion to represent the shortest rotation from one vector to
     * another.
     *
     * Both vectors are assumed to be unit length.
     *
     * @param {quat} out the receiving quaternion.
     * @param {vec3} a the initial vector
     * @param {vec3} b the destination vector
     * @returns {quat} out
     */
    Quaternion rotationTo(Vector3f a, Vector3f b) {
        Quaternion out = new Quaternion();
        Vector3f tmpvec3;
        Vector3f xUnitVec3 = new Vector3f(1, 0, 0);
        Vector3f yUnitVec3 = new Vector3f(0, 1, 0);
        float dot = a.dot(b);
        if (dot < -0.999999) {
            tmpvec3 = xUnitVec3.cross(a);
            if (tmpvec3.length() < 0.000001) {
                tmpvec3 = a.cross(yUnitVec3);
            }
            tmpvec3.normalizeLocal();
            out = setAxisAngle(tmpvec3, FastMath.PI);
        } else if (dot > 0.999999) {
            out.set(0, 0, 0, 1);
        } else {
            tmpvec3 = a.cross(b);
            out.set(tmpvec3.getX(), tmpvec3.getY(), tmpvec3.getZ(), 1 + dot);
            out.normalizeLocal();
        }
        return out;
    }

    /**
     * Sets a quat from the given angle and rotation axis, then returns it.
     *
     * @param {quat} out the receiving quaternion
     * @param {vec3} axis the axis around which to rotate
     * @param {Number} rad the angle in radians
     * @returns {quat} out
     *
     */
    //quat.setAxisAngle = function(out, axis, rad) {
    //    rad = rad * 0.5;
    //    var s = Math.sin(rad);
    //    out[0] = s * axis[0];
    //    out[1] = s * axis[1];
    //    out[2] = s * axis[2];
    //    out[3] = Math.cos(rad);
    //    return out;
    //};
    static Quaternion setAxisAngle(Vector3f axis, float rad) {
        Quaternion out = new Quaternion();
        rad = rad * 0.5f;
        float s = FastMath.sin(rad);
        out.set(s * axis.getX(), s * axis.getY(), s * axis.getZ(), FastMath.cos(rad));
        return out;
    }

    ;

/**
 * Calculates the conjugate of a quat
 * If the quaternion is normalized, this function is faster than quat.inverse and produces the same result.
 *
 * @param {a} a quat to calculate conjugate of
 * @returns {quat} out
 */
    Quaternion conjugate(Quaternion a) {
        Quaternion out = new Quaternion();
        out.set(-a.getX(), -a.getY(), -a.getZ(), a.getZ());
        return out;
    }

    ;


    
    /**
     * from:
     * https://bitbucket.org/sinbad/ogre/src/9db75e3ba05c/OgreMain/include/OgreVector3.h?fileviewer=file-view-default#cl-651
     * Source ogre / OgreMain / include / OgreVector3.h
     */
    /**
     * Calculates the cross-product of 2 vectors, i.e. the vector that lies
     * perpendicular to them both.
     *
     * @remarks The cross-product is normally used to calculate the normal
     * vector of a plane, by calculating the cross-product of 2 non-equivalent
     * vectors which lie on the plane (e.g. 2 edges of a triangle).
     * @param vec2 Vector which, together with this one, will be used to
     * calculate the cross-product.
     * @return A vector which is the result of the cross-product. This vector
     * will <b>NOT</b> be normalised, to maximise efficiency - call
     * Vector3::normalise on the result if you wish this to be done. As for
     * which side the resultant vector will be on, the returned vector will be
     * on the side from which the arc from 'this' to rkVector is anticlockwise,
     * e.g. UNIT_Y.crossProduct(UNIT_Z) = UNIT_X, whilst
     * UNIT_Z.crossProduct(UNIT_Y) = -UNIT_X. This is because OGRE uses a
     * right-handed coordinate system.
     * @par For a clearer explanation, look a the left and the bottom edges of
     * your monitor's screen. Assume that the first vector is the left edge and
     * the second vector is the bottom edge, both of them starting from the
     * lower-left corner of the screen. The resulting vector is going to be
     * perpendicular to both of them and will go <i>inside</i> the screen,
     * towards the cathode tube (assuming you're using a CRT monitor, of
     * course).
     */
    Vector3f crossProduct(Vector3f vec1, Vector3f vec2) {
        return new Vector3f(
                vec1.y * vec2.z - vec1.z * vec2.y,
                vec1.z * vec2.x - vec1.x * vec2.z,
                vec1.x * vec2.y - vec1.y * vec2.x);
    }

    /**
     * Calculates the dot (scalar) product of this vector with another.
     *
     * @remarks The dot product can be used to calculate the angle between 2
     * vectors. If both are unit vectors, the dot product is the cosine of the
     * angle; otherwise the dot product must be divided by the product of the
     * lengths of both vectors to get the cosine of the angle. This result can
     * further be used to calculate the distance of a point from a plane.
     * @param vec1 Vector with which to calculate the dot product (together with
     * this one).
     * @return A float representing the dot product value.
     */
    float dotProduct(Vector3f vec1, Vector3f vec2) {
        return vec1.x * vec2.x + vec1.y * vec2.y + vec1.z * vec2.z;
    }

//   /**
// * Sets a quaternion to represent the shortest rotation from one
// * vector to another.
// *
// * Both vectors are assumed to be unit length.
// *
// * @param {quat} out the receiving quaternion.
// * @param {vec3} a the initial vector
// * @param {vec3} b the destination vector
// * @returns {quat} out
// */
//quat.rotationTo = (function() {
//    var tmpvec3 = vec3.create();
//    var xUnitVec3 = vec3.fromValues(1,0,0);
//    var yUnitVec3 = vec3.fromValues(0,1,0);
//
//    return function(out, a, b) {
//        var dot = vec3.dot(a, b);
//        if (dot < -0.999999) {
//            vec3.cross(tmpvec3, xUnitVec3, a);
//            if (vec3.length(tmpvec3) < 0.000001)
//                vec3.cross(tmpvec3, yUnitVec3, a);
//            vec3.normalize(tmpvec3, tmpvec3);
//            quat.setAxisAngle(out, tmpvec3, Math.PI);
//        } else if (dot > 0.999999) {
//            out[0] = 0;
//            out[1] = 0;
//            out[2] = 0;
//            out[3] = 1;
//        } else {
//            vec3.cross(tmpvec3, a, b);
//            out[0] = tmpvec3[0];
//            out[1] = tmpvec3[1];
//            out[2] = tmpvec3[2];
//            out[3] = 1 + dot;
//            return quat.normalize(out, out);
//        }
//    };
//})();
    /**
     * Gets the shortest arc quaternion to rotate 'from' vector to the
     * destination vector.
     */
    public static Quaternion getRotationTo(Vector3f from, Vector3f dest) {
        final Vector3f xUnitVec3 = new Vector3f(1, 0, 0);
        final Vector3f yUnitVec3 = new Vector3f(0, 1, 0);
        Quaternion out = new Quaternion();
        Vector3f a = new Vector3f(from);
        Vector3f b = new Vector3f(dest);
        a.normalizeLocal();
        b.normalizeLocal();

        float dot = a.dot(b);
        // If dot == 1, vectors are the same
        if (dot > 0.999999) {
            out = Quaternion.IDENTITY;
        } else if (dot < -0.999999) {
            // First try X axis
            Vector3f axis = a.cross(xUnitVec3);
            if (axis.length() < 0.000001) {
                // Pick Y axis if 'from' vector is colinear with X axis
                axis = a.cross(yUnitVec3);
            }
            axis.normalizeLocal();
            out = setAxisAngle(axis, FastMath.PI);
        } else {
            Vector3f tmpvec3 = a.cross(b);
            out.set(tmpvec3.x, tmpvec3.y, tmpvec3.z, 1 + dot);
            out.normalizeLocal();
        }
        return out;
    }

///////////////////////////////////////////////////////////////////////////////
//
// Quaternion.cpp : Quaternion System structure implementation file
//
// Purpose:	Quaternion Conversion and Evaluation Functions
//
// I DIDN'T PUT THESE IN A C++ CLASS FOR CROSS PLATFORM COMPATIBILITY
// SINCE THE ENGINE MAY BE IMPLEMENTED ON CONSOLES AND OTHER SYSTEMS
// ALSO NOT TOTALLY OPTIMIZED AND TRICKED OUT FOR CLARITY
//
// Created:
//		JL 9/1/97		
//
// Sources:
//	Shoemake, Ken, "Animating Rotations with Quaternion Curves"
//		Computer Graphics 85, pp. 245-254
//	Watt and Watt, Advanced Animation and Rendering Techniques
//		Addison Wesley, pp. 360-368
//  Shoemake, Graphic Gems II.
//
// Notes:
//			There are a couple of methods of conversion here so it
// can be played around with a bit.  One is more clear and the other
// is a bit faster.  
//
///////////////////////////////////////////////////////////////////////////////
//
//	Copyright 1997 Jeff Lander, All Rights Reserved.
//  For educational purposes only.
//  Please do not republish in electronic or print form without permission
//  Thanks - jeffl@darwin3d.com
//
///////////////////////////////////////////////////////////////////////////////
// https://answers.unity.com/questions/532297/rotate-a-vector-around-a-certain-point.html
// Useage: To get the position of a bone of a rotated node:
//                RotatePointAroundPivot(
//                        boxer1Control.getSkeleton().getBone("hand.L").getModelSpacePosition(),
//                        Vector3f.ZERO,
//                        boxer1Node.getLocalRotation()));
    public static Vector3f RotatePointAroundPivot(Vector3f point, Vector3f pivot, Quaternion rotation) {
        Vector3f dir = point.subtract(pivot); // get point direction relative to pivot
        dir = rotation.mult(dir);
        point = dir.add(pivot); // calculate rotated point
        return point; // return it
    }

    // https://answers.unity.com/questions/532297/rotate-a-vector-around-a-certain-point.html
    public static Vector3f RotatePointAroundPivot(Vector3f point, Vector3f pivot, Vector3f angles) {
        final float[] anglesArray = {angles.x, angles.y, angles.z};
        Vector3f dir = point.subtract(pivot); // get point direction relative to pivot
        Quaternion rotation = new Quaternion(anglesArray);
        dir = rotation.mult(dir);
        point = dir.add(pivot); // calculate rotated point
        return point; // return it
    }

///////////////////////////////////////////////////////////////////////////////
// Function:	CopyVector
// Purpose:		Copy a vector
// Arguments:	pointer to destination and source
///////////////////////////////////////////////////////////////////////////////
    public static Vector3f CopyVector(Vector3f vect) {
        return new Vector3f(vect.x, vect.y, vect.z);
    }
//// CopyVector ///////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
// Function:	ScaleVector
// Purpose:		Scale a vector
// Arguments:	pointer to vector and scale factor
///////////////////////////////////////////////////////////////////////////////
    public static Vector3f ScaleVector(Vector3f vect, float scale) {
        return new Vector3f(vect.x * scale,
                vect.y * scale,
                vect.z * scale);

    }
//// ScaleVector ///////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
// Function:	AddVectors
// Purpose:		Add two vectors
// Arguments:	pointer to vectors and dest
///////////////////////////////////////////////////////////////////////////////
    public static Vector3f AddVectors(Vector3f vect1, Vector3f vect2) {
        return new Vector3f(vect1.x + vect2.x, vect1.y + vect2.y, vect1.z + vect2.z);
    }
//// AddVectors ///////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
// Function:	DoVector3fs
// Purpose:		Compute the dot product of two vectors
// Arguments:	pointer to vectors
// Returns:		Dot product
///////////////////////////////////////////////////////////////////////////////
    public static float DoVector3fs(Vector3f vect1, Vector3f vect2) {
        return (vect1.x * vect2.x)
                + (vect1.y * vect2.y)
                + (vect1.z * vect2.z);
    }
//// DoVector3fs ///////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
// Function:	CrossVectors
// Purpose:		Computes the cross product of two vectors
// Arguments:	pointer to vectors and dest
///////////////////////////////////////////////////////////////////////////////
    public static Vector3f CrossVectors(Vector3f vect1, Vector3f vect2) {
        // COMPUTE THE CROSS PRODUCT
        return new Vector3f(
                (vect1.y * vect2.z) - (vect1.z * vect2.y),
                (vect1.z * vect2.x) - (vect1.x * vect2.z),
                (vect1.x * vect2.y) - (vect1.y * vect2.x));
    }
//// CrossVectors /////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
// Function:	QuatToAxisAngle
// Purpose:		Convert a Quaternion to Axis Angle representation
// Arguments:	A quaternion to convert, a axisAngle to set
///////////////////////////////////////////////////////////////////////////////
    public static Quaternion QuatToAxisAngle(Quaternion quat) {
/// Local Variables ///////////////////////////////////////////////////////////
        float scale, tw;
///////////////////////////////////////////////////////////////////////////////
        tw = FastMath.acos(quat.getW()) * 2;
        scale = FastMath.sin(tw / 2.0f);
        float x = quat.getX() / scale;
        float y = quat.getY() / scale;
        float z = quat.getZ() / scale;

        // NOW CONVERT THE ANGLE OF ROTATION BACK TO DEGREES
        float w = (tw * (360 / 2)) / FastMath.PI;
        return new Quaternion(x, y, z, w);
    }
// QuatToAxisAngle  /////////////////////////////////////////////////////////

    public static Quaternion AxisAngleToQuat(Vector3f axis, float angle) {
/// Local Variables ///////////////////////////////////////////////////////////
        float scale, tw;
///////////////////////////////////////////////////////////////////////////////
        tw = (angle * FastMath.PI) / (180.0f);		// TO RADIANS
        float w = FastMath.cos(tw / 2.0f);
        scale = FastMath.sin(tw / 2.0f);
        float x = axis.x * scale;
        float y = axis.y * scale;
        float z = axis.z * scale;
        return new Quaternion(x, y, z, w);
    }

///////////////////////////////////////////////////////////////////////////////
// Function:	QuatToEuler
// Purpose:		Convert a Quaternion back to Euler Angles
// Arguments:	Quaternions and target Euler vector
// Notes:		The method is to convert Quaternion to a 3x3 matrix and
//				decompose the matrix.  This is subject to the
//				ambiguities of square roots and problems with inverse trig.
//				Matrix to Euler conversion is really very ill-defined but works
//				for my purposes.
///////////////////////////////////////////////////////////////////////////////
    public static Vector3f QuatToEuler(Quaternion quat) {
        Logger logger = Logger.getLogger(Quaternions.class.getName());
        logger.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();

        logger.addHandler(handler);

/// Local Variables ///////////////////////////////////////////////////////////
        float[][] matrix = new float[3][3];
        float cx, sx, x;
        float cy, sy, y, yr;
        float cz, sz, z;
        Vector3f result = new Vector3f();
///////////////////////////////////////////////////////////////////////////////
        // CONVERT QUATERNION TO MATRIX - I DON'T REALLY NEED ALL OF IT
        matrix[0][0] = 1.0f - (2.0f * quat.getY() * quat.getY()) - (2.0f * quat.getZ() * quat.getZ());
//	matrix[0][1] = (2.0f * quat.x * quat.y) - (2.0f * quat.w * quat.z);
//	matrix[0][2] = (2.0f * quat.x * quat.z) + (2.0f * quat.w * quat.y);
        matrix[1][0] = (2.0f * quat.getX() * quat.getY()) + (2.0f * quat.getW() * quat.getZ());
//	matrix[1][1] = 1.0f - (2.0f * quat.x * quat.x) - (2.0f * quat.z * quat.z);
//	matrix[1][2] = (2.0f * quat.y * quat.z) - (2.0f * quat.w * quat.x);
        matrix[2][0] = (2.0f * quat.getX() * quat.getZ()) - (2.0f * quat.getW() * quat.getY());
        matrix[2][1] = (2.0f * quat.getY() * quat.getZ()) + (2.0f * quat.getW() * quat.getX());
        matrix[2][2] = 1.0f - (2.0f * quat.getX() * quat.getX()) - (2.0f * quat.getY() * quat.getY());

        sy = -matrix[2][0];
        cy = FastMath.sqrt(1 - (sy * sy));
        yr = FastMath.atan2(sy, cy);
        result.y = yr;

        // AVOID DIVIDE BY ZERO ERROR ONLY WHERE Y= +-90 or +-270 
        // NOT CHECKING cy BECAUSE OF PRECISION ERRORS
        if (sy != 1.0f && sy != -1.0f) {
            cx = matrix[2][2] / cy;
            sx = matrix[2][1] / cy;
            result.x = FastMath.atan2(sx, cx);

            cz = matrix[0][0] / cy;
            sz = matrix[1][0] / cy;
            result.z = FastMath.atan2(sz, cz);
        } else {
            logger.warning("SINCE Cos(Y) IS 0, I AM SCREWED.  ADOPT THE STANDARD Z = 0");
            // SINCE Cos(Y) IS 0, I AM SCREWED.  ADOPT THE STANDARD Z = 0
            // I THINK THERE IS A WAY TO FIX THIS BUT I AM NOT SURE.  EULERS SUCK
            // NEED SOME MORE OF THE MATRIX TERMS NOW
            matrix[1][1] = 1.0f - (2.0f * quat.getX() * quat.getX()) - (2.0f * quat.getZ() * quat.getZ());
            matrix[1][2] = (2.0f * quat.getY() * quat.getZ()) - (2.0f * quat.getW() * quat.getX());
            cx = matrix[1][1];
            sx = -matrix[1][2];
            result.x = FastMath.atan2(sx, cx);

            cz = 1.0f;
            sz = 0.0f;
            result.z = FastMath.atan2(sz, cz);
        }
        return result;
    }
// QuatToEuler  ///////////////////////////////////////////////////////////////

    public static Vector3f QuatToEulerDegrees(Quaternion quat) {
        Vector3f vecRadians = QuatToEuler(quat);
        return new Vector3f(
                vecRadians.x * 180.0f / FastMath.PI,
                vecRadians.y * 180.0f / FastMath.PI,
                vecRadians.z * 180.0f / FastMath.PI);
    }
}
