// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flexiblepathfinding.metrics;

/**
 * POJO for metrics relating to the pathfinder service as a whole
 */
public class PathfinderMetric {
    public double pendingTasks;
    public double runningTasks;
    public double recentlyCompletedTasks;
}
