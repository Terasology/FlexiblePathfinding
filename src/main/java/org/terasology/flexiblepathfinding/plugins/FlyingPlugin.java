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

import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;

public class FlyingPlugin extends WalkingPlugin {
    public FlyingPlugin(WorldProvider world) {
        super(world);
    }

    @Override
    public boolean isReachable(Vector3i a, Vector3i b) {
        // only allowed to move 1 unit in each axis
        if(Math.max(Math.abs(a.x - b.x), Math.max(Math.abs(a.y - b.y), Math.abs(a.z - b.z))) > 1) {
            return false;
        }

        // check that all blocks passed through by this movement are penetrable
        Region3i movementBounds = Region3i.createBounded(a, b);
        for (Vector3i pos : movementBounds) {
            if (!world.getBlock(pos).isPenetrable()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isWalkable(Vector3i pos) {
        return world.getBlock(pos).isPenetrable();
    }
}
