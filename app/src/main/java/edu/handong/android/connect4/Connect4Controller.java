package edu.handong.android.connect4;

import static android.content.ContentValues.TAG;

import edu.handong.android.connect4.Connect4Logic.Outcome;

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

    public Connect4Controller(){
        initialize();
    }

    private void initialize() {

        connPlayerTurn = Connect4GameActivity.firstTurnStatic;
        connFinished = false;
        for (int j = 0; j < COLS; ++j) {
            for (int i = 0; i < ROWS; ++i) {
                connGrid[i][j] = 0;
            }
            spFree[j] = ROWS;
        }
    }


    @Override
    public void onClick(View v) {
        Log.d("aa",v.toString());
        System.out.println("Vgetx"+v.getX());
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
        connBoardLogic.displayBoard();
        checkForWin();
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
}
