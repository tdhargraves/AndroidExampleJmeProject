package com.tharg.ninja;

import android.os.Bundle;
import android.view.View;

import com.jme3.system.AppSettings;

import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

public class MainActivity extends AndroidHarness {
    public MainActivity() {
        appClass = Main.class.getCanonicalName();
        exitDialogTitle = "Exit?";
        exitDialogMessage = "Are you sure you want to quit?";
        mouseEventsEnabled = true;
        screenShowTitle=true;
        splashPicID = R.drawable.splash;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Needed to hide android buttons again
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppSettings settings = new AppSettings(true);
//        settings.setAudioRenderer(null);
        app.setSettings(settings);
        // Hide android buttons again
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        app.stop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    //    Main.setFilePath(getApplicationContext().getFilesDir().toPath());
    //    setTheme(R.style.Theme_Ialon);
        super.onCreate(savedInstanceState);
    }

}
