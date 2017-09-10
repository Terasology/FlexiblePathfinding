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

    private float horizontalPadding;
    private float verticalPadding;
    public StandardPlugin(WorldProvider world) {
        this.world = world;
    }

    @Override
    public boolean inSight(Vector3i start, Vector3i stop) {
        return new LineOfSight3d(world).inSight(start, stop);
    }

    @Override
    public void setHorizontalPadding(float horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
    }

    @Override
    public void setVerticalPadding(float verticalPadding) {
        this.verticalPadding = verticalPadding;
    }

    @Override
    public float getHorizontalPadding() {
        return horizontalPadding;
    }

    @Override
    public float getVerticalPadding() {
        return verticalPadding;
    }

    @Override
    public Region3i getOccupiedRegion() {
        int h = (int) Math.ceil(horizontalPadding);
        int v = (int) Math.ceil(verticalPadding);
        Vector3i min = new Vector3i(-h, -v, -h);
        Vector3i max = new Vector3i(h, v, h);
        return Region3i.createBounded(min, max);
    }
}
