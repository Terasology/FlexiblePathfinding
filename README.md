# FlexiblePathfinding

A plugin-based JPS pathfinder for Terasology.

This is a clone of the [original repository by Kaen](https://github.com/kaen/FlexiblePathfinding).

See also [FlexibleMovement](https://github.com/Terasology/FlexibleMovement).

# Usage

You probably want to use the nodes in `FlexibleMovement` directly.

However, if you're not actually moving anything then you can try the `PathfinderSystem#requestPath` method.

You can see the basic usage example in the
[unit test helper](https://github.com/kaen/FlexiblePathfinding/blob/master/src/test/java/org/terasology/flexiblepathfinding/helpers/JPSTestHelper.java#L99-L116)

# Hacking

Check out the unit tests. You can add a new movement type by writing a new JPSPlugin.

# Debugging

This module comes with some limited debugging GUIs. By default, they're bound to F7. This and some other debug
 bindings are configurable from the Input settings menu).

Because of the overhead required to record, transmit, and analyze path finding results you'll need to enable metrics
recording from the same menu.

A log of the most recently finished path finding requests is available from the menu.

These tools don't replace the traditional logging and profiling tools used with Java. Rather, they compile and
 present data with some context of FPF to easily get an overview of potential performance issues.
 
 ## The Debug HUD
 
 With metrics recording and the debug HUD enabled, several histograms and graphs will be rendered in-game. These
  histograms plot the distribution of several properties of the last 1000 path finding requests:
 
   - Success/Failure Times: execution time of the request in (wall clock) milliseconds
   - Costs: The cost of the final path of successful requests as calculated by the JPSPlugin
   - Sizes: The size of the final path of successful requests
   - Depths: The maximum depth of moves explored
   - Explored: The total number of moves explored
 
 Some graphs are also present:
 
   - Success Rate: The portion (from 0.0-1.0) of requests that succeeded from the last 1000
   - Throughput: The number of requests finished since the last metrics update
   - Pending: The number of outstanding requests that haven't issued their callback yet