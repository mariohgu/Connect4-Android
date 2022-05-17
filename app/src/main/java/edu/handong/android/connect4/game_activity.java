package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.content.res.AssetManager;
import android.widget.ImageButton;
import android.widget.TextView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import static java.lang.Math.exp;
import static java.lang.Math.max;

/**
 * Our code is based in:
 * - The book "Intelligent Mobile Projects with TensorFlow" by Jeff Tang,
 *          ISBN: 978-1-78883-454-4
 * - The post "How to build your own AlphaZero AI using Python and Keras" by David Foster
 *          URL: https://medium.com/applied-data-science/how-to-build-your-own-alphazero-ai-using-python-and-keras-7f664945c188
 */

public class game_activity extends AppCompatActivity implements Runnable{
    //The model is in the assets folder, it names alphazero19.
    private static final String MODEL_ALPHA = "file:///android_asset/alphazero19.pb";
    private static final String I_NODE = "main_input";
    private static final String O_NODE1 = "value_head/Tanh"; //To find out the exact output node names
    private static final String O_NODE2 = "policy_head/MatMul";
    TextView cTxtV;
    Connect4View cBoardV;
    public static final int MACHINE_PIECE = -1;
    public static final int HUMAN_PIECE = 1;
    private static final int NUM_PIECES = 42;
    private Boolean machineFirst = false;
    private Boolean machineTurn = false;
    private Vector<Integer> machineMoves = new Vector<>();
    private Vector<Integer> humanMoves = new Vector<>();
    private int board[] = new int[NUM_PIECES];
    private static final HashMap<Integer, String> PIECE_SYMBOL;
    private TensorFlowInferenceInterface infInterface;
    static
    {
        PIECE_SYMBOL = new HashMap<Integer, String>();
        PIECE_SYMBOL.put(MACHINE_PIECE, "X");
        PIECE_SYMBOL.put(HUMAN_PIECE, "O");
        PIECE_SYMBOL.put(0, "-");
    }
    //Both the aiWon and aiLost functions use a constant array that defines all the 69 possible winning positions:
    private final int winMoves[][] = {
            {0,1,2,3},
            {1,2,3,4},
            {2,3,4,5},
            {3,4,5,6},

            {7,8,9,10},
            {8,9,10,11},
            {9,10,11,12},
            {10,11,12,13},

            {14,15,16,17},
            {15,16,17,18},
            {16,17,18,19},
            {17,18,19,20},

            {21,22,23,24},
            {22,23,24,25},
            {23,24,25,26},
            {24,25,26,27},

            {28,29,30,31},
            {29,30,31,32},
            {30,31,32,33},
            {31,32,33,34},

            {35,36,37,38},
            {36,37,38,39},
            {37,38,39,40},
            {38,39,40,41},

            // vertically
            {0,7,14,21},
            {7,14,21,28},
            {14,21,28,35},

            {1,8,15,22},
            {8,15,22,29},
            {15,22,29,36},

            {2,9,16,23},
            {9,16,23,30},
            {16,23,30,37},

            {3,10,17,24},
            {10,17,24,31},
            {17,24,31,38},

            {4,11,18,25},
            {11,18,25,32},
            {18,25,32,39},

            {5,12,19,26},
            {12,19,26,33},
            {19,26,33,40},

            {6,13,20,27},
            {13,20,27,34},
            {20,27,34,41},
            {29,22,15,8},

            // diagonally
            {3,9,15,21},

            {4,10,16,22},
            {10,16,22,28},

            {5,11,17,23},
            {11,17,23,29},
            {17,23,29,35},

            {6,12,18,24},
            {12,18,24,30},
            {18,24,30,36},
            {13,19,25,31},
            {19,25,31,37},
            {20,26,32,38},

            {3,11,19,27},

            {2,10,18,26},
            {10,18,26,34},

            {1,9,17,25},
            {9,17,25,33},
            {17,25,33,41},

            {0,8,16,24},
            {8,16,24,32},
            {16,24,32,40},
            {7,15,23,31},
            {15,23,31,39},
            {14,22,30,38} };


    public boolean getMachineTurn() {
        return machineTurn;
    }

    public boolean getMachineFirst() {
        return machineFirst;
    }

    public Vector<Integer> getMachineMoves() {
        return machineMoves;
    }

    public Vector<Integer> getHumanMoves() {
        return humanMoves;
    }

    public int[] getBoard() {
        return board;
    }

    public void setMachineTurn() {
        machineTurn = true;
    }


//helper functions are defined to test the game-end status
    public boolean machineWon(int bd[]) {
        for (int i=0; i<69; i++) {
            int sum = 0;
            for (int j=0; j<4; j++)
                sum += bd[winMoves[i][j]];
            if (sum == 4* MACHINE_PIECE) return true;
        }
        return false;
    }

    public boolean machineLost(int bd[]) {
        for (int i=0; i<69; i++) {
            int sum = 0;
            for (int j=0; j<4; j++)
                sum += bd[winMoves[i][j]];
            if (sum == 4*HUMAN_PIECE) return true;
        }
        return false;
    }

    public boolean humanDraw(int bd[]) {
        boolean hasZero = false;
        for (int i = 0; i< NUM_PIECES; i++) {
            if (bd[i] == 0) {
                hasZero = true;
                break;
            }
        }
        if (!hasZero) return true;
        return false;
    }


    public boolean gameEnded(int[] bd) {
        if (machineWon(bd) || machineLost(bd) || humanDraw(bd)) return true;

        return false;
    }
//sets all the allowed actions for a given board position to the actions vector
    void getAllowedActions(int bd[], Vector<Integer> actions) {

        for (int i = 0; i< NUM_PIECES; i++) {
            if (i>= NUM_PIECES -7) {
                if (bd[i] == 0)
                    actions.add(i);
            }
            else {
                if (bd[i] == 0 && bd[i+7] != 0)
                    actions.add(i);
            }
        }


    }
    public TextView getTextView() {
        return cTxtV;
    }

    /**
     * In the onCreate method, instantiate the three UI elements, and set the button click listener
     * so it randomly decides who makes the first move. And we have a reset button, when it is
     * tapped the user will replay the game, so we need to reset the MachineMoves and HumanMoves
     * vectors before drawing the board and starting a thread to play the game:
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ImageButton cButton = findViewById(R.id.reload_game_button);
        ImageButton settings = findViewById(R.id.settings_button);
        ImageButton back_button = findViewById(R.id.back_button);
        cTxtV = findViewById(R.id.textview);
        cBoardV = findViewById(R.id.boardview);
        loadPref();
        back_button.setOnClickListener(view -> {
            super.finish();
                }
        );

        settings.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
        /**
         * When the Replay button is tapped, randomly decide who goes first, reset the board represented
         * as an integer array, clear the two vectors that store our moves and the AI's moves, and redraw the
         * original board grid
         */

