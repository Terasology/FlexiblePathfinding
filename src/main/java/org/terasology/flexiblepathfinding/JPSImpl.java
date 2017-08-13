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
package org.terasology.flexiblepathfinding;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author kaen
 *         <p>
 *         An implementation of the algorithm presented in http://users.cecs.anu.edu.au/~dharabor/data/papers/harabor-grastien-aaai11.pdf
 *         <p>
 *         The implementation is adapted to 3D but is constructed with references to the original paper.
 */
public class JPSImpl implements JPS {
    private static boolean statsEnabled = false;

    private Logger logger = LoggerFactory.getLogger(JPSImpl.class);
    private JPSConfig config;
    private Map<Vector3i, JPSJumpPoint> points = Maps.newHashMap();
    private List<Vector3i> path = Lists.newArrayList();
    private List<JPSJumpPoint> open = Lists.newArrayList();
    private JPSJumpPoint start;
    private JPSJumpPoint goal;
    private double startMillis;

    // some helper classes from Guava
    private LoadingCache<VectorPair, Boolean> reachabilityCache;
    private TimeLimiter timeLimiter;

    public JPSImpl(JPSConfig config) {
        this.config = config;

        if (config.executor != null) {
            this.timeLimiter = new SimpleTimeLimiter(config.executor);
        }
    }

    public static void setStatsEnabled(boolean statsEnabled) {
        JPSImpl.statsEnabled = statsEnabled;
    }

    /**
     * Performs the search using a {@link TimeLimiter} if `config.executor` was set. In either case, blocks
     * synchronously until the search completes or fails.
     */
    public boolean run() throws InterruptedException {
        startMillis = System.currentTimeMillis();

        Callable<Boolean> callable = () -> performSearch();
        boolean result = false;

        if (this.timeLimiter != null) {
            try {
//                Thread.currentThread().setPriority(Thread.MIN_PRIORITY+1);
                result = timeLimiter.callWithTimeout(callable, (long) (config.maxTime * 1000.0f), TimeUnit.MILLISECONDS, true);
            } catch (Exception e) {
                LoggerFactory.getLogger(this.getClass()).warn(e.toString());
            }
            recordMetrics();
            return result;
        }

        result = performSearch();
        recordMetrics();
        return result;
    }

    @Override
    public boolean performSearch() throws InterruptedException {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        reachabilityCache = CacheBuilder.newBuilder()
                .maximumSize(100000)
                .build(new CacheLoader<VectorPair, Boolean>() {
                    public Boolean load(VectorPair key) {
                        return config.plugin.isReachable(key.a, key.b);
                    }
                });

        points.clear();
        start = getJumpPoint(config.start);
        goal = getJumpPoint(config.stop);
        logger.debug("Starting JPS search: {} -> {}", start.getPosition(), goal.getPosition());

        if (start == goal || (config.useLineOfSight && config.plugin.inSight(start.getPosition(), goal.getPosition()))) {
            path.clear();
            path.add(start.getPosition());
            path.add(goal.getPosition());
            logger.debug("Start and goal are within line of sight");
            return true;
        }

        open.add(start);
        while (open.size() > 0) {
            logger.debug("Starting open list loop, open list size: {}", open.size());
            JPSJumpPoint point = open.remove(open.size() - 1);
            open.addAll(identifySuccessors(point));

            if (goal.getParent() != null) {
                logger.debug("Goal position has a parent, breaking open loop");
                break;
            }

            open.sort(new Comparator<JPSJumpPoint>() {
                @Override
                public int compare(JPSJumpPoint o1, JPSJumpPoint o2) {
                    if (o1.getHeurisitic() > o2.getHeurisitic()) {
                        return -1;
                    }

                    if (o1.getHeurisitic() == o2.getHeurisitic()) {
                        return 0;
                    }
                    return 1;
                }
            });
        }

        if (goal.getParent() == null) {
            logger.debug("Goal position has no parent after open list loop. Failure.");
            return false;
        }

        path.clear();
        path.add(goal.getPosition());
        JPSJumpPoint parent = goal.getParent();
        while (parent != null) {
            path.add(0, parent.getPosition());
            parent = parent.getParent();
        }
        logger.debug("Found path: {}", path);
        return true;
    }

