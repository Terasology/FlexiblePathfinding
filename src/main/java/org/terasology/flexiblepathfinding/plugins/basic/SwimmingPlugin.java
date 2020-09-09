// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.plugins.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.plugins.StandardPlugin;
import org.terasology.math.geom.Vector3i;

public class SwimmingPlugin extends StandardPlugin {
    private static final Logger logger = LoggerFactory.getLogger(SwimmingPlugin.class);

    public SwimmingPlugin(WorldProvider world, float width, float height) {
        super(world, width, height);
    }

    @Override
    public boolean isReachable(Vector3i a, Vector3i b) {
        // only allowed to move 1 unit in each axis
        if (Math.max(Math.abs(a.x - b.x), Math.max(Math.abs(a.y - b.y), Math.abs(a.z - b.z))) > 1) {
            return false;
        }

        // check that all blocks passed through by this movement are penetrable
        for (Vector3i occupiedBlock : getOccupiedRegionRelative()) {

            // the start/stop for this block in the occupied region
            Vector3i blockA = new Vector3i(a).add(occupiedBlock);
            Vector3i blockB = new Vector3i(b).add(occupiedBlock);

            Region3i movementBounds = Region3i.createBounded(blockA, blockB);
            for (Vector3i pos : movementBounds) {
                if (!world.getBlock(pos).isLiquid()) {
                    return false;
                }
            }
        }

        return isWalkable(a);
    }

    public boolean isWalkable(Vector3i a) {
        return world.getBlock(a).isLiquid();
    }
}
