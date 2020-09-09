// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;
import org.terasology.math.geom.Vector3i;

import java.util.concurrent.ExecutorService;

public class JPSConfig {
    public int maxDepth = 100;
    public float maxTime = 3.0f;
    public Vector3i start = Vector3i.zero();
    public Vector3i stop = Vector3i.zero();
    public EntityRef requester;
    public JPSPlugin plugin;
    public double goalDistance;
    public boolean useLineOfSight;
    public ExecutorService executor;

    public JPSConfig(Vector3i start, Vector3i stop) {
        this.start = start;
        this.stop = stop;
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
