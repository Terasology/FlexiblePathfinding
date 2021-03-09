// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.debug;

import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.nui.databinding.DefaultBinding;

import java.util.Map;

/**
 * Created by kaen on 8/25/16.
 */
public class DebugHud extends CoreHudWidget {
    @In
    FlexiblePathfindingDebugClientSystem system;

    boolean bound = false;

    public void bind() {
        UIHistogram successTimes = find("successTimes", UIHistogram.class);
        UIHistogram failureTimes = find("failureTimes", UIHistogram.class);
        UIHistogram sizes = find("sizes", UIHistogram.class);
        UIHistogram costs = find("costs", UIHistogram.class);
        UIHistogram depths = find("depths", UIHistogram.class);
        UIHistogram explored = find("explored", UIHistogram.class);

        successTimes.bindValue(new DefaultBinding<Map<? extends Comparable, Integer>>() {
            @Override
            public Map<? extends Comparable, Integer> get() {
                return system.getLastResponse().successTimes;
            }
        });

        failureTimes.bindValue(new DefaultBinding<Map<? extends Comparable, Integer>>() {
            @Override
            public Map<? extends Comparable, Integer> get() {
                return system.getLastResponse().failureTimes;
            }
        });

        sizes.bindValue(new DefaultBinding<Map<? extends Comparable, Integer>>() {
            @Override
            public Map<? extends Comparable, Integer> get() {
                return system.getLastResponse().sizes;
            }
        });

        costs.bindValue(new DefaultBinding<Map<? extends Comparable, Integer>>() {
            @Override
            public Map<? extends Comparable, Integer> get() {
                return system.getLastResponse().costs;
            }
        });

        depths.bindValue(new DefaultBinding<Map<? extends Comparable, Integer>>() {
            @Override
            public Map<? extends Comparable, Integer> get() {
                return system.getLastResponse().depths;
            }
        });

        explored.bindValue(new DefaultBinding<Map<? extends Comparable, Integer>>() {
            @Override
            public Map<? extends Comparable, Integer> get() {
                return system.getLastResponse().explored;
            }
        });
    }

    @Override
    public void initialise() {
        bind();
    }
}
