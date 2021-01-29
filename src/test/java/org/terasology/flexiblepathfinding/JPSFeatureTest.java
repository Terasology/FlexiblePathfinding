/*
 * Copyright 2018 MovingBlocks
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

import org.joml.Vector3i;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terasology.flexiblepathfinding.plugins.basic.FreeMovementPlugin;
import org.terasology.world.WorldProvider;
import org.terasology.world.time.WorldTime;

public class JPSFeatureTest {
    @Test
    public void maxDepth() throws InterruptedException {
        WorldTime time = Mockito.mock(WorldTime.class);
        Mockito.when(time.getTimeRate()).thenReturn(2.0f);
        Mockito.when(time.getSeconds()).thenReturn(0.0f);

        WorldProvider world = Mockito.mock(WorldProvider.class);
        Mockito.when(world.getTime()).thenReturn(time);

        JPSConfig config = new JPSConfig(new Vector3i(), new Vector3i(0,0,1).mul(10));
        config.plugin = new FreeMovementPlugin(null, 0, 0);
        config.maxDepth = 0;
        JPSImpl jps = new JPSImpl(config);
        Assert.assertFalse(jps.run());
    }

    @Test
    public void pathToSelf() throws InterruptedException {
        JPSConfig config = new JPSConfig(new Vector3i(), new Vector3i());
        config.plugin = new FreeMovementPlugin(null, 0, 0);
        JPSImpl jps = new JPSImpl(config);
        Assert.assertTrue(jps.run());
    }

    @Test
    public void pathAdjacent() throws InterruptedException {
        JPSConfig config = new JPSConfig(new Vector3i(), new Vector3i(1,1,1));
        config.plugin = new FreeMovementPlugin(null, 0, 0);
        JPSImpl jps = new JPSImpl(config);
        Assert.assertTrue(jps.run());
    }
}
