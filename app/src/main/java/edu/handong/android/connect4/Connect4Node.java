package edu.handong.android.connect4;
import java.util.ArrayList;

/**
 *
 *      Page 280
 *      The book "Intelligent Mobile Projects with TensorFlow" by Jeff Tang,
 *            ISBN: 978-1-78883-454-4
 *            "first define the input and output node names, then prepare the input tensor
 *             using the values in binary"
 *
 *
 *
 */

public class Connect4Node {
    int move;
    double n1,n2,n3,n4;
    Connect4Node parent;
    ArrayList<Connect4Node> children;
    int[] state;
    int finished = 0; //0 = not finished, otherwise equals winner
    int Player;

    public Connect4Node(Connect4Node parent, int[] state, int move, int Player) {
        this.parent = parent;
        this.state = state;
        this.move = move;
        this.Player = Player;
        children = new ArrayList<>();
    }
}
