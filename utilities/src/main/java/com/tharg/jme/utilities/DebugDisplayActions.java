package com.tharg.jme.utilities;

import com.jme3.math.Transform;
import com.jme3.scene.Node;

import java.time.LocalTime;

public class DebugDisplayActions {
    private static boolean isDebugEnabled = false;

    public static void setIsDebugEnabled(boolean enabled) {
        isDebugEnabled = enabled;
    }
    public static boolean getIsDebugEnabled() {
        return isDebugEnabled;
    }


    public static void printDebugMessageForce(String message) {
        LocalTime localTime = LocalTime.now();
        System.out.println(
                localTime.getHour() + ":" + localTime.getMinute() + ":" + localTime.getSecond() + " " + message);

    }
    public static void printDebugMessage(String message) {
        if (isDebugEnabled) {
            LocalTime localTime = LocalTime.now();
            System.out.println(
                    localTime.getHour()+":"+localTime.getMinute()+":"+localTime.getSecond()+" "+message);
        }
    }


    public static void debugPrintNodeLocationRotation(Node node) {
        printDebugMessage (node.getName() +
                "L" +
                node.getLocalTranslation() +
                ", Rdeg" +
                Quaternions.quaternionInDegrees(node.getLocalRotation()));
    }

    public static void debugPrintTransformLocationRotation(Transform transform) {
        printDebugMessage ("Transform = " + Quaternions.transformToStringDegrees(transform));
    }
}
