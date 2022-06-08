package edu.handong.android.connect4;

import umontreal.ssj.probdist.GammaDist;

public class Connect4RobotPlayer {

    /** Loading the tensorFlow library */
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private final Connect4Logic mBoardLogic;
    public Connect4RobotPlayer(Connect4Logic boardLogic) {
        mBoardLogic = boardLogic;
    }
    private static int[] connActions;
    static Connect4Node start;
    private static int[][] connActualState = new int[6][7];
    private static int[] spaceFree = new int[7];
    private static Connect4Logic pBoardLogic = new Connect4Logic(connActualState, spaceFree);
    private static Connect4Logic.Outcome connOutcome = Connect4Logic.Outcome.NOTHING;
    private static float[] FORECAST = new float[42];
    private static float[] VALUE = new float[42];

    /**
     * Page 289
     * The book "Intelligent Mobile Projects with TensorFlow" by Jeff Tang,
     *  *          ISBN: 978-1-78883-454-4
     * To find out the exact output node names (the input node name is specified as 'main_input'), we can add
     * print(vh) and print(ph) in model.py; now running python play.py will output the following two lines:
     * PATH TO OUR MODEL FILE AND NAMES OF THE INPUT AND OUTPUT NODES
     * Tensor("value_head/Tanh:0",
     * Tensor("policy_head/MatMul:0"
     * We'll need them when freezing the TensorFlow checkpoint files and loading the model in mobile apps.
     */

    private static String INPUT_NAME = "main_input";
    private static String OUTPUT_NAME_1 = "value_head/Tanh";
    private static String OUTPUT_NAME_2 = "policy_head/MatMul";

    /**
     * In this class, the robot chooses the best column to put the disc
     * @return column chosen
     *
     * @ M .F
     */

    public int getColumn() {
        double[] pi = simulate(toList(Connect4Controller.connGrid));
        Object[] result = MaxPred(pi);
        int bestMove = (int) result[0];
        return bestMove % 7;
    }

    /**
     * In this method, the "robot will "know" if it can put the disc in a column, for
     * example if the column has 7 disc in the column, it would be a prohibited movement
     * @param board
     * @return movement
     */

