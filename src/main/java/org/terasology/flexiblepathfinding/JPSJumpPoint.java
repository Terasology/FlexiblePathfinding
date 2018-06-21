/*
 * Copyright 2018 MovingBlocks
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

import org.terasology.math.geom.Vector3i;

public class JPSJumpPoint {

    private JPSJumpPoint parent;

    public JPSDirection getParentDirection() {
        return parentDirection;
    }

    public void setParentDirection(JPSDirection parentDirection) {
        this.parentDirection = parentDirection;
    }

    // direction of travel from the parent to this point
    private JPSDirection parentDirection;
    private double cost = 0;
    private double heurisitic = 0;
    private Vector3i position;
    private JPSJumpPoint successors[] = new JPSJumpPoint[JPSDirection.values().length];

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

    public JPSJumpPoint(Vector3i position) {
        this.position = position;
    }

    public JPSJumpPoint(Vector3i position, JPSDirection parentDirection) {
        this.parentDirection = parentDirection;
        this.position = position;
    }

    public void setSuccessor(JPSDirection dir, JPSJumpPoint successor) {
        successors[dir.ordinal()] = successor;
        if(successor != null) {
            double dist = successor.getPosition().distance(getPosition());
            if(successor.getParent() == null || successor.getCost() > getCost() + dist) {
                successor.setCost(getCost() + dist);
                successor.setParent(this);
                successor.setParentDirection(dir);
            }
        }
    }

    public JPSJumpPoint getSuccessor(JPSDirection dir) {
        return successors[dir.ordinal()];
    }

    public Vector3i getPosition() {
        return new Vector3i(position);
    }

    public void setPosition(Vector3i position) {
        this.position.set(position);
    }

}
