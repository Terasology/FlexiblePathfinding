// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.plugins.basic;

import org.joml.Vector3ic;
import org.terasology.flexiblepathfinding.plugins.StandardPlugin;
import org.terasology.world.WorldProvider;

public class FreeMovementPlugin extends StandardPlugin {
    public FreeMovementPlugin(WorldProvider world, float width, float height) {
        super(world, width, height);
    }

    @Override
    public boolean isReachable(Vector3ic to, Vector3ic from) {
        return true;
    }

    @Override
    public boolean inSight(Vector3ic start, Vector3ic stop) { return true; }
}
