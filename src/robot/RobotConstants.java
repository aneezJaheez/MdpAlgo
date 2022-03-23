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

    //inside the lab
    public static final double LEFT_TURN_RADIUS_Y = 28; //17;
    public static final double LEFT_TURN_RADIUS_X = 24; //28;
    public static final double RIGHT_TURN_RADIUS_Y = 30; //17;
    public static final double RIGHT_TURN_RADIUS_X = 27; //28;

    //outside the lab 2
//    public static final double LEFT_TURN_RADIUS_Y = 29; //29;//25; //17;
//    public static final double LEFT_TURN_RADIUS_X = 27; //27;//30;//32; //28;
//    public static final double RIGHT_TURN_RADIUS_Y = 27; //29; //25; //17;
//    public static final double RIGHT_TURN_RADIUS_X = 20; //18;//35; //28;

    //outside the lab 1
//    public static final double LEFT_TURN_RADIUS_Y = 30;//30//25; //17;
//    public static final double LEFT_TURN_RADIUS_X = 28;//32; //28;
//    public static final double RIGHT_TURN_RADIUS_Y = 28; //25; //17;
//    public static final double RIGHT_TURN_RADIUS_X = 21;//18;//35; //28;

    public static final double MOVE_SPEED = 21; // in cm per second

    public enum ROBOT_DIRECTION {
        NORTH, EAST, SOUTH, WEST;
    }
}
