package edu.handong.android.connect4;

import edu.handong.android.connect4.Connect4Logic;
import umontreal.ssj.probdist.GammaDist;

public class Connect4AiPlayer {
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private final Connect4Logic mBoardLogic;
    public static String mPath;
    private int[] LastBoard;
    private static final String TAG = Connect4Controller.class.getName();

    public Connect4AiPlayer(Connect4Logic boardLogic) {
        mBoardLogic = boardLogic;
    }

    //PATH TO OUR MODEL FILE AND NAMES OF THE INPUT AND OUTPUT NODES
    private static String INPUT_NAME = "main_input";
    private static String OUTPUT_NAME_1 = "value_head/Tanh";
    private static String OUTPUT_NAME_2 = "policy_head/MatMul";

    private static int[] ArrayToList(int[][] arr)
    {
        int[] new_arr;
        new_arr = new int[42];
        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 7; j++)
            {
                new_arr[j + 7*i] = arr[i][j];
                if(new_arr[j + 7*i] == 2)
                {
                    new_arr[j + 7*i] = -1;
                }
            }
        }
        return new_arr;
    }

    public void setDifficulty(int depth) {
        if(depth == 0)
        {
            mPath = "alphazero19.pb";
        }
        else if(depth == 1)
        {
            mPath = "tf_model20.pb";
        }
        else if(depth == 2)
        {
            mPath = "tf_model42.pb";
        }
    }

    public static int[][] ListToArray(int[] arr)
    {
        int[][] new_arr;
        new_arr = new int[6][7];
        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 7; j++)
            {
                new_arr[i][j] = arr[j + 7*i];
                if(new_arr[i][j] == -1)
                {
                    new_arr[i][j] = 2;
                }
            }
        }
        return new_arr;
    }

    private static float[] Input_Array(int[] state, int Player)
    {
        float[] input_array;
        input_array = new float[6*7*2];
        for(int i = 0; i < state.length; i++)
        {
            if(state[i] == -Player)
            {
                input_array[state.length + i] = 1;
            }else if(state[i] == Player)
            {
                input_array[i] = 1;
            }
        }
        return input_array;
    }

    /**
     * run ai move
     * @return column to put AI disc
     */

    public int getColumn() {
        double[] pi = simulate(ArrayToList(Connect4Controller.connGrid)); //MCTS AQUIIIIIIII EEEEEEEEEEESSSSS

        Object[] result = argmax(pi);
        int best_move = (int) result[0];
        return best_move % 7;
    }


    //ARRAY TO HOLD THE PREDICTIONS AND FLOAT VALUES TO HOLD THE IMAGE DATA
    private static float[] PREDICTIONS = new float[42];
    private static float[] VALUE = new float[42];
    private float[] floatValues;
    private int[] INPUT_SIZE = {2,6,7};


    public static int[] AllowedActions(int[] board)
    {
        int[] allowed;
        allowed = new int[42];
        for(int i = 0; i < board.length; i++)
        {
            if( i >= board.length - 7) {
                if (board[i] == 0) {
                    allowed[i] = i+1;
                }
            }
            else
            {
                if(board[i] == 0 & board[i+7] != 0)
                {
                    allowed[i] = i+1;
                }
            }
        }
        return allowed;
    }

    //FUNCTION TO COMPUTE THE MAXIMUM PREDICTION AND ITS CONFIDENCE
    private Object[] argmax(double[] array){

        int best = -1;
        //float probs;
        double best_confidence = -10.0f;
        for(int i = 0;i < array.length;i++){

            double value = array[i];

            if (value > best_confidence){
                best_confidence = value;
                best = i;
            }
        }

        return new Object[]{best,best_confidence};

    }


    public static float[] predict(int[] Board, int Player){
        //Pass input into the tensorflow
        float[] input_array = Input_Array(Board, Player);
        Connect4GameActivity.tf.feed(INPUT_NAME, input_array,1,2,6,7);

        //compute predictions
        Connect4GameActivity.tf.run(new String[]{OUTPUT_NAME_1, OUTPUT_NAME_2});

        //copy the output into the PREDICTIONS array
        Connect4GameActivity.tf.fetch(OUTPUT_NAME_1,VALUE);
        Connect4GameActivity.tf.fetch(OUTPUT_NAME_2,PREDICTIONS);

        int[] allowed = AllowedActions(Board);
        float sum = 0;
        float[] probs = new float[43];
        for(int i = 0; i < PREDICTIONS.length; i++)
        {
            if(allowed[i] == 0) {
                sum = sum + (float) Math.exp(-100);
            }
            else{
                sum = sum + (float) Math.exp(PREDICTIONS[i]);
            }
        }
        for(int i = 0; i < PREDICTIONS.length; i++) {

            float value = PREDICTIONS[i];
            double odds = Math.exp(value);
            probs[i] = (float) odds/sum;
        }
        probs[42] = VALUE[0];
        return probs;
    }

    ///////////////////////////////////////////////////////////////////////////////

    static Connect4Node start;
    private static int[] mFree = new int[7];
    private static int[] breadcrumbs;
    private static int[][] currentstate = new int[6][7];
    private static Connect4Logic pBoardLogic = new Connect4Logic(currentstate, mFree);

    private static Connect4Logic.Outcome mOutcome = Connect4Logic.Outcome.NOTHING;

    private static void buildMCTS(int[] state){
        Connect4Node s = start;
        if(s != null){
            for(Connect4Node n: s.children){
                if(n.Player == state[n.move]){
                    if(n.children != null)
                    {
                        for(Connect4Node c : n.children)
                        {
                            if(state[c.move] == c.Player)
                            {
                                start = c;
                                start.Player = -start.Player;
                                return;
                            }
                        }
                    }
                }
            }
        }
        NewConnect4Node(state);
    }

    private static void NewConnect4Node(int[] state){
        int turn = 1;
        if (Connect4Controller.connPlayerTurn != 1) {
            turn = -1;
        }
        start = new Connect4Node(null, state, (byte) -1, turn);
    }

    private static void backFill(float value, int currentPlayer) {
        Connect4Node s = start;
        int direction = 0;
        int turn = s.Player;
        for (int action : breadcrumbs) {
            if (action == 0) {
                break;
            }
            for (Connect4Node n : s.children) {
                n.Player = turn;
                if (n.move == action - 1) {
                    if (currentPlayer == n.Player) {
                        direction = 1;
                    } else {
                        direction = -1;
                    }
                    n.N = n.N + 1;
                    n.W = n.W + value * direction; //value
                    n.Q = n.W / n.N;
                    s = n;
                    break;
                }
            }
            turn = -turn;
        }
    }

    private static Connect4Node selection() { //select a leaf node return index
        double epsilon = 0;
        double cpuct = 1;
        double alpha = 0.8;
        double[] nu = new double[7];
        double[] y = new double[7];
        double Q = 0;
        double U = 0;
        int first = 1;
        int count = 0;
        breadcrumbs = new int[42];
        int simulationAction = 0;
        Connect4Node s = start;
        Connect4Node simulationEdge = start;
        Connect4Node sim = s;
        while (sim.children.size() != 0) {
            double maxQU = -9999;
            if (first == 1) {
                double sum = 0;
                for (int i = 0; i < sim.children.size(); i++) {
                    epsilon = 0.2;
                    y[i] = GammaDist.inverseF(alpha, 1.0, 1, Math.random());
                    sum += y[i];
                }
                for(int i = 0; i < y.length; i++){
                    nu[i] = y[i]/sum;
                }
            } else {
                for (int i = 0; i < sim.children.size(); i++) {
                    epsilon = 0;
                    nu[i] = 0;
                }
            }
            double Nb = 0;
            for (Connect4Node child : sim.children) {
                Nb = Nb + child.N;
            }
            double maxP = 0;
            for (int j = 0; j < sim.children.size(); j++) {
                Connect4Node n = sim.children.get(j);

                U = cpuct * ((1 - epsilon) * n.P + epsilon * nu[j]) * Math.sqrt(Nb) / (1 + n.N);
                Q = n.Q;
                if(Q + U > maxQU) {//(n.P > maxP){
                    maxQU = Q + U;
                    maxP = n.P;
                    simulationAction = n.move;
                    simulationEdge = n;
                }
            }
            simulationEdge.state[simulationAction] = simulationEdge.Player;
            simulationEdge.Player = -simulationEdge.Player;
            first = 0;
            sim = simulationEdge;
            breadcrumbs[count] = simulationAction + 1;
            count += 1;
        }
        return sim;
    }

    private static float expansion(Connect4Node n) { // choose child of leaf node
        float value = -1;
        currentstate = ListToArray(n.state);
        mOutcome = pBoardLogic.checkWin(currentstate);
        if(mOutcome != Connect4Logic.Outcome.NOTHING)
        {
            return value;
        }
        else {
            int[] moves = AllowedActions(n.state);
            float[] probs = predict(n.state, n.Player);
            Connect4Node c = null;
            for (int m = 0; m < moves.length; m++) {
                if (moves[m] > 0) {
                    int[] state = new int[42];
                    for (int i = 0; i < n.state.length; i++) {
                        state[i] = n.state[i];
                    }
                    state[moves[m]-1] = n.Player;

                    c = new Connect4Node(n, state, moves[m]-1, n.Player);
                    c.P = probs[m]; //probability for the move from model
                    n.children.add(c);
                }
            }
            return probs[42];

        }
    }

    private static double[] getAV(){
        Connect4Node s = start;
        double sum = 0;
        double[] pi = new double[42];
        //double[] values = new double[42];
        for(Connect4Node n: s.children)
        {
            pi[n.move] = n.N;
            //values[n.move] = n.Q;
            System.out.print(n.W + ", " + n.Q + ", " + n.N + ", ");
        }
        System.out.println(" ");
        for(int i = 0;i < pi.length; i++)
        {
            sum += pi[i];
        }
        for(int i = 0;i < pi.length; i++)
        {
            pi[i] = pi[i]/(sum + 1);
        }
        System.out.println();
        for (int i = 0; i <= 5; ++i) {
            for (int j = 0; j <= 6; ++j) {
                System.out.print(pi[j + 7*i] + " ");
            }
            System.out.println();
        }
        System.out.println();
        return pi;
    }
    public static double[] simulate(int[] state){
        buildMCTS(state);
        for(int i = 0; i < 50; i++)
        {
            Connect4Node n = selection();

            float value = expansion(n);

            backFill(value, n.Player);
        }
        return getAV();
    }



}
