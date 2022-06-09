package edu.handong.android.connect4;

import static android.content.ContentValues.TAG;
import static edu.handong.android.connect4.Connect4GameActivity.connMultiplayer;
import static java.lang.Thread.sleep;
import edu.handong.android.connect4.Connect4Logic.Outcome;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Connect4Controller implements View.OnClickListener {
    public static final int COLS = 7;
    public static final int ROWS = 6;
    public static int[][] connGrid = new int[ROWS][COLS];
    /** Free space in the column */
    private static int[] spFree = new int[COLS];
    /** Create the Board to check if somebody win or it is a draw using the Connect4Logic class */
    private final Connect4Logic connBoardLogic = new Connect4Logic(connGrid, spFree);
    public static int connPlayerTurn;
    /** Check if the status of the game changes (WINS, DRAW, NOTHING) */
    private Outcome connOutcome = Outcome.NOTHING;
    /** Boolean to set if the game finished */
    private boolean connFinished = true;
    /** connAiPlayer use the logic of the connectAiPlayer class */
    private Connect4RobotPlayer connRobotPlayer;
    /** A boolean to check if it is the turn of the robot */
    private boolean connRobotTurn;
    /** Boolean tu check the mode of the game (multiplayer o single player). This variable will receive the value from connect4GameActivity */
    public static boolean mode;
    private static int pointsPlayer1;
    private static int pointsPlayer2;
    ExecutorService service;

    public Connect4Controller(){initialize(); }


    /**
     * In here we initialize all the objects of the controller class
     */
    private void initialize() {
        connPlayerTurn = Connect4GameActivity.firstTurnStatic;

        mode = connMultiplayer;
        connFinished = false;
        connOutcome = Outcome.NOTHING;
        for (int j = 0; j < COLS; ++j) {
            for (int i = 0; i < ROWS; ++i) {
                connGrid[i][j] = 0;        }
            spFree[j] = ROWS;        }

       if (!connMultiplayer) {
           service = Executors.newFixedThreadPool(10);
           connRobotPlayer = new Connect4RobotPlayer(connBoardLogic);
       }
       else  connRobotPlayer = null;
        pointsPlayer1=1000;
        pointsPlayer2=1000;
    }


    /**
     * This method is very important because the player and robot choose the column of the board.
     * In this method we check constantly the status of the game, switch the turn player, start the
     * animation to drop the disc and change the progressbar. And if it is robot turn, call the robotTurn class.
     * @param column number of the column
     */
    private void selectColumn(int column) {
        if (spFree[column] == 0) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "This column is full");
            }
            return;
        }
        connBoardLogic.placeMove(column, connPlayerTurn);
        System.out.println("Row "+spFree[column]);
        // put disc
        Connect4GameActivity.getInstance().dropDisc(spFree[column], column, connPlayerTurn);
        Connect4GameActivity.getInstance().progressBarSwap(connPlayerTurn);
        //compute the points
        if (connPlayerTurn==1) updateP1(-20);
        else updateP2(-20);
        togglePlayer(connPlayerTurn);
        checkForWin();
        connRobotTurn = false;
        if (BuildConfig.DEBUG) {
            connBoardLogic.displayBoard();
            Log.e(TAG, "Turn: " + connPlayerTurn);
            Log.e(TAG, "Points Player: " + connPlayerTurn+" "+pointsPlayer2);
            Log.e(TAG, "Points Player: " +pointsPlayer1);
        }
        if (connPlayerTurn == 2 && connRobotPlayer != null) robotTurn();

        }


    /**
     *This class call to execute the class RoboticTask
     */
    public void robotTurn() {
    if (connFinished) return;
    RoboticTask();
    }

    /**
     * Method to switch the turns of the players.
     * @param playerTurn the turn of the player
     */
    public void togglePlayer(int playerTurn) {
        if(playerTurn==1) connPlayerTurn =2;
        else connPlayerTurn =1;
    }

    private void checkForWin() {
        connOutcome = connBoardLogic.checkWin(connGrid);
        System.out.println("Checking game status");
        if (connOutcome != Outcome.NOTHING) {
            connFinished = true;
            ArrayList<ImageView> winDiscs =
                    connBoardLogic.getWinDiscs(Connect4GameActivity.getInstance().getCells());
            Connect4GameActivity.getInstance().showWinStatus(connOutcome, winDiscs);

        }
    }




    /**
     * Everytime that the users does a click in the board and if the game is not finished or it is
     * turn of robot, we are going to take the position where the users did click using the Column position method
     * of the Connect4GameActivity class and then we will send this value to the selectColumn method.
     */
    @Override
    public void onClick(View v) {
        if (connFinished || connRobotTurn) return;
        int col = Connect4GameActivity.getInstance().colChosen(v.getX());
        System.out.println(col);
        selectColumn(col);
    }

    public int getPointsPlayer2() {
        return pointsPlayer2;
    }

    public void setPointsPlayer2(int pointsPlayer2) {
        this.pointsPlayer2 = pointsPlayer2;
    }

    public int getPointsPlayer1() {
        return pointsPlayer1;
    }

    public void setPointsPlayer1(int pointsPlayer1) {
        this.pointsPlayer1 = pointsPlayer1;
    }

    public void updateP1(int p){
        setPointsPlayer1(getPointsPlayer1()+p);
    }

    public void updateP2(int p){
        setPointsPlayer2(getPointsPlayer2()+p);
    }

    /**
     "Executor is a simple standardized interface for defining custom thread-like subsystems,
     including thread pools, asynchronous I/O, and lightweight task frameworks.
     Depending on which concrete Executor class is being used, tasks may execute in
     a newly created thread, an existing task-execution thread, or the thread calling
     execute, and may execute sequentially or concurrently. ExecutorService provides a
     more complete asynchronous task execution framework."
     https://developer.android.com/reference/java/util/concurrent/package-summary
     */

    private void RoboticTask(){
        service.execute(() -> {
            //onPreExecute
            Connect4GameActivity.getInstance().runOnUiThread(() -> connRobotTurn = true);
            //InBackground
            try {
                Thread.currentThread();
                sleep(500);
            } catch (InterruptedException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "RobotPlayer chooses column " + connRobotPlayer.getColumn());
            }
           int colChosen = connRobotPlayer.getColumn();
            //PostExecute
            Connect4GameActivity.getInstance().runOnUiThread(() -> selectColumn(colChosen));


        });
    }



}
