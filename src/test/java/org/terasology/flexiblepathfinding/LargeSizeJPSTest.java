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

import org.junit.Test;
import org.terasology.flexiblepathfinding.helpers.JPSTestHelper;
import org.terasology.flexiblepathfinding.helpers.MapWorldProvider;
import org.terasology.flexiblepathfinding.plugins.basic.CompositePlugin;
import org.terasology.flexiblepathfinding.plugins.basic.LeapingPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;

public class LargeSizeJPSTest {
    @Test
    public void stairsUp() throws InterruptedException {
        executeExample(new String[]{
                "XXX      |XXXXX    |XXXXXXX  |XXXXXXXXX",
                "XXX      |XXXXX    |XXXXXXX  |XXXXXXXXX",
                "XXX      |XXXXX    |XXXXXXX  |XXXXXXXXX",
        }, new String[]{
                "         |         |         |         ",
                "         | ?       | 1!      |         ",
                "         |         |         |         ",
        });
    }

    @Test
    public void simple() throws InterruptedException {
        executeExample(new String[]{
                "XXXXXXX|XXXXXXX|XXXXXXX",
                "XXX   X|XXX   X|XXX   X",
                "XXX   X|XXX   X|XXX   X",
                "XXX   X|XXX   X|XXX   X",
                "XXXXXXX|XXXXXXX|XXXXXXX",
                "XXXXXXX|XXXXXXX|XXXXXXX",
                "XXXXXXX|XXXXXXX|XXXXXXX",
        }, new String[]{
                "       |       |       ",
                "       | ?     |       ",
                "       | 1     |       ",
                "       | 2     |       ",
                "       | 3     |       ",
                "       | 4567! |       ",
                "       |       |       ",
        });
    }


    @Test
    public void simpleNotEnoughRoom() throws InterruptedException {
        executeFailingExample(new String[]{
                "XXXXXXX|XXXXXXX",
                "XXX   X|XXX   X",
                "XXX   X|XXX   X",
                "XXX   X|XXX   X",
                "XXXXXXX|XXXXXXX",
                "XXXXXXX|XXXXXXX",
                "XXXXXXX|XXXXXXX",
        }, new String[]{
                "       |       ",
                " ?     |       ",
                " 1     |       ",
                " 2     |       ",
                " 3     |       ",
                " 45678!|       ",
                "       |       ",
        });
    }

    private void executeExample(String[] ground, String[] pathData) throws InterruptedException {
        JPSConfig config = new JPSConfig();
        config.useLineOfSight = false;
        
        MapWorldProvider worldProvider = new MapWorldProvider(ground);
        CompositePlugin plugin = new CompositePlugin(
                new WalkingPlugin(worldProvider, 2.9f, 2.9f),
                new LeapingPlugin(worldProvider, 2.9f, 2.9f)
        );

        config.plugin = plugin;
        JPSTestHelper.runTest(config, pathData, worldProvider);
    }

    private void executeFailingExample(String[] ground, String[] pathData) throws InterruptedException {
        JPSConfig config = new JPSConfig();
        config.useLineOfSight = false;

        MapWorldProvider worldProvider = new MapWorldProvider(ground);
        CompositePlugin plugin = new CompositePlugin(
                new WalkingPlugin(worldProvider, 2.9f, 2.9f),
                new LeapingPlugin(worldProvider, 2.9f, 2.9f)
        );

        config.plugin = plugin;
        JPSTestHelper.runFailingTest(config, pathData, worldProvider);
    }
}
