// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.plugins.basic;

import com.google.common.collect.Lists;
import org.joml.Vector3ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;

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
    public boolean inSight(Vector3ic start, Vector3ic goal) {
        return false;
    }

    @Override
    public boolean isReachable(Vector3ic a, Vector3ic b) {
        for (JPSPlugin plugin : plugins) {
            if (plugin.isReachable(a, b)) {
                return true;
            }
        }
        return false;
    }
}
