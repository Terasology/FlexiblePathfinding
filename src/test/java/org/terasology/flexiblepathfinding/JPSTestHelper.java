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

import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.terasology.core.world.generator.AbstractBaseWorldGenerator;
import org.terasology.engine.SimpleUri;
import org.terasology.math.geom.Vector3i;
import org.terasology.naming.Name;
import org.terasology.flexiblepathfinding.plugins.StandardPlugin;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.WorldProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JPSTestHelper {
    static public <T extends StandardPlugin> void runTest(Class<T> pluginClass, String[] ground, String[] pathData) throws InterruptedException  {
        class TestDataPojo {
            Vector3i start;
            Vector3i end;
        }

        TestDataPojo testData = new TestDataPojo();

        WorldProvidingHeadlessEnvironment env = new WorldProvidingHeadlessEnvironment(new Name("Pathfinding"));
        env.setupWorldProvider(new AbstractBaseWorldGenerator(new SimpleUri("")) {
            @Override
            public void initialize() {

            }
        });
        TextWorldBuilder builder = new TextWorldBuilder(env);
        builder.setGround(ground);

        final Map<Integer, Vector3i> expected = new HashMap<Integer, Vector3i>();
        builder.parse(new TextWorldBuilder.Runner() {
            @Override
            public char run(int x, int y, int z, char value) {
                Vector3i vec = new Vector3i(x, y, z);

                switch (value) {
                    case '?':
                        testData.start = vec;
                        break;
                    case '!':
                        testData.end = vec;
                        break;
                    default:
                        if (value == '0') {
                            expected.put(10, vec);
                        } else if (value > '0' && value <= '9') {
                            expected.put(value - '0', vec);
                        } else if (value >= 'a' && value <= 'z') {
                            expected.put(value - 'a' + 11, vec);
                        } else if (value >= 'A' && value <= 'Z') {
                            expected.put(value - 'A' + 11 + 27, vec);
                        }
                        break;
                }
                return 0;
            }
        }, pathData);
        if (expected.size() > 0) {
            expected.put(0, testData.start);
            expected.put(expected.size(), testData.end);
        }

        JPSConfig config = new JPSConfig(testData.start, testData.end);
        if(pluginClass != null) {
            WorldProvider world = CoreRegistry.get(WorldProvider.class);
            try {
                config.plugin = pluginClass.getConstructor(WorldProvider.class).newInstance(world);
            } catch (Exception e) {
                LoggerFactory.getLogger(JPSTestHelper.class).warn(e.toString());
                Assert.assertTrue(false);
            }
        }
        JPSImpl jps = new JPSImpl(config);
        jps.run();

        List<Vector3i> path = jps.getPath();
        Assert.assertEquals(expected.size(), path.size());
        int i = 0;
        for (Vector3i pos : path) {
            Assert.assertEquals(expected.get(i).toString(), pos.toString());
            i++;
        }
    }
}