    //  Algorithm 1 Identify Successors
    //  Require: x: current node, s: start, g: goal
    // 1: successors(x) ← ∅
    // 2: neighbours(x) ← prune(x, neighbours(x))
    // 3: for all n ∈ neighbours(x) do
    // 4:     n ← jump(x, direction(x, n), s, g)
    // 5:     add n to successors(x)
    // 6: return successors(x)
    private List<JPSJumpPoint> identifySuccessors(JPSJumpPoint current) throws InterruptedException {
        List<JPSJumpPoint> result = Lists.newArrayList();
        Map<JPSDirection, JPSJumpPoint> prunedNeighbors = prune(current.getParentDirection(), current);

        for (Map.Entry<JPSDirection, JPSJumpPoint> neighbor : prunedNeighbors.entrySet()) {
            double dist = current.getPosition().distance(neighbor.getValue().getPosition());

            // don't explore a neighbor that has been more optimally explored
            if (neighbor.getValue() == start || (neighbor.getValue().getParent() != null && neighbor.getValue().getCost() < current.getCost() + dist)) {
                continue;
            }
            JPSJumpPoint jumpedNeighbor = jump(current.getPosition(), neighbor.getKey(), start, goal);

            // updates parent if this is optimal path so far
            current.setSuccessor(neighbor.getKey(), jumpedNeighbor);

            if(null != jumpedNeighbor &&
                    jumpedNeighbor.getPosition().distanceSquared(goal.getPosition()) <= config.goalDistance * config.goalDistance
                    ) {
                goal = jumpedNeighbor;
                return Lists.newArrayList(jumpedNeighbor);
            }

            // not parent means not optimal path, and we don't have to explore
            if (jumpedNeighbor != null) { // && jumpedNeighbor.getParent() == current) {
                jumpedNeighbor.setHeurisitic(goal.getPosition().distance(jumpedNeighbor.getPosition()));
                result.add(jumpedNeighbor);
            }
        }
        return result;
    }

    private Map<JPSDirection, JPSJumpPoint> getNeighbors(JPSJumpPoint point) {
        Map<JPSDirection, JPSJumpPoint> result = Maps.newHashMap();
        // TODO we don't even need a jump point here
        for (JPSDirection dir : JPSDirection.values()) {
            Vector3i neighborPos = new Vector3i(point.getPosition()).add(dir.getVector());

            if (isReachable(neighborPos, point.getPosition())) {
                result.put(dir, getJumpPoint(neighborPos));
            }
        }
        return result;
    }

    private JPSJumpPoint getJumpPoint(Vector3i pos) {
        JPSJumpPoint result = points.get(pos);
        if (result == null) {
            result = new JPSJumpPoint(pos);
            points.put(pos, result);

        }
        return result;
    }

