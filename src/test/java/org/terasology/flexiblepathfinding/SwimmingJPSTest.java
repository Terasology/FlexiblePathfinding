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
import org.terasology.flexiblepathfinding.plugins.basic.FlyingPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.SwimmingPlugin;

public class SwimmingJPSTest {
    @Test
    public void stairs2() throws InterruptedException {
        executeExample(new String[]{
                "~~~~~~~~~|         |         |~~~~~~~~~|         |         |~~~~~~~~~",
                "~~~~~~~~~|         |         |~~~~~~~~~|         |         |~~~~~~~~~",
                "~~~~~~~~~|         |         |~~~~~~~~~|         |         |~~~~~~~~~",
                "~~~~~~~~~|         |         |~~~ ~~~~~|         |         |~~~~~~~~~",
                "~~~~~~~~~|   ~     |   ~     |~~~ ~~~~~|  ~~     |   ~     |~~~ ~~~~~",
                "~~~~~~~~~|         |   ~     |~~~~~~~~~|         |   ~     |~~~~~~~~~",
                "~~~~~~~~~|         |         |~~~~~~~~~|         |         |~~~~~~~~~",
                "~~~~~~~~~|         |         |~~~~~~~~~|         |         |~~~~~~~~~",
        }, new String[]{
                "?        |         |         |         |         |         |         ",
                " 1       |         |         |         |         |         |         ",
                "  2      |         |         |         |         |         |         ",
                "   3     |         |         |         |         |         |         ",
                "   4     |   5     |   6     |         |         |         |         ",
                "         |         |   7     |   8!    |         |         |         ",
                "         |         |         |         |         |         |         ",
                "         |         |         |         |         |         |         ",
        });
    }

    @Test
    public void simple() throws InterruptedException {
        executeExample(new String[]{
                "~~~|~~~|~~~",
                "~~~|~~~|~~~",
                "~~~|~~~|~~~",
        }, new String[]{
                "?  |   |   ",
                "   | 1 |  !",
                "   |   |   ",
        });
    }


    @Test
    public void simpleReverse() throws InterruptedException {
        executeExample(new String[]{
                "~~~|~~~|~~~",
                "~~~|~~~|~~~",
                "~~~|~~~|~~~",
        }, new String[]{
                "!  | 1 |   ",
                "   |   |  ?",
                "   |   |   ",
        });
    }

    @Test
    public void highWall() throws InterruptedException {
        executeExample(new String[]{
                "~ ~|~ ~|~ ~|~~~",
                "~ ~|~ ~|~ ~|   ",
                "~ ~|~ ~|~ ~|   ",
        }, new String[]{
                "?  |1  |2 6|345",
                "   |  7|   |   ",
                "  !|   |   |   ",
        });
    }

    @Test
    public void nonTrivial() throws InterruptedException {
        executeExample(new String[]{
                "~~~~~~~~~~~~~~~",
                "             ~~",
                "~~~~~~~~~~~~~~~",
                "~~~            ",
                "~~~~~~~~~~~~~~~",
        }, new String[]{
                "?123456789abcd ",
                "             e ",
                "  qponmlkjihgf ",
                "  r            ",
                "  stuvwxyz!    ",
        });
    }

    @Test
    public void threeDimensionalMoves() throws InterruptedException {
        executeExample(new String[]{
                "~~~|~~ |   |   ",
                "~ ~|~~ | ~ | ~~",
                "~~~|   |   |   "
        }, new String[]{
                "?  | 1 |   |   ",
                "   | 2 | 3 | 4!",
                "   |   |   |   "
        });
    }


    @Test
    public void startInBox() throws InterruptedException {
        executeFailingExample(new String[]{
                "   ~|   ~",
                " ~ ~|   ~",
                "   ~|   ~"
        }, new String[]{
                "    |    ",
                " ? !|    ",
                "    |    "
        });
    }

    @Test
    public void endInBox() throws InterruptedException {
        executeFailingExample(new String[]{
                "   ~|   ~",
                " ~ ~|   ~",
                "   ~|   ~"
        }, new String[]{
                "    |    ",
                " ! ?|    ",
                "    |    "
        });
    }

    @Test
    public void startUnwalkable() throws InterruptedException {
        executeFailingExample(new String[]{
                " ~~~",
                " ~~~",
                " ~~~"
        }, new String[]{
                "   !",
                "?   ",
                "    "
        });
    }


    @Test
    public void endUnwalkable() throws InterruptedException {
        executeFailingExample(new String[]{
                " ~~~",
                " ~~~",
                " ~~~"
        }, new String[]{
                "    ",
                "!  ?",
                "    "
        });
    }

    private void executeExample(String[] ground, String[] pathData) throws InterruptedException {
        JPSTestHelper.runTest(SwimmingPlugin.class, ground, pathData);
    }

    private void executeFailingExample(String[] ground, String[] pathData) throws InterruptedException {
        JPSTestHelper.runFailingTest(SwimmingPlugin.class, ground, pathData);
    }
}
