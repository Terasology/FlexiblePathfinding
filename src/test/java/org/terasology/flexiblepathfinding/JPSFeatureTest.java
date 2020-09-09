// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.time.WorldTime;
import org.terasology.flexiblepathfinding.plugins.basic.FreeMovementPlugin;
import org.terasology.math.geom.Vector3i;

public class JPSFeatureTest {
    @Test
    public void maxDepth() throws InterruptedException {
        WorldTime time = Mockito.mock(WorldTime.class);
        Mockito.when(time.getTimeRate()).thenReturn(2.0f);
        Mockito.when(time.getSeconds()).thenReturn(0.0f);

        WorldProvider world = Mockito.mock(WorldProvider.class);
        Mockito.when(world.getTime()).thenReturn(time);

        JPSConfig config = new JPSConfig(Vector3i.zero(), Vector3i.north().mul(10));
        config.plugin = new FreeMovementPlugin(null, 0, 0);
        config.maxDepth = 0;
        JPSImpl jps = new JPSImpl(config);
        Assert.assertFalse(jps.run());
    }

    @Test
    public void pathToSelf() throws InterruptedException {
        JPSConfig config = new JPSConfig(Vector3i.zero(), Vector3i.zero());
        config.plugin = new FreeMovementPlugin(null, 0, 0);
        JPSImpl jps = new JPSImpl(config);
        Assert.assertTrue(jps.run());
    }

    @Test
    public void pathAdjacent() throws InterruptedException {
        JPSConfig config = new JPSConfig(Vector3i.zero(), new Vector3i(1, 1, 1));
        config.plugin = new FreeMovementPlugin(null, 0, 0);
        JPSImpl jps = new JPSImpl(config);
        Assert.assertTrue(jps.run());
    }
}
