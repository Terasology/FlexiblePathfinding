// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import com.google.common.collect.Lists;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.registry.CoreRegistry;
import org.terasology.utilities.concurrency.Task;
import org.terasology.world.WorldProvider;

import java.util.List;

public class PathfinderTask implements Task, Comparable<PathfinderTask> {
    private JPSConfig config;
    private Vector3i start;
    private Vector3i stop;
    private PathfinderCallback callback;
    private WorldProvider world;
    private Logger logger = LoggerFactory.getLogger(PathfinderTask.class);
    private static int nextPriority = 0;
    private int priority;

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
            if(jps.run()) {
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
        if(this.priority < o.priority) {
            return -1;
        }
        return 1;
    }
}
