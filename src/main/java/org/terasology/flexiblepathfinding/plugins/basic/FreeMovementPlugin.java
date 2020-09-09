// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.plugins.basic;

import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.plugins.StandardPlugin;
import org.terasology.math.geom.Vector3i;

public class FreeMovementPlugin extends StandardPlugin {
    public FreeMovementPlugin(WorldProvider world, float width, float height) {
        super(world, width, height);
    }

    @Override
    public boolean isReachable(Vector3i to, Vector3i from) {
        return true;
    }

    @Override
    public boolean inSight(Vector3i start, Vector3i stop) {
        return true;
    }
}
