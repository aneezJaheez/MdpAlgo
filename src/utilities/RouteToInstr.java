package utilities;

import algorithm.*;
import environment.Map;
import environment.MapConstants;
import environment.Grid;
import environment.Obstacle;
import robot.Robot;
import robot.RobotConstants;
import utilities.CommunicationConstants.INSTRUCTION_TYPE;

import java.util.ArrayList;
import org.json.*;

public class RouteToInstr {
    static Robot bot = new Robot(RobotConstants.ROBOT_INITIAL_CENTER_COORDINATES, RobotConstants.ROBOT_DIRECTION.NORTH, false);
    static Map arena = new Map(bot);
    static CommMgr comm = CommMgr.getCommMgr();

    static PathPlanner fast = new PathPlanner(arena);
    static InterPathPlanner algo = new InterPathPlanner(arena);


    /**
     * main module to start the algo and establish communication with rpi
     * @param args
     */
    public static void main(String[] args) {
        comm.connectToRPi();

        // receive obstacles from android
        recvObstacles();
        int[] path = fast.planFastestPath();

        System.out.println(path.toString());
//
        doThePath(path);
        System.out.println("No more possible nodes to visit. Pathing finished");
        comm.endConnection();
    }

    /**
     * receive obstacle info from android, plan path, and send instructions to robot
     * @param path
     */
    private static void doThePath(int[] path) {
        algo.constructMap();
        ArrayList<Obstacle> map = Map.getObstacles();
        Robot r = arena.getRobot();
        int startX = r.getX();
        int startY = r.getY();
        int startAngle = r.getRobotDirectionAngle();
        Obstacle next;
        ArrayList<Movement> arrayList;
        int count = 0;
        for (int i : path) {
            next = map.get(i);
            System.out.println("---------------Path " + count + "---------------");
            System.out.println(next.getX() + ", " + next.getY());
            arrayList = algo.planPath(startX, startY, startAngle, next.getX(), next.getY(), next.getImadeDirectionAngle(), true, true, true);
            if (arrayList != null) {// if there is a path
                sendMovesToRobot(arrayList, next.getImageId());
                int[] coords = algo.getEndPosition();
                startX = coords[0];
                startY = coords[1];
                startAngle = coords[2];
                count++;
            } else {
                System.out.println("No path found, trying to path to the next obstacle");
            }
        }
    }

    /**
     * send instructions to robot; reverse and retake image if img recognition is null; send img result to android
     * @param moveList
     * @param i
     */
    private static void sendMovesToRobot(ArrayList<Movement> moveList, int i) {
        int tryCount = 4;
        ArrayList<Movement> backwardMoveList;
        int[] coords;

        int[] backwardCoords;

        String commandsToSend = encodeMoves(moveList);
        System.out.println(commandsToSend);

        sendToRobot(commandsToSend);

        String str = takeImage(i);
//        String str = " ";
        // retry if image taken is null
        while ((str.equals("null") || str.equals("bullseye")) && tryCount > 0) {
            tryCount--;
            // try to go backwards by 1.
            coords = algo.getEndPosition();
            backwardCoords = algo.getReversePos(coords[0], coords[1], coords[2] / 90);
            if (backwardCoords == null) break; // if no backwards position available, just break.
            System.out.println("Reversing to retake picture...");
            backwardMoveList = algo.planPath(coords[0], coords[1], coords[2], backwardCoords[0], backwardCoords[1], backwardCoords[2] * 90, false, true, true);
            if (backwardMoveList != null) { // if we can't reverse, just break from the loop.
                commandsToSend = encodeMoves(backwardMoveList);
                sendToRobot(commandsToSend);
                str = takeImage(i);
            } else {
                break;
            }
        }
//        sendImageToAndroid(i, str);
    }

    /**
     * encode each robot move to string instructions
     * @param moveList
     * @return
     */
    private static String encodeMoves(ArrayList<Movement> moveList) {
        String commandsToSend = ":STM:0008,";
        INSTRUCTION_TYPE instructionType;
        String formatted;

        for (Movement move : moveList) {
            int measure = 0;
            if (move.isLine()) {
                measure = (int) move.getLength();
                formatted = String.format("%03d", measure);
                if (move.isReverse()) {
                    instructionType = INSTRUCTION_TYPE.BACKWARD;
                } else {
                    instructionType = INSTRUCTION_TYPE.FORWARD;
                }
                commandsToSend += formatted + INSTRUCTION_TYPE.encode(instructionType) + ",";
            } else {
                TurnMovement moveConverted = (TurnMovement) move;
                if (moveConverted.isTurnLeft()) {
                    instructionType = INSTRUCTION_TYPE.FORWARD_LEFT;
                } else {
                    instructionType = INSTRUCTION_TYPE.FORWARD_RIGHT;
                }
//                commandsToSend += "090" + INSTRUCTION_TYPE.encode(instructionType) + ",";
//                if(commandsToSend.charAt(commandsToSend.length() - 2) == '1') {
                commandsToSend += "0000,090" + INSTRUCTION_TYPE.encode(instructionType) + ",";
//                }else
            }

        }
        return commandsToSend.substring(0, commandsToSend.length() - 1);
    }

