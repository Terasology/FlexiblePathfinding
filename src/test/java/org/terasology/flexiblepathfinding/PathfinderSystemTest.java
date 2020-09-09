// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import org.junit.Assert;
import org.junit.Test;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.internal.PojoEntityManager;
import org.terasology.math.geom.Vector3i;

public class PathfinderSystemTest {
    @Test
    public void exclusion() {
        PathfinderSystem pathfinderSystem = new PathfinderSystem();
        EntityManager entityManager = new PojoEntityManager();
        EntityRef entity = entityManager.create();

        JPSConfig config = new JPSConfig(Vector3i.zero(), Vector3i.north());
        config.requester = entity;

        Assert.assertEquals(pathfinderSystem.requestPath(config, null), 0);
        Assert.assertEquals(pathfinderSystem.requestPath(config, null), -1);
    }
}
