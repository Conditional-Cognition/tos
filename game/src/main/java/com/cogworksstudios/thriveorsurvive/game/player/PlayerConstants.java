package com.thriveorsurvive.game.player;

public final class PlayerConstants {
    public static final float STANDING_HEIGHT = 1.8f;
    public static final float CROUCH_HEIGHT = 0.9f;
    public static final float RADIUS = 0.35f;

    public static final float WALK_SPEED = 4.0f;
    public static final float RUN_SPEED = 6.1f;
    public static final float GRAVITY = 11.4f;
    public static final float JUMP_HEIGHT = 0.85f;
    public static final float JUMP_VELOCITY = (float) Math.sqrt(2 * GRAVITY * JUMP_HEIGHT);

    public static final float GROUND_ACCEL = 10f;
    public static final float AIR_ACCEL = 10f;
    public static final float AIR_ACCEL_CAP = 0.9f;
    public static final float FRICTION = 4f;

    public static final float MOUSE_SENSITIVITY = 0.0025f;
    public static final float RAYCAST_DISTANCE = 5f;

    private PlayerConstants() {}
}