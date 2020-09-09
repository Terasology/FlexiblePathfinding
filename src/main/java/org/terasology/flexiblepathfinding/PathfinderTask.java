// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.utilities.concurrency.Task;
import org.terasology.engine.world.WorldProvider;
import org.terasology.math.geom.Vector3i;

import java.util.List;

public class PathfinderTask implements Task, Comparable<PathfinderTask> {
    private static int nextPriority = 0;
    private final JPSConfig config;
    private final PathfinderCallback callback;
    private final WorldProvider world;
    private final Logger logger = LoggerFactory.getLogger(PathfinderTask.class);
    private final int priority;
    private Vector3i start;
    private Vector3i stop;

    public PathfinderTask(WorldProvider world, JPSConfig config, PathfinderCallback callback) {
        this.world = world;
        this.config = config;
        this.callback = callback;
        this.priority = nextPriority++;
    }

    @Override
    public String getName() {
        return this.getClass().toString();
    }

    @Override
    public void run() {
        JPSImpl jps = new JPSImpl(config);
        List<Vector3i> path = Lists.newArrayList();
        try {
            if (jps.run()) {
                path = jps.getPath();
            }
        } catch (InterruptedException e) {
            // do nothing
        }
        CoreRegistry.get(PathfinderSystem.class).completePathFor(config.requester);
        callback.pathReady(path, this.stop);
    }

    @Override
    public boolean isTerminateSignal() {
        return false;
    }

    @Override
    public int compareTo(PathfinderTask o) {
        if (this.priority < o.priority) {
            return -1;
        }
        return 1;
    }
}
