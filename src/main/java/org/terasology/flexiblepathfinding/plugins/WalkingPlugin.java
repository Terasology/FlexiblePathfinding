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
package org.terasology.flexiblepathfinding.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;

public class WalkingPlugin extends StandardPlugin {
    public WalkingPlugin(WorldProvider world) {
        super(world);
    }
    private static final Logger logger = LoggerFactory.getLogger(WalkingPlugin.class);

    @Override
    public boolean isReachable(Vector3i a, Vector3i b) {
        // only allowed to move 1 unit in each axis
        if(Math.max(Math.abs(a.x - b.x), Math.max(Math.abs(a.y - b.y), Math.abs(a.z - b.z))) > 1) {
            return false;
        }

        // check that all blocks passed through by this movement are penetrable
        Region3i movementBounds = Region3i.createBounded(a, b);
        Vector3i aBelow = new Vector3i(a).add(Vector3i.down());
        Vector3i bBelow = new Vector3i(b).add(Vector3i.down());
        for (Vector3i pos : movementBounds) {
            // don't check the blocks below a or b, since those should be solid anyway
            if (pos.distanceSquared(aBelow) == 0 || pos.distanceSquared(bBelow) == 0) {
                continue;
            }

            if (!world.getBlock(pos).isPenetrable()) {
                return false;
            }
        }
        return isWalkable(a);
    }

    @Override
    public boolean isWalkable(Vector3i a) {
        Vector3i aBelow = new Vector3i(a).sub(0, 1, 0);
        return world.getBlock(a).isPenetrable() && !world.getBlock(aBelow).isPenetrable();
    }

    @Override
    public float getTime() {
        return world.getTime().getSeconds() / world.getTime().getTimeRate();
    }
}
