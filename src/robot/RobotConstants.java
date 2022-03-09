package robot;

import map.MapConstants;

import java.awt.*;

public class RobotConstants {
    public static final int ROBOT_VIRTUAL_WIDTH = 30;
    public static final Point ROBOT_INITIAL_CENTER_COORDINATES = new Point(1 + MapConstants.ARENA_BORDER_SIZE, 18 + MapConstants.ARENA_BORDER_SIZE);
    //
    public static final int MOVE_COST = 10;
    public static final int REVERSE_COST = 10;
    public static final int TURN_COST_90 = 60;
    public static final int MAX_COST = Integer.MAX_VALUE;

//    public static final double LEFT_TURN_RADIUS_Y = 15; //17;
//    public static final double LEFT_TURN_RADIUS_X = 28; //28;
//    public static final double RIGHT_TURN_RADIUS_Y = 15; //17;
//    public static final double RIGHT_TURN_RADIUS_X = 28; //28;

    //inside the lab
    public static final double LEFT_TURN_RADIUS_Y = 30;//25; //17;
    public static final double LEFT_TURN_RADIUS_X = 30;//32; //28;
    public static final double RIGHT_TURN_RADIUS_Y = 30; //25; //17;
    public static final double RIGHT_TURN_RADIUS_X = 30;//35; //28;

    //outside the lab
//    public static final double LEFT_TURN_RADIUS_Y = 30;//25; //17;
//    public static final double LEFT_TURN_RADIUS_X = 31;//32; //28;
//    public static final double RIGHT_TURN_RADIUS_Y = 29; //25; //17;
//    public static final double RIGHT_TURN_RADIUS_X = 30;//35; //28;

    public static final double MOVE_SPEED = 21; // in cm per second

    public enum ROBOT_DIRECTION {
        NORTH, EAST, SOUTH, WEST;
    }
}
