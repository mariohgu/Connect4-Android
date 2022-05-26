package edu.handong.android.connect4;

//This class is used to store a set of values that the ContentResolver can process.
import static android.content.ContentValues.TAG;
import static edu.handong.android.connect4.game_activity.connPlayer1;

import android.util.Log;
import android.widget.ImageView;
import java.util.ArrayList;


public class ConnectLogic {
    /**
     * Reference to player win
     */
    private int connCellValue;
    /**
     * Reference to a main mGrid
     */
    private final int[][] pGrid;
    /**
     * number of columns in the mGrid
     */
    public final int numCols;
    /**
     * number of rows in the mGrid
     */
    private final int numRows;
    /**
     * player win starting index
     */
    private int p, q;
    /**
     * reference to spFree cells in every column
     */
    private final int[] spFree;
    /**
     * Possible outcomes
     */
    public enum Outcome {
        NOTHING, DRAW, PLAYER1_WINS, PLAYER2_WINS
    }
    /**
     * flag to mark mDraw
     */
    private boolean conDraw;
    /**
     * winner direction
     */
    private int WIN_X = 0;
    private int WIN_Y = 0;

    /**
     * Initialise members
     *
     * @param grid reference to board grid
     * @param free reference to column height
     */

    public ConnectLogic(int[][] grid, int[] free) {
        pGrid = grid;
        numRows = grid.length;
        numCols = grid[0].length;
        this.spFree = free;
    }

    /**
     * Class to set the piece in the column
     */

    public void placeMove(int column, int player) {
        if (spFree[column] > 0) {
            pGrid[spFree[column] - 1][column] = player;
            spFree[column]--;
        }
    }

    /**
     * Create the Board
     */

    public void displayBoard() {
        System.out.println();
        for (int i = 0; i <= 5; ++i) {
            for (int j = 0; j <= 6; ++j) {
                System.out.print(pGrid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public int columnHeight(int index) {
        return spFree[index];
    }

    /**
     * Check if some of the players get 4 pieces in horizontal
     */
    private boolean horizontalCheck(int[][] board) {
        // horizontalCheck
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols - 3; j++) {
                connCellValue = board[i][j];
                if (connCellValue == 0) conDraw = false;
                if (connCellValue != 0 && board[i][j + 1] == connCellValue && board[i][j + 2] == connCellValue && board[i][j + 3] == connCellValue) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Horizontal check pass");
                    }
                    p = i;
                    q = j;
                    WIN_X = 1;
                    WIN_Y = 0;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if some of the player get 4 pieces in vertical
     */

    private boolean verticalCheck(int[][] board) {
        // verticalCheck
        for (int j = 0; j < numCols; j++) {
            for (int i = 0; i < numRows - 3; i++) {
                connCellValue = board[i][j];
                if (connCellValue == 0) conDraw = false;
                if (connCellValue != 0 && board[i + 1][j] == connCellValue && board[i + 2][j] == connCellValue && board[i + 3][j] == connCellValue) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Vertical check pass");
                    }
                    p = i;
                    q = j;
                    WIN_X = 0;
                    WIN_Y = 1;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *Check if some of the player get an diagonal in right
     */

    private boolean ascendingDiagonalCheck(int[][] board) {
        // ascendingDiagonalCheck
        for (int i = 3; i < numRows; i++) {
            for (int j = 0; j < numCols - 3; j++) {
                connCellValue = board[i][j];
                if (connCellValue == 0) conDraw = false;
                if (connCellValue != 0 && board[i - 1][j + 1] == connCellValue && board[i - 2][j + 2] == connCellValue && board[i - 3][j + 3] == connCellValue) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "ascendingDiagonal check pass");
                    }
                    p = i;
                    q = j;
                    WIN_X = 1;
                    WIN_Y = -1;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean descendingDiagonalCheck(int[][] board) {
        // descendingDiagonalCheck
        for (int i = 3; i < numRows; i++) {
            for (int j = 3; j < numCols; j++) {
                connCellValue = board[i][j];
                if (connCellValue == 0) conDraw = false;
                if (connCellValue != 0 && board[i - 1][j - 1] == connCellValue && board[i - 2][j - 2] == connCellValue && board[i - 3][j - 3] == connCellValue) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "descendingDiagonal check pass");
                    }
                    p = i;
                    q = j;
                    WIN_X = -1;
                    WIN_Y = -1;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if somebody wins the match
     * @param board
     * @return
     */

    public Outcome checkWin(int[][] board) {
        conDraw = true;
        connCellValue = 0;
        if (horizontalCheck(board) || verticalCheck(board) ||
                ascendingDiagonalCheck(board) || descendingDiagonalCheck(board)) {
            return connCellValue ==  connPlayer1 ? Outcome.PLAYER1_WINS : Outcome.PLAYER2_WINS;
        }
        // nobody won, return mDraw if it is, nothing if it's not
        return conDraw ? Outcome.DRAW : Outcome.NOTHING;
    }

    /**
     * Returns sprites of a winning combination
     *
     * @param cells cell mGrid
     * @return winning move discs
     */
    public ArrayList<ImageView> getWinDiscs(ImageView[][] cells) {
        ArrayList<ImageView> combination = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            combination.add(cells[p + WIN_Y * i][q + WIN_X * i]);
        }
        return combination;
    }


}
