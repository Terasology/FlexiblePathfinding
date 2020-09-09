// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import org.junit.Assert;
import org.junit.Test;
import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.helpers.MapWorldProvider;
import org.terasology.flexiblepathfinding.helpers.TestDataPojo;
import org.terasology.flexiblepathfinding.plugins.basic.FlyingPlugin;
import org.terasology.math.geom.Vector3i;

public class LineOfSightTest {
    static WorldProvider runLineOfSight(boolean expected, String[] ground, String[] pathData) {
        TestDataPojo testData = new TestDataPojo();
        MapWorldProvider worldProvider = new MapWorldProvider(ground);
        MapWorldProvider.parseExpectedPath(pathData, testData);
        Assert.assertEquals(expected, new LineOfSight3d(worldProvider).inSight(testData.start, testData.stop));
        return worldProvider;
    }

    @Test
    public void testSimple() {
        runLineOfSight(true, new String[]{
                "XXX",
                "XXX",
                "XXX"
        }, new String[]{
                "?  ",
                "   ",
                "  !"
        });
    }

    @Test
    public void testSimpleFail() {
        runLineOfSight(false, new String[]{
                "XXX",
                "X X",
                "XXX"
        }, new String[]{
                "?  ",
                "   ",
                "  !"
        });
    }

    @Test
    public void simple3d() throws InterruptedException {
        WorldProvider worldProvider = runLineOfSight(true, new String[]{
                "XXX|XXX|XXX",
                "XXX|XXX|XXX",
                "XXX|XXX|XXX",
        }, new String[]{
                "?  |   |   ",
                "   |   |  !",
                "   |   |   ",
        });

        JPSConfig config = new JPSConfig(Vector3i.zero(), new Vector3i(2, 2, 1));
        config.useLineOfSight = true;
        config.plugin = new FlyingPlugin(worldProvider, 0, 0);
        JPSImpl jps = new JPSImpl(config);
        Assert.assertTrue(jps.run());
        Assert.assertEquals(2, jps.getPath().size());
    }

    @Test
    public void simple3dGap() {
        // LoS has no height requirement, but you probably have to be perfectly aligned for this to work
        runLineOfSight(true, new String[]{
                "XXX|X X|XXX",
                "XXX| X |X  ",
                "XXX|X  |X X",
        }, new String[]{
                "?  |   |   ",
                "   |   |   ",
                "   |   |  !",
        });
    }
}
