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
package org.terasology.flexiblepathfinding.plugins.basic;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;

import java.util.List;

public class CompositePlugin implements JPSPlugin {
    public CompositePlugin(JPSPlugin ... plugins) {
        this.plugins = Lists.newArrayList(plugins);
    }

    private static final Logger logger = LoggerFactory.getLogger(CompositePlugin.class);
    private List<JPSPlugin> plugins = Lists.newArrayList();

    public void addPlugin(JPSPlugin plugin) {
        plugins.add(plugin);
    }

    public List<JPSPlugin> getPlugins() {
        return plugins;
    }

    @Override
    public boolean inSight(Vector3i start, Vector3i goal) {
        return false;
    }

    @Override
    public boolean isReachable(Vector3i a, Vector3i b) {
        for (JPSPlugin plugin : plugins) {
            if (plugin.isReachable(a, b)) {
                return true;
            }
        }
        return false;
    }
}
