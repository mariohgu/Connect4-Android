package edu.handong.android.connect4;

import umontreal.ssj.probdist.GammaDist;

public class Connect4AiPlayer {
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private final Connect4Logic mBoardLogic;
    public Connect4AiPlayer(Connect4Logic boardLogic) {
        mBoardLogic = boardLogic;
    }
    private static int[] connActions;
    static Connect4Node start;
    private static int[][] connActualState = new int[6][7];
    private static int[] spaceFree = new int[7];
    private static Connect4Logic pBoardLogic = new Connect4Logic(connActualState, spaceFree);
    private static Connect4Logic.Outcome connOutcome = Connect4Logic.Outcome.NOTHING;

    //ARRAY TO HOLD THE PREDICTIONS AND FLOAT VALUES TO HOLD THE IMAGE DATA
    private static float[] FORECAST = new float[42];
    private static float[] VALUE = new float[42];

    /**
     * Page 289
     * The book "Intelligent Mobile Projects with TensorFlow" by Jeff Tang,
     *  *          ISBN: 978-1-78883-454-4
     * To find out the exact output node names (the input node name is specified as 'main_input'), we can add
     * print(vh) and print(ph) in model.py; now running python play.py will output the following two lines:
     * Tensor("value_head/Tanh:0",
     * Tensor("policy_head/MatMul:0"
     * We'll need them when freezing the TensorFlow checkpoint files and loading the model in mobile apps.
     */

    //PATH TO OUR MODEL FILE AND NAMES OF THE INPUT AND OUTPUT NODES
    private static String INPUT_NAME = "main_input";
    private static String OUTPUT_NAME_1 = "value_head/Tanh";
    private static String OUTPUT_NAME_2 = "policy_head/MatMul";

