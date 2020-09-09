// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.console.commandSystem.annotations.Command;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.utilities.concurrency.TaskMaster;
import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.debug.PathMetricsRequestEvent;
import org.terasology.flexiblepathfinding.debug.PathMetricsResponseEvent;
import org.terasology.flexiblepathfinding.metrics.Histogram;
import org.terasology.flexiblepathfinding.metrics.PathMetric;
import org.terasology.flexiblepathfinding.metrics.PathMetricsRecorder;
import org.terasology.math.geom.Vector3i;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This systems helps finding a paths through the game world.
 * <p/>
 * Since paths finding takes some time, it completely runs in a background thread. So, a requested paths is not
 * available in the moment it is requested. Instead you need to listen for a PathReadyEvent.
 * <p/>
 * Here we also listen for world changes (OnChunkReady and OnBlockChanged). Currently, both events reset the pathfinder
 * (clear path cache) and rebuild the modified chunk.
 * </p>
 * Chunk updates are processed before any pathfinding request. However, this system does not inform about paths getting
 * invalid.
 *
 * @author synopia
 */
@RegisterSystem(RegisterMode.AUTHORITY)
@Share(value = PathfinderSystem.class)
public class PathfinderSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(PathfinderSystem.class);
    private final Set<EntityRef> entitiesWithPendingTasks = Sets.newHashSet();
    private final TaskMaster<PathfinderTask> workerTaskMaster = TaskMaster.createFIFOTaskMaster("PathfinderWorker", 4);
    private final TaskMaster<PathfinderTask> taskMaster = TaskMaster.createPriorityTaskMaster(
            "PathfinderQueue", 1, 1024
    );
    private int nextId;
    @In
    private WorldProvider world;

    @Override
    public void initialise() {
        logger.info("PathfinderSystem started");

        // TODO: HACK: We offer this taskmaster a shutdown task just to use its raw executor service directly
        workerTaskMaster.offer(new ShutdownTask(null, null, null));
    }

    @Override
    public void shutdown() {
        taskMaster.shutdown(new ShutdownTask(null, null, null), false);
        workerTaskMaster.shutdown(new ShutdownTask(null, null, null), false);
    }

    public int requestPath(EntityRef requester, Vector3i target, List<Vector3i> start) {
        return requestPath(requester, target, start, null);
    }

    public int requestPath(EntityRef requester, Vector3i target, List<Vector3i> start, PathfinderCallback callback) {
        JPSConfig config = new JPSConfig(start.get(0), target);
        config.requester = requester;
        return requestPath(config, callback);
    }

    public int requestPath(JPSConfig config, PathfinderCallback callback) {
        if (config.requester != null && config.requester.exists()) {
            if (entitiesWithPendingTasks.contains(config.requester)) {
                return -1;
            }
            entitiesWithPendingTasks.add(config.requester);
        }

        if (config.executor == null) {
            config.executor = workerTaskMaster.getExecutorService();
        }

        PathfinderTask task = new PathfinderTask(world, config, callback);
        taskMaster.offer(task);
        return nextId++;
    }

    public int requestPath(Vector3i start, Vector3i target, PathfinderCallback callback) {
        return requestPath(null, target, Lists.newArrayList(start), callback);
    }

    public void completePathFor(EntityRef requestor) {
        if (requestor == null) {
            return;
        }
        entitiesWithPendingTasks.remove(requestor);
    }

    @Command
    public void recordPathStats() {
        JPSImpl.setStatsEnabled(true);
    }

    @Command
    public void printPathStats() {
        logger.info(PathMetricsRecorder.getStats());
    }

    @ReceiveEvent
    public void onPathMetricsRequest(PathMetricsRequestEvent event, EntityRef entity) {
        PathMetricsResponseEvent response = new PathMetricsResponseEvent();
        Collection<PathMetric> metrics = PathMetricsRecorder.getPathMetrics();

        if (metrics.size() == 0) {
            return;
        }

        Histogram successTime = new Histogram();
        Histogram failTime = new Histogram();
        Histogram size = new Histogram();
        Histogram cost = new Histogram();
        Histogram depth = new Histogram();
        Histogram explored = new Histogram();

        Collection<PathMetric> successes = metrics.stream().filter(stat -> stat.success).collect(Collectors.toList());
        Collection<PathMetric> failures = metrics.stream().filter(stat -> !stat.success).collect(Collectors.toList());

        int buckets = 5;
        response.sizes = size.analyze(successes, pathMetric -> pathMetric.size, buckets);
        response.costs = cost.analyze(successes, pathMetric -> pathMetric.cost, buckets);
        response.depths = depth.analyze(successes, pathMetric -> pathMetric.maxDepth, buckets);
        response.explored = explored.analyze(successes, pathMetric -> pathMetric.nodesExplored, buckets);
        response.successTimes = successTime.analyze(successes, pathMetric -> pathMetric.time, buckets);
        response.failureTimes = failTime.analyze(failures, pathMetric -> pathMetric.time, buckets);


        world.getWorldEntity().send(response);
    }
}
