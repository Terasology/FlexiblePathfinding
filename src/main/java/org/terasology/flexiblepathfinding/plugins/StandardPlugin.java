/*
 * Copyright 2017 MovingBlocks
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

import org.terasology.flexiblepathfinding.LineOfSight3d;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;

import java.math.RoundingMode;

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
    public boolean inSight(Vector3i start, Vector3i stop) {
        return new LineOfSight3d(world).inSight(start, stop);
    }

    /**
     * Get the region occupied by the subject based on padding when located at position
     * @return
     */
    public Region3i getOccupiedRegionRelative() {
        double halfHeight = height / 2.0f;
        double halfWidth = width / 2.0f;

        // y-value at which the center of the body rests (assuming each block is completely full or empty
        double yOffset = (halfHeight % 1.0f) - 0.5f;

        int x1 = (int) Math.floor(0.5f - halfWidth);
        int x2 = (int) Math.floor(0.5f + halfWidth);
        int y1 = (int) Math.floor(0.5f + yOffset - halfHeight);
        int y2 = (int) Math.floor(0.5f + yOffset + halfHeight);

        Vector3i min = new Vector3i(x1, y1, x1);
        Vector3i max = new Vector3i(x2, y2, x2);
        return Region3i.createBounded(min, max);
    }

    /**
     * Get the region of blocks immediately under the subject based on padding
     * @return
     */
    public Region3i getSupportingRegionRelative() {
        double halfHeight = height / 2.0f;
        double halfWidth = width / 2.0f;

        // y-value at which the center of the body rests (assuming each block is completely full or empty
        double yOffset = (halfHeight % 1.0f) - 0.5f;

        int x1 = (int) Math.floor(0.5f - halfWidth);
        int x2 = (int) Math.floor(0.5f + halfWidth);
        int y1 = (int) Math.floor(0.5f + yOffset - halfHeight) - 1;

        Vector3i min = new Vector3i(x1, y1, x1);
        Vector3i max = new Vector3i(x2, y1, x2);
        return Region3i.createBounded(min, max);
    }
}
