// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.plugins.basic;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.flexiblepathfinding.plugins.StandardPlugin;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockRegion;
import org.terasology.world.block.BlockRegionIterable;

public class WalkingPlugin extends StandardPlugin {
    private static final Logger logger = LoggerFactory.getLogger(WalkingPlugin.class);

    public WalkingPlugin(WorldProvider world, float width, float height) {
        super(world, width, height);
    }

    @Override
    public boolean isReachable(Vector3ic to, Vector3ic from) {
        if (to.y() > from.y()) {
            return false;
        }

        // check that all blocks passed through by this movement are penetrable
        for (Vector3ic occupiedBlock : BlockRegionIterable.region(getOccupiedRegionRelative()).build()) {

            // the start/stop for this block in the occupied region
            Vector3i occupiedBlockTo = new Vector3i(to).add(occupiedBlock);
            Vector3i occupiedBlockFrom = new Vector3i(from).add(occupiedBlock);

            BlockRegion movementBounds = new BlockRegion().union(occupiedBlockTo).union(occupiedBlockFrom);
            for (Vector3ic block : BlockRegionIterable.region(movementBounds).build()) {
                if (!world.getBlock(block).isPenetrable()) {
                    return false;
                }
            }
        }

        return isWalkable(to) || isWalkable(from);
    }

    public boolean isWalkable(Vector3ic a) {
        Vector3i walkablePos = new Vector3i();
        for (Vector3ic supportingBlockPos : BlockRegionIterable.region(getSupportingRegionRelative()).build()) {
            walkablePos.set(supportingBlockPos).add(a);
            if (!world.getBlock(walkablePos).isPenetrable()) {
                return true;
            }
        }
        return false;
    }
}
