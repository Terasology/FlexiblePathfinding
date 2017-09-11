# FlexiblePathfinding

A plugin-based JPS pathfinder for Terasology.

See also [FlexibleMovement](https://github.com/kaen/FlexibleMovement)

# Usage

You probably want to use the nodes in `FlexibleMovement` directly.

However, if you're not actually moving anything then you can try the `PathfinderSystem#requestPath` method.

You can see the basic usage example in the
[unit test helper](https://github.com/kaen/FlexiblePathfinding/blob/master/src/test/java/org/terasology/flexiblepathfinding/JPSTestHelper.java#L83-L99)

# Hacking

Check out the unit tests. You can add a new movement type by writing a new JPSPlugin.