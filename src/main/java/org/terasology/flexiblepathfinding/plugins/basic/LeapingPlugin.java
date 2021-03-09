/*
 * Copyright 2018 MovingBlocks
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
package org.terasology.flexiblepathfinding.plugins.basic;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.BlockRegion;

public class LeapingPlugin extends WalkingPlugin {
    private static final Logger logger = LoggerFactory.getLogger(LeapingPlugin.class);

    public LeapingPlugin(WorldProvider world, float width, float height) {
        super(world, width, height);
    }

    @Override
    public boolean isReachable(Vector3ic to, Vector3ic from) {
        // only allowed to move 1 unit in positive y axis
        if (to.x() - from.x() != 0 || to.z() - from.z() != 0 || to.y() - from.y() != 1) {
            return false;
        }

        // check that all blocks passed through by this movement are penetrable
        for (Vector3ic occupiedBlock : getOccupiedRegionRelative()) {
            // the start/stop for this block in the occupied region
            Vector3i occupiedBlockTo = new Vector3i(to).add(occupiedBlock);
            Vector3i occupiedBlockFrom = new Vector3i(from).add(occupiedBlock);

            BlockRegion movementBounds = new BlockRegion(occupiedBlockTo).union(occupiedBlockFrom);
            for (Vector3ic block : movementBounds) {
                if (!world.getBlock(block).isPenetrable()) {
                    return false;
                }
            }
        }

        return isWalkable(from);
    }
}
