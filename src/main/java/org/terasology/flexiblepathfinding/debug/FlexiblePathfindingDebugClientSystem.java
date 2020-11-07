// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.debug;

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.world.WorldProvider;

@Share(FlexiblePathfindingDebugClientSystem.class)
@RegisterSystem(RegisterMode.CLIENT)
public class FlexiblePathfindingDebugClientSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    @In
    private Time time;

    @In
    private NUIManager nuiManager;

    @In
    private WorldProvider world;

    private boolean showPathDebugger;
    private float lastUpdate;
    private static final float UPDATE_MILLIS = 1000;
    PathMetricsResponseEvent lastResponse = new PathMetricsResponseEvent();

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
