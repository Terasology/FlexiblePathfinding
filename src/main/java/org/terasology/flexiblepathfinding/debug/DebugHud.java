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
package org.terasology.flexiblepathfinding.debug;

import org.terasology.nui.databinding.DefaultBinding;
import org.terasology.registry.In;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;

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
