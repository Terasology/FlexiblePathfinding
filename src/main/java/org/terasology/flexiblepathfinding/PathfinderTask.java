/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.flexiblepathfinding;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.geom.Vector3i;
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
        jps.start();
        List<Vector3i> path = Lists.newArrayList();
        try {
            if(jps.run()) {
                path = jps.getPath();
            }
        } catch (InterruptedException e) {
            // do nothing
        }
        CoreRegistry.get(PathfinderSystem.class).completePathFor(config.requestor);
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