    /**
     * send to robot the completed list of paths.
     * @param cmd
     */
    private static void sendToRobot(String cmd) {
        comm.sendMsg(cmd);
        String receiveMsg = null;

        // buffer so we can space co mmands

        //confirmation - :REACHED:
        try {
            Thread.sleep(500);//time is in ms (1000 ms = 1 second)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        sendPathToAndroid();
        while (receiveMsg == null || !receiveMsg.equals(":REACHED:")) {
            receiveMsg = comm.recieveMsg();
        }

        System.out.println("Message: " + receiveMsg + "\n");
        try {
            Thread.sleep(500); //time is in ms (1000 ms = 1 second)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * send img recognition result to android
     * @param obstacleID
     * @param image
     */
    private static void sendImageToAndroid(int obstacleID, String image) {
        String msg;
        msg = ":AND:TARGET," + (obstacleID + 1) + "," + image;
        comm.sendMsg(msg);
        try { // try to wait
            Thread.sleep(500);//time is in ms (1000 ms = 1 second)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * send instruction to to rpi to take the image
     */
    private static String takeImage(int obstacleId) {
        //message - :IMG:
        comm.sendMsg(":IMG:" + obstacleId);
        System.out.println("Scanning...");
        String receiveMsg = null;
        while (receiveMsg == null || receiveMsg.isEmpty()) {
            receiveMsg = comm.recieveMsg();
        }
        System.out.println("Message: " + receiveMsg + "\n");
        return receiveMsg;
    }

    /**
     * send the path to android to provide real time location update
     */
    private static void sendPathToAndroid() {
        ArrayList<Grid> path = algo.getNodePath();
        String pathString = ":AND:PATH,";
        // PATH|x,y,0-270|
        for (Grid n : path) {
            pathString += "|" + (n.getX() - MapConstants.ARENA_BORDER_SIZE) + "," + (n.getY() - MapConstants.ARENA_BORDER_SIZE) + "," + n.getDim() * 90;
        }
        comm.sendMsg(pathString);
    }

    /**
     * receive obstacle information (coord and facing) from android
     */
    private static void recvObstacles() {
        String receiveMsg = null;
        System.out.println("Waiting to receive obstacle list...");
//        while (receiveMsg == null || !receiveMsg.startsWith("POS")) {
        while (receiveMsg == null){
            try{
                receiveMsg = comm.recieveMsg();
            }catch (NullPointerException e){
                continue;
            }

        }
        System.out.println("Received Obstacles String: " + receiveMsg + "\n");

        // "POS|3,4,N|4,5,E|5,6,S|9,4,N|9,10,E"
//        String[] positions = receiveMsg.split("\\|");
//        String [] positions = new String[]{
//                "POS", "3,4,S",
//        };


//        receiveMsg = "{'obstacles': [{'X': 6, 'Y': 5, 'id': 1, 'direction': 'W'}, {'X': 12, 'Y': 5, 'id': 2, 'direction': 'S'}, {'X': 10, 'Y': 12, 'id': 3, 'direction': 'E'}, {'X': 4, 'Y': 15, 'id': 4, 'direction': 'S'}, {'X': 15, 'Y': 15, 'id': 5, 'direction': 'N'}]}";
        JSONObject positionsJson = new JSONObject(receiveMsg);
        System.out.println(positionsJson);
        JSONArray positionsArray = positionsJson.getJSONArray("obstacles");

        for(int i = 0; i < positionsArray.length(); ++i){
            JSONObject temp = (JSONObject) positionsArray.get(i);
            System.out.println(temp);
            System.out.println(temp.getString("direction"));

            arena.addPictureObstacle(temp.getInt("X"), 19 - temp.getInt("Y"), temp.getInt("id"), MapConstants.IMAGE_DIRECTION.getImageDirection(temp.getString("direction")));
        }


//        for (int i = 1; i < positions.length; i++) {
//            String[] obs = positions[i].split(",");
//            arena.addPictureObstacle(Integer.parseInt(obs[0]), Integer.parseInt(obs[1]), MapConstants.IMAGE_DIRECTION.getImageDirection(obs[2]));
//        }
    }
}
