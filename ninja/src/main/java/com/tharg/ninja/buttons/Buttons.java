package com.tharg.ninja.buttons;

import com.jme3.app.Application;
import com.simsilica.lemur.Container;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;


import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.DynamicInsetsComponent;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.event.DefaultMouseListener;
import com.simsilica.lemur.event.MouseListener;
import com.tharg.ninja.Main;


public class Buttons {//} extends BaseAppState{ // implements ActionListener, AnalogListener{
    private static final int SCREEN_MARGIN = 30;
    private Main main;
    private int buttonSize;
    private boolean isPressed = false;
    private Container buttonRagdoll;
    private Container buttonKinematic;
    private Container buttonQuit;

    public Buttons(Application application) {
        main = (Main) application;
        buttonSize = main.getCamera().getHeight() / 8;
        GuiGlobals.initialize(main);
        createButtons();
        showControlButtons();
    }

    public Container getButtonRagdoll() {
        return buttonRagdoll;
    }
    public Container getButtonKinematic() {
        return buttonKinematic;
    }
    public Container getButtonQuit() {
        return buttonQuit;
    }

    private void createButtons() {
        final int SCREEN_WIDTH = main.getCamera().getWidth();


        // Configuration
        buttonRagdoll = createButton(
                "Kinematic", null, buttonSize, 2 * SCREEN_MARGIN + 2 * buttonSize, SCREEN_MARGIN + buttonSize,
                new DefaultMouseListener() {
                    public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
                        event.setConsumed();
                        boolean isPressed = event.isPressed();
                        highlight(isPressed, buttonRagdoll);
                    }
                });

        buttonKinematic = createButton(
                "Ragdoll", null, buttonSize, 4 * SCREEN_MARGIN + 4 * buttonSize, SCREEN_MARGIN + buttonSize,
                new DefaultMouseListener() {
                    public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
                        event.setConsumed();
                        boolean isPressed = event.isPressed();
                        highlight(isPressed, buttonKinematic);
                    }
                });
        IconComponent iconQuit = new IconComponent("Interface/icons/off48x48.png");
        buttonQuit = createButton(
                "Quit", iconQuit, buttonSize, SCREEN_WIDTH - 4* SCREEN_MARGIN - 4*buttonSize, SCREEN_MARGIN + buttonSize,
                new DefaultMouseListener() {
                    public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
                        event.setConsumed();
                        boolean isPressed = event.isPressed();
                        highlight(isPressed, buttonKinematic);
                    }
                });
    }

    private Container createButton(String text, IconComponent icon, float size, float posx, float posy, MouseListener listener) {
        Container buttonContainer = new Container();
        buttonContainer.setPreferredSize(new Vector3f(size, size, 0));
        QuadBackgroundComponent background = new QuadBackgroundComponent(new ColorRGBA(0, 0, 0, 0.5f));
        // Clear AlphaDiscardThreshold because it is useless here and generates a new specific Shader
        background.getMaterial().getMaterial().clearParam("AlphaDiscardThreshold");
        buttonContainer.setBackground(background);
        //    buttonContainer.setBackground(QuadBackgroundComponent.create("/com/simsilica/lemur/icons/border.png", 1, 2, 2, 3, 3, 0, false));
        Label label = buttonContainer.addChild(new Label(text));
        label.getFont().getPage(0).clearParam("AlphaDiscardThreshold");
        label.getFont().getPage(0).clearParam("VertexColor");

        // Center the text in the box.
        label.setInsetsComponent(new DynamicInsetsComponent(0.5f, 0.5f, 0.5f, 0.5f));
        label.setColor(ColorRGBA.White);
        label.setIcon(icon);
        buttonContainer.setLocalTranslation(posx, posy, 1);
        buttonContainer.addMouseListener(listener);
        return buttonContainer;
    }

    private void highlight(boolean isPressed, Container button) {
        if (isPressed) {
            ((QuadBackgroundComponent) button.getBackground()).getColor().set(1f, 0.5f, 0.5f, 0.5f);
        } else {
            ((QuadBackgroundComponent) button.getBackground()).getColor().set(0, 0, 0.2f, 0.5f);
        }
    }

    public void showControlButtons() {
        main.getGuiNode().attachChild(buttonRagdoll);
        main.getGuiNode().attachChild(buttonKinematic);
        main.getGuiNode().attachChild(buttonQuit);
    }
}
