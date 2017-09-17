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

import org.junit.Test;
import org.terasology.flexiblepathfinding.helpers.JPSTestHelper;
import org.terasology.flexiblepathfinding.helpers.MapWorldProvider;
import org.terasology.flexiblepathfinding.plugins.basic.CompositePlugin;
import org.terasology.flexiblepathfinding.plugins.basic.LeapingPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;

public class WalkingLeapingJPSTest {
    @Test
    public void stairs2() throws InterruptedException {
        executeExample(new String[]{
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXX XXXXX|         |         |XXXXXXXXX",
                "XXX  XXXX|  XX     |   X     |XXX XXXXX|  XX     |   X     |XXX XXXXX",
                "XXXXXXXXX|         |   X     |XXXXXXXXX|         |   X     |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
        }, new String[]{
                "?        |         |         |         |         |         |         ",
                " 1       |         |         |         |         |         |         ",
                "  2      |         |         |         |         |         |         ",
                "  3      |         |         |         |         |         |         ",
                "  4      |  56     |   7     |         |         |         |         ",
                "         |         |   8     |   9     |         |         |         ",
                "         |         |         |   !     |         |         |         ",
                "         |         |         |         |         |         |         ",
        });
    }

    @Test
    public void stairsClosed2() throws InterruptedException {
        executeFailingExample(new String[]{
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXX XXXXX|         |         |XXXXXXXXX",
                "XXX  XXXX|  X      |   X     |XXX XXXXX|  XX     |   X     |XXX XXXXX",
                "XXXXXXXXX|         |   X     |XXXXXXXXX|         |   X     |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
        }, new String[]{
                "?        |         |         |         |         |         |         ",
                " 1       |         |         |         |         |         |         ",
                "  2      |         |         |         |         |         |         ",
                "  3      |         |         |         |         |         |         ",
                "  4      |  56     |   7     |         |         |         |         ",
                "         |         |   8     |   9     |         |         |         ",
                "         |         |         |   !     |         |         |         ",
                "         |         |         |         |         |         |         ",
        });
    }

    @Test
    public void simple() throws InterruptedException {
        executeExample(new String[]{
                "X  ",
                "X  ",
                "XXX",
        }, new String[]{
                "?  ",
                "1  ",
                "23!"
        });
    }


    @Test
    public void simpleSteps() throws InterruptedException {
        executeExample(new String[]{
                "X  |XX |XXX"
        }, new String[]{
                "?  |12 | 3!"
        });
    }


    @Test
    public void simpleStepsTooHigh() throws InterruptedException {
        executeFailingExample(new String[]{
                "XX |XX |XXX"
        }, new String[]{
                "?  |   |  !"
        });
    }

    @Test
    public void simpleDiagonal() throws InterruptedException {
        executeExample(new String[]{
                "X  |   ",
                "X  | X ",
                "XXX|   ",
        }, new String[]{
                "?  |   ",
                "1  |   ",
                "23!|   "
        });
    }

    @Test
    public void nonTrivial() throws InterruptedException {
        executeExample(new String[]{
                "XXXXXXXXXXXXXXX",
                "             XX",
                "XXXXXXXXXXXXXXX",
                "XXX            ",
                "XXXXXXXXXXXXXXX",
        }, new String[]{
                "?123456789abcd ",
                "             e ",
                "  qponmlkjihgf ",
                "  r            ",
                "  stuvwxyz!    ",
        });

    }

    @Test
    public void startInBox() throws InterruptedException {
        executeFailingExample(new String[]{
                "   X|   X",
                " X X|   X",
                "   X|   X"
        }, new String[]{
                "    |    ",
                " ? !|    ",
                "    |    "
        });
    }

    @Test
    public void endInBox() throws InterruptedException {
        executeFailingExample(new String[]{
                "   X|   X",
                " X X|   X",
                "   X|   X"
        }, new String[]{
                "    |    ",
                " ! ?|    ",
                "    |    "
        });
    }

    @Test
    public void startUnwalkable() throws InterruptedException {
        executeFailingExample(new String[]{
                " XXX",
                " XXX",
                " XXX"
        }, new String[]{
                "   !",
                "?   ",
                "    "
        });
    }


    @Test
    public void endUnwalkable() throws InterruptedException {
        executeFailingExample(new String[]{
                " XXX",
                " XXX",
                " XXX"
        }, new String[]{
                "    ",
                "!  ?",
                "    "
        });
    }

    @Test
    public void simpleGoalDistance() throws InterruptedException {
        JPSTestHelper.runTestWithGoalDistance(3, WalkingPlugin.class, new String[]{
                "X       ",
                "X       ",
                "XXXXXXXX",
        }, new String[]{
                "?       ",
                "1       ",
                "23456  !"
        });
    }

    private void executeExample(String[] ground, String[] pathData) throws InterruptedException {
        JPSConfig config = new JPSConfig();
        config.useLineOfSight = false;

        MapWorldProvider worldProvider = new MapWorldProvider(ground);
        CompositePlugin plugin = new CompositePlugin(
                new WalkingPlugin(worldProvider, 0.4f, 0.4f),
                new LeapingPlugin(worldProvider, 0.4f, 0.4f)
        );

        config.plugin = plugin;
        JPSTestHelper.runTest(config, pathData, worldProvider);
    }

    private void executeFailingExample(String[] ground, String[] pathData) throws InterruptedException {
        JPSConfig config = new JPSConfig();
        config.useLineOfSight = false;

        MapWorldProvider worldProvider = new MapWorldProvider(ground);
        CompositePlugin plugin = new CompositePlugin(
                new WalkingPlugin(worldProvider, 0.4f, 0.4f),
                new LeapingPlugin(worldProvider, 0.4f, 0.4f)
        );

        config.plugin = plugin;
        JPSTestHelper.runFailingTest(config, pathData, worldProvider);
    }
}
