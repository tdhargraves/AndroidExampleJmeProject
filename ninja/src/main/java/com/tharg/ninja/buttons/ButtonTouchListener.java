package com.tharg.ninja.buttons;


import static com.tharg.ninja.Main.rotateCamera;

import com.jme3.input.controls.TouchListener;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.tharg.ninja.Main;

public class ButtonTouchListener implements TouchListener {

    private Main main;

    public ButtonTouchListener(Main main) {
        this.main = main;
    }

    @Override
    public void onTouch(String name, TouchEvent event, float tpf) {
        if (event.getType() == TouchEvent.Type.TAP) {
            if (main.buttons.getButtonKinematic().getWorldBound().intersects(new Vector3f(event.getX(), event.getY(), 1))) {
                main.actionRagdoll();
            } else if (main.buttons.getButtonRagdoll().getWorldBound().intersects(new Vector3f(event.getX(), event.getY(), 1))) {
                main.actionKinematic();
            } else if (main.buttons.getButtonQuit().getWorldBound().intersects(new Vector3f(event.getX(), event.getY(), 1))) {
                main.actionQuit();
            }
            event.setConsumed();
        }
        if (event.getType() == TouchEvent.Type.MOVE) {
            rotateCamera(main.getCamera(), -event.getDeltaX() / 2000, Vector3f.UNIT_Y);
            rotateCamera(main.getCamera(), -event.getDeltaY() / 2000, main.getCamera().getLeft());
            event.setConsumed();
        }
        if (event.getType() == TouchEvent.Type.SCALE_MOVE) {
            Vector3f vel = new Vector3f();
            Camera cam = main.getCamera();
            Vector3f pos = cam.getLocation().clone();
            cam.getDirection(vel);
            vel.multLocal(event.getDeltaScaleSpan()/500);
            pos.addLocal(vel);
            cam.setLocation(pos);
            event.setConsumed();
        }
    }
}

