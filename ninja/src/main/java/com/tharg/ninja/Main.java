/**
 * Loads puppet and Ninja. Press space puts them in ragdoll mode
 * Ninja is suspended by upward force on sword.
 * Puppet tries to grab Ninja's sword.
 * Hit return to go back to Kinematic mode, and step through the model animations
 */
package com.tharg.ninja;

import static com.tharg.jme.utilities.DebugDisplayActions.printDebugMessageForce;

import java.util.Arrays;
import java.util.Map;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.util.AnimMigrationUtils;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.NativePhysicsObject;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.animation.BoneLink;
import com.jme3.bullet.animation.TorsoLink;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.input.KeyInput;
import com.jme3.input.TouchInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.tharg.jme.utilities.TimeUtilities;
import com.tharg.ninja.buttons.ButtonTouchListener;
import com.tharg.ninja.controller.GhostController;
import com.tharg.ninja.controller.NinjaControllerTharg;
import com.tharg.ninja.controller.PuppetControllerTharg;
import com.tharg.ninja.scene.Lighting;
import com.tharg.ninja.buttons.Buttons;


/**
 *
 * @author tharg
 */
public class Main extends SimpleApplication implements PhysicsCollisionListener {
    public Buttons buttons;
    private TouchListener touchListener;
    private static final String TOUCH_MAPPING = "touch";
    private boolean lastCollisionGhostObjectIsTracked = false;
    private long lastCollisionGhostNativeId = 0;
    private String lastCollisionBoneLinkName = null;
    private long lastCollisionBoneLinkNativeId = 0;
    private int collisionWithFloorCount = 0;
    private int collisionBetweenModelsCount = 0;
    private int collisionFunctionCallsCount = 0;
    private boolean startCounters = false;
    private String collisionSourceA;
    private String collisionSourceB;
    private PhysicsSpace physicsSpace;
    private Spatial ninjaModel;
    private Transform NINJA_ORIGINAL_TRANSFORM;
    private Spatial puppetModelChild;
    private Transform PUPPET_ORIGINAL_TRANSFORM;

    private final NinjaControllerTharg ninjaController = new NinjaControllerTharg();
    private final PuppetControllerTharg puppetController = new PuppetControllerTharg();
    private GhostController ghostController;


    private final Vector3f PUPPET_ORIGINAL_POSITION = new Vector3f(-1, 0, 0);
    private final Vector3f NINJA_ORIGINAL_POSITION = new Vector3f(1, 0, 0);
    private final Vector3f NINJA_ORIGINAL_POSITION_ELEVATED = new Vector3f(1, 1, 0);
    private final float NINJA_MODEL_SCALE = 0.01f;
    private AnimComposer puppetComposer;
    private AnimComposer ninjaComposer;
    private String[] ninjaAnimationNames;
    private String[] puppetAnimationNames;
    private String currentNinjaAnimationName;
    private String currentPuppetAnimationName;



    private enum OSType {Windows, Android, IOS}
    private OSType osType;
    boolean firstStop = true;

    @Override
    public void simpleInitApp() {
        Lighting lightingManager = new Lighting(this);
        lightingManager.setupLighting();
        cam.setLocation(new Vector3f(0f, 1.0f, 5.0f));
        flyCam.setDragToRotate(true);
        osType = setupOsType();
        setupAndroidButtons();
        setupBulletPhysics(false);
        setupNinja();
        setupPuppet();
        setupInputManager();
        setupFloor();
        setupGhostController();
    }

    private void setupAndroidButtons() {
        if (osType == OSType.Android) {
            // Setup the android buttons and touch listener
            buttons = new Buttons(this);
            touchListener = new ButtonTouchListener(this);
        }
    }

