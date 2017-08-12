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
import org.terasology.flexiblepathfinding.plugins.WalkingPlugin;
import org.terasology.math.geom.Vector3i;

/**
 * @author synopia
 */
public class JPSTest {
    private Vector3i start;
    private Vector3i end;

    @Test
    public void stairs2() throws InterruptedException {
        executeExample(new String[]{
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXX XXXXX|         |         |XXXXX XXX",
                "XXX XXXXX|   X     |         |XXX XXXXX|   X     |         |XXXXX XXX",
                "XXX XXXXX|         |   X     |XXX XXXXX|         |   X     |XXXXX XXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
        }, new String[]{
                "?        |         |         |         |         |         |         ",
                " 1       |         |         |         |         |         |         ",
                "  2      |         |         |         |         |         |         ",
                "   3     |         |         |         |         |         |         ",
                "         |   4     |         |         |   7     |         |         ",
                "         |         |   5     |    6    |         |   8     |         ",
                "         |         |         |         |         |         |    9    ",
                "         |         |         |         |         |         |    0abc!",
        });
    }

    @Test
    public void stairsClosed2() throws InterruptedException {
        executeExample(new String[]{
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXX XXXXX|         |         |XXXXX XXX",
                "XXX XXXXX|   X     |         |XXX XXXXX|     X   |         |XXXXX XXX",
                "XXX XXXXX|         |   X     |XXXXXXXXX|         |     X   |XXXXX XXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
                "XXXXXXXXX|         |         |XXXXXXXXX|         |         |XXXXXXXXX",
        }, new String[]{
                "?        |         |         |         |         |         |         ",
                "         |         |         |         |         |         |         ",
                "         |         |         |         |         |         |         ",
                "         |         |         |         |         |         |         ",
                "         |         |         |         |         |         |         ",
                "         |         |         |         |         |         |         ",
                "         |         |         |         |         |         |         ",
                "         |         |         |         |         |         |        !",
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
                " 2!"
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
                "              X",
        }, new String[]{
                "?           1  ",
                "             2 ",
                "   4        3  ",
                "  5            ",
                "   6         7 ",
                "              !",
        });

    }

    @Test
    public void threeDimensionalMoves() throws InterruptedException {
        executeExample(new String[]{
                "XXX|   |   |   ",
                "X X| X |X  | X ",
                "XXX|   |X  |   "
        }, new String[]{
                "?  |   |   |   ",
                "   | 1 |2  | ! ",
                "   |   |   |   "
        });
    }


    @Test
    public void startInBox() throws InterruptedException {
        executeExample(new String[]{
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
        executeExample(new String[]{
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
        executeExample(new String[]{
                " XXX",
                " XXX",
                " XXX"
        }, new String[]{
                " 12!",
                "?   ",
                "    "
        });
    }


    @Test
    public void endUnwalkable() throws InterruptedException {
        executeExample(new String[]{
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
                " 2  3  !"
        });
    }

    private void executeExample(String[] ground, String[] pathData) throws InterruptedException {
        JPSTestHelper.runTest(WalkingPlugin.class, ground, pathData);
    }
}
