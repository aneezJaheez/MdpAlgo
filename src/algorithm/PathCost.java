package algorithm;

import environment.Map;
import environment.Obstacle;
import robot.Robot;
import robot.RobotConstants;

import java.util.ArrayList;

/**
 * Class for runnable function when doing multi-threading exhaustive search
 */
public class PathCost {
    /**
     * Calculate the path cost for a given permutation
     */
    public double getPathCost(int[] path, ArrayList<Obstacle> list, InterPathPlanner algo, Map arena) {
        //double pathDistance = 0.0;
        Obstacle next;
        Robot bot = arena.getRobot();
        int startX = bot.getX();
        int startY = bot.getY();
        int startAngle = bot.getRobotDirectionAngle();
        double cost;
        algo.constructMap();
        for (int i : path) {
            next = list.get(i);
            algo.planPath(startX, startY, startAngle, next.getX(), next.getY(), next.getImadeDirectionAngle(), true, false, false);
            // do the reverse before finding the next path
            int[] coords = algo.getEndPosition();
            //bot.setCenterCoordinate(new Point(coords[0], coords[1]));
            //bot.setDirection(coords[2]);
            startX = coords[0];
            startY = coords[1];
            startAngle = coords[2];
        }
        cost = algo.getTotalCost();
        algo.clearCost();
        bot.setCenterCoordinate(RobotConstants.ROBOT_INITIAL_CENTER_COORDINATES);
        bot.setDirection(RobotConstants.ROBOT_DIRECTION.NORTH);
        return cost;
    }
}
