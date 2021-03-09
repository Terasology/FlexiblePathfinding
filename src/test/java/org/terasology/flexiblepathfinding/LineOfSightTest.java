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
import org.junit.jupiter.api.Test;
import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.helpers.MapWorldProvider;
import org.terasology.flexiblepathfinding.helpers.TestDataPojo;
import org.terasology.flexiblepathfinding.plugins.basic.FlyingPlugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LineOfSightTest {
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

        JPSConfig config = new JPSConfig(new Vector3i(), new Vector3i(2, 2, 1));
        config.useLineOfSight = true;
        config.plugin = new FlyingPlugin(worldProvider, 0, 0);
        JPSImpl jps = new JPSImpl(config);
        assertTrue(jps.run());
        assertEquals(2, jps.getPath().size());
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

    static WorldProvider runLineOfSight(boolean expected, String[] ground, String[] pathData) {
        TestDataPojo testData = new TestDataPojo();
        MapWorldProvider worldProvider = new MapWorldProvider(ground);
        worldProvider.parseExpectedPath(pathData, testData);
        assertEquals(expected, new LineOfSight3d(worldProvider).inSight(testData.start, testData.stop));
        return worldProvider;
    }
}
