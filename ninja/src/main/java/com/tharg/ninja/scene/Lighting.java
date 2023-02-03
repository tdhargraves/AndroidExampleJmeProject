/*
 * Functions to setup lighting
 */
package com.tharg.ninja.scene;

import com.jme3.light.AmbientLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.light.PointLight;
import com.jme3.renderer.ViewPort;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;

/**
 *
 * @author tharg
 */
public class Lighting {

    final private Node rootNode;
    final private SimpleApplication application;
    final private ViewPort viewPort;
    final private Platform platform;

    public Lighting(SimpleApplication app) {
        rootNode = app.getRootNode();
        application = app;
        viewPort = application.getViewPort();
        platform = JmeSystem.getPlatform();
    }

    void setupSpotlight(Vector3f position) {
        SpotLight spot = new SpotLight();
        spot.setSpotRange(100f);                           // distance
        spot.setSpotInnerAngle(10f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(30f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(1.3f));         // light color
        spot.setPosition(position);
        spot.setDirection(position.negate());
        rootNode.addLight(spot);

        /* Shadows can be implemented 2 ways:
           1. Shadow filters: All objects receive shadows.
           2. Shadow renderers pay attention to shadow modes when determining which objects receive shadows.
        NOTE:  Frame rate drops from 350 to 160!!!
        See https://jmonkeyengine.github.io/wiki/jme3/advanced/light_and_shadow.html
         */
 /* PS. found this hint https://hub.jmonkeyengine.org/t/requesting-high-quality-jme-media/42185/7
        * Shadows whould look much better if used something like:
        * dlsf.setEdgesThickness(2); //or different number, depends on world scale.
        * Because currently its too hard, too much pixel difference
         */
        final int SHADOWMAP_SIZE = 1024;

        // Works on Windows.
        // Works on Android but Causes horizontal lines on Jaime's face and hands.
        SpotLightShadowFilter slsf = new SpotLightShadowFilter(application.getAssetManager(), SHADOWMAP_SIZE);
        slsf.setLight(spot);
        slsf.setShadowIntensity(0.5f);
        slsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(application.getAssetManager());
        fpp.addFilter(slsf);
        viewPort.addProcessor(fpp);

//    // Works on Windows.
//    // Doesn't works on Android.
//    SpotLightShadowRenderer shadows = new SpotLightShadowRenderer(application.getAssetManager(), 1024);
//        shadows.setLight(spot);
//        shadows.setShadowIntensity(0.7f);
//        shadows.setEdgeFilteringMode(EdgeFilteringMode.PCF8);
//        shadows.setShadowCompareMode(CompareMode.Software);
//        viewPort.addProcessor(shadows);
    }

    void setupPointLight(Vector3f position) {
        PointLight pl = new PointLight();
        pl.setColor(ColorRGBA.White.mult(1.5f));
        pl.setPosition(position);
        pl.setRadius(100.0f);
        rootNode.addLight(pl);

        if (platform.toString().contains("Android")) {
            // Works well on Android.
            // Doesn't work on Windows
            PointLightShadowRenderer plShadows = new PointLightShadowRenderer(application.getAssetManager(), 1024);
//          plShadows.setShadowCompareMode(CompareMode.Software);
            plShadows.setLight(pl);
            plShadows.setShadowIntensity(1.0f);
//          plShadows.setEdgeFilteringMode(EdgeFilteringMode.PCF8); //default Bilinear
            viewPort.addProcessor(plShadows);
        } else {
            // ASSUME WINDOWS
            // Works well on Windows. 
            // On Android, causes horizontal lines on Jaime's face and hands.
            PointLightShadowFilter plsf = new PointLightShadowFilter(application.getAssetManager(), 1024);
            plsf.setLight(pl);
            plsf.setEnabled(true);
            FilterPostProcessor fpp = new FilterPostProcessor(application.getAssetManager());
            fpp.addFilter(plsf);
            viewPort.addProcessor(fpp);
        }
    }

    void setupGroundLight() {
        //directionallight to fake indirect light coming from the ground
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.mult(1.0f));
        dl.setEnabled(true);
        dl.setDirection(Vector3f.UNIT_Y);
        rootNode.addLight(dl);
    }

    void setupAmbientLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.7f));
        al.setEnabled(true);
        rootNode.addLight(al);
    }

    void setupSunlight() {
        Vector3f direction = new Vector3f(1f, -2f, -1f).normalizeLocal();
        DirectionalLight sun = new DirectionalLight(direction);
        sun.setColor(ColorRGBA.White.mult(0.1f));
        rootNode.addLight(sun);
    }

    public void setupLighting() {
        setupAmbientLight();
        setupGroundLight();
//        setupSpotlight(new Vector3f(10.0f, 10.0f, 10.0f));
        setupPointLight(new Vector3f(10.0f, 10.0f, 10.0f));
        setupSunlight();
    }
}
