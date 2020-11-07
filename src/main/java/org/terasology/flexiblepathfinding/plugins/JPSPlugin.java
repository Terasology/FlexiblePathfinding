// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.plugins;


import org.joml.Vector3ic;

public interface JPSPlugin {
    boolean inSight(Vector3ic start, Vector3ic goal);
    boolean isReachable(Vector3ic to, Vector3ic from);
}
