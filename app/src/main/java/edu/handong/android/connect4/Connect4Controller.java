package edu.handong.android.connect4;

import static android.content.ContentValues.TAG;
import static edu.handong.android.connect4.Connect4GameActivity.connMultiplayer;
import static java.lang.Thread.sleep;
import edu.handong.android.connect4.Connect4Logic.Outcome;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;


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

       if (!connMultiplayer) connRobotPlayer = new Connect4RobotPlayer(connBoardLogic);
       else  connRobotPlayer = null;
        pointsPlayer1=1000;
        pointsPlayer2=1000;
    }


    /**
     * This method is very important because the player and robot choose the column of the board.
     * In this method we check constantly the status of the game, switch the turn player, start the
     * animation to drop the disc and change the progressbar. And if it is robot turn, call the robotTurn class.
     * @param column
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
     *This class call to execute the class robottask
     */
    public void robotTurn() {
    if (connFinished) return;
    new robotTask().execute(); }

    /**
     * Method to switch the turns of the players.
     * @param playerTurn
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



    @Override
    /**
     * Everytime that the users does a click in the board and if the game is not finished or it is
     * turn of robot, we are going to take the position where the users did click using the Column position method
     * of the Connect4GameActivity class and then we will send this value to the selectColumn method.
     */
    public void onClick(View v) {
        if (connFinished || connRobotTurn) return;
        int col = Connect4GameActivity.getInstance().colAtX(v.getX());
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
     * "AsyncTask is designed to be a helper class around Thread and Handler and does not constitute
     * a generic threading framework. AsyncTasks should ideally be used for short operations
     * (a few seconds at the most.) If you need to keep threads running for long periods of time,
     * it is highly recommended you use the various APIs provided by the java.util.concurrent package
     * such as Executor, ThreadPoolExecutor and FutureTask.
     *
     * An asynchronous task is defined by a computation that runs on a background thread and whose
     * result is published on the UI thread. An asynchronous task is defined by 3 generic types,
     * called Params, Progress and Result, and 4 steps, called onPreExecute, doInBackground,
     * onProgressUpdate and onPostExecute." https://developer.android.com/reference/android/os/AsyncTask
     *
     * In here we are using AsyncTask to execute the turn of the robot as a task in another thread. Previously
     * we set as true the turn of the Robot. then we create the thread to execute the getcolum in the RobotPlayer class
     * in order to decide the best position to put the disc. in the postexecute the task execute the selectcolumn class
     * with the number of the column previously decided.
     */

    class robotTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            connRobotTurn = true;
        }
        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                Thread.currentThread();
                sleep(100);
            } catch (InterruptedException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "RobotPlayer " + connRobotPlayer.getColumn());
            }
            return connRobotPlayer.getColumn();
        }
        @Override
        protected void onPostExecute(Integer integer) {
            selectColumn(integer);
        }
    }



}
