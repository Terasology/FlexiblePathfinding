/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.flexiblepathfinding;

import com.google.common.collect.Queues;

import java.util.Collection;
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

    private static class Histogram {
        int data[] = new int[10];
        double min = 0;
        double max = 0;
        double bucketMax = 0;
        double bucketSize = 0;

        public <T> void build(Collection<T> source, Function<T, Double> fn) {
            min = source.stream().map(fn).min((o1, o2) -> Double.compare(o1, o2)).get();
            max = source.stream().map(fn).max((o1, o2) -> Double.compare(o1, o2)).get();
            bucketSize = (max - min) / (data.length - 1);
            for (T el : source) {
                double val = fn.apply(el);
                int bucket = (int) Math.floor((val - min) / bucketSize);
                data[bucket]++;
                bucketMax = Math.max(bucketMax, data[bucket]);
            }
        }

        @Override
        public String toString() {
            String result = "";

            double scale = 20.0 / bucketMax;
            for (int i = 0; i < data.length; i++) {
                String line = String.format("% 7.1f (% 4d) |", min + bucketSize * (i + 1), data[i]);
                for (int j = 0; j < data[i] * scale; j++) {
                    line += "#";
                }
                result += line + "\n";
            }
            result += "\n";
            return result;
        }
    }

    protected static class PathMetric {
        double time;
        double cost;
        double size;
        boolean success;
    }
}