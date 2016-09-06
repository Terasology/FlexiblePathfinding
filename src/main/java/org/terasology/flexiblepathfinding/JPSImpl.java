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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.AABB;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author kaen
 *
 * An implementation of the algorithm presented in http://users.cecs.anu.edu.au/~dharabor/data/papers/harabor-grastien-aaai11.pdf
 *
 * The implementation is adapted to 3D but is constructed with references to the original paper.
 */
public class JPSImpl implements JPS {
    private WorldProvider world;
    private Logger logger = LoggerFactory.getLogger(JPSImpl.class);
    private List<Vector3i> path = Lists.newArrayList();
    private Map<Vector3i, JPSJumpPoint> points = Maps.newHashMap();
    private JPSConfig config;

    private List<JPSJumpPoint> open = Lists.newArrayList();
    private List<JPSJumpPoint> toOpen = Lists.newArrayList();

    private LoadingCache<VectorPair, Boolean> reachabilityCache;
    private TimeLimiter timeLimiter;

    private static boolean statsEnabled = false;
    private static ArrayBlockingQueue<PathStats> stats = Queues.newArrayBlockingQueue(1000);
    private double startMillis;
    private JPSJumpPoint start;
    private JPSJumpPoint goal;

    public JPSImpl(JPSConfig config) {
        this.config = config;

        if(config.executor != null) {
            this.timeLimiter = new SimpleTimeLimiter(config.executor);
        }
    }

    public static void setStatsEnabled(boolean statsEnabled) {
        JPSImpl.statsEnabled = statsEnabled;
    }

    public void start() {
    }

    public boolean run() throws InterruptedException {
        startMillis = System.currentTimeMillis();
        if(config.maxTime == 0) {
            return false;
        }
        Callable<Boolean> callable = () -> performSearch();
        boolean result = false;

        if(this.timeLimiter != null) {
            try {
                result = timeLimiter.callWithTimeout(callable, (long) (config.maxTime * 1000.0f), TimeUnit.MILLISECONDS, true);
            } catch(Exception e) {
                LoggerFactory.getLogger(this.getClass()).warn(e.toString());
            }
            recordStats();
            return result;
        }

        result = performSearch();
        recordStats();
        return result;
    }

    private void recordStats() {
        if(!statsEnabled) {
            return;
        }

        if(stats.remainingCapacity() < 10) {
            stats.remove();
        }

        PathStats stat = new PathStats();
        stat.success = path.size() > 0;
        stat.cost = goal.getCost();
        stat.size = path.size();
        stat.time = System.currentTimeMillis() - startMillis;
        stats.add(stat);
    }

    public static String getStats() {
        String result = "";

        Histogram successTime = new Histogram();
        Histogram failTime = new Histogram();
        Histogram size = new Histogram();
        Histogram cost = new Histogram();

        Collection<PathStats> successes = stats.stream().filter(stat -> stat.success).collect(Collectors.toList());
        Collection<PathStats> failures = stats.stream().filter(stat -> !stat.success).collect(Collectors.toList());

        successTime.build(successes, pathStats -> pathStats.time);
        failTime.build(failures, pathStats -> pathStats.time);
        size.build(stats, pathStats -> pathStats.size);
        cost.build(stats, pathStats -> pathStats.cost);

        result = String.format("total: %d\nsuccess: %d\nfail: %d\n", stats.size(), successes.size(), failures.size());
        return result + successTime.toString() + failTime.toString() + size.toString() + cost.toString();
    }

    private static class Histogram {
        int data[] = new int[10];
        double min = 0;
        double max = 0;
        double bucketMax = 0;
        double bucketSize = 0;
        public <T> void build(Collection<T> source, Function<T,Double> fn) {
            min = source.stream().map(fn).min((o1, o2) -> Double.compare(o1, o2)).get();
            max = source.stream().map(fn).max((o1, o2) -> Double.compare(o1, o2)).get();
            bucketSize = (max - min) / (data.length - 1);
            for(T el : source) {
                double val = fn.apply(el);
                int bucket = (int) Math.floor((val - min) / bucketSize);
                data[bucket]++;
                bucketMax = Math.max(bucketMax, data[bucket]);
            }
        }

