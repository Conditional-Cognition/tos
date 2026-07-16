package com.thriveorsurvive.game;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.thriveorsurvive.game.player.PlayerController;
import com.thriveorsurvive.game.world.FlatGridWorld;

public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;
    private PlayerController playerController;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        PhysicsSpace physicsSpace = bulletAppState.getPhysicsSpace();
        physicsSpace.setGravity(new Vector3f(0, -com.thriveorsurvive.game.player.PlayerConstants.GRAVITY, 0));

        FlatGridWorld world = new FlatGridWorld(assetManager, physicsSpace, 20);
        rootNode.attachChild(world.getWorldNode());

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.6f));
        rootNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        flyCam.setEnabled(false);
        inputManager.setCursorVisible(false);

        Vector3f spawnPoint = new Vector3f(0, 3, 0);
        playerController = new PlayerController(cam, physicsSpace, inputManager, spawnPoint);
    }

    @Override
    public void simpleUpdate(float tpf) {
        playerController.update(tpf);
    }
}