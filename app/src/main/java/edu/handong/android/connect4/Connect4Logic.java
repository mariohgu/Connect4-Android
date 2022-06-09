package edu.handong.android.connect4;


import static android.content.ContentValues.TAG; //This class is used to store a set of values that the ContentResolver can process.
import static edu.handong.android.connect4.Connect4GameActivity.connPlayer1;
import android.util.Log;
import android.widget.ImageView;
import java.util.ArrayList;

/**
 *      Our code is based in the book
 *      The book "Intelligent Mobile Projects with TensorFlow" by Jeff Tang,
 *            ISBN: 978-1-78883-454-4
 *
 */



public class Connect4Logic {
    /** Check the winner */
    private int connCellValue;
    /** Variable to make a reference to a main Grid */
    private final int[][] connBoardL;
    /**number of columns*/
    public final int numCols;
    /** number of rows */
    private final int numRows;
    /** player win starting index */
    private int index1, index2;
    /** free cells in the column */
    private final int[] spFree;
    /** Possible outcomes */
    public enum Outcome {
        NOTHING, DRAW, PLAYER1_WINS, PLAYER2_WINS
    }

    private boolean conDraw;
    /** winner direction */
    private int WIN_X = 0;
    private int WIN_Y = 0;

    public Connect4Logic(int[][] grid, int[] free) {
        connBoardL = grid;
        numRows = grid.length;
        numCols = grid[0].length;
        this.spFree = free;
    }


    /**
     * Method to set the piece in the column
     * @param column
     * @param player
     */
    public void placeMove(int column, int player) {
        if (spFree[column] > 0) {
            connBoardL[spFree[column] - 1][column] = player;
            spFree[column]--;
        }
    }

    /**
     * class to create the Board in the console
     */
    public void displayBoard() {
        System.out.println();
        for (int i = 0; i <= 5; ++i) {
            for (int j = 0; j <= 6; ++j) {
                System.out.print(connBoardL[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }


    /**
     * Check if some of the players get 4 pieces in horizontal
     * @param board
     * @return boolean
     */
    private boolean horizontalCheck(int[][] board) {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols - 3; j++) {
                connCellValue = board[i][j];
                if (connCellValue == 0) conDraw = false;
                if (connCellValue != 0 && board[i][j + 1] == connCellValue && board[i][j + 2] == connCellValue && board[i][j + 3] == connCellValue) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Possible winning in horizontal way");
                    }
                    index1 = i;
                    index2 = j;
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
        for (int j = 0; j < numCols; j++) {
            for (int i = 0; i < numRows - 3; i++) {
                connCellValue = board[i][j];
                if (connCellValue == 0) conDraw = false;
                if (connCellValue != 0 && board[i + 1][j] == connCellValue && board[i + 2][j] == connCellValue && board[i + 3][j] == connCellValue) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Possible winning in Vertical way");
                    }
                    index1 = i;
                    index2 = j;
                    WIN_X = 0;
                    WIN_Y = 1;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *Check if some of the player get an diagonal in right or left
     */

    private boolean ascendingDiagonalCheck(int[][] board) {
        for (int i = 3; i < numRows; i++) {
            for (int j = 0; j < numCols - 3; j++) {
                connCellValue = board[i][j];
                if (connCellValue == 0) conDraw = false;
                if (connCellValue != 0 && board[i - 1][j + 1] == connCellValue && board[i - 2][j + 2] == connCellValue && board[i - 3][j + 3] == connCellValue) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Possible winning in diagonal (ascending) way");
                    }
                    index1 = i;
                    index2 = j;
                    WIN_X = 1;
                    WIN_Y = -1;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean descendingDiagonalCheck(int[][] board) {
        for (int i = 3; i < numRows; i++) {
            for (int j = 3; j < numCols; j++) {
                connCellValue = board[i][j];
                if (connCellValue == 0) conDraw = false;
                if (connCellValue != 0 && board[i - 1][j - 1] == connCellValue && board[i - 2][j - 2] == connCellValue && board[i - 3][j - 3] == connCellValue) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Possible winning in Diagonal (descending) way");
                    }
                    index1 = i;
                    index2 = j;
                    WIN_X = -1;
                    WIN_Y = -1;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if somebody wins the match,or if it is a Draw. set the Outcome DRAW
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
        return conDraw ? Outcome.DRAW : Outcome.NOTHING;
    }


    /**
     * This is the class to help to identify the winning pieces
     * @param cells
     * @return winning combination
     */
    public ArrayList<ImageView> getWinDiscs(ImageView[][] cells) {
        ArrayList<ImageView> combine = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            combine.add(cells[index1 + WIN_Y * i][index2 + WIN_X * i]);
        }
        return combine;
    }


}
