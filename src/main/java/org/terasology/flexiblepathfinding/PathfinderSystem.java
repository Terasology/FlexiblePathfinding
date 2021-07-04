// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.GameScheduler;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.console.commandSystem.annotations.Command;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.debug.PathMetricsRequestEvent;
import org.terasology.flexiblepathfinding.debug.PathMetricsResponseEvent;
import org.terasology.flexiblepathfinding.metrics.Histogram;
import org.terasology.flexiblepathfinding.metrics.PathMetric;
import org.terasology.flexiblepathfinding.metrics.PathMetricsRecorder;
import reactor.core.publisher.Mono;

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
 * Here we also listen for world changes (OnChunkReady and OnBlockChanged). Currently, both events reset the
 * pathfinder (clear path cache) and rebuild the modified chunk.
 * </p>
 * Chunk updates are processed before any pathfinding request. However, this system does not inform about
 * paths getting invalid.
 *
 * @author synopia
 */
@RegisterSystem(RegisterMode.AUTHORITY)
@Share(value = PathfinderSystem.class)
public class PathfinderSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(PathfinderSystem.class);

    private int nextId;
    private Set<EntityRef> entitiesWithPendingTasks = Sets.newHashSet();


    @In
    private WorldProvider world;

    @Override
    public void initialise() {
        logger.info("PathfinderSystem started");
    }

    @Override
    public void shutdown() {
    }

    public int requestPath(EntityRef requester, Vector3i target, List<Vector3i> start) {
        return requestPath(requester, target, start, null);
    }

    public int requestPath(EntityRef requester, Vector3i target, List<Vector3i> start, PathfinderCallback callback) {
        JPSConfig config = new JPSConfig(start.get(0), target);
        config.requester = requester;
        return requestPath(config, callback);
    }

    private void processPath(ConfigWrapper wrapper) {
        JPSImpl jps = new JPSImpl(wrapper.config);
        List<Vector3i> path = Lists.newArrayList();
        try {
            if (jps.run()) {
                path = jps.getPath();
            }
        } catch (InterruptedException e) {
            // do nothing
        }
        this.completePathFor(wrapper.config.requester);
        wrapper.callback.pathReady(path, new Vector3i()); // unusued :??
    }

    public int requestPath(JPSConfig config, PathfinderCallback callback) {
        if (config.requester != null && config.requester.exists()) {
            if (entitiesWithPendingTasks.contains(config.requester)) {
                return -1;
            }
            entitiesWithPendingTasks.add(config.requester);
        }
        Mono.just(new ConfigWrapper(callback, config))
            .subscribeOn(GameScheduler.boundedElastic())
            .subscribe(this::processPath);
        return nextId++;
    }

    private static class ConfigWrapper {
        PathfinderCallback callback;
        JPSConfig config;

        public ConfigWrapper(PathfinderCallback callback, JPSConfig config) {
            this.callback = callback;
            this.config = config;
        }

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
