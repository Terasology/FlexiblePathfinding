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

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathMetricsRecorder {
    private static ArrayBlockingQueue<PathMetric> stats = Queues.newArrayBlockingQueue(1000);

    public static void recordMetrics(PathMetric stat) {
        if (stats.remainingCapacity() < 10) {
            stats.remove();
        }

        stats.add(stat);
    }

    public static Collection<PathMetric> getPathMetrics() {
        return stats;
    }

    public static String getStats() {
        String result = "";

        Histogram successTime = new Histogram();
        Histogram failTime = new Histogram();
        Histogram size = new Histogram();
        Histogram cost = new Histogram();

        Collection<PathMetric> successes = stats.stream().filter(stat -> stat.success).collect(Collectors.toList());
        Collection<PathMetric> failures = stats.stream().filter(stat -> !stat.success).collect(Collectors.toList());

        successTime.build(successes, pathMetric -> pathMetric.time);
        failTime.build(failures, pathMetric -> pathMetric.time);
        size.build(stats, pathMetric -> pathMetric.size);
        cost.build(stats, pathMetric -> pathMetric.cost);

        result = String.format("total: %d\nsuccess: %d\nfail: %d\n", stats.size(), successes.size(), failures.size());
        return result + successTime.toString() + failTime.toString() + size.toString() + cost.toString();
    }
}