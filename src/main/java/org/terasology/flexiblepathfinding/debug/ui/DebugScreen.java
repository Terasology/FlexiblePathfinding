// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.debug.ui;

import org.terasology.assets.ResourceUrn;
import org.terasology.flexiblepathfinding.debug.DebugClientSystem;
import org.terasology.flexiblepathfinding.debug.ToggleDebugHudEvent;
import org.terasology.flexiblepathfinding.debug.ToggleRecordingEvent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.widgets.ActivateEventListener;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.world.WorldProvider;

import java.util.Map;

public class DebugScreen extends CoreScreenLayer {
    @In
    private DebugClientSystem system;

    @In
    private LocalPlayer localPlayer;

    @In
    private NUIManager nuiManager;

    @Override
    public void initialise() {
        UIButton toggleRecording = find("toggleRecording", UIButton.class);
        UIButton toggleDebugHud = find("toggleDebugHud", UIButton.class);
        UIButton viewPathLogs = find("viewPathLogs", UIButton.class);

        if (toggleRecording != null) {
            toggleRecording.subscribe((UIWidget widget) -> localPlayer.getClientEntity().send(new ToggleRecordingEvent()));
            toggleRecording.bindText(new DefaultBinding<String>() {
                @Override
                public String get() {
                    return (system.lastPathMetricsResponseEvent.recording ? "Enable" : "Disable") + " Path Metric Recording";
                }
            });
        }

        if (toggleDebugHud != null) {
            toggleDebugHud.subscribe((UIWidget widget) -> localPlayer.getClientEntity().send(new ToggleDebugHudEvent()));
        }

        if (viewPathLogs != null) {
            viewPathLogs.subscribe((UIWidget widget) -> nuiManager.pushScreen("pathRequestLog"));
        }
    }
}
