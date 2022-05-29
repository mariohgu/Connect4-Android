package edu.handong.android.connect4;

import static android.content.ContentValues.TAG;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;

import static edu.handong.android.connect4.Connect4Controller.COLS;
import static edu.handong.android.connect4.Connect4Controller.ROWS;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.ArrayList;

import static java.lang.Math.max;

/**
 * Our code is based in:
 * - The book "Intelligent Mobile Projects with TensorFlow" by Jeff Tang,
 *          ISBN: 978-1-78883-454-4
 * - The post "How to build your own AlphaZero AI using Python and Keras" by David Foster
 *          URL: https://medium.com/applied-data-science/how-to-build-your-own-alphazero-ai-using-python-and-keras-7f664945c188
 */

public class Connect4GameActivity extends AppCompatActivity{
    private View connBoardView;
    static Connect4GameActivity boardView;
    private Connect4Controller mGameController;
    private Connect4Controller mListener;
    private View connBoardFrontView;
    private ImageView[][] connCells;
    public static int connPlayer1 =1;
    public static int connPlayer2 =2;
    public static int firstTurnStatic;
    public static int connMode;
    public static int discColorPlayer1;
    public static int discColorPlayer2;
 //   private TextView connWinnerView;
    public static String player1Name;
    public String player1DiscColor;
    public static String player2Name;
    public ImageView[][] getCells() {
        return connCells;
    }

    private static final String MODEL_FILE =
            "file:///android_asset/alphazero19.pb";

    static {
        System.loadLibrary("tensorflow_inference");
    }
    static public TensorFlowInferenceInterface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        loadPref();
        boardView = this;
        connBoardView = findViewById(R.id.gameBoard);
        connBoardFrontView = findViewById(R.id.game_board_front);
        TextView clock = findViewById(R.id.player_time);
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        AssetManager assetManager = getAssets();
        tf = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
        connMode = extras.getInt("Mode");
     //     player1Name=extras.getString("Player1Name");
   //     player2Name=extras.getString("Player2Name");
        String firstTurn="Player1Turn"; //extras.getString("FirstTurn");
        player1DiscColor="Red"; //extras.getString("Player1DiscColor");
        if(firstTurn.equals("Player1Turn")) {
            firstTurnStatic= connPlayer1;
        }else {
            firstTurnStatic= connPlayer2;
        }
        if(player1DiscColor.equals("Red")) {
            discColorPlayer1=R.drawable.red_disc_image_round;
            discColorPlayer2=R.drawable.yellow_disc_image_round;
        }else {
            discColorPlayer1=R.drawable.yellow_disc_image_round;
            discColorPlayer2=R.drawable.red_disc_image_round;
        }
        if(firstTurnStatic==1) {
            ImageView imageView1=findViewById(R.id.player1_disc);
            imageView1.setImageResource(discColorPlayer1);
            ImageView imageView2=findViewById(R.id.player2_disc);
            imageView2.setImageResource(discColorPlayer2);
            ProgressBar progressBar1=findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(VISIBLE);
            ProgressBar progressBar2=findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(INVISIBLE);
          //  TextView textView1=(TextView) findViewById(R.id.player1_name);
         //   textView1.setText(player1Name);
        //    TextView textView2=(TextView) findViewById(R.id.player2_name);
         //   textView2.setText(player2Name);
        }else {
            ImageView imageView1=findViewById(R.id.player1_disc);
            imageView1.setImageResource(discColorPlayer1);
            ImageView imageView2=findViewById(R.id.player2_disc);
            imageView2.setImageResource(discColorPlayer2);
            ProgressBar progressBar1= findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(INVISIBLE);
            ProgressBar progressBar2=findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(VISIBLE);
         //   TextView textView1=(TextView) findViewById(R.id.player1_name);
         //   textView1.setText(player1Name);
         //   TextView textView2=(TextView) findViewById(R.id.player2_name);
         //   textView2.setText(player2Name);
        }
 //--------------------------------------------BUTTONS-------------------------------------------
        //---------------------------------CLOSE BUTTON---------------------------------------------
        ImageButton close = findViewById(R.id.back_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent i= new Intent(getApplicationContext(),NewGame_Settings.class);
                                startActivity(i);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Connect4GameActivity.this);
                builder.setMessage("BACK TO MENU?").setPositiveButton("YES", dialogClickListener)
                        .setTitle("CONNECT 4 - HANDONG")
                        .setNegativeButton("NO", dialogClickListener).show();

            }
        });

