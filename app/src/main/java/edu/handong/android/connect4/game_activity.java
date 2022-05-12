package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    private TensorFlowInferenceInterface mInferenceInterface;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ImageButton cButton = findViewById(R.id.reload_game_button);
        cTxtV = findViewById(R.id.textview);
        cBoardV = findViewById(R.id.boardview);
        ImageButton settings = findViewById(R.id.settings_button);
        settings.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

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

            Thread thread = new Thread(game_activity.this);
            thread.start();
        });



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

    void softmax(float vals[], int count) {
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

    void getProbs(int binary[], float probs[]) {
        if (mInferenceInterface == null) {
            AssetManager assetManager = getAssets();
            mInferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_ALPHA);
        }

        float[] floatValues  = new float[2*6*7];

        for (int i=0; i<2*6*7; i++) {
            floatValues[i] = binary[i];
        }

        float[] value = new float[1];
        float[] policy = new float[42];

        mInferenceInterface.feed(I_NODE, floatValues, 1, 2, 6, 7);
        mInferenceInterface.run(new String[] {O_NODE1, O_NODE2}, false);
        mInferenceInterface.fetch(O_NODE1, value);
        mInferenceInterface.fetch(O_NODE2, policy);

        Vector<Integer> actions = new Vector<>();
        getAllowedActions(board, actions);
        for (int action : actions) {
            probs[action] = policy[action];
        }

        softmax(probs, NUM_PIECES);
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
            probs[i] = -100.0f;
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

        if (machineWon(board)) return "THE MACHINE BEATS YOU!";
        else if (machineLost(board)) return "You Won!";
        else if (humanDraw(board)) return "Draw";

        machineTurn = false;
        return "Tap the column for your move";

    }



}
/*
    protected void message (int a){
        CharSequence text = "COLUMN "+a;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this,text,duration);
        toast.show();
    }

 */

