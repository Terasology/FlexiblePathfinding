// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import org.joml.Vector3i;

import java.util.List;

public interface PathfinderCallback {
    void pathReady(List<Vector3i> path, Vector3i target);
}