    private OSType setupOsType() {
        OSType returnValue;
        String javaRuntimeName = System.getProperty("java.runtime.name");
        String osName = System.getProperty("os.name");

        if (javaRuntimeName != null && javaRuntimeName.contains("Android")) {
            returnValue = OSType.Android;
        } else if (osName != null && osName.contains("Windows")) {
            returnValue = OSType.Windows;
        } else {
            returnValue = OSType.IOS;
        }
        printDebugMessageForce("osType = " + returnValue);
        return returnValue;
    }
    private void setupBulletPhysics(boolean enableDebugDisplay) {
        // Set up Bullet physics (with debug enabled).
        BulletAppState bulletAppState = new BulletAppState();
        bulletAppState.setDebugEnabled(enableDebugDisplay);
        bulletAppState.setSpeed(1);
        stateManager.attach(bulletAppState);
        physicsSpace = bulletAppState.getPhysicsSpace();
    }
    private void setupNinja() {
        ////////////////////////////Ninja/////////////////////////////////
        // Ninja model seems to be about 200m tall.
        // Scale the model by 0.01 to match the 2m tall
        // Ninja is facing backwards, so rotate the model 180 degrees
        // Save original transform so we can return to the models original state
        NINJA_ORIGINAL_TRANSFORM = new Transform();
        NINJA_ORIGINAL_TRANSFORM.setScale(NINJA_MODEL_SCALE);
        NINJA_ORIGINAL_TRANSFORM.setTranslation(NINJA_ORIGINAL_POSITION);
        ninjaModel = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        rootNode.attachChild(ninjaModel);

        ninjaModel.rotate(0f, 3.142f, 0f);
        NINJA_ORIGINAL_TRANSFORM.setRotation(ninjaModel.getLocalRotation());

        ninjaModel.setLocalTranslation(NINJA_ORIGINAL_POSITION);
        ninjaModel.scale(NINJA_MODEL_SCALE);

        AnimMigrationUtils.migrate(ninjaModel);
        ninjaComposer = ninjaModel.getControl(AnimComposer.class);

        ninjaAnimationNames = setupAnimationNameArray(ninjaComposer, "ninja");
        currentNinjaAnimationName = getNextAnimationName(ninjaAnimationNames, currentNinjaAnimationName);
        ninjaComposer.setCurrentAction(currentNinjaAnimationName);

        ninjaModel.addControl(ninjaController);
        for (PhysicsRigidBody prb : ninjaController.listRigidBodies()) {
            prb.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
            prb.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_04);
        }
        ninjaController.setPhysicsSpace(physicsSpace);
    }
    private void setupPuppet() {
        ////////////////////////////Puppet/////////////////////////////////
        // Puppet model is approx 2m tall, no scaling required
        // Save original transform so we can return to the models original state

        PUPPET_ORIGINAL_TRANSFORM = new Transform();
        PUPPET_ORIGINAL_TRANSFORM.setTranslation(PUPPET_ORIGINAL_POSITION);
        Node puppetModel = (Node) assetManager.loadModel("Models/Puppet.j3o");
        puppetModelChild = puppetModel.getChild(1);
        AnimMigrationUtils.migrate(puppetModelChild);
        puppetComposer = puppetModelChild.getControl(AnimComposer.class);

        puppetAnimationNames = setupAnimationNameArray(puppetComposer, "Puppet");
        currentPuppetAnimationName = getNextAnimationName(puppetAnimationNames, currentPuppetAnimationName);
        puppetComposer.setCurrentAction(currentPuppetAnimationName);

        rootNode.attachChild(puppetModelChild);

        puppetModelChild.setLocalTranslation(PUPPET_ORIGINAL_POSITION);

        puppetModelChild.addControl(puppetController);

        for (PhysicsRigidBody prb : puppetController.listRigidBodies()) {
            prb.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_03);
            prb.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_05);
        }
        puppetController.setPhysicsSpace(physicsSpace);
    }
    private void setupInputManager() {
        inputManager.addMapping(TOUCH_MAPPING, new TouchTrigger(TouchInput.ALL));
        inputManager.addMapping("ragdollMode", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("kinematicMode", new KeyTrigger(KeyInput.KEY_RETURN));
        InputListener actionListener;
        actionListener = new ActionListener() {
            @Override
            public void onAction(String actionString, boolean ongoing, float tpf) {
                if (actionString.equals("ragdollMode") && ongoing) {
                    actionRagdoll();
                }
                if (actionString.equals("kinematicMode") && ongoing) {
                    actionKinematic();
                }
            }
        };
        inputManager.addListener(touchListener, TOUCH_MAPPING);
        inputManager.addListener(actionListener, "ragdollMode");
        inputManager.addListener(actionListener, "kinematicMode");
    }
    private void setupGhostController() {
        ghostController = new GhostController(
                puppetController,
                physicsSpace,
                PhysicsCollisionObject.COLLISION_GROUP_04,
                PhysicsCollisionObject.COLLISION_GROUP_02);
        Map<String, PhysicsGhostObject> ghostObjectList = ghostController.getBoneNameGhostObjectList();
        for (String boneName : ghostObjectList.keySet()) {
            PhysicsGhostObject PGO = ghostObjectList.get(boneName);
            physicsSpace.add(PGO);
            PGO.setPhysicsLocation(new Vector3f(0, 2, 0));
            //System.out.println("GhostController added " + boneName);
        }
        physicsSpace.addCollisionListener(this);
    }

    private String[] setupAnimationNameArray(AnimComposer animComposer, String modelName) {

        Object[] animNameObjects = animComposer.getAnimClipsNames().toArray();
        if (animNameObjects == null || animNameObjects.length == 0) {
            System.out.println("setupAnimationNameArray error. No animations found for model " + modelName);
            return null;
        }
        String[] returnArray = new String[animNameObjects.length];
        for (int i = 0; i < animNameObjects.length; i++) {
            returnArray[i] = (String) animNameObjects[i];
        }
        // Sort the amimation name array in alphbetical order
        Arrays.sort(returnArray);
        System.out.println("Animation List for " + modelName);
        for (String name : returnArray) {
            System.out.println("   " + name);
        }
        return returnArray;
    }

    // get the next name from a string array with wrapping
    private String getNextAnimationName(String[] animNames, String currentAnimationName) {
        //System.out.println("getNextAnimationName called, currentAnimationName = " + currentAnimationName);
        int animationIndex = 0;
        if (animNames == null || animNames.length == 0) {
            System.out.println("getNextAnimationName error. Names array either not setup (null) or empty");
            return null;
        }
        if (currentAnimationName == null) {
            return animNames[0];
        }
        for (int i = 0; i <= animNames.length; i++) {
            if (animNames[i].compareTo(currentAnimationName) == 0) {
                animationIndex = i + 1;
                break;
            }
        }

        if (animationIndex >= animNames.length) {
            animationIndex = 0;
        }
        //System.out.println("getNextAnimationName called, currentAnimationName = " + currentAnimationName + ", return " + animNames[animationIndex]);
        return animNames[animationIndex];
    }

    /**
     * Add a large static cube to serve as a platform.
     */
    private void setupFloor() {
        float halfExtent = 50f; // mesh units
        Mesh mesh = new Box(halfExtent, halfExtent, halfExtent);
        Geometry geometry = new Geometry("Floor", mesh);
        geometry.move(0f, -halfExtent, 0f);
        rootNode.attachChild(geometry);

        ColorRGBA color = new ColorRGBA(0.1f, 0.4f, 0.1f, 1f);
        Material material = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors", true);
        material.setColor("Diffuse", color);
        geometry.setMaterial(material);
        geometry.setShadowMode(RenderQueue.ShadowMode.Receive);

        BoxCollisionShape shape = new BoxCollisionShape(halfExtent);
        RigidBodyControl boxBody
                = new RigidBodyControl(shape, PhysicsBody.massForStatic);
        geometry.addControl(boxBody);
        boxBody.setPhysicsSpace(physicsSpace);
        boxBody.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_01);
        boxBody.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_02+
                PhysicsCollisionObject.COLLISION_GROUP_03);
    }
    public void actionRagdoll() {
        ninjaController.setRagdollMode();
        puppetController.setRagdollMode();
        startCounters = true;
    }
    public void actionKinematic() {
        currentNinjaAnimationName = getNextAnimationName(ninjaAnimationNames, currentNinjaAnimationName);
        ninjaComposer.setCurrentAction(currentNinjaAnimationName);
        ninjaController.blendToKinematicMode(1.0f, NINJA_ORIGINAL_TRANSFORM);

        currentPuppetAnimationName = getNextAnimationName(puppetAnimationNames, currentPuppetAnimationName);
        puppetComposer.setCurrentAction(currentPuppetAnimationName);
        puppetController.blendToKinematicMode(1.0f, PUPPET_ORIGINAL_TRANSFORM);
    }
    public void actionQuit() {
        System.out.flush();
        System.exit(0);
    }
    public static void rotateCamera(Camera cam, float value, Vector3f axis) {
        final float ROTATION_SPEED = 1.5f;
        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(ROTATION_SPEED * value, axis);

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        if (up.getY() < 0) {
            return;
        }

        Quaternion q = new Quaternion();
        q.fromAxes(left, up, dir);
        q.normalizeLocal();

        cam.setAxes(q);
    }
    private boolean isCollisionWithFloor(PhysicsCollisionEvent event) {
        if (event.getNodeA() != null && event.getNodeA().getName().contains("Floor")) {
            return true;
        }
        return event.getNodeB() != null && event.getNodeB().getName().contains("Floor");
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (!startCounters){
            collisionWithFloorCount = 0;
            collisionBetweenModelsCount = 0;
            collisionFunctionCallsCount = 0;
        }
        collisionFunctionCallsCount++;
        if (event != null) {
            // Ignore collisions with Floor
            if (isCollisionWithFloor(event)) {
                collisionWithFloorCount ++;
                return;
            }

            Vector3f collisionSourceALocation = new Vector3f(Vector3f.ZERO);
            Vector3f collisionSourceBLocation = new Vector3f(Vector3f.ZERO);
            BoneLink boneLinkA = null;
            BoneLink boneLinkB = null;
            TorsoLink torsoLinkA;
            TorsoLink torsoLinkB;
            float impulseScaleA;
            float impulseScaleB;

            // Check the collision and extract the names and impact locations of A and B.
            // If the collision is with a node it is a geometric shape.
            if (event.getNodeA() != null) {
                collisionSourceA = new String(event.getNodeA().getName());
                collisionSourceALocation = new Vector3f(event.getNodeA().getLocalTranslation());
                // Check for a bone collision
            } else if (event.getObjectA().getUserObject() instanceof BoneLink) {
                boneLinkA = (BoneLink) event.getObjectA().getUserObject();
                collisionSourceA = new String(boneLinkA.name());
                collisionSourceALocation = new Vector3f(boneLinkA.getRigidBody().getPhysicsLocation());
                lastCollisionBoneLinkName = collisionSourceA;
                lastCollisionBoneLinkNativeId = boneLinkA.getRigidBody().nativeId();
                // Check for a Torso collision
            } else if (event.getObjectA().getUserObject() instanceof TorsoLink) {
                torsoLinkA = (TorsoLink) event.getObjectA().getUserObject();
                // Torso links can be either Armature Joint type, or bones.
                if (torsoLinkA.getArmatureJoint() != null) {
                    collisionSourceA = new String(torsoLinkA.name() + torsoLinkA.getArmatureJoint().getName());
                    collisionSourceALocation = new Vector3f(torsoLinkA.getArmatureJoint().getLocalTranslation());
                } else if (torsoLinkA.getBone() != null) {
                    collisionSourceA = new String(torsoLinkA.name() + torsoLinkA.getBone().getName());
                    collisionSourceALocation = new Vector3f(torsoLinkA.getBone().getBindScale());
                } else {
                    System.out.println("No bone or armatureJoint found for TorsoLink");
                }
            }

            if (event.getNodeB() != null) {
                collisionSourceB = new String(event.getNodeB().getName());
                collisionSourceBLocation = new Vector3f(event.getNodeB().getLocalTranslation());
            } else if (event.getObjectB().getUserObject() instanceof BoneLink) {
                boneLinkB = (BoneLink) event.getObjectB().getUserObject();
                collisionSourceB = new String(boneLinkB.name());
                collisionSourceBLocation = new Vector3f(boneLinkB.getRigidBody().getPhysicsLocation());
                lastCollisionBoneLinkName = collisionSourceB;
                lastCollisionBoneLinkNativeId = boneLinkB.getRigidBody().nativeId();

            } else if (event.getObjectB().getUserObject() instanceof TorsoLink) {
                torsoLinkB = (TorsoLink) event.getObjectB().getUserObject();
                if (torsoLinkB.getArmatureJoint() != null) {
                    collisionSourceB = new String(torsoLinkB.getArmatureJoint().getName());
                    collisionSourceBLocation = new Vector3f(torsoLinkB.getArmatureJoint().getLocalTranslation());
                } else if (torsoLinkB.getBone() != null) {
                    collisionSourceB = new String(torsoLinkB.name() + torsoLinkB.getBone().getName());
                    collisionSourceBLocation = new Vector3f(torsoLinkB.getBone().getBindScale());
                } else {
                    System.out.println("ERROR No bone or armatureJoint found for TorsoLink");
                }
            }
            PhysicsGhostObject PGO = null;
            if (event.getObjectA() instanceof PhysicsGhostObject){
                PGO = (PhysicsGhostObject)event.getObjectA();
                collisionSourceA = new String("Ghost:"+ghostController.getBoneName(PGO));
                collisionSourceALocation = new Vector3f(PGO.getPhysicsLocation());
            } else if (event.getObjectB() instanceof PhysicsGhostObject){
                PGO = (PhysicsGhostObject)event.getObjectB();
                collisionSourceB = new String("Ghost:"+ghostController.getBoneName(PGO));
                collisionSourceBLocation = new Vector3f(PGO.getPhysicsLocation());
            }
            lastCollisionGhostObjectIsTracked = ghostController.isGhostObjectInList(PGO);
            lastCollisionGhostNativeId = PGO.nativeId();
            // Test if it's a collision between two different models
            // All ninja bone names contain "Joint"
            // "Joint" must be contained in one collisionSource, and Ghost in the other
            if ((collisionSourceA.contains("Ghost") && collisionSourceB.contains("Joint")) ||
                    (collisionSourceB.contains("Ghost") && collisionSourceA.contains("Joint"))) {
                collisionBetweenModelsCount++;
                // The two models have collided. Apply an opposing impulse to the colliding bodies
                // to impulse then apart.
                if (collisionSourceA.contains("Joint")) {
                    //collisionSource A is Ninja, unscaled size is 100 times that of puppet
                    //so it seems we need a much larger impulse ninja to get the same effect as puppet.
                    impulseScaleA = 3.0f;
                    impulseScaleB = 0.1f;
                } else {
                    impulseScaleA = 0.1f;
                    impulseScaleB = 3.0f;
                }
                // Apply impulse to each colliding body, to kick the bodies away from each other
                Vector3f AtoBVector = new Vector3f(collisionSourceBLocation.subtract(collisionSourceALocation)).normalizeLocal();
                // Negate the AtoBVector when applying the impulse to A.
                if (boneLinkA != null) {
                    boneLinkA.getRigidBody().applyImpulse(AtoBVector.negate().mult(impulseScaleA), Vector3f.ZERO);
                }
                if (boneLinkB != null) {
                    boneLinkB.getRigidBody().applyImpulse(AtoBVector.mult(impulseScaleB), Vector3f.ZERO);
                }
            }
        }
    }

    private void ninjaSwordRaiseInTheAir() {
        //Hang Ninja up in the air by his sword (Joint13) and rotates it with torque
        //Ninja has 20 Bone links all weighing 1.0. Upward force = mass*9.8 to overcome gravity
        //Also direct the force back to the origin if Ninja has moved away from it
        PhysicsRigidBody torsoRigidBody = ninjaController.getTorsoLink().getRigidBody();
        Vector3f torsoLocation = torsoRigidBody.getPhysicsLocation();
        Vector3f ninjaVectorToHome = torsoLocation.negateLocal();
        ninjaVectorToHome.multLocal(10.0f);
        //ninjaVectorToHome.setY(0.1f);
        // Keep Ninja returning to 'home' (0,0,0)
        ninjaController.getTorsoLink().getRigidBody().applyImpulse(ninjaVectorToHome, Vector3f.ZERO);

        // Keep the sword up un the air. This pulls Ninja up to just standing.
        PhysicsRigidBody swordRigidBody = ninjaController.findLink("Bone:Joint13").getRigidBody();
        // Scale the upward force to keep the sword high
        float swordY = swordRigidBody.getPhysicsLocation().getY();
        float verticalImpulseScale = (3.0f-swordY)*30f;
        swordRigidBody.applyImpulse(Vector3f.UNIT_Y.mult(verticalImpulseScale), Vector3f.ZERO);
        // oppose ninja rotation
        TorsoLink torsoLink = ninjaController.getTorsoLink();
        Vector3f opposingLinearVelocity =
                torsoLink.getRigidBody().getLinearVelocity().normalizeLocal().negateLocal().multLocal(0.1f);
        opposingLinearVelocity.z = 0;
        torsoLink.getRigidBody().applyImpulse(opposingLinearVelocity, Vector3f.ZERO);
    }

    private Vector3f calcPuppetToNinjaDirectionVector() {
        // Ninja: Joint13 is sword. Joint8 is head,
        // Puppet: "Bone:hand.L"
        Vector3f ninjaBoneLocation = ninjaController.findLink("Bone:Joint13").getRigidBody().getPhysicsLocation();
        Vector3f puppetBoneLocation = puppetController.findLink("Bone:hand.L").getRigidBody().getPhysicsLocation();
        return new Vector3f(ninjaBoneLocation.subtract(puppetBoneLocation)).normalizeLocal();
    }

    private void puppetTriesToGrabNinjaSword() {
        // Puppet tries to grab Ninja's sword. Puppet has 21 Links
        puppetController.findLink("Bone:hand.L").
                getRigidBody().applyForce(calcPuppetToNinjaDirectionVector().mult(3.0f), Vector3f.ZERO);
        // oppose puppet rotation
        TorsoLink torsoLink = puppetController.getTorsoLink();
        Vector3f opposingLinearVelocity =
                torsoLink.getRigidBody().getLinearVelocity().normalizeLocal().negateLocal().multLocal(0.1f);
        opposingLinearVelocity.z = 0;
        torsoLink.getRigidBody().applyImpulse(opposingLinearVelocity, Vector3f.ZERO);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (!puppetController.getTorsoLink().isKinematic()) {
            ninjaSwordRaiseInTheAir();
            puppetTriesToGrabNinjaSword();
        }
        ghostController.matchGhostToRagdoll(puppetController);

        long TIMEOUT_MILLIS = 2000;
        if (TimeUtilities.timeoutMillis(TIMEOUT_MILLIS) && startCounters) {
            System.out.println("Floor Collisions: " + collisionWithFloorCount +
                    ", Ninja/puppet collisions: " + collisionBetweenModelsCount +
                    ", collision Function Calls: " + collisionFunctionCallsCount);
            System.out.println("collisionSourceA = " + collisionSourceA + ", collisionSourceB = "+collisionSourceB);
            System.out.println("physiceCleaner NPO countTrackers (active trackers) = " + NativePhysicsObject.countTrackers());
            System.out.println("ghostObjectIsTracked = "+ lastCollisionGhostObjectIsTracked +", nativeId = "+lastCollisionGhostNativeId);
            System.out.println("lastCollisionBoneLinkName = "+lastCollisionBoneLinkName+", NativeId = "+lastCollisionBoneLinkNativeId);
            System.out.println("physicsSpace.countRigidBodies() = " + physicsSpace.countRigidBodies());
            System.out.println("physicsSpace.countCollisionObjects() = " + physicsSpace.countCollisionObjects());
            System.out.println(" ");
        }
    }
}

