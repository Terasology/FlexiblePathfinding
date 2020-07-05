// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.debug.ui;

import org.terasology.flexiblepathfinding.debug.DebugClientSystem;
import org.terasology.flexiblepathfinding.metrics.Histogram;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;

import java.util.Map;

public class DebugHud extends CoreHudWidget {
    @In
    DebugClientSystem system;

    @Override
    public void initialise() {
        UIHistogram successTimes = find("successTimes", UIHistogram.class);
        UIHistogram failureTimes = find("failureTimes", UIHistogram.class);
        UIHistogram sizes = find("sizes", UIHistogram.class);
        UIHistogram costs = find("costs", UIHistogram.class);
        UIHistogram depths = find("depths", UIHistogram.class);
        UIHistogram explored = find("explored", UIHistogram.class);

        successTimes.bindValue(new DefaultBinding<Histogram>() {
            @Override
            public Histogram get() {
                return system.successTimes;
            }
        });

        failureTimes.bindValue(new DefaultBinding<Histogram>() {
            @Override
            public Histogram get() {
                return system.failureTimes;
            }
        });

        sizes.bindValue(new DefaultBinding<Histogram>() {
            @Override
            public Histogram get() {
                return system.sizes;
            }
        });

        costs.bindValue(new DefaultBinding<Histogram>() {
            @Override
            public Histogram get() {
                return system.costs;
            }
        });

        depths.bindValue(new DefaultBinding<Histogram>() {
            @Override
            public Histogram get() {
                return system.depths;
            }
        });

        explored.bindValue(new DefaultBinding<Histogram>() {
            @Override
            public Histogram get() {
                return system.explored;
            }
        });
    }
}
