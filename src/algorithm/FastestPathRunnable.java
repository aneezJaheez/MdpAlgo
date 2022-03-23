package algorithm;

import environment.Map;
import environment.Obstacle;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Used for multi-threading the different path calculations
 */
public class FastestPathRunnable implements Runnable {
    private double cost;
    private int[] order;
    private Map arena;

    public FastestPathRunnable(int[] order, Map arena) {
        this.order = order;
        this.arena = arena;
    }

    /**
     * Do the fastest path calculation for the given permutation
     */
    public void run() {
        ArrayList<Obstacle> list = Map.getObstacles();
        InterPathPlanner algo = new InterPathPlanner(arena);
        int[] indexArray = IntStream.range(0, list.size()).toArray();
        try {
            PathCost pathing = new PathCost();
            cost = pathing.getPathCost(order, list, algo, arena);
        } catch (Exception e) {
            // Throwing an exception
            e.printStackTrace();
            //System.out.println("Exception is caught");
        }
    }

    public double getCost() {
        return cost;
    }

    public int[] getPath() {
        return order;
    }
}
