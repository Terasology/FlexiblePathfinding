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

/**
 * POJO for metrics on a single path finding request
 */
public class PathMetric {
    public double time;
    public double cost;
    public double size;
    public double maxDepth;
    public double nodesExplored;
    public boolean success;

    @Override
    public String toString() {
        return String.format("success: %s time: %s cost: %s size: %s", success, time, cost, size);
    }
}
