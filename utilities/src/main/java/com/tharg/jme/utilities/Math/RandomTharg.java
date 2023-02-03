/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tharg.jme.utilities.Math;

import com.jme3.math.FastMath;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Odd Math utilities
 *
 * @author tom_h
 */
public class RandomTharg {

    public static float MIN_DIFF_BETWEEN_MAX_AND_MIN = 0.0001f;

    // Returns random number scaled to be any value between max and min
    public static float random(float min, float max) {
        float myMax;
        float myMin;
        if ((max - min) < MIN_DIFF_BETWEEN_MAX_AND_MIN) {
            //max and min almost equal 
            return max;
        } else {
            if (max > min) {
                myMax = max;
                myMin = min;
            } else {
                // max and min wrong way round. return a value anyway.
                myMax = min;
                myMin = max;
            }
        }
        Random randomGenerator = new Random();
        float randomValue = randomGenerator.nextFloat(); // Value between 0 and 1.

        float scaleFactor = myMax - myMin;
        float offset = myMin;
        //System.out.println("randomValue = "+randomValue+", scaleFactor = "+scaleFactor+", offset = "+offset);
        return randomValue * scaleFactor + offset;
    }

    public static void main(String... argv) {
        boolean allTestsPass = true;
        HashMap<Float, Float> inputValues = new HashMap<>();
        inputValues.put(-0.0f, 0.0f);
        inputValues.put(1.0f, 1.00001f);
        inputValues.put(4.0f, 5.0f);
        inputValues.put(5.0f, 4.0f);
        inputValues.put(-5.0f, 5.0f);
        inputValues.put(-10.0f, 0.0f);
        inputValues.put(-15.0f, -5.0f);
        inputValues.put(-1000.0f, -1000.1f);

        Iterator<Float> keySetIterator = inputValues.keySet().iterator();
        while (keySetIterator.hasNext()) {
            boolean testPass;
            Float key = keySetIterator.next();
            for (int i = 0; i < 100; i++) {
                float result = random(key, inputValues.get(key));
                // If min and max are almost identical, result should be too
                boolean inputValuesAlmostIdentical = (FastMath.abs(
                        FastMath.abs(key) - FastMath.abs(inputValues.get(key)))
                        < RandomTharg.MIN_DIFF_BETWEEN_MAX_AND_MIN);
                boolean resultAlmostEqualInputMin = (FastMath.abs(
                        FastMath.abs(key) - FastMath.abs(result))
                        < RandomTharg.MIN_DIFF_BETWEEN_MAX_AND_MIN);
                boolean resultAlmostEqualInputMax = (FastMath.abs(
                        FastMath.abs(inputValues.get(key)) - FastMath.abs(result))
                        < RandomTharg.MIN_DIFF_BETWEEN_MAX_AND_MIN);
                if (inputValuesAlmostIdentical && (resultAlmostEqualInputMin || resultAlmostEqualInputMax)) {
                    testPass = true;
                } else {
                    // Test if the result is between max and min input values
                    if (key < inputValues.get(key)) {
                        testPass = !(result < key || result > inputValues.get(key));
                    } else { // min and max switched is supported!
                        testPass = (result < key || result > inputValues.get(key));
                    }
                }
                if (!testPass) {
                    allTestsPass = false;
                    System.out.println("FAILED: Input " + key + "," + inputValues.get(key) + ", output = " + random(key, inputValues.get(key)));
                }
            }
            System.out.println("PASSED: Input " + key + "," + inputValues.get(key));

        }
        System.out.println("All Tests Pass = " + allTestsPass);
    }
}
