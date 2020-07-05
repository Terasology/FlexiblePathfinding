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

import com.google.common.collect.Lists;
import org.terasology.entitySystem.event.Event;
import org.terasology.flexiblepathfinding.metrics.PathMetric;
import org.terasology.flexiblepathfinding.metrics.PathfinderMetric;
import org.terasology.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PathMetricsResponseEvent extends NetworkEvent {
    public List<PathMetric> pathMetrics = Lists.newArrayList();
    public List<PathfinderMetric> pathfinderMetrics = Lists.newArrayList();
}
