// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import org.junit.Test;
import org.terasology.flexiblepathfinding.helpers.JPSTestHelper;
import org.terasology.flexiblepathfinding.helpers.MapWorldProvider;
import org.terasology.flexiblepathfinding.plugins.basic.CompositePlugin;
import org.terasology.flexiblepathfinding.plugins.basic.LeapingPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;

public class HumanoidSizeJPSTest {
    @Test
    public void simple() throws InterruptedException {
        executeExample(new String[]{
                "XXXXXXXXX|XXXXXXXXX",
        }, new String[]{
                "?1234567!|         ",
        });
    }

    @Test
    public void failing() throws InterruptedException {
        executeFailingExample(new String[]{
                "XXXXXXXXX|X XXXXXXX",
        }, new String[]{
                "?1234567!|         ",
        });
    }


    @Test
    public void jumps() throws InterruptedException {
        executeExample(new String[]{
                "X |XX|XX",
        }, new String[]{
                "? |1!|  ",
        });
    }

    private void executeExample(String[] ground, String[] pathData) throws InterruptedException {
        JPSConfig config = new JPSConfig();
        config.useLineOfSight = false;

        MapWorldProvider worldProvider = new MapWorldProvider(ground);
        CompositePlugin plugin = new CompositePlugin(
                new WalkingPlugin(worldProvider, 0.8f, 1.7f),
                new LeapingPlugin(worldProvider, 0.8f, 1.7f)
        );

        config.plugin = plugin;
        JPSTestHelper.runTest(config, pathData, worldProvider);
    }

    private void executeFailingExample(String[] ground, String[] pathData) throws InterruptedException {
        JPSConfig config = new JPSConfig();
        config.useLineOfSight = false;

        MapWorldProvider worldProvider = new MapWorldProvider(ground);
        CompositePlugin plugin = new CompositePlugin(
                new WalkingPlugin(worldProvider, 0.8f, 1.7f),
                new LeapingPlugin(worldProvider, 0.8f, 1.7f)
        );

        config.plugin = plugin;
        JPSTestHelper.runFailingTest(config, pathData, worldProvider);
    }
}
