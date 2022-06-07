package edu.handong.android.connect4;

import static android.content.ContentValues.TAG;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;

import static edu.handong.android.connect4.Connect4Controller.COLS;
import static edu.handong.android.connect4.Connect4Controller.ROWS;
import static edu.handong.android.connect4.Connect4Controller.connPlayerTurn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Math.max;

/**
 * Our code is based in:
 * - The book "Intelligent Mobile Projects with TensorFlow" by Jeff Tang,
 *          ISBN: 978-1-78883-454-4
 * - The post "How to build your own AlphaZero AI using Python and Keras" by David Foster
 *          URL: https://medium.com/applied-data-science/how-to-build-your-own-alphazero-ai-using-python-and-keras-7f664945c188
 */

public class Connect4GameActivity extends AppCompatActivity{
    private View connBoardGame;
    static Connect4GameActivity connBoardView;
 //   private Connect4Controller mGameController;
  //  private Connect4Controller mListener;
    private View connBoardFrontView;
    private ImageView[][] connCells;
    public int counter;
    public static int connPlayer1 =1;
    public static int connPlayer2 =2;
    public static int firstTurnStatic;
    long sec, min;
    String timeActual;
    boolean launchSounds;
    /**
     * Name of the preferences object
     */
    public static final String PREF = "PlayerPref";
    public static final String RANKINGS = "rankings";
    public static final String SOUNDS = "sounds";

    public static boolean connMultiplayer;
    public static int discColorPlayer1;
    public static int discColorPlayer2;
    private TextView connWinnerView;
    public String draw;
    public String wins;
    public static String player1Name;
    public String player1DiscColor;
    public String modelPiecePlayer1;
 //   public String modeldiscPlayer2;
    public static String player2Name;
    public static String firstTurn;
    public boolean modeTimer;
    TextView clock;
    ToggleButton pause;
    ImageButton reset;

    public MyTimer connCrones;

    Connect4Controller connect4Controller = new Connect4Controller();


    public ImageView[][] getCells() {
        return connCells;
    }

    private static final String MODEL_FILE =
            "file:///android_asset/3_hard.pb";

    static {
        System.loadLibrary("tensorflow_inference");
    }

    static public TensorFlowInferenceInterface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        SoundEffect clickSound=new SoundEffect(this);
        SharedPreferences preferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        launchSounds=preferences.getBoolean(SOUNDS,false);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        loadPref();
        connBoardView = this;
        connBoardGame = findViewById(R.id.gameBoard);
        connBoardFrontView = findViewById(R.id.game_board_front);
        clock = findViewById(R.id.player_time);
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        AssetManager assetManager = getAssets();
        connMultiplayer = extras.getBoolean("Mode");
        modeTimer = extras.getBoolean("Timer");
        modelPiecePlayer1 = extras.getString("ModelPiece");
        if (!connMultiplayer) tf = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);

        TextView name1 = findViewById(R.id.player1_turn_label);
        player1Name = name1.getText().toString();
        if(!connMultiplayer) player2Name="Computer";
        else player2Name=extras.getString("Player2Name");
        firstTurn="Player1Turn"; //extras.getString("FirstTurn");
        player1DiscColor="Red"; //extras.getString("Player1DiscColor");


        draw = getResources().getString(R.string.draw);
        wins = getResources().getString(R.string.wins);


        if(firstTurn.equals("Player1Turn")) {
            firstTurnStatic= connPlayer1;
        }else {
            firstTurnStatic= connPlayer2;
        }

        //------------------------------------------GAME PIECE -----------------------------
        choosePiece(player1DiscColor,modelPiecePlayer1);
        //--------------------------------------------END GAME PIECE -------------------------

        //-------------------------------------NAMES BOARD---------------------------------
        turn();
        //--------------------------------------------------------------------
 //--------------------------------------------BUTTONS-------------------------------------------
        //---------------------------------CLOSE BUTTON---------------------------------------------
        ImageButton close = findViewById(R.id.back_button);
        close.setOnClickListener(view -> {
            if(launchSounds){
                clickSound.playSound();
            }
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Connect4GameActivity.this.finish();
                        Intent i= new Intent(getApplicationContext(),NewGame_Settings.class);
                        startActivity(i);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(Connect4GameActivity.this);
            builder.setMessage("Back to menu?").setPositiveButton("YES", dialogClickListener)
                    .setTitle("CONNECT 4")
                    .setNegativeButton("NO", dialogClickListener).show();

        });

