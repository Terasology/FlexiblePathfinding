// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.metrics;


import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class Histogram {
    int[] data = new int[0];
    int buckets = 10;
    double min = 0;
    double max = 0;
    double bucketMax = 0;
    double bucketSize = 0;

    public <T> void build(Collection<T> source, Function<T, Double> fn) {
        data = new int[buckets];
        try {
            min = source.stream().map(fn).min((o1, o2) -> Double.compare(o1, o2)).get();
            max = source.stream().map(fn).max((o1, o2) -> Double.compare(o1, o2)).get();
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

    public <T> Map<Float, Integer> analyze(Collection<T> source, Function<T, Double> fn, int buckets) {
        this.buckets = buckets;
        build(source, fn);
        Map<Float, Integer> result = Maps.newHashMap();
        for (int i = 0; i < buckets; i++) {
            double k = min + bucketSize * i;
            result.put((float) k, data[i]);
        }
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
}