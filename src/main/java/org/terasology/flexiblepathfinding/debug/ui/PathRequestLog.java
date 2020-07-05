// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.debug.ui;

import org.terasology.flexiblepathfinding.debug.DebugClientSystem;
import org.terasology.registry.In;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;

import java.util.Map;

public class PathRequestLog extends CoreHudWidget {
    @In
    DebugClientSystem system;

    @Override
    public void initialise() {

    }
}
