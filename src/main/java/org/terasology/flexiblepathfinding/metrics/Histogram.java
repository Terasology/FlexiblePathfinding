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
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class Histogram {
    int data[] = new int[0];
    int buckets = 25;
    double min = 0;
    double max = 0;
    double bucketMax = 0;
    double bucketSize = 0;
    private Map<Float, Integer> bucketData = Maps.newHashMap();

    public Histogram() {

    }

    public <T> Histogram(Collection<T> source, int numBuckets, Function<T, Double> fn) {
        analyze(source, numBuckets, fn);
    }

    public <T> void build(Collection<T> source, Function<T, Double> fn) {
        data = new int[buckets];
        try {
            min = source.stream().map(fn).min(Double::compare).get();
            max = source.stream().map(fn).max(Double::compare).get();
        } catch (NoSuchElementException e) {
            min = 0;
            max = 1000;
        }
        bucketSize = (max - min) / (data.length - 1);
        for (T el : source) {
            double val = fn.apply(el);
            int bucket = (int) Math.floor((val - min) / bucketSize);
            data[bucket]++;
            bucketMax = Math.max(bucketMax, data[bucket]);
        }
    }

    public <T> Map<Float, Integer> analyze(Collection<T> source, int numBuckets, Function<T, Double> fn) {
        this.buckets = numBuckets;
        build(source, fn);
        Map<Float, Integer> result = Maps.newHashMap();
        for (int i = 0; i < numBuckets; i++) {
            double k = min + bucketSize * i;
            result.put((float) k, data[i]);
        }

        bucketData = result;
        return result;
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

    public Map<Float, Integer> getBucketData() {
        return bucketData;
    }
}
