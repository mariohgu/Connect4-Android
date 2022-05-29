package edu.handong.android.connect4;

import static android.content.ContentValues.TAG;

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
    public static int mode;
    /////////

    public Connect4Controller(){
        initialize();
    }

    private void initialize() {

        connPlayerTurn = Connect4GameActivity.firstTurnStatic;
        mode = Connect4GameActivity.connMode;
        connFinished = false;
        for (int j = 0; j < COLS; ++j) {
            for (int i = 0; i < ROWS; ++i) {
                connGrid[i][j] = 0;
            }
            spFree[j] = ROWS;
        }
        if (mode==4) {
            connAiPlayer = new Connect4AiPlayer(connBoardLogic);

        } else {
            connAiPlayer = null;
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

        togglePlayer(connPlayerTurn);

        checkForWin();
        mAiTurn = false;

        if (BuildConfig.DEBUG) {
            connBoardLogic.displayBoard();
            Log.e(TAG, "Turn: " + connPlayerTurn);
        }
        if (connPlayerTurn == 2 && connAiPlayer != null) aiTurn();
    }

    private void aiTurn() {

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
