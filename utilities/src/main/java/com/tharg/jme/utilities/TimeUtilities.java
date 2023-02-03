/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tharg.jme.utilities;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tharg
 */
public class TimeUtilities {

    public class Timer{
            final LocalTime START_TIME = LocalTime.now();
            long lastTimeoutMillis = 0;

            //Return true if it's been timeoutDurationMillis since I last returned true
            public boolean timeoutMillis(long timeoutDurationMillis) {
                Duration duration = Duration.between(START_TIME, LocalTime.now());
                long millisSinceStart = duration.toMillis();
                if ((millisSinceStart - lastTimeoutMillis) > timeoutDurationMillis) {
                    lastTimeoutMillis = millisSinceStart;
                    return true;
                }
                return false;
            }
        }
    private static Logger logger = Logger.getLogger(TimeUtilities.class.getName());

    private static final LocalTime START_TIME = LocalTime.now();
    private static long lastTimeoutMillis = 0;

    //Return true if it's been timeoutDurationMillis since I last returned true
    public static boolean timeoutMillis(long timeoutDurationMillis) {
        Duration duration = Duration.between(START_TIME, LocalTime.now());
        long millisSinceStart = duration.toMillis();
        if ((millisSinceStart - lastTimeoutMillis) > timeoutDurationMillis) {
            lastTimeoutMillis = millisSinceStart;
            return true;
        }
        return false;
    }

    //Return time in format hh:mm:ss.SSS
    public static String getLocalTimeNow() {
        LocalTime thisSec = LocalTime.now();
        return (thisSec.getHour() + ":" + thisSec.getMinute() + ":" + thisSec.getSecond());
    }

    //Return date in format yyyy/MM/dd
    public static String getLocalDateNow() {
        LocalDate thisSec = LocalDate.now();
        return (thisSec.getYear() + ":" + thisSec.getMonthValue() + ":" + thisSec.getDayOfMonth());
    }

    //Return date and tiime in format yyyy/MM/dd hh:mm:ss.SSS
    public static String getLocalDateTimeNow() {
        return (getLocalTimeNow() + " " + getLocalDateNow());
    }

    //Blocks and waits for delay (milliseconds)
    public static void waitMilliseconds(int delay) {
        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

}
