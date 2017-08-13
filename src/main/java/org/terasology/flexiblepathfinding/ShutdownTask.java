/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.flexiblepathfinding;

import org.terasology.registry.CoreRegistry;
import org.terasology.utilities.concurrency.Task;
import org.terasology.world.WorldProvider;

public class ShutdownTask extends PathfinderTask {
    public ShutdownTask(WorldProvider world, JPSConfig config, PathfinderCallback callback) {
        super(world, config, callback);
    }

    @Override
    public String getName() {
        return "Shutdown";
    }

    @Override
    public void run() { }

    @Override
    public boolean isTerminateSignal() {
        return true;
    }
}
