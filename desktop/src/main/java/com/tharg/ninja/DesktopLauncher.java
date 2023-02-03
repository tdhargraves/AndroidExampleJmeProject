package com.tharg.ninja;

import com.jme3.system.AppSettings;
import com.tharg.ninja.Main;

public class DesktopLauncher {
    public static void main(String[] args) {
        Main app = new Main();

        AppSettings newSettings = new AppSettings(true);
        newSettings.setFrameRate(60);
        newSettings.setVSync(false);
        newSettings.setResolution(1280, 720);
        newSettings.setTitle(newSettings.getTitle() + " Android Example JME Project");
        newSettings.setFullscreen(false);
        newSettings.setRenderer(AppSettings.LWJGL_OPENGL32);
        app.setSettings(newSettings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false); // free the mouse!
        app.start();
    }
}