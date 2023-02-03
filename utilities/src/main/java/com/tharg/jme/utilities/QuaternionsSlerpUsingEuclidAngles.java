package com.tharg.jme.utilities;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class QuaternionsSlerpUsingEuclidAngles {
    /*
     * Recommended Usage: Use constructor with parameters, then call interpolate(increment);
     * OR: use setup to set up the start and end angles
     *
     */
    private boolean isDebugEnabled = true;

    private float[] startAngleDegrees;
    private float[] endAngleDegrees;
    public boolean xRotateClockwise;
    public boolean yRotateClockwise;
    public boolean zRotateClockwise;

    private float xDegreesFromStartToEnd;
    private float yDegreesFromStartToEnd;
    private float zDegreesFromStartToEnd;
    private boolean xRotationEnabled = true;
    private boolean yRotationEnabled = true;
    private boolean zRotationEnabled = true;

    public QuaternionsSlerpUsingEuclidAngles() {
    }

    public QuaternionsSlerpUsingEuclidAngles(Quaternion startQuaternion, Quaternion endQuaternion) {
        setup(startQuaternion, endQuaternion);
    }

    // The is used when a model has a head which needs to be kept rising as the model is rotated
    protected boolean[] rotateCockwiseHeadUp(Float[] startAnglesDegrees, Float[] endAnglesDegrees){
        boolean[] returnClockwiseFlags = {false, false, false};

        return returnClockwiseFlags;
    }
    // Call to setup/change start and end angles.
    public void setup(Quaternion startQuaternion, Quaternion endQuaternion) {
        // The torso of the boxer is rotated -90 degrees aroung the X-axis
        float INITIAL_X_DEGREES_OFFSET = -90;
        float [] tempAnglesRadians = Quaternions.quaternionToAngles(startQuaternion);
        this.startAngleDegrees = Quaternions.convertRadiansToDegrees(tempAnglesRadians);
        tempAnglesRadians = Quaternions.quaternionToAngles(endQuaternion);
        this.endAngleDegrees = Quaternions.convertRadiansToDegrees(tempAnglesRadians);

        if (isDebugEnabled) {
            if (this.startAngleDegrees[0] - INITIAL_X_DEGREES_OFFSET > 90 &&
                    FastMath.abs(this.startAngleDegrees[1]) < 90 ||
                    this.startAngleDegrees[0] - INITIAL_X_DEGREES_OFFSET < -90 &&
                            FastMath.abs(this.startAngleDegrees[1]) > 45) {
                System.out.println("setup *****************Boxer is face DOWN DOWN *****************");
            } else {
                System.out.println("setup *****************Boxer is face UP UP UP *****************");

            }
        }
        xDegreesFromStartToEnd = leastDegreesFromStartToEnd(startAngleDegrees[0], endAngleDegrees[0]);
        yDegreesFromStartToEnd = leastDegreesFromStartToEnd(startAngleDegrees[1], endAngleDegrees[1]);
        zDegreesFromStartToEnd = leastDegreesFromStartToEnd(startAngleDegrees[2], endAngleDegrees[2]);

    }

    // Minimum of 3 values
    public static float min(float a, float b, float c) {
        return Math.min(Math.min(a, b), c);
    }
    // Maximum of 3 values
    public static float max(float a, float b, float c) {
        return Math.max(Math.max(a, b), c);
    }
/*
 * Let END be the target bearing and START be the current bearing. Let's consider three numbers:
 * A = END - START
 * B = END - START + 360
 * C = END - START - 360
 * Now, whichever of |A|, |B|, and |C| is the smallest tells us which of A, B and C is relevant
 * And if the one with the smallest absolute value is positive, go clockwise,
 * and if it's negative, go counterclockwise.
 */
    public float leastDegreesFromStartToEnd(float startDegrees, float endDegrees) {
        float returnDegrees;
        // Change negative angles to be positive, i.e. in the range 0 to 360 degrees
        if (startDegrees < 0) {
            startDegrees += 360.0f;
        }
        if (endDegrees < 0) {
            endDegrees += 360.0f;
        }
        float a = endDegrees - startDegrees;
        float b = endDegrees - startDegrees + 360;
        float c = endDegrees - startDegrees - 360;
        float absA = FastMath.abs(a);
        float absB = FastMath.abs(b);
        float absC = FastMath.abs(c);

        float min = min(absA, absB, absC);
        if (absA == min){
            returnDegrees = a;
        } else if (absB == min){
            returnDegrees = b;
        } else {
            returnDegrees = c;
        }
        return returnDegrees;
    }

    /*
     * Let END be the target bearing and START be the current bearing. Let's consider three numbers:
     * A = END - START
     * B = END - START + 360
     * C = END - START - 360
     * Now, whichever of |A|, |B|, and |C| is the smallest tells us which of A, B and C is relevant
     * And if the one with the smallest absolute value is positive, go clockwise,
     * and if it's negative, go counterclockwise.
     */
    public float mostDegreesFromStartToEnd(float startDegrees, float endDegrees) {
        float mostDegrees;
        float leastDegrees = leastDegreesFromStartToEnd(startDegrees, endDegrees);
        mostDegrees = (360 - FastMath.abs(leastDegrees));
        if (leastDegrees> 0){
            mostDegrees = -mostDegrees;
        }
        return mostDegrees;
    }

    /*
     * This is a slerp function which interpolates between two 3d angles using increment (0 to 1)
     * Start and end angles must have been initialised by calling setup,
     * Setup also calculates the DegreesFromStartToEnd for each axis.
     */
    public Quaternion interpolate(float increment) {
        if (startAngleDegrees == null || startAngleDegrees == null) {
            throw new IllegalStateException(
                    "ERROR: Start and End angles not setup for SlerpUsingEuclidAngles.interpolate");
        }
        if (increment < 0 || increment > 1) {
            throw new IllegalArgumentException(
                    "ERROR: increment must be between 0 and 1");
        }

        float x = 0;
        float y = 0;
        float z = 0;
        if (xRotationEnabled) {
                x = startAngleDegrees[0] + (xDegreesFromStartToEnd * increment);
        }
        if (yRotationEnabled) {
                y = startAngleDegrees[1] + (yDegreesFromStartToEnd * increment);
        }
        if (zRotationEnabled) {
                z = startAngleDegrees[2] + (zDegreesFromStartToEnd * increment);
        }
        Quaternion returnQuaternion = new Quaternion();
        returnQuaternion.fromAngles(
                x * FastMath.DEG_TO_RAD,
                y * FastMath.DEG_TO_RAD,
                z * FastMath.DEG_TO_RAD);
        return returnQuaternion;
    }

    // Do not repeatedly call this if start and end angles do not change
    // Call once to setup start and end angle data, then call interpolate(increment) thereafter
    public Quaternion interpolateBetween(Quaternion startQuaternion, Quaternion endQuaternion, float increment) {
        setup(startQuaternion, endQuaternion);
        return interpolate(increment);
    }

    public float[] getStartAngleDegrees() {
        return startAngleDegrees;
    }

    public float[] getEndAngleDegrees() {
        return endAngleDegrees;
    }

    public void setRotationsEnabled(boolean xRotationEnabled, boolean yRotationEnabled, boolean zRotationEnabled) {
        this.xRotationEnabled = xRotationEnabled;
        this.yRotationEnabled = yRotationEnabled;
        this.zRotationEnabled = zRotationEnabled;
    }
}
