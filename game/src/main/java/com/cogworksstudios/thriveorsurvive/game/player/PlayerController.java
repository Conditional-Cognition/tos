package com.thriveorsurvive.game.player;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.MouseInput;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class PlayerController implements ActionListener, AnalogListener {

    private final Camera camera;
    private final PhysicsSpace physicsSpace;
    private final RigidBodyControl body;

    private final SourceStyleMovement movement = new SourceStyleMovement();

    private float yaw = 0f;
    private float pitch = 0f;

    private boolean movingForward, movingBackward, movingLeft, movingRight;
    private boolean jumpHeld;
    private boolean crouching;
    private boolean flying;

    private float currentHeight = PlayerConstants.STANDING_HEIGHT;

    public PlayerController(Camera camera, PhysicsSpace physicsSpace, InputManager inputManager, Vector3f spawnPosition) {
        this.camera = camera;
        this.physicsSpace = physicsSpace;

        CapsuleCollisionShape shape = new CapsuleCollisionShape(
                PlayerConstants.RADIUS,
                PlayerConstants.STANDING_HEIGHT - 2 * PlayerConstants.RADIUS
        );
        body = new RigidBodyControl(shape, 80f);
        body.setPhysicsLocation(spawnPosition);
        body.setAngularFactor(0f);
        body.setFriction(0f);
        physicsSpace.add(body);

        setUpInput(inputManager);
    }

    private void setUpInput(InputManager inputManager) {
        inputManager.addMapping("MoveForward", new KeyTrigger(com.jme3.input.KeyInput.KEY_W));
        inputManager.addMapping("MoveBackward", new KeyTrigger(com.jme3.input.KeyInput.KEY_S));
        inputManager.addMapping("MoveLeft", new KeyTrigger(com.jme3.input.KeyInput.KEY_A));
        inputManager.addMapping("MoveRight", new KeyTrigger(com.jme3.input.KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(com.jme3.input.KeyInput.KEY_SPACE));
        inputManager.addMapping("Crouch", new KeyTrigger(com.jme3.input.KeyInput.KEY_LSHIFT));
        inputManager.addMapping("ToggleFly", new KeyTrigger(com.jme3.input.KeyInput.KEY_V));

        inputManager.addMapping("LookLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("LookRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("LookUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("LookDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        inputManager.addListener(this,
                "MoveForward", "MoveBackward", "MoveLeft", "MoveRight",
                "Jump", "Crouch", "ToggleFly");
        inputManager.addListener(this, "LookLeft", "LookRight", "LookUp", "LookDown");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "MoveForward": movingForward = isPressed; break;
            case "MoveBackward": movingBackward = isPressed; break;
            case "MoveLeft": movingLeft = isPressed; break;
            case "MoveRight": movingRight = isPressed; break;
            case "Jump": jumpHeld = isPressed; break;
            case "Crouch": crouching = isPressed; break;
            case "ToggleFly":
                if (isPressed) {
                    flying = !flying;
                    body.setGravity(flying ? Vector3f.ZERO : new Vector3f(0, -PlayerConstants.GRAVITY, 0));
                    movement.getVelocity().set(0, 0, 0);
                    body.setLinearVelocity(Vector3f.ZERO);
                }
                break;
            default: break;
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        switch (name) {
            case "LookLeft": yaw += value * PlayerConstants.MOUSE_SENSITIVITY * 100; break;
            case "LookRight": yaw -= value * PlayerConstants.MOUSE_SENSITIVITY * 100; break;
            case "LookUp": pitch += value * PlayerConstants.MOUSE_SENSITIVITY * 100; break;
            case "LookDown": pitch -= value * PlayerConstants.MOUSE_SENSITIVITY * 100; break;
            default: break;
        }
        pitch = FastMath.clamp(pitch, -FastMath.HALF_PI + 0.01f, FastMath.HALF_PI - 0.01f);
    }

    public void update(float tpf) {
        currentHeight = crouching ? PlayerConstants.CROUCH_HEIGHT : PlayerConstants.STANDING_HEIGHT;

        Quaternion yawRotation = new Quaternion().fromAngleAxis(yaw, Vector3f.UNIT_Y);
        Vector3f forward = yawRotation.mult(Vector3f.UNIT_Z).negate();
        Vector3f right = yawRotation.mult(Vector3f.UNIT_X);

        Vector3f wishDirection = new Vector3f();
        if (movingForward) wishDirection.addLocal(forward);
        if (movingBackward) wishDirection.subtractLocal(forward);
        if (movingRight) wishDirection.addLocal(right);
        if (movingLeft) wishDirection.subtractLocal(right);
        if (wishDirection.lengthSquared() > 0) wishDirection.normalizeLocal();

        boolean onGround = isOnGround();
        movement.setOnGround(onGround);

        if (flying) {
            float verticalSpeed = jumpHeld ? PlayerConstants.RUN_SPEED
                    : crouching ? -PlayerConstants.RUN_SPEED : 0f;
            movement.getVelocity().set(
                    wishDirection.x * PlayerConstants.RUN_SPEED,
                    verticalSpeed,
                    wishDirection.z * PlayerConstants.RUN_SPEED
            );
        } else {
            movement.update(tpf, wishDirection, jumpHeld && onGround);
        }

        body.setLinearVelocity(movement.getVelocity());

        Vector3f feetPosition = body.getPhysicsLocation();
        Vector3f eyePosition = feetPosition.add(0, currentHeight * 0.9f - PlayerConstants.STANDING_HEIGHT / 2f, 0);
        camera.setLocation(eyePosition);

        Quaternion lookRotation = new Quaternion();
        lookRotation.fromAngles(pitch, yaw, 0);
        camera.setRotation(lookRotation);
    }

    private boolean isOnGround() {
        Vector3f origin = body.getPhysicsLocation();
        Vector3f rayEnd = origin.subtract(0, PlayerConstants.STANDING_HEIGHT / 2f + 0.1f, 0);
        return !physicsSpace.rayTest(origin, rayEnd).isEmpty();
    }
}