/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tharg.jme.utilities;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * Assuming the calling class extends SimpleApplication:
 * Usage example MyAppSettings.getSettings(this, APP_NAME).getFrameRate();
 * @author tharg
 */
public class MyAppSettings {

    public static AppSettings setAppSettingsNoSplashScreen(SimpleApplication app){
        AppSettings newSettings = new AppSettings(true);
        app.setShowSettings(false);
        newSettings.setWidth(1);
        newSettings.setHeight(1);
        app.setSettings(newSettings);
        return newSettings;
    }
    
    public static AppSettings getSettings(SimpleApplication app, String AppName){
        final String iconPath = "./assets/Interface/icons";
        AppSettings newSettings = new AppSettings(true);
        newSettings.setRenderer(AppSettings.LWJGL_OPENGL2);
        newSettings.setWidth(1024);
        newSettings.setHeight(720);
        newSettings.setFrameRate(0);
        newSettings.setTitle(AppName);
        newSettings.setResizable(false);
        
        // Input all icons found in iconPath
        final File iconFolder = new File(iconPath);
        String[] iconFileArray = listFilesForFolder(iconFolder);
        BufferedImage[] iconImages = new BufferedImage[iconFileArray.length];
        for (int i = 0; i < iconFileArray.length; i++) {
            try {
                iconImages[i] = ImageIO.read(new File(iconFileArray[i]));
            } catch (IOException e) {
                System.err.println("Exception reading icons " + e);
                System.err.println("couldn't read icon: " + iconFileArray[i]);
            }
        }
        //System.out.println("icons files: "+Arrays.toString(iconFileArray));
        Object[] icons = iconImages;
        newSettings.setIcons(icons);
        
        return newSettings;
    }

    public static String[] listFilesForFolder(final File folder) {
        ArrayList<String> al = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                al.add(fileEntry.toString());
            }
        }
        String[] result = new String[al.size()];
        result = al.toArray(result);
        return result;
    }

}
