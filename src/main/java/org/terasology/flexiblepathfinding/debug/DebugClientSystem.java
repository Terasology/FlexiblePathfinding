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

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.flexiblepathfinding.debug.ui.DebugHud;
import org.terasology.flexiblepathfinding.metrics.Histogram;
import org.terasology.flexiblepathfinding.metrics.PathMetric;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.world.WorldProvider;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Share(DebugClientSystem.class)
@RegisterSystem(RegisterMode.CLIENT)
public class DebugClientSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final float UPDATE_MILLIS = 1000;

    public Histogram successTimes;
    public Histogram failureTimes;
    public Histogram sizes;
    public Histogram costs;
    public Histogram depths;
    public Histogram explored;

    @In
    private Time time;

    @In
    private NUIManager nuiManager;

    @In
    private WorldProvider world;

    private float lastUpdate;

    @Override
    public void update(float delta) {
        if (time.getGameTimeInMs() < lastUpdate + UPDATE_MILLIS) {
            return;
        }

        lastUpdate = time.getGameTimeInMs();
        world.getWorldEntity().send(new PathMetricsRequestEvent());
    }

    @ReceiveEvent
    public void onPathMetricsResponse(PathMetricsResponseEvent event, EntityRef entity) {
        List<PathMetric> successes = event.metrics.stream().filter((x) -> x.success).collect(Collectors.toList());
        List<PathMetric> failures = event.metrics.stream().filter((x) -> !x.success).collect(Collectors.toList());

        successTimes = new Histogram(successes, 30, (PathMetric x) -> x.time);
        failureTimes = new Histogram(failures, 30, (PathMetric x) -> x.time);
        sizes = new Histogram(successes, 30, (PathMetric x) -> x.size);
        costs = new Histogram(successes, 30, (PathMetric x) -> x.cost);
        depths = new Histogram(event.metrics, 30, (PathMetric x) -> x.maxDepth);
        explored = new Histogram(event.metrics, 30, (PathMetric x) -> x.nodesExplored);
    }

    @Command
    public String togglePathDebugger() {
        nuiManager.toggleScreen("FlexiblePathfinding:debugscreen");
        return "Toggled path debugger";
    }

    @Command
    public String togglePathHud() {
        Collection<DebugHud> widgets = nuiManager.getHUD().findAll(DebugHud.class);
        if (widgets.isEmpty()) {
            nuiManager.getHUD().addHUDElement("FlexiblePathfinding:debughud");
        } else {
            for (DebugHud widget : widgets) {
                nuiManager.getHUD().removeHUDElement(widget);
            }
        }
        return "Toggled path hud";
    }
}
