// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.debug;

import org.terasology.network.NetworkEvent;

import java.util.Map;

public class PathMetricsResponseEvent extends NetworkEvent {
    public Map<Float, Integer> successTimes;
    public Map<Float, Integer> failureTimes;
    public Map<Float, Integer> costs;
    public Map<Float, Integer> sizes;
    public Map<Float, Integer> depths;
    public Map<Float, Integer> explored;

    public int total;
    public int success;
    public int failure;
    public int lineOfSight;
}
