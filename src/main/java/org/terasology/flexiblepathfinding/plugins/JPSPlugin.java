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
package org.terasology.flexiblepathfinding.plugins;

import org.terasology.flexiblepathfinding.JPSJumpPoint;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;

public interface JPSPlugin {
    boolean inSight(Vector3i start, Vector3i goal);
    boolean isReachable(Vector3i to, Vector3i from);
    boolean isWalkable(Vector3i v);
    float getTime();

    void setXzPadding(int i);
    void setUpwardPadding(int i);
    Region3i getOccupiedRegion();

}
