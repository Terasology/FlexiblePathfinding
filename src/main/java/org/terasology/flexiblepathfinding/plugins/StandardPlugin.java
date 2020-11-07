// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.plugins;

import org.joml.Vector3ic;
import org.terasology.flexiblepathfinding.LineOfSight3d;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockRegion;

public abstract class StandardPlugin implements JPSPlugin {
    public final WorldProvider world;

    private float width;
    private float height;

    public StandardPlugin(WorldProvider world, float width, float height) {
        this.world = world;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean inSight(Vector3ic start, Vector3ic stop) {
        return new LineOfSight3d(world).inSight(start, stop);
    }

    /**
     * Get the region occupied by the subject based on padding when located at position
     * @return
     */
    public BlockRegion getOccupiedRegionRelative() {
        double halfHeight = height / 2.0f;
        double halfWidth = width / 2.0f;

        // y-value at which the center of the body rests (assuming each block is completely full or empty
        double yOffset = (halfHeight % 1.0f) - 0.5f;

        int x1 = (int) Math.floor(0.5f - halfWidth);
        int x2 = (int) Math.floor(0.5f + halfWidth);
        int y1 = (int) Math.floor(0.5f + yOffset - halfHeight);
        int y2 = (int) Math.floor(0.5f + yOffset + halfHeight);

        return new BlockRegion(x1, y1, x1, x2, y2, x2);
    }

    /**
     * Get the region of blocks immediately under the subject based on padding
     * @return
     */
    public BlockRegion getSupportingRegionRelative() {
        double halfHeight = height / 2.0f;
        double halfWidth = width / 2.0f;

        // y-value at which the center of the body rests (assuming each block is completely full or empty
        double yOffset = (halfHeight % 1.0f) - 0.5f;

        int x1 = (int) Math.floor(0.5f - halfWidth);
        int x2 = (int) Math.floor(0.5f + halfWidth);
        int y1 = (int) Math.floor(0.5f + yOffset - halfHeight) - 1;
        return new BlockRegion(x1, y1, x1, x2, y1, x2);
    }
}
