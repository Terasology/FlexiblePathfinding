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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.core.world.generator.AbstractBaseWorldGenerator;
import org.terasology.engine.SimpleUri;
import org.terasology.math.geom.Vector3i;
import org.terasology.naming.Name;
import org.terasology.flexiblepathfinding.plugins.StandardPlugin;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.WorldProvider;

import java.util.List;
import java.util.Map;

public class JPSTestHelper {
    private static final char NEW_LEVEL = '|';
    private static Logger logger = LoggerFactory.getLogger(JPSTestHelper.class);
    static public <T extends StandardPlugin> void runFailingTest(Class<T> pluginClass, String[] ground, String[]
            pathData) throws InterruptedException {
        TestDataPojo testData = new TestDataPojo();
        MapWorldProvider worldProvider = new MapWorldProvider(ground);
        final Map<Integer, Vector3i> expected = worldProvider.parseExpectedPath(pathData, testData);
        List<Vector3i> path = runJps(0, pluginClass, testData, worldProvider);
        Assert.assertEquals(0, path.size());
    }

    static public <T extends StandardPlugin> void runTest(Class<T> pluginClass, String[] ground, String[] pathData) throws InterruptedException {
        TestDataPojo testData = new TestDataPojo();
        MapWorldProvider worldProvider = new MapWorldProvider(ground);
        final Map<Integer, Vector3i> expected = worldProvider.parseExpectedPath(pathData, testData);
        List<Vector3i> path = runJps(0, pluginClass, testData, worldProvider);
        assertPathsEqual(expected, path);
    }

    private static void assertPathsWithinGoalDistance(float goalDistance, Map<Integer, Vector3i> expected, List<Vector3i> path) {
        assertPathsEqual(expected, path);
        Assert.assertTrue(path.get(path.size()-1).distanceSquared(expected.get(expected.size()-1)) <= goalDistance*goalDistance);
    }

    static public <T extends StandardPlugin> List<Vector3i> runTestWithGoalDistance(float goalDistance, Class<T> pluginClass, String[] ground, String[] pathData) throws InterruptedException  {
        TestDataPojo testData = new TestDataPojo();
        MapWorldProvider worldProvider = new MapWorldProvider(ground);
        final Map<Integer, Vector3i> expected = worldProvider.parseExpectedPath(pathData, testData);
        expected.remove(expected.size()-1);
        List<Vector3i> path = runJps(goalDistance, pluginClass, testData, worldProvider);
        assertPathsWithinGoalDistance(goalDistance, expected, path);
        return path;
    }

    private static void assertPathsEqual(Map<Integer, Vector3i> expected, List<Vector3i> path) {
        int i = 0;
        for (Vector3i pos : path) {
            logger.warn("{}: e {}", i, expected.get(i));
            logger.warn("{}: a {}", i, pos);
            Assert.assertEquals(expected.get(i).toString(), pos.toString());
            i++;
        }
        Assert.assertEquals(expected.size(), path.size());
    }

    private static <T extends StandardPlugin> List<Vector3i> runJps(float goalDistance, Class<T> pluginClass,
                                                                    TestDataPojo testData, WorldProvider world) throws
            InterruptedException {
        JPSConfig config = new JPSConfig(testData.start, testData.end);
        config.goalDistance = goalDistance;
        config.useLineOfSight = false;
        if(pluginClass != null) {
            try {
                config.plugin = pluginClass.getConstructor(WorldProvider.class).newInstance(world);
            } catch (Exception e) {
                LoggerFactory.getLogger(JPSTestHelper.class).warn(e.toString());
                Assert.assertTrue(false);
            }
        }

        logger.warn("{}", config);
        JPSImpl jps = new JPSImpl(config);
        jps.run();

        return jps.getPath();
    }
}
