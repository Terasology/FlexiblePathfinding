// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.WorldProvider;

import java.util.concurrent.ExecutorService;

public class JPSConfig {
    public int maxDepth = 100;
    public float maxTime = 3.0f;
    public Vector3i start = new Vector3i();
    public Vector3i stop = new Vector3i();
    public EntityRef requester;
    public JPSPlugin plugin;
    public double goalDistance;
    public boolean useLineOfSight;
    public ExecutorService executor;

    public JPSConfig(Vector3ic start, Vector3ic stop) {
        this.start = new Vector3i(start);
        this.stop = new Vector3i(stop);
        this.maxDepth = (int) stop.distance(start) * 10;
        this.plugin = new WalkingPlugin(CoreRegistry.get(WorldProvider.class), 0.4f, 0.4f);
    }

    public JPSConfig() {
        this.plugin = new WalkingPlugin(CoreRegistry.get(WorldProvider.class), 0.4f, 0.4f);
    }

    @Override
    public String toString() {
        return "start: " + start.toString() + " stop: " + stop.toString() + " " + maxDepth + " " + maxTime;
    }
}