        cButton.setOnClickListener(view -> {
            cTxtV.setText("");

            Random rand = new Random();
            int n = rand.nextInt(2);

            machineFirst = (n==0); // make this random between true and false

            if (machineFirst) machineTurn = true;
            else machineTurn = false;

            if (machineTurn)
                cTxtV.setText("Waiting for Machine's move");
            else
                cTxtV.setText("Tap the column for your move");

            for (int i = 0; i< NUM_PIECES; i++)
                board[i] = 0;
            machineMoves.clear();
            humanMoves.clear();
            cBoardV.drawBoard();

            Thread thread = new Thread(this);
            thread.start();
        });


/**
 * The thread starts the run method, which further calls the playGame method to first convert the board
 * position to a binary integer array to be used as the input of the model
 */
    }
    @Override
    public void run() {

        final String result = playGame();

        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        cBoardV.invalidate();
                        cTxtV.setText(result);
                    }
                });
    }

    /**
     * maxValue makes the sum of the probs values for the allowed actions be 1
     * @param vals
     * @param count
     */

    void maxValue(float vals[], int count) {
        float maxval = -Float.MAX_VALUE;
        for (int i=0; i<count; i++) {
            maxval = max(maxval, vals[i]);
        }
        float sum = 0.0f;
        for (int i=0; i<count; i++) {
            vals[i] = (float)exp(vals[i] - maxval);
            sum += vals[i];
        }
        for (int i=0; i<count; i++) {
            vals[i] /= sum;
        }
    }

    /**
     * The getProbs method loads the model if it hasn't been loaded, runs the model with the current board
     * state as input, and gets the output policy before calling maxValue to get the true probability values, which
     * sum to 1.
     * @param binary
     * @param probs
     */

    void getProbs(int binary[], float probs[]) {
        if (infInterface == null) {
            AssetManager assetManager = getAssets();
            infInterface = new TensorFlowInferenceInterface(assetManager, MODEL_ALPHA);
        }

        float[] floatValues  = new float[2*6*7];

        for (int i=0; i<2*6*7; i++) {
            floatValues[i] = binary[i];
        }

        float[] value = new float[1];
        float[] policy = new float[42];

        infInterface.feed(I_NODE, floatValues, 1, 2, 6, 7);
        infInterface.run(new String[] {O_NODE1, O_NODE2}, false);
        infInterface.fetch(O_NODE1, value);
        infInterface.fetch(O_NODE2, policy);

        Vector<Integer> actions = new Vector<>();
        getAllowedActions(board, actions);
        for (int action : actions) {
            probs[action] = policy[action];
        }

        maxValue(probs, NUM_PIECES);
    }

    void printBoard(int bd[]) {
        for (int i = 0; i<6; i++) {
            for (int j=0; j<7; j++) {
                System.out.print(" " + PIECE_SYMBOL.get(bd[i*7+j]));
            }
            System.out.println("");
        }

        System.out.println("\n\n");
    }


    String playGame() {
        if (!machineTurn) return "Tap the column for your move";

        int binary[] = new int[NUM_PIECES *2];

        // convert board to binary input
        for (int i = 0; i< NUM_PIECES; i++)
            if (board[i] == 1) binary[i] = 1;
            else binary[i] = 0;

        for (int i = 0; i< NUM_PIECES; i++)
            if (board[i] == -1) binary[42+i] = 1;
            else binary[NUM_PIECES +i] = 0;

        float probs[] = new float[NUM_PIECES];
        for (int i = 0; i< NUM_PIECES; i++)
        /**
         * The reason we initialize all probs array elements to -100.0 is that inside the
         * getProbs method the probs array will be changed, only for
         * the allowed actions, to the values (all small ones around -1.0 to 1.0) returned in the
         * policy, so the probs values for all the illegal actions will remain -100.0 and after
         * the maxValue function, which makes the probabilities for the illegal moves basically
         * zero, we can just use the probabilities for the legal moves.
          */

            probs[i] = -100.0f;
        /**
         * getProbs method, which runs the frozen model with the binary input
         * and returns the probability policy in probs, and finds the maximum probability
         * value among the p
         */
        getProbs(binary, probs);
        int action = -1;

        float max = 0.0f;
        for (int i = 0; i< NUM_PIECES; i++) {
            if (probs[i] > max) {
                max = probs[i];
                action = i;
            }
        }

        board[action] = MACHINE_PIECE;
        printBoard(board);
        machineMoves.add(action);

        if (machineWon(board)) return "Player lost";
        else if (machineLost(board)) return "Player won!";
        else if (humanDraw(board)) return "Draw game";

        machineTurn = false;
        return "Tap the column for your move";

    }

    private void loadPref(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PlayerPref", Context.MODE_PRIVATE);
        String player = preferences.getString("player1", "");
        TextView player_name = findViewById(R.id.player_turn_label);
        player_name.setText(player);

    }



}

