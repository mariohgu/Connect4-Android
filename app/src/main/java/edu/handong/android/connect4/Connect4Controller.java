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

    /**
     * number of columns
     */
    public static final int COLS = 7;
    /**
     * number of rows
     */
    public static final int ROWS = 6;
    /**
     * mGrid, contains 0 for empty cell or player ID
     */
    public static int[][] connGrid = new int[ROWS][COLS];
    /**
     * mFree cells in every column
     */
    private static int[] spFree = new int[COLS];
    /**
     * board mBoardLogic (winning check)
     */
    private final Connect4Logic connBoardLogic = new Connect4Logic(connGrid, spFree);
    /**
     * player turn
     */
    public static int connPlayerTurn;
    /**
     * current status
     */
    private Outcome connOutcome = Outcome.NOTHING;
    /**
     * if the game is mFinished
     */
    private boolean connFinished = true;
    //////////////
    private Connect4AiPlayer connAiPlayer;
    private boolean mAiTurn;
    public static boolean mode;
    public static int First_Player;
    private static int pointsPlayer1=1000;
    private static int pointsPlayer2=1000;
    /////////

    public Connect4Controller(){
        initialize();

    }

    private void initialize() {

        connPlayerTurn = Connect4GameActivity.firstTurnStatic;
        First_Player = connPlayerTurn;
        mode = connMultiplayer;
        connFinished = false;
        connOutcome = Outcome.NOTHING;
        for (int j = 0; j < COLS; ++j) {
            for (int i = 0; i < ROWS; ++i) {
                connGrid[i][j] = 0;
            }
            spFree[j] = ROWS;
        }
    //    connAiPlayer = new Connect4AiPlayer(connBoardLogic);
       if (!connMultiplayer) {
            connAiPlayer = new Connect4AiPlayer(connBoardLogic);

        } else {
            connAiPlayer = null;
        }

        if (First_Player == 2 && connAiPlayer != null) aiTurn();


    }


    private void selectColumn(int column) {
        System.out.println("Free column"+ spFree[column]);
        if (spFree[column] == 0) {
            System.out.println("No more space in this column");
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "full column or game is Finished");
            }
            return;
        }
        connBoardLogic.placeMove(column, connPlayerTurn);
        System.out.println("Row "+spFree[column]);
        // put disc
        Connect4GameActivity.getInstance().dropDisc(spFree[column], column, connPlayerTurn);
        Connect4GameActivity.getInstance().progressBarSwap(connPlayerTurn);
        //compute the points
        if (connPlayerTurn==1){
            updateP1(-20);
        }
        else {
            updateP2(-20);
        }
        togglePlayer(connPlayerTurn);

        checkForWin();
        mAiTurn = false;

        if (BuildConfig.DEBUG) {
            connBoardLogic.displayBoard();
            Log.e(TAG, "Turn: " + connPlayerTurn);
            Log.e(TAG, "Points Player: " + connPlayerTurn+" "+pointsPlayer2);
            Log.e(TAG, "Points Player: " +pointsPlayer1);
        }
        if (connPlayerTurn == 2 && connAiPlayer != null) aiTurn();
        }


    public void aiTurn() {

        if (connFinished) return;
        new AiTask().execute();
    }


    public void togglePlayer(int playerTurn) {
        if(playerTurn==1)
        {
            connPlayerTurn =2;


        }else {
            connPlayerTurn =1;

        }
    }

    private void checkForWin() {
        connOutcome = connBoardLogic.checkWin(connGrid);
        System.out.println("Checking");
        if (connOutcome != Outcome.NOTHING) {
            connFinished = true;
            ArrayList<ImageView> winDiscs =
                    connBoardLogic.getWinDiscs(Connect4GameActivity.getInstance().getCells());
            Connect4GameActivity.getInstance().showWinStatus(connOutcome, winDiscs);

        } else {
//            togglePlayer(mPlayerTurn);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d("aa",v.toString());
        System.out.println("Vgetx"+v.getX());
        if (connFinished || mAiTurn) return;
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

    class AiTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAiTurn = true;
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
                Log.e(TAG, "mAiPlayer " + connAiPlayer.getColumn());
            }

            return connAiPlayer.getColumn();

        }
        @Override
        protected void onPostExecute(Integer integer) {
            selectColumn(integer);
        }
    }



}
