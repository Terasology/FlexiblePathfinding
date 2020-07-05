// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flexiblepathfinding.metrics;

import com.google.common.collect.Queues;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Collects time series data for displaying metrics
 */
public class TimeSeries {
    private final ArrayBlockingQueue<Double> values = Queues.newArrayBlockingQueue(1000);
    public void add(Double value) {
        if (values.remainingCapacity() < 1) {
            values.poll();
        }

        values.add(value);
    }

    public ArrayBlockingQueue<Double> getValues() {
        return values;
    }
}
