// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.plugins.basic;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.flexiblepathfinding.plugins.StandardPlugin;

public class SwimmingPlugin extends StandardPlugin {
    private static final Logger logger = LoggerFactory.getLogger(SwimmingPlugin.class);

    public SwimmingPlugin(WorldProvider world, float width, float height) {
        super(world, width, height);
    }

    @Override
    public boolean isReachable(Vector3ic a, Vector3ic b) {
        // only allowed to move 1 unit in each axis
        if (Math.max(Math.abs(a.x() - b.x()), Math.max(Math.abs(a.y() - b.y()), Math.abs(a.z() - b.z()))) > 1) {
            return false;
        }

        // check that all blocks passed through by this movement are penetrable
        for (Vector3ic occupiedBlock : getOccupiedRegionRelative()) {

            // the start/stop for this block in the occupied region
            Vector3i blockA = new Vector3i(a).add(occupiedBlock);
            Vector3i blockB = new Vector3i(b).add(occupiedBlock);

            BlockRegion movementBounds = new BlockRegion(blockA).union(blockB);
            for (Vector3ic pos : movementBounds) {
                if (!world.getBlock(pos).isLiquid()) {
                    return false;
                }
            }
        }

        return isWalkable(a);
    }

    public boolean isWalkable(Vector3ic a) {
        return world.getBlock(a).isLiquid();
    }
}