    /**
     * Find forced neighbors as described in the paper. Here we essentially implement an exhaustive search of the 3x3x3
     * adjacency cube. We retreive some basic statically computable information (the "potential forced neighbors" and
     * "key nodes").
     * <p>
     * Briefly, key nodes are the nodes which determine whether there is an alternative optimal path to one or more
     * potential forced neighbors. By iterating over the key nodes and potential forced neighbors, and comparing the
     * "optimality" of paths through the adjacency cube, we can determine whether a given neighbor is actually "forced".
     *
     * Recall the general definition of a forced neighbor:
     *
     * Definition 1. A node n ∈ neighbours(x) is forced if:
     *  1. n is not a natural neighbour of x
     *  2. len( p(x), x, ni ) < len( p(x), ... , ni \ x )
     *
     * Also the specific definitions for straight and diagonal pruning rules:
     *
     *  Straight Moves: We prune any node n ∈ neighbours(x)
     *  which satisfies the following dominance constraint:
     *  len( p(x), ... , ni \ x ) ≤ len( p(x), x, ni )
     *
     *  Diagonal Moves: This case is similar to the pruning rules
     *  we developed for straight moves; the only difference is that
     *  the path which excludes x must be strictly dominant:
     *  len( p(x), ... , ni \ x ) < len( p(x), x, ni )
     *
     * "Strictly dominant" means that the path has diagonals before the path against which it is being compared. To
     * extend this into 3D, a path is strictly dominant if its cost is less than or equal to another's, and the
     * manhatten distance of each delta vector in the first path is greater than or equal to the corresponding delta
     * vector in the other path.
     *
     *
     * @param parent
     * @param current
     * @see JPSDirection
     * @return
     */
    private List<Vector3i> findForcedNeighbors(Vector3i parent, Vector3i current) {
        // reusable vectors
        Vector3i keyPos = new Vector3i();
        Vector3i neighborPos = new Vector3i();

        // constant throughout the loops below
        Vector3i parentDelta = new Vector3i(parent).sub(current);
        JPSDirection dir = JPSDirection.fromVector(new Vector3i(current).sub(parent));

        List<Vector3i> potentialForcedNeighbors = dir.getPotentialForcedNeighbors();
        List<Vector3i> keyNodes = dir.getKeyNodes();

        // we immediately prune any potential forced neighbors that are not reachable from the current block
        Set<Vector3i> prunedNeighbors = Sets.newHashSet();
        for (Vector3i potentialForcedNeighbor : potentialForcedNeighbors) {
            neighborPos.set(current).add(potentialForcedNeighbor);
            if (!isReachable(neighborPos, current)) {
                prunedNeighbors.add(potentialForcedNeighbor);
            }
        }

        for (Vector3i keyNode : keyNodes) {
            keyPos.set(current).add(keyNode);

            // not reachable means not optimal
            boolean parentToKey = isReachable(keyPos, parent);
            if (!parentToKey) {
                continue;
            }

            for (Vector3i potentialForcedNeighbor : potentialForcedNeighbors) {
                // rather than actually pruning the neighbors each iteration, we skip over them
                if (prunedNeighbors.contains(potentialForcedNeighbor)) {
                    continue;
                }

                neighborPos.set(current).add(potentialForcedNeighbor);

                boolean keyToNeighbor = isReachable(neighborPos, keyPos);

                // again unreachable means not optimal
                if (keyToNeighbor) {

                    // find the component distances of the paths P -> K -> N and P -> C -> N
                    double parentToKeyDistance = parentDelta.distance(keyNode);
                    double keyToNeighborDistance = keyNode.distance(potentialForcedNeighbor);
                    double parentToCurrentDistance = parentDelta.length();
                    double currentToNeighborDistance = potentialForcedNeighbor.length();

                    //  find the total distances
                    double keyDistance = parentToKeyDistance + keyToNeighborDistance;
                    double currentDistance = parentToCurrentDistance + currentToNeighborDistance;

                    // if the path through the current node is shorter, the neighbor is forced no matter what
                    double epsilon = 0.001;
                    boolean nearlyEqual = Math.abs(keyDistance - currentDistance) < epsilon;
                    if (currentDistance < keyDistance) {
                        continue;
                    }

                    // if the paths are equal in length, we compare the distance (and therefore the number of axes traveled)
                    // for each step. The first path with a higher distance step is deemed the optimal path, and if this
                    // is the current node then the neighbor is forced
                    if (nearlyEqual) {
                        if (Math.abs(parentToKeyDistance - parentToCurrentDistance) < epsilon) {
                            if (keyToNeighborDistance <= currentToNeighborDistance) {
                                continue;
                            }
                        } else if (parentToKeyDistance <= parentToCurrentDistance) {
                            continue;
                        }
                    }

                    // if we get here, the neighbor is not forced and we can prune it
                    prunedNeighbors.add(potentialForcedNeighbor);
                }
            }
        }
//        potentialForcedNeighbors.removeAll(prunedNeighbors);
        return potentialForcedNeighbors;
    }

    // proof is left as an excercise to the reader :)
    private List<Vector3i> findNaturalNeighbors(Vector3i parent, Vector3i current) {
        return Lists.newArrayList(JPSDirection.fromVector(new Vector3i(current).sub(parent)).getComponentPermutations());
    }

