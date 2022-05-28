package edu.handong.android.connect4;
import java.util.ArrayList;

public class Connect4Node {

    int move;
    double Q;
    double N;
    double P;
    double W;
    Connect4Node parent;
    ArrayList<Connect4Node> children;
    int[] state;
    int finished = 0; //0 = not fininshed, otherwise equals winner
    int Player;

    public Connect4Node(Connect4Node parent, int[] state, int move, int Player) {
        this.parent = parent;
        this.state = state;
        this.move = move;
        this.Player = Player;
        children = new ArrayList<>();
    }
}
