// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding;

import org.joml.Vector3i;
import org.joml.Vector3ic;

public class JPSJumpPoint {

    // direction of travel from the parent to this point
    private JPSDirection parentDirection;
    private double cost = 0;
    private double heurisitic = 0;
    private Vector3i position;
    private JPSJumpPoint successors[] = new JPSJumpPoint[JPSDirection.values().length];

    public JPSJumpPoint(Vector3i position) {
        this.position = position;
    }

    public JPSJumpPoint(Vector3i position, JPSDirection parentDirection) {
        this.parentDirection = parentDirection;
        this.position = position;
    }

    private JPSJumpPoint parent;

    public JPSDirection getParentDirection() {
        return parentDirection;
    }

    public void setParentDirection(JPSDirection parentDirection) {
        this.parentDirection = parentDirection;
    }

    public JPSJumpPoint getParent() {
        return parent;
    }

    public void setParent(JPSJumpPoint parent) {
        this.parent = parent;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getHeurisitic() {
        return heurisitic;
    }

    public void setHeurisitic(double heurisitic) {
        this.heurisitic = heurisitic;
    }

    public void setSuccessor(JPSDirection dir, JPSJumpPoint successor) {
        successors[dir.ordinal()] = successor;
        if (successor != null) {
            double dist = successor.getPosition().distance(getPosition());
            if (successor.getParent() == null || successor.getCost() > getCost() + dist) {
                successor.setCost(getCost() + dist);
                successor.setParent(this);
                successor.setParentDirection(dir);
            }
        }
    }

    public JPSJumpPoint getSuccessor(JPSDirection dir) {
        return successors[dir.ordinal()];
    }

    public Vector3ic getPosition() {
        return position;
    }

    public void setPosition(Vector3i position) {
        this.position.set(position);
    }
}