    /*
     @param dir The direction from the parent to the current pos

     This implements a 3D version of the pruning rules mentioned in the paper.

     */
    private Map<JPSDirection, JPSJumpPoint> prune(JPSDirection dir, JPSJumpPoint current) {
        Map<JPSDirection, JPSJumpPoint> result = Maps.newHashMap();
        // TODO: why does this work?
        if (true || dir == null) {
            return getNeighbors(current);
        }

        Vector3i parentPos = current.getPosition().sub(dir.getVector());
        Vector3i currentPos = current.getPosition();
        Vector3i pos = new Vector3i();
        for (Vector3i vec : findNaturalNeighbors(parentPos, current.getPosition())) {
            pos.set(vec).add(currentPos);
            result.put(JPSDirection.fromVector(vec), getJumpPoint(pos));
        }

        for (Vector3i vec : findForcedNeighbors(parentPos, current.getPosition())) {
            pos.set(vec).add(currentPos);
            result.put(JPSDirection.fromVector(vec), getJumpPoint(pos));
        }

        return result;
    }

    /*
    Algorithm 2 Function jump
    Require: x: initial node, ~d: direction, s: start, g: goal
    1: n ← step(x, ~d)
    2: if n is an obstacle or is outside the grid then
    3:     return null
    4: if n = g then
    5:     return n
    6: if ∃ n' ∈ neighbours(n) s.t. n' is forced then
    7:     return n
    8: if d is diagonal then
    9:     for all i ∈ {1, 2} do
    10:         if jump(n, ~di, s, g) is not null then
    11:             return n
    12: return jump(n, d, s, g)

    Adapted for N dimensions, we simply take lines 8-12, and compress them into:
    8: for all c component permutation vectors of d, ordered by manhatten(c)
    9:     if jump(n, c, s, g) is not null then
    10:        if c == d
    11:            return jump(n, c, s, g)
    12:        else
    13:            return n
    14: return null
     */
    private JPSJumpPoint jump(Vector3i current, JPSDirection dir, JPSJumpPoint start, JPSJumpPoint goal) throws InterruptedException {
        return jump(current, dir, start, goal, 0);
    }

    private JPSJumpPoint jump(Vector3i current, JPSDirection dir, JPSJumpPoint start, JPSJumpPoint goal, int level) throws InterruptedException {
        if (level >= config.maxDepth) {
            return null;
        }

        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        Vector3i neighbor = dir.getVector().add(current);

        // this is the goal (or close enough to it)
        if (neighbor.distanceSquared(goal.getPosition()) <= config.goalDistance * config.goalDistance) {
            goal = getJumpPoint(neighbor);
            return goal;
        }

        if (!isReachable(neighbor, current)) {
            return null;
        }

        List<Vector3i> forcedNeighbors = findForcedNeighbors(current, neighbor);
        if (forcedNeighbors.size() > 0) {
            return getJumpPoint(neighbor);
        }

        // this method provides the components sorted by manhatten length
        for (Vector3i vec : dir.getComponentPermutations()) {
            JPSJumpPoint result = jump(neighbor, JPSDirection.fromVector(vec), start, goal, level + 1);
            if (result != null) {
                if (vec.distanceSquared(dir.getVector()) == 0) {
                    return result;
                } else {
                    return getJumpPoint(neighbor);
                }
            }
        }

        return null;
    }

    public List<Vector3i> getPath() {
        return path;
    }

    private boolean isReachable(Vector3i a, Vector3i b) {
        try {
            return reachabilityCache.get(new VectorPair(a, b));
        } catch (ExecutionException e) {
            logger.warn(e.toString());
        }
        return false;
    }

    private void recordMetrics() {
        if (!statsEnabled) {
            return;
        }

        PathMetricsRecorder.PathMetric metric = new PathMetricsRecorder.PathMetric();
        metric.success = path.size() > 0;
        metric.cost = goal.getCost();
        metric.size = path.size();
        metric.time = System.currentTimeMillis() - startMillis;
        PathMetricsRecorder.recordMetrics(metric);
    }

    /**
     * Used as a cache key for `reachabilityCache`
     */
    private class VectorPair {
        Vector3i a;
        Vector3i b;

        public VectorPair(Vector3i a, Vector3i b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            return hashCode() == o.hashCode();
        }

        @Override
        public int hashCode() {
            int result = a.hashCode();
            result = (result << 16) + b.hashCode();
            return result;
        }
    }
}

