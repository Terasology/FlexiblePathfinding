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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.math.geom.Vector3i;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.flexiblepathfinding.plugins.WalkingPlugin;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.WorldProvider;

import java.util.concurrent.ExecutorService;

public class JPSConfig {
    public int maxDepth;
    public float maxTime;
    public Vector3i start;
    public Vector3i stop;
    public EntityRef requestor;
    public JPSPlugin plugin;
    public float goalDistance;
    public boolean useLineOfSight;
    public ExecutorService executor;

    public JPSConfig(Vector3i start, Vector3i stop) {
        this.start = start;
        this.stop = stop;

        this.maxDepth = (int) stop.distance(start) * 2;
        this.maxTime = 3.0f;
        this.plugin = new WalkingPlugin(CoreRegistry.get(WorldProvider.class));
    }
}
