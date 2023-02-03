package com.tharg.ninja.controller;

import com.jme3.bullet.animation.DynamicAnimControl;
import com.jme3.bullet.animation.LinkConfig;
import com.jme3.bullet.animation.RangeOfMotion;

public class NinjaControllerTharg extends DynamicAnimControl {

    public NinjaControllerTharg() {
        super();

        // Configure a DynamicAnimControl.
        LinkConfig defaultConfig = new LinkConfig();
        RangeOfMotion defaultRom = new RangeOfMotion(1f);

        // Configure a DynamicAnimControl.
        super.link("Joint3", defaultConfig, defaultRom); // Lower Spine?
        super.link("Joint4", defaultConfig, defaultRom); // Spine
        super.link("Joint5", defaultConfig, defaultRom); // Upper Spine
        super.link("Joint6", defaultConfig, defaultRom); // Neck
        super.link("Joint8", defaultConfig, defaultRom); // Head

        super.link("Joint9", defaultConfig, defaultRom); // right shoulder
        super.link("Joint11", defaultConfig, defaultRom); // right elbow
        super.link("Joint12", defaultConfig, defaultRom); // right wrist
        super.link("Joint13", defaultConfig, defaultRom); // Sword
        super.link("Joint15", defaultConfig, defaultRom); // Right upper arm
        super.link("Joint16", defaultConfig, defaultRom); // Right forearm
        super.link("Joint17", defaultConfig, defaultRom); // Right hand        

        super.link("Joint18", defaultConfig, defaultRom); // Right hip
        super.link("Joint19", defaultConfig, defaultRom); // Right knee
        super.link("Joint20", defaultConfig, defaultRom); // Right ankle
        super.link("Joint21", defaultConfig, defaultRom); // Right toe
        super.link("Joint23", defaultConfig, defaultRom); // Left hip
        super.link("Joint24", defaultConfig, defaultRom); // Left knee
        super.link("Joint25", defaultConfig, defaultRom); // Left ankle
        super.link("Joint26", defaultConfig, defaultRom); // Left toe
    }
}