// ------------------------------------END CLOSE ---------------------------------------------------

                    //-------------------------RESET BUTTON----------------------------------------

        reset = findViewById(R.id.reload_game_button);
        reset.setOnClickListener(view -> {
            if(launchSounds){
                clickSound.playSound();
            }
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        resetBoard();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(Connect4GameActivity.this);
            builder.setMessage("Reset the game ?").setPositiveButton("YES", dialogClickListener)
                    .setTitle("CONNECT 4")
                    .setNegativeButton("NO", dialogClickListener).show();
        });
                    //---------------------END RESET BUTTON ----------------------------------------

        //-------------------------PAUSE BUTTON----------------------------------------

        pause = findViewById(R.id.pause_button);
        pause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!modeTimer || connMultiplayer) return;
                if (pause.isChecked()){
                    if(launchSounds){
                        clickSound.playSound();
                    }
                    if(connCrones !=null) connCrones.cancel();
                    BoardClick(true);
                    connBoardGame.setVisibility(INVISIBLE);
                    connBoardFrontView.setVisibility(INVISIBLE);



                }
                else {
                    resumeTimer();
                    BoardClick(false);
                    connBoardGame.setVisibility(VISIBLE);
                    connBoardFrontView.setVisibility(VISIBLE);

                }

            }
        });


        //---------------------END PAUSE BUTTON ----------------------------------------

        //-------------------------SETTINGS BUTTON----------------------------------------

        ImageButton settings = findViewById(R.id.settings_button);
        settings.setOnClickListener(view -> {
            if(launchSounds){
                clickSound.playSound();
            }
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        });
        //---------------------END PAUSE BUTTON ----------------------------------------

        //---------------------------------------END---BUTTONS------------------------------------------

        initialize();
    }
    public void initialize(){
        buildCells();
    }

    public void buildCells() {
        pause.setEnabled(false);

        connWinnerView = findViewById(R.id.final_message);
        connWinnerView.setVisibility(INVISIBLE);
        connCells = new ImageView[ROWS][COLS];
        for(int r = 0; r < 6; r++){
            ViewGroup row = (ViewGroup) ((ViewGroup) connBoardGame).getChildAt(r);
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

    public void turn(){

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
            TextView name2=findViewById(R.id.player2_turn_label);
            name2.setText(player2Name);
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
            TextView name2=findViewById(R.id.player2_turn_label);
            name2.setText(player2Name);
        }

    }

    public void resetBoard() {
        initialize();
        for (ImageView[] cell : connCells) {
            for (ImageView imageView : cell) {
                imageView.setImageResource(android.R.color.transparent);
            }
        }
        if(firstTurn.equals("Player1Turn")) {
            firstTurnStatic= connPlayer1;
        }else {
            firstTurnStatic= connPlayer2;
        }
        connBoardGame.setVisibility(VISIBLE);
        connBoardFrontView.setVisibility(VISIBLE);
        pause.setChecked(false);
        choosePiece(player1DiscColor,modelPiecePlayer1);
        turn();
        showWinStatus(Connect4Logic.Outcome.NOTHING, null);
    }

    public void BoardClick(boolean value){
        if(!connMultiplayer){
            LinearLayout myLayout = findViewById(R.id.game_board_front);
            for( int i = 0; i < myLayout.getChildCount();  i++ ) {
                View view = myLayout.getChildAt(i);
                view.setClickable(value);
            }
            LinearLayout myLayout2 = findViewById(R.id.gameBoard);
            for( int i = 0; i < myLayout2.getChildCount();  i++ ) {
                View view = myLayout2.getChildAt(i);
                view.setClickable(value);
            }    }
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
        return connBoardView;
    }

    public void progressBarSwap(int playerTurn)
    {
        if(playerTurn==1)
        {
            ProgressBar progressBar1= findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(INVISIBLE);
            ProgressBar progressBar2=findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(VISIBLE);
            pause.setEnabled(false);
            BoardClick(true);
            if(!connMultiplayer) reset.setEnabled(false);
            if(connCrones !=null) connCrones.cancel();
        }else {
            ProgressBar progressBar1= findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(VISIBLE);
            ProgressBar progressBar2= findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(INVISIBLE);
            BoardClick(false);
            if(!connMultiplayer) reset.setEnabled(true);
            if(modeTimer) pause.setEnabled(true);
            connCrones = new MyTimer(10000, 1000);
            connCrones.start();
        }
    }

    public void choosePiece(String color, String model){
        if(color.equals("Red")) {
            switch (model){
                case("Classic"):
                    discColorPlayer1=R.drawable.red_disc_image_round;
                    discColorPlayer2=R.drawable.yellow_disc_image_round;
                    break;
                case ("Poker"):
                    discColorPlayer1=R.drawable.red_disc_image_round_poker;
                    discColorPlayer2=R.drawable.yellow_disc_image_round_poker;
                    break;
            }
        }else {
            switch (model){
                case("Classic"):
                    discColorPlayer1=R.drawable.yellow_disc_image_round;
                    discColorPlayer2=R.drawable.red_disc_image_round;
                    break;
                case ("Poker"):
                    discColorPlayer1=R.drawable.yellow_disc_image_round_poker;
                    discColorPlayer2=R.drawable.red_disc_image_round_poker;
                    break;
            }
        }
    }

    //////////////////////////////
    public class MyTimer extends CountDownTimer {
      //  TextView clock = findViewById(R.id.player_time);


        public MyTimer(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);


        }


        @Override
        public void onFinish() {
            if(modeTimer && !connMultiplayer){
                BoardClick(false);
            connect4Controller.togglePlayer(connPlayerTurn);
            connect4Controller.aiTurn();
                BoardClick(true);
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            if(modeTimer && !connMultiplayer) {
                NumberFormat f = new DecimalFormat("00");

                min = (millisUntilFinished / 60000) % 60;
                sec = (millisUntilFinished / 1000) % 60;
                System.out.println(f.format(sec));
                timeActual = f.format(min) + ":" + f.format(sec);

                clock.setText(timeActual);
            }

        }
    }

    private void resumeTimer() {
        int seco = Integer.parseInt((timeActual.charAt(timeActual.length()-1)+"000"));
        NumberFormat f = new DecimalFormat("00");
        long minu = (seco / 60000) % 60;
        long secon = (seco / 1000) % 60;

        connCrones = new MyTimer(seco, 1000);
        connCrones.start();
        clock.setText(f.format(minu) + ":" + f.format(secon));

    }
    ///////////////////////////




    @Override
    public void onBackPressed() {
        counter++;
        if(counter==3) {
            Toast.makeText(this, R.string.alert_back, Toast.LENGTH_LONG).show();
            counter=0;
        }
    }

    @SuppressLint("SetTextI18n")
    public void showWinStatus(Connect4Logic.Outcome outcome, ArrayList<ImageView> winDiscs) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, outcome.name());
        }
        if (outcome != Connect4Logic.Outcome.NOTHING) {
            System.out.println("Hello inside outcome");
            connWinnerView.setVisibility(VISIBLE);
            if(connCrones !=null) connCrones.cancel();
            pause.setEnabled(false);
            ProgressBar progressBar1=findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(INVISIBLE);
            ProgressBar progressBar2=findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(INVISIBLE);
            SharedPreferences preferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
            switch (outcome) {
                case DRAW:
                    connWinnerView.setText(draw);
                  
                    for(int r = 0; r < 6; r++){
                        ViewGroup row = (ViewGroup) ((ViewGroup) connBoardGame).getChildAt(r);
                        row.setClipChildren(false);
                        for (int c = 0; c < 7; c++){
                            ImageView imageView = (ImageView) row.getChildAt(c);
                            imageView.setOnClickListener(null);
                        }
                    }
                    break;
                case PLAYER1_WINS:
                    System.out.println("Hello inside player1");
                    connWinnerView.setText(player1Name+" "+wins);
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
                        ViewGroup row = (ViewGroup) ((ViewGroup) connBoardGame).getChildAt(r);
                        row.setClipChildren(false);
                        for (int c = 0; c < 7; c++){
                            ImageView imageView = (ImageView) row.getChildAt(c);
                            imageView.setOnClickListener(null);
                        }
                    }
                    break;
                case PLAYER2_WINS:

                    connWinnerView.setText(player2Name+" "+wins);
                    for (ImageView winDisc : winDiscs) {
                        if(player1DiscColor.equals("Red"))
                        {
                            winDisc.setImageResource(R.drawable.win_yellow);
                        }else {
                            winDisc.setImageResource(R.drawable.win_red);
                        }

                    }

                    for(int r = 0; r < 6; r++){
                        ViewGroup row = (ViewGroup) ((ViewGroup) connBoardGame).getChildAt(r);
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
            connWinnerView.setVisibility(INVISIBLE);
        }
    }




    private void loadPref(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PlayerPref", Context.MODE_PRIVATE);
        String player = preferences.getString("player1", "");
        TextView player_name = findViewById(R.id.player1_turn_label);
        player_name.setText(player);

    }




}

