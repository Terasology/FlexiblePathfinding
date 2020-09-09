// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.plugins;

import org.terasology.math.geom.Vector3i;

public interface JPSPlugin {
    boolean inSight(Vector3i start, Vector3i goal);

    boolean isReachable(Vector3i to, Vector3i from);
}