    private static int[] toList(int[][] array) {
        int[] newArr;
        newArr = new int[42];
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 7; j++) {
            newArr[j + 7*i] = array[i][j];
            if(newArr[j + 7*i] == 2) newArr[j + 7*i] = -1;
            }  }
        return newArr;
    }

    public static int[][] toArray(int[] array) {
        int[][] newMatrix;
        newMatrix = new int[6][7];
        for(int i = 0; i < 6; i++)  {
            for(int j = 0; j < 7; j++)  {
                newMatrix[i][j] = array[j + 7*i];
                if(newMatrix[i][j] == -1) {
                    newMatrix[i][j] = 2;
                }  }  }
        return newMatrix;
    }

    private static float[] InputArray(int[] position, int Player)  {
        float[] inpArray;
        inpArray = new float[6*7*2];
        for(int i = 0; i < position.length; i++) {
            if(position[i] == -Player) {
                inpArray[position.length + i] = 1;
            }else if(position[i] == Player)  {
                inpArray[i] = 1;
            }  }
        return inpArray;
    }

    /**
     * run ai move
     * @return column to put AI disc
     */

    public int getColumn() {
        double[] pi = simulate(toList(Connect4Controller.connGrid)); //MCTS AQUIIIIIIII EEEEEEEEEEESSSSS
        Object[] result = MaxPred(pi);
        int bestMove = (int) result[0];
        return bestMove % 7;
    }



    public static int[] AllowMovement(int[] board)
    {
        int[] movement;
        movement = new int[42];
        for(int i = 0; i < board.length; i++) {
            if( i >= board.length - 7) {
                if (board[i] == 0) {
                    movement[i] = i+1;
                }
            } else {
                if(board[i] == 0 & board[i+7] != 0) {
                    movement[i] = i+1;
                }  }  }
        return movement;
    }

    //FUNCTION TO COMPUTE THE MAXIMUM PREDICTION AND ITS CONFIDENCE
    private Object[] MaxPred(double[] array){
        int best = -1;
        double bestResult = -10.0f;
        for(int i = 0;i < array.length;i++){
            double value = array[i];
            if (value > bestResult){
                bestResult = value;
                best = i;
            }   }
        return new Object[]{best,bestResult};
    }


    public static float[] predictionTensoFlow(int[] Board, int Player){
        //Pass input into the tensorflow
        float[] inpArray = InputArray(Board, Player);
        Connect4GameActivity.tf.feed(INPUT_NAME, inpArray,1,2,6,7);

        //compute predictions
        Connect4GameActivity.tf.run(new String[]{OUTPUT_NAME_1, OUTPUT_NAME_2});

        //copy the output into the PREDICTIONS array
        Connect4GameActivity.tf.fetch(OUTPUT_NAME_1,VALUE);
        Connect4GameActivity.tf.fetch(OUTPUT_NAME_2, FORECAST);

        int[] move = AllowMovement(Board);
        float sum = 0;
        float[] probs = new float[43];
        for(int i = 0; i < FORECAST.length; i++) {
            if(move[i] == 0) {
                sum = sum + (float) Math.exp(-100);
            }else sum = sum + (float) Math.exp(FORECAST[i]);
        }
        for(int i = 0; i < FORECAST.length; i++) {
            float value = FORECAST[i];
            double odds = Math.exp(value);
            probs[i] = (float) odds/sum;
        }
        probs[42] = VALUE[0];
        return probs;
    }


    private static void Mcts(int[] position){
        Connect4Node s = start;
        if(s != null){
            for(Connect4Node n: s.children){
            if(n.Player == position[n.move]){
            if(n.children != null) {
            for(Connect4Node c : n.children) {
            if(position[c.move] == c.Player) {
              start = c;
              start.Player = -start.Player;
              return;
            }  } }  } }
        }
        NewConnect4Node(position);
    }

    private static void NewConnect4Node(int[] state){
        int turn = 1;
        if (Connect4Controller.connPlayerTurn != 1) {
            turn = -1;
        }
        start = new Connect4Node(null, state, (byte) -1, turn);
    }

    private static void backFill(float value, int actualPlayer) {
        Connect4Node node1 = start;
        int direction = 0;
        int turn = node1.Player;
        for (int action : connActions) {
            if (action == 0) {
                break;
            }
            for (Connect4Node node : node1.children) {
                node.Player = turn;
                if (node.move == action - 1) {
                    if (actualPlayer == node.Player) {
                        direction = 1;
                    } else {
                        direction = -1;
                    }
                    node.N = node.N + 1;
                    node.W = node.W + value * direction; //value
                    node.Q = node.W / node.N;
                    node1 = node;
                    break;
                }
            }
            turn = -turn;
        }
    }


    //In
    private static Connect4Node selection() { //select a leaf node return index
        double epsilon = 0;
        double connCPU = 1;
        double alpha = 0.8;
        double[] nu = new double[7];
        double[] y = new double[7];
        double Q = 0;
        double U = 0;
        int first = 1;
        int count = 0;
        connActions = new int[42];
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
                    //Computes the inverse distribution function using the algorithm implemented in the Cephes Math Library.
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

                U = connCPU * ((1 - epsilon) * n.P + epsilon * nu[j]) * Math.sqrt(Nb) / (1 + n.N);
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
            connActions[count] = simulationAction + 1;
            count += 1;
        }
        return sim;
    }

    private static float expansion(Connect4Node n) { // choose child of leaf node
        float value = -1;
        connActualState = toArray(n.state);
        connOutcome = pBoardLogic.checkWin(connActualState);
        if(connOutcome != Connect4Logic.Outcome.NOTHING)
        {
            return value;
        }
        else {
            int[] moves = AllowMovement(n.state);
            float[] probs = predictionTensoFlow(n.state, n.Player);
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
        Mcts(state);
        for(int i = 0; i < 50; i++)
        {
            Connect4Node n = selection();

            float value = expansion(n);

            backFill(value, n.Player);
        }
        return getAV();
    }



}