    public static int[] AllowMovement(int[] board)  {
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

    /**In this method the robot calculate the maximun prediction
     * @ M.F
     * @param array
     * @return
     */
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

    /**
     * In here we transform the array in a List to later send it to a getcolum class, and
     * choose the better column where the robot will set the piece.
     *
     * @param array
     * @return
     */
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

    /**
     * This method take the column number (list) in an array and put this in a node
     * @param array
     * @return the new matrix in a matrix
     */

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

    /**
     * "This is iteration of MCTS given a specific root node in the game tree.
     * In practice, we can do as many iterations as possible of these 4 steps,
     * during which in the simulation step multiple rollouts can be done.
     * After computation time or resources are exhausted, we stop the iteration
     * loop, and decide what is the next move to take, then we end up with a new
     * root node, and run the iterations again"
     * The book "Intelligent Mobile Projects with TensorFlow" by Jeff Tang,
     *      *  *          ISBN: 978-1-78883-454-4
     * @param position
     */

    private static void Mcts(int[] position){
        Connect4Node s = start;
        if(s != null){
         for(Connect4Node node: s.children){
         if(node.Player == position[node.move]){
         if(node.children != null) {
         for(Connect4Node node1 : node.children) {
         if(position[node1.move] == node1.Player) {
           start = node1;
           start.Player = -start.Player;
            return;
       }  } }  } }
        }
        NewConnect4Node(position);
    }

    private static double[] getAV(){
        Connect4Node node1 = start;
        double sum = 0;
        double[] piece = new double[42];
        for(Connect4Node node: node1.children) {
            piece[node.move] = node.n2;       }
        for(int i = 0;i < piece.length; i++) {
            sum += piece[i];        }
        for(int i = 0;i < piece.length; i++) {
            piece[i] = piece[i]/(sum + 1);   }
        return piece;
    }


    public static double[] simulate(int[] state){
        Mcts(state);
        for(int i = 0; i < 50; i++) {
            Connect4Node n = selection();
            float value = expansion(n);
            fillingBack(value, n.Player);
        }
        return getAV();
    }


    /**
     * In this class we will pass the input (Board) to Tensorflow, and with the model the
     * robot will compute the predictions ,making a copy (fetch) into de prediction array
     *
     * @param Board
     * @param Player
     * @return probs
     */


    public static float[] predictionTensoFlow(int[] Board, int Player){
        float[] inpArray = InputArray(Board, Player);
        Connect4GameActivity.tf.feed(INPUT_NAME, inpArray,1,2,6,7);
        Connect4GameActivity.tf.run(new String[]{OUTPUT_NAME_1, OUTPUT_NAME_2});
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

    /**
     * if the turn is different than 1, we set the turn as -1 and set a new node and wait for the
     * "robot" turn.
     * @param state
     */


    private static void NewConnect4Node(int[] state){
        int turn = 1;
        if (Connect4Controller.connPlayerTurn != 1) {
            turn = -1;
        }
        start = new Connect4Node(null, state, (byte) -1, turn);
    }

    private static void fillingBack(float value, int actualPlayer) {
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
                    node.n2 = node.n2 + 1;
                    node.n4 = node.n4 + value * direction; //value
                    node.n1 = node.n4 / node.n2;
                    node1 = node;
                    break;
                }
            }
            turn = -turn;
        }
    }


    /**
     * We will choose the the leaf node and return the index, also in the line "GammaDist.inverseF(alpha, 1.0, 1, Math.random());"
     * it computes the inverse distribution function using the algorithm implemented in the Cephes Math Library.
     *
     *
     * @return
     */
    private static Connect4Node selection() {
        double psi = 0,connCPU = 1, alpha = 0.8, firstNode = 0,secondNode = 0;
        double[] colu = new double[7];
        double[] nextcolu = new double[7];
        int first = 1,count = 0, probableMove = 0;
        connActions = new int[42];
        Connect4Node node1 = start;
        Connect4Node probableEdge = start;
        Connect4Node sim = node1;
        while (sim.children.size() != 0) {
            double maxQU = -9999;
            if (first == 1) {
                double sum = 0;
                for (int i = 0; i < sim.children.size(); i++) {
                    psi = 0.2;
                    nextcolu[i] = GammaDist.inverseF(alpha, 1.0, 1, Math.random());
                    sum += nextcolu[i];  }
                for(int i = 0; i < nextcolu.length; i++){
                    colu[i] = nextcolu[i]/sum; }
            } else {
                for (int i = 0; i < sim.children.size(); i++) {
                    psi = 0;
                    colu[i] = 0;
                }  }
            double Nb = 0;
            for (Connect4Node child : sim.children) {
                Nb = Nb + child.n2;
            }
            double maxP = 0;
            for (int j = 0; j < sim.children.size(); j++) {
                Connect4Node node = sim.children.get(j);
                secondNode = connCPU * ((1 - psi) * node.n3 + psi * colu[j]) * Math.sqrt(Nb) / (1 + node.n2);
                firstNode = node.n1;
                if(firstNode + secondNode > maxQU) {
                    maxQU = firstNode + secondNode;
                    maxP = node.n3;
                    probableMove = node.move;
                    probableEdge = node;
                }
            }
            probableEdge.state[probableMove] = probableEdge.Player;
            probableEdge.Player = -probableEdge.Player;
            first = 0;
            sim = probableEdge;
            connActions[count] = probableMove + 1;
            count += 1;
        }
        return sim;
    }

    /**
     * With this method the robot chooses child of leaf node and probability for the move from model
     * @param node
     * @return
     */

    private static float expansion(Connect4Node node) {
        float value = -1;
        connActualState = toArray(node.state);
        connOutcome = pBoardLogic.checkWin(connActualState);
        if(connOutcome != Connect4Logic.Outcome.NOTHING) return value;
        else {
            int[] moves = AllowMovement(node.state);
            float[] probs = predictionTensoFlow(node.state, node.Player);
            Connect4Node node0 = null;
            for (int m = 0; m < moves.length; m++) {
                if (moves[m] > 0) {
                    int[] state = new int[42];
                    for (int i = 0; i < node.state.length; i++) {
                        state[i] = node.state[i];
                    }
                    state[moves[m]-1] = node.Player;
                    node0 = new Connect4Node(node, state, moves[m]-1, node.Player);
                    node0.n3 = probs[m];
                    node.children.add(node0);
                }
            }
            return probs[42];

        }
    }




}
