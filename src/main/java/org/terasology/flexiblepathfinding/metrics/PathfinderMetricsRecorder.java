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
package org.terasology.flexiblepathfinding.metrics;

import com.google.common.collect.Queues;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

public final class PathfinderMetricsRecorder {
    private static final ArrayBlockingQueue<PathMetric> PATH_METRICS = Queues.newArrayBlockingQueue(1000);
    private static final ArrayBlockingQueue<PathfinderMetric> PATHFINDER_METRICS = Queues.newArrayBlockingQueue(50);
    private PathfinderMetricsRecorder() { }

    public static void recordPathMetric(PathMetric pathMetric) {
        if (PATH_METRICS.remainingCapacity() < 1) {
            PATH_METRICS.poll();
        }

        PATH_METRICS.add(pathMetric);
    }

    public static void recordPathfinderMetric(PathfinderMetric pathfinderMetric) {
        if (PATHFINDER_METRICS.remainingCapacity() < 1) {
            PATHFINDER_METRICS.poll();
        }

        PATHFINDER_METRICS.add(pathfinderMetric);
    }

    public static Collection<PathMetric> getPathMetrics() {
        return PATH_METRICS;
    }
    public static Collection<PathfinderMetric> getPathfinderMetrics() {
        return PATHFINDER_METRICS;
    }

}