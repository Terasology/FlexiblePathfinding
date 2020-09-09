// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import org.terasology.engine.world.WorldProvider;

public class ShutdownTask extends PathfinderTask {
    public ShutdownTask(WorldProvider world, JPSConfig config, PathfinderCallback callback) {
        super(world, config, callback);
    }

    @Override
    public String getName() {
        return "Shutdown";
    }

    @Override
    public void run() {
    }

    @Override
    public boolean isTerminateSignal() {
        return true;
    }
}