// ------------------------------------END CLOSE ---------------------------------------------------

                    //-------------------------RESET BUTTON----------------------------------------

        ImageButton reset = findViewById(R.id.reload_game_button);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                initialize();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Connect4GameActivity.this);
                builder.setMessage("RESET BOARD?").setPositiveButton("YES", dialogClickListener)
                        .setTitle("CONNECT 4 - HANDONG")
                        .setNegativeButton("NO", dialogClickListener).show();
            }

        });
                    //---------------------END RESET BUTTON ----------------------------------------



        //---------------------------------------END---BUTTONS------------------------------------------


        initialize();
    }
    public void initialize(){
        buildCells();
    }

    public void buildCells() {
        if(firstTurnStatic==1) {
            ImageView imageView1=findViewById(R.id.player1_disc);
            imageView1.setImageResource(discColorPlayer1);
            ImageView imageView2=findViewById(R.id.player2_disc);
            imageView2.setImageResource(discColorPlayer2);
            ProgressBar progressBar1=findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(VISIBLE);
            ProgressBar progressBar2=findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(INVISIBLE);
            //  TextView textView1=(TextView) findViewById(R.id.player1_name);
            //   textView1.setText(player1Name);
            //    TextView textView2=(TextView) findViewById(R.id.player2_name);
            //   textView2.setText(player2Name);
        }else {
            ImageView imageView1 = findViewById(R.id.player1_disc);
            imageView1.setImageResource(discColorPlayer1);
            ImageView imageView2 = findViewById(R.id.player2_disc);
            imageView2.setImageResource(discColorPlayer2);
            ProgressBar progressBar1 = findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(INVISIBLE);
            ProgressBar progressBar2 = findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(VISIBLE);
            //   TextView textView1=(TextView) findViewById(R.id.player1_name);
            //   textView1.setText(player1Name);
            //   TextView textView2=(TextView) findViewById(R.id.player2_name);
            //   textView2.setText(player2Name);
        }

     /**   connWinnerView = (TextView) findViewById(R.id.winner_text);
        connWinnerView.setVisibility(INVISIBLE); */
        connCells = new ImageView[ROWS][COLS];
        for(int r = 0; r < 6; r++){
            ViewGroup row = (ViewGroup) ((ViewGroup) connBoardView).getChildAt(r);
            ViewGroup row1 = (ViewGroup) ((ViewGroup) connBoardFrontView).getChildAt(r);
            row.setClipChildren(false);
            for (int c = 0; c < 7; c++){
                ImageView imageView = (ImageView) row.getChildAt(c);
                ImageView imageView1 = (ImageView) row1.getChildAt(c);
                imageView1.setBackgroundResource(R.color.transparenttwo);
                imageView.setImageResource(R.color.white);
                imageView.setOnClickListener(new Connect4Controller());
                connCells[r][c] = imageView;
                Log.d("comp",imageView.toString());
            }
        }
    }



    public void dropDisc(int row, int col,int playerTurn) {
        final ImageView cell = connCells[row][col];
        float move = -(cell.getHeight() * row + cell.getHeight() + 15);
        cell.setY(move);
        cell.setImageResource(playerTurn == connPlayer1 ? discColorPlayer1 : discColorPlayer2);
        cell.animate().translationY(0).setInterpolator(new BounceInterpolator()).start();
    }

    public int colAtX(float x) {
        float colWidth = connCells[0][0].getWidth();
        System.out.println("Col width "+colWidth); //send the width of the column
        int col = (int) x / (int) colWidth;
        System.out.println("Column "+col); //number of the column choose
        if (col < 0)
            return 0;
        if (col > 6)
            return 6;
        return col;
    }
    public static Connect4GameActivity getInstance(){
        return   boardView;
    }

    public void progressBarSwap(int playerTurn)
    {
        if(playerTurn==1)
        {
            ProgressBar progressBar1= findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(INVISIBLE);
            ProgressBar progressBar2=findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(VISIBLE);
        }else {
            ProgressBar progressBar1= findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(VISIBLE);
            ProgressBar progressBar2= findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(INVISIBLE);
        }
    }

    public void showWinStatus(Connect4Logic.Outcome outcome, ArrayList<ImageView> winDiscs) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, outcome.name());
        }
        if (outcome != Connect4Logic.Outcome.NOTHING) {
            System.out.println("Hello inside outcome");
        //    connWinnerView.setVisibility(VISIBLE);
            ProgressBar progressBar1=findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(INVISIBLE);
            ProgressBar progressBar2=findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(INVISIBLE);
            switch (outcome) {
                case DRAW:
                  //  connWinnerView.setText("DRAW");
                    for(int r = 0; r < 6; r++){
                        ViewGroup row = (ViewGroup) ((ViewGroup) connBoardView).getChildAt(r);
                        row.setClipChildren(false);
                        for (int c = 0; c < 7; c++){
                            ImageView imageView = (ImageView) row.getChildAt(c);
                            imageView.setOnClickListener(null);
                        }
                    }
                    break;
                case PLAYER1_WINS:
                    System.out.println("Hello inside player1");
                 //   connWinnerView.setText(player1Name+" WINS!");
                    for (ImageView winDisc : winDiscs) {
                        if(player1DiscColor.equals("Red"))
                        {
                            winDisc.setImageResource(R.drawable.win_red);
                        }else {
                            winDisc.setImageResource(R.drawable.win_yellow);
                        }

                    }

                    for(int r = 0; r < 6; r++){
                        ViewGroup row = (ViewGroup) ((ViewGroup) connBoardView).getChildAt(r);
                        row.setClipChildren(false);
                        for (int c = 0; c < 7; c++){
                            ImageView imageView = (ImageView) row.getChildAt(c);
                            imageView.setOnClickListener(null);
                        }
                    }
                    break;
                case PLAYER2_WINS:
              //      connWinnerView.setText(player2Name+" WINS!");
                    for (ImageView winDisc : winDiscs) {
                        if(player1DiscColor.equals("Red"))
                        {
                            winDisc.setImageResource(R.drawable.win_yellow);
                        }else {
                            winDisc.setImageResource(R.drawable.win_red);
                        }

                    }

                    for(int r = 0; r < 6; r++){
                        ViewGroup row = (ViewGroup) ((ViewGroup) connBoardView).getChildAt(r);
                        row.setClipChildren(false);
                        for (int c = 0; c < 7; c++){
                            ImageView imageView = (ImageView) row.getChildAt(c);
                            imageView.setOnClickListener(null);
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
         //   connWinnerView.setVisibility(INVISIBLE);
        }
    }

    private void loadPref(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PlayerPref", Context.MODE_PRIVATE);
        String player = preferences.getString("player1", "");
        TextView player_name = findViewById(R.id.player_turn_label);
        player_name.setText(player);

    }




}