        @Override
        public String toString() {
            String result = "";

            double scale = 20.0 / bucketMax;
            for(int i = 0; i < data.length; i++) {
                String line = String.format("% 7.1f (% 4d) |", min + bucketSize*(i+1), data[i]);
                for(int j = 0; j < data[i]*scale; j++) {
                    line += "#";
                }
                result += line + "\n";
            }
            result += "\n";
            return result;
        }
    }

    @Override
    public boolean performSearch() throws InterruptedException {
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

        if(!config.plugin.isWalkable(goal.getPosition())) {
            return false;
        }

        // let's not waste time ...
        if(start == goal || (config .useLineOfSight && config.plugin.inSight(start.getPosition(), goal.getPosition()))) {
            path.clear();
            path.add(start.getPosition());
            path.add(goal.getPosition());
            return true;
        }

        open.add(start);
        while(open.size() > 0) {
            JPSJumpPoint point = open.remove(open.size()-1);
            open.addAll(identifySuccessors(point, start, goal));
            if(Thread.interrupted()) {
                throw new InterruptedException();
            }

            if(goal.getParent() != null) {
                break;
            }

            open.sort(new Comparator<JPSJumpPoint>() {
                @Override
                public int compare(JPSJumpPoint o1, JPSJumpPoint o2) {
                    if(o1.getHeurisitic() > o2.getHeurisitic()) {
                        return -1;
                    }

                    if(o1.getHeurisitic() == o2.getHeurisitic()) {
                        return 0;
                    }
                    return 1;
                }
            });
        }

        if(goal.getParent() == null) {
            return false;
        }

//        if(identifySuccessors(null, start, start, goal)) {
        path.clear();
        path.add(goal.getPosition());
        JPSJumpPoint parent = goal.getParent();
        while(parent != null) {
            path.add(0, parent.getPosition());
            parent = parent.getParent();
        }
//        debug(start, goal);
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

    private List<JPSJumpPoint> identifySuccessors(JPSJumpPoint current, JPSJumpPoint start, JPSJumpPoint goal) throws InterruptedException {
//        debug(start, goal);
        List<JPSJumpPoint> result = Lists.newArrayList();
        Map<JPSDirection, JPSJumpPoint> prunedNeighbors = prune(current.getParentDirection(), current);

//        logger.info("" + prunedNeighbors.size());
        for(Map.Entry<JPSDirection, JPSJumpPoint> neighbor : prunedNeighbors.entrySet()) {
//            logger.info(neighbor.getKey().name());
            double dist = current.getPosition().distance(neighbor.getValue().getPosition());

            // don't explore a neighbor that has been more optimally explored
            if(neighbor.getValue() == start || (neighbor.getValue().getParent() != null && neighbor.getValue().getCost() < current.getCost() + dist)) {
                continue;
            }
            JPSJumpPoint jumpedNeighbor = jump(current.getPosition(), neighbor.getKey(), start, goal);

            // updates parent if this is optimal path so far
            current.setSuccessor(neighbor.getKey(), jumpedNeighbor);

            // not parent means not optimal path, and we don't have to explore
            if(jumpedNeighbor != null) { // && jumpedNeighbor.getParent() == current) {
                jumpedNeighbor.setHeurisitic(goal.getPosition().distance(jumpedNeighbor.getPosition()));
                result.add(jumpedNeighbor);
            }
        }

//        logger.info("successors " + current.getPosition().toString() + ": " + result.size());

        return result;
    }

    private Map<JPSDirection, JPSJumpPoint> getNeighbors(JPSJumpPoint point) {
        Map<JPSDirection, JPSJumpPoint> result = Maps.newHashMap();
        // TODO we don't even need a jump point here
        for(JPSDirection dir : JPSDirection.values()) {
            Vector3i neighborPos = new Vector3i(point.getPosition()).add(dir.getVector());

            if(isReachable(neighborPos, point.getPosition())) {
                result.put(dir, getJumpPoint(neighborPos));
            }
        }
        return result;
    }

    private JPSJumpPoint getJumpPoint(Vector3i pos) {
        JPSJumpPoint result = points.get(pos);
        if(result == null) {
            result = new JPSJumpPoint(pos);
            points.put(pos, result);

        }
        return result;
    }

    private List<Vector3i> findForcedNeighbors(Vector3i parent, Vector3i current) {
        Vector3i keyPos = new Vector3i();
        Vector3i neighborPos = new Vector3i();

        Vector3i parentDelta = new Vector3i(parent).sub(current);
        JPSDirection dir = JPSDirection.fromVector(new Vector3i(current).sub(parent));

        List<Vector3i> potentialForcedNeighbors = dir.getPotentialForcedNeighbors();
        List<Vector3i> keyNodes = dir.getKeyNodes();

        Set<Vector3i> prunedNeighbors = Sets.newHashSet();
        for(Vector3i potentialForcedNeighbor : potentialForcedNeighbors) {
            neighborPos.set(current).add(potentialForcedNeighbor);
            if(!isReachable(neighborPos, current)) {
                prunedNeighbors.add(potentialForcedNeighbor);
            }
        }

        for(Vector3i keyNode : keyNodes) {
            neighborPos.set(current).add(keyNode);

//            potentialForcedNeighbors.removeAll(prunedNeighbors);
//            prunedNeighbors.clear();

            boolean parentToKey = isReachable(keyPos, parent);
            if(!parentToKey) {
                continue;
            }

            for(Vector3i potentialForcedNeighbor : potentialForcedNeighbors) {
                if(prunedNeighbors.contains(potentialForcedNeighbor)) {
                    continue;
                }

                neighborPos.set(current).add(potentialForcedNeighbor);

                boolean keyToNeighbor = isReachable(neighborPos, keyPos);

                if(keyToNeighbor) {

                    double parentToKeyDistance = parentDelta.distance(keyNode);
                    double keyToNeighborDistance = keyNode.distance(potentialForcedNeighbor);
                    double parentToCurrentDistance = parentDelta.length();
                    double currentToNeighborDistance = potentialForcedNeighbor.length();

                    double keyDistance = parentToKeyDistance + keyToNeighborDistance;
                    double currentDistance = parentToCurrentDistance + currentToNeighborDistance;

                    double epsilon = 0.001;
                    boolean nearlyEqual = Math.abs(keyDistance - currentDistance) < epsilon;
                    if(currentDistance < keyDistance) {
                        continue;
                    }

                    if(nearlyEqual) {
                        if(Math.abs(parentToKeyDistance - parentToCurrentDistance) < epsilon) {
                            if(keyToNeighborDistance <= currentToNeighborDistance) {
                                continue;
                            }
                        } else if(parentToKeyDistance <= parentToCurrentDistance) {
                            continue;
                        }
                    }

                    prunedNeighbors.add(potentialForcedNeighbor);
                }
            }
        }
        return potentialForcedNeighbors;
    }

    private List<Vector3i> findNaturalNeighbors(Vector3i parent, Vector3i current) {
        return Lists.newArrayList(JPSDirection.fromVector(new Vector3i(current).sub(parent)).getComponentPermutations());
    }

    /*
     @param dir The direction from the parent to the current pos

     This implements a 3D version of the pruning rules mentioned in the paper.

     */
    private Map<JPSDirection, JPSJumpPoint> prune(JPSDirection dir, JPSJumpPoint current) {
        Map<JPSDirection, JPSJumpPoint> result = Maps.newHashMap();
        if(dir == null) {
            return getNeighbors(current);
        }

        Vector3i parentPos = current.getPosition().sub(dir.getVector());
        Vector3i currentPos = current.getPosition();
        Vector3i pos = new Vector3i();
        for(Vector3i vec : findNaturalNeighbors(parentPos, current.getPosition())) {
            pos.set(vec).add(currentPos);
            result.put(JPSDirection.fromVector(vec), getJumpPoint(pos));
        }

        for(Vector3i vec : findForcedNeighbors(parentPos, current.getPosition())) {
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
    10:        return n
    11: return null
     */
    private JPSJumpPoint jump(Vector3i current, JPSDirection dir, JPSJumpPoint start, JPSJumpPoint goal) {
        return jump(current, dir, start, goal, 0);
    }
    private JPSJumpPoint jump(Vector3i current, JPSDirection dir, JPSJumpPoint start, JPSJumpPoint goal, int level) {
        if(level >= config.maxDepth) {
            return null;
        }

        Vector3i neighbor = dir.getVector().add(current);

        if(neighbor.distanceSquared(goal.getPosition()) == 0) {
            return getJumpPoint(neighbor);
        }

        if(!isReachable(neighbor, current)) {
            return null;
        }

        List<Vector3i> forcedNeighbors = findForcedNeighbors(current, neighbor);
        if(forcedNeighbors.size() > 0) {
            return getJumpPoint(neighbor);
        }

        // the method provides the components sorted by manhatten length
        for(Vector3i vec : dir.getComponentPermutations()) {
            JPSJumpPoint result = jump(neighbor, JPSDirection.fromVector(vec), start, goal, level + 1);
            if(result != null) {
                if(vec.distanceSquared(dir.getVector()) == 0) {
                    return result;
                } else {
                    return getJumpPoint(neighbor);
                }
            }
        }

        return null;
    }

//    @Override
//    public String toString() {
//
//    }

    public List<Vector3i> getPath() {
        return path;
    }

    public void getPath(List<Vector3i> list) {
    }

    protected void expand(int current) {
    }

    public void debug(JPSJumpPoint start, JPSJumpPoint goal) {
        Map<Vector3i, String> result = Maps.newHashMap();
        result.put(start.getPosition(), "S");
        result.put(goal.getPosition(), "G");

        Vector3i min = goal.getPosition();
        min.min(start.getPosition());

        Vector3i max = goal.getPosition();
        max.max(start.getPosition());

        mark(result, start, min, max);

//        for(Vector3i v : checkedNodes) {
//            result.put(v, ".");
//            min.min(v);
//            max.max(v);
//        }

        int i = 0;
        for(Vector3i v : path) {
            if(i == 0) {
                result.put(v, "?");
            } else {
                result.put(v, "" + (char) ((int) '0' + i));
            }
            i++;
        }

        for(int y = min.y; y<= max.y; y++) {
            String out = "y level: " + y + "\n";
            for(int z = min.z; z <= max.z; z++) {
                for(int x = min.x; x <= max.x; x++) {
                    out += result.getOrDefault(new Vector3i(x,y,z)," ");
                }
                out += "\n";

            }
            out += "\n";
            logger.info(out);
        }
    }

    public void mark(Map<Vector3i, String> result, JPSJumpPoint point, Vector3i min, Vector3i max) {
        result.put(point.getPosition(), "J");
        min.min(point.getPosition());
        max.max(point.getPosition());
        for(JPSDirection dir : JPSDirection.values()) {
            JPSJumpPoint successor = point.getSuccessor(dir);
            if(successor != null) {
                mark(result, successor, min, max);
            }
        }
    }

    private boolean isReachable(Vector3i a, Vector3i b) {
        try {
            return reachabilityCache.get(new VectorPair(a, b));
        } catch (ExecutionException e) {
            logger.warn(e.toString());
        }
        return false;
//        return config.plugin.isReachable(a,b);
    }

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
            result = 31 * result + b.hashCode();
            return result;
        }
    }

    private static class PathStats {
        double time;
        double cost;
        double size;
        boolean success;
    }

//    protected float c(int from, int to) {
//        return graph.exactDistance(from, to);
//    }
//
//    protected float h(int current) {
//        return graph.fastDistance(current, end);
//    }
//
//    public float getG(int id) {
//        return gMap[id];
//    }
//
//    public float getF(int id) {
//        return fMap[id];
//    }
//
//    public int getP(int id) {
//        return pMap[id];
//    }
}

