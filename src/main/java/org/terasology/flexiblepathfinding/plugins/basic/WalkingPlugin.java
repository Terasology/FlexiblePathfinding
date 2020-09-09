// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.plugins.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.plugins.StandardPlugin;
import org.terasology.math.geom.Vector3i;

public class WalkingPlugin extends StandardPlugin {
    private static final Logger logger = LoggerFactory.getLogger(WalkingPlugin.class);

    public WalkingPlugin(WorldProvider world, float width, float height) {
        super(world, width, height);
    }

    @Override
    public boolean isReachable(Vector3i to, Vector3i from) {
        if (to.y > from.y) {
            return false;
        }

        // check that all blocks passed through by this movement are penetrable
        for (Vector3i occupiedBlock : getOccupiedRegionRelative()) {

            // the start/stop for this block in the occupied region
            Vector3i occupiedBlockTo = new Vector3i(to).add(occupiedBlock);
            Vector3i occupiedBlockFrom = new Vector3i(from).add(occupiedBlock);

            Region3i movementBounds = Region3i.createBounded(occupiedBlockTo, occupiedBlockFrom);
            for (Vector3i block : movementBounds) {
                if (!world.getBlock(block).isPenetrable()) {
                    return false;
                }
            }
        }

        return isWalkable(to) || isWalkable(from);
    }

    public boolean isWalkable(Vector3i a) {
        for (Vector3i supportingBlockPos : getSupportingRegionRelative()) {
            supportingBlockPos.add(a);
            if (!world.getBlock(supportingBlockPos).isPenetrable()) {
                return true;
            }
        }
        return false;
    }
}
