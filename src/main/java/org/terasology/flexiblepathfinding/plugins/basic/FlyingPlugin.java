// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.plugins.basic;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockRegion;
import org.terasology.world.block.BlockRegionIterable;

public class FlyingPlugin extends WalkingPlugin {
    public FlyingPlugin(WorldProvider world, float width, float height) {
        super(world, width, height);
    }

    @Override
    public boolean isReachable(Vector3ic a, Vector3ic b) {
        // only allowed to move 1 unit in each axis
        if (Math.max(Math.abs(a.x() - b.x()), Math.max(Math.abs(a.y() - b.y()), Math.abs(a.z() - b.z()))) > 1) {
            return false;
        }

        // check that all blocks passed through by this movement are penetrable
        for (Vector3ic occupiedBlock : BlockRegionIterable.region(getOccupiedRegionRelative()).build()) {

            // the start/stop for this block in the occupied region
            Vector3i blockA = new Vector3i(a).add(occupiedBlock);
            Vector3i blockB = new Vector3i(b).add(occupiedBlock);

            BlockRegion movementBounds = new BlockRegion().union(blockA).union(blockB);
            for (Vector3ic pos : BlockRegionIterable.region(movementBounds).build()) {
                if (!world.getBlock(pos).isPenetrable()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isWalkable(Vector3ic pos) {
        return world.getBlock(pos).isPenetrable();
    }
}
