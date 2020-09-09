// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import org.junit.Test;
import org.terasology.flexiblepathfinding.helpers.JPSTestHelper;
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
