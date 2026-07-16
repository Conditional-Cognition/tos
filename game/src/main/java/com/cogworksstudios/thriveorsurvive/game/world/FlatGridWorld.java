package com.thriveorsurvive.game.world;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class FlatGridWorld {

    private final Node worldNode = new Node("world");

    public FlatGridWorld(AssetManager assetManager, PhysicsSpace physicsSpace, int gridSize) {
        float tileSize = WorldConstants.TILE_SIZE;
        float halfExtent = tileSize / 2f;

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Green);

        for (int x = 0; x < gridSize; x++) {
            for (int z = 0; z < gridSize; z++) {
                Box box = new Box(halfExtent, halfExtent, halfExtent);
                Geometry geometry = new Geometry("tile_" + x + "_" + z, box);
                geometry.setMaterial(material);

                float worldX = (x - gridSize / 2f) * tileSize;
                float worldZ = (z - gridSize / 2f) * tileSize;
                geometry.setLocalTranslation(worldX, 0, worldZ);

                RigidBodyControl rigidBody = new RigidBodyControl(
                        new BoxCollisionShape(new Vector3f(halfExtent, halfExtent, halfExtent)),
                        0f
                );
                geometry.addControl(rigidBody);
                physicsSpace.add(rigidBody);

                worldNode.attachChild(geometry);
            }
        }
    }

    public Node getWorldNode() {
        return worldNode;
    }
}