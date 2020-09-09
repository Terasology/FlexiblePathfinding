// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.debug;

import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.console.commandSystem.annotations.Command;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.world.WorldProvider;

@Share(FlexiblePathfindingDebugClientSystem.class)
@RegisterSystem(RegisterMode.CLIENT)
public class FlexiblePathfindingDebugClientSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final float UPDATE_MILLIS = 1000;
    PathMetricsResponseEvent lastResponse = new PathMetricsResponseEvent();
    @In
    private Time time;
    @In
    private NUIManager nuiManager;
    @In
    private WorldProvider world;
    private boolean showPathDebugger;
    private float lastUpdate;

    public PathMetricsResponseEvent getLastResponse() {
        return lastResponse;
    }

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
        lastResponse = event;
    }

    @Command
    public String showPathDebugger() {
        if (showPathDebugger) {
            return "Already shown";
        }

        showPathDebugger = true;
        nuiManager.getHUD().addHUDElement("FlexiblePathfinding:debug");
        return "Showing path debugger";
    }
}
