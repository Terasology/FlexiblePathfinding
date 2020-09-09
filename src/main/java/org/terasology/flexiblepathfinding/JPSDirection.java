// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.terasology.math.geom.Vector3i;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum JPSDirection {
    NORTH,
    SOUTH,
    EAST,
    WEST,
    UP_NORTH,
    UP_SOUTH,
    UP_EAST,
    UP_WEST,
    DOWN_NORTH,
    DOWN_SOUTH,
    DOWN_EAST,
    DOWN_WEST,
    NORTH_WEST,
    NORTH_EAST,
    SOUTH_EAST,
    SOUTH_WEST,
    UP_NORTH_WEST,
    UP_NORTH_EAST,
    UP_SOUTH_EAST,
    UP_SOUTH_WEST,
    DOWN_NORTH_WEST,
    DOWN_NORTH_EAST,
    DOWN_SOUTH_EAST,
    DOWN_SOUTH_WEST,
    UP,
    DOWN;

    private static Vector3i[] directionVectors;
    private static final Map<Vector3i, JPSDirection> vectorToDirection = Maps.newHashMap();
    private static final List<Vector3i>[] componentPermutations = new List[JPSDirection.values().length];
    private static final List<Vector3i>[] potentialForcedNeighborsCache = new List[JPSDirection.values().length];
    private static final List<Vector3i>[] keyNodesCache = new List[JPSDirection.values().length];

    public static JPSDirection fromVector(Vector3i vec) {
        if (directionVectors == null) {
            bakeDirectionVectors();
        }
        return vectorToDirection.get(vec);
    }

    public static List<Vector3i> valuesAsVectors() {
        List<Vector3i> result = Lists.newArrayList();
        for (JPSDirection dir : values()) {
            result.add(dir.getVector());
        }
        return result;
    }

    private static void bakeDirectionVectors() {
        directionVectors = new Vector3i[JPSDirection.values().length];
        for (JPSDirection dir : JPSDirection.values()) {
            String[] parts = dir.name().split("_");
            Vector3i vector = new Vector3i();
            for (String part : parts) {
                switch (part) {
                    case "EAST":
                        vector.x = 1;
                        break;
                    case "WEST":
                        vector.x = -1;
                        break;
                    case "UP":
                        vector.y = 1;
                        break;
                    case "DOWN":
                        vector.y = -1;
                        break;
                    case "NORTH":
                        vector.z = 1;
                        break;
                    case "SOUTH":
                        vector.z = -1;
                        break;
                }
            }

            directionVectors[dir.ordinal()] = vector;
            vectorToDirection.put(vector, dir);
        }
    }

    public static List<Vector3i> vectorsAdjacentTo(Vector3i parentDelta) {
        List<Vector3i> result = Lists.newArrayList();
        for (Vector3i vec : valuesAsVectors()) {
            Vector3i delta = new Vector3i(parentDelta).sub(vec);
            delta.absolute();
            if (Math.max(Math.max(delta.x, delta.y), delta.z) <= 1) {
                result.add(vec);
            }
        }
        return result;
    }

    public Vector3i getVector() {
        if (directionVectors == null) {
            bakeDirectionVectors();
        }
        return new Vector3i(directionVectors[this.ordinal()]);
    }

    public List<Vector3i> getComponentPermutations() {
        List<Vector3i> permutations = componentPermutations[ordinal()];
        if (permutations == null) {
            componentPermutations[ordinal()] = Lists.newArrayList();
            permutations = componentPermutations[ordinal()];

            Vector3i vec = getVector();
            permutations.add(new Vector3i(0, 0, 0));
            permutations.add(new Vector3i(vec.x, 0, 0));
            permutations.add(new Vector3i(0, vec.y, 0));
            permutations.add(new Vector3i(vec.x, vec.y, 0));
            permutations.add(new Vector3i(0, 0, vec.z));
            permutations.add(new Vector3i(vec.x, 0, vec.z));
            permutations.add(new Vector3i(0, vec.y, vec.z));
            permutations.add(new Vector3i(vec.x, vec.y, vec.z));

            // remove duplicates
            Set<Vector3i> tmp = Sets.newHashSet(permutations);
            permutations.clear();
            permutations.addAll(tmp);

            // remove zero vectors
            while (permutations.contains(Vector3i.zero())) {
                permutations.remove(Vector3i.zero());
            }

            // sort the result by manhatten distance
            permutations.sort(new Comparator<Vector3i>() {
                @Override
                public int compare(Vector3i o1, Vector3i o2) {
                    int m1 = manhatten(o1);
                    int m2 = manhatten(o2);
                    if (m1 < m2) {
                        return -1;
                    }

                    if (m1 == m2) {
                        return 0;
                    }

                    return 1;
                }
            });
        }
        return Lists.newArrayList(permutations);
    }

    public List<Vector3i> getKeyNodes() {
        if (keyNodesCache[ordinal()] == null) {
            Vector3i parentDelta = getVector().mul(-1);
            List<Vector3i> keyNodes = JPSDirection.vectorsAdjacentTo(parentDelta);
            keyNodes.remove(parentDelta);
            keyNodesCache[ordinal()] = keyNodes;
        }
        return Lists.newArrayList(keyNodesCache[ordinal()]);
    }

    public List<Vector3i> getPotentialForcedNeighbors() {
        if (potentialForcedNeighborsCache[ordinal()] == null) {
            Vector3i parentDelta = getVector().mul(-1);
            List<Vector3i> potentialForcedNeighbors = JPSDirection.valuesAsVectors();
//            potentialForcedNeighbors.removeAll(getComponentPermutations());
            potentialForcedNeighbors.removeAll(JPSDirection.vectorsAdjacentTo(parentDelta));
            potentialForcedNeighborsCache[ordinal()] = potentialForcedNeighbors;
        }
        return Lists.newArrayList(potentialForcedNeighborsCache[ordinal()]);
    }

    private int manhatten(Vector3i vec) {
        return Math.abs(vec.x) + Math.abs(vec.y) + Math.abs(vec.z);
    }

    public List<JPSDirection> getPerpendicularDirections() {
        List<JPSDirection> result = Lists.newArrayList();
        Vector3i a = directionVectors[this.ordinal()];
        for (JPSDirection dir : JPSDirection.values()) {
            if (this == dir) {
                continue;
            }
            Vector3i b = dir.getVector();
            if (b.add(a) != Vector3i.zero()) {
                result.add(dir);
            }
        }
        return result;
    }
}
