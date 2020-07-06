// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.debug.ui;

import com.google.common.collect.Lists;
import org.terasology.flexiblepathfinding.debug.DebugClientSystem;
import org.terasology.flexiblepathfinding.metrics.PathMetric;
import org.terasology.registry.In;
import org.terasology.rendering.nui.ControlWidget;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.layouts.FlowLayout;
import org.terasology.rendering.nui.layouts.relative.RelativeLayout;
import org.terasology.rendering.nui.widgets.UIText;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PathRequestLog extends CoreScreenLayer {
    @In
    DebugClientSystem system;

    UIText pathMetricLogs;

    @Override
    public void initialise() {
        pathMetricLogs = find("pathMetricLogs", UIText.class);
        pathMetricLogs.bindText(new DefaultBinding<String>() {
            @Override
            public String get() {
                String results = "";
                List<String> metricsStrings = system.lastPathMetricsResponseEvent.pathMetrics.stream()
                        .map(PathMetric::toString)
                        .collect(Collectors.toList());
                return String.join("\n", Lists.reverse(metricsStrings));
            }
        });
    }
}
