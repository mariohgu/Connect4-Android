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


/**
 * Our code is based in:
 * - The book "Intelligent Mobile Projects with TensorFlow" by Jeff Tang,
 *          ISBN: 978-1-78883-454-4
 * - The post "How to build your own AlphaZero AI using Python and Keras" by David Foster
 *          URL: https://medium.com/applied-data-science/how-to-build-your-own-alphazero-ai-using-python-and-keras-7f664945c188
 */

public class Connect4GameActivity extends AppCompatActivity{
    /** board views (cell frame)     */
    private View connBoardGame,connBoardFrontView;
    static Connect4GameActivity connBoardView;
    private ImageView[][] connCells;
    /** int variables for a counter and user points */
    public int counter,poi1,poi2;;
    /** assign the number 1 to player 1   */
    public static int connPlayer1 =1, firstTurnStatic=1;
    /** variables of seconds and minutes for the clock */
    long sec, min;
    /** Name of the preferences object  */
    public static final String PREF = "PlayerPref";
 //   public static final String RANKINGS = "rankings";
    public static final String SOUNDS = "sounds";
    /** Boolean for the multiplayer mode */
    public static boolean connMultiplayer;
    /** variables int to identify the R.id of the disc in drawable folder */
    public static int discColorPlayer1,discColorPlayer2;
    public static String player1Name,player2Name;
    public String player1DiscColor,modelPiecePlayer1, draw,wins,timeActual;
    /** boolean to declare if the user chose to play with timer or/and sound  */
    public boolean modeTimer,launchSounds;
    TextView clock, connWinnerView;
    ToggleButton pause;
    ImageButton reset;
    public MyTimer connCrones;
    /** set variable to use the tensorflow library */
    static public TensorFlowInferenceInterface tf;
    private static final String MODEL_FILE = "file:///android_asset/finalModel.pb";

    Connect4Controller connect4Controller = new Connect4Controller();
    public ImageView[][] getCells() {
        return connCells;
    }
    static { System.loadLibrary("tensorflow_inference"); }


    /**
     * In the onCreate method, we create the objects of the game, Preferences, intents, buttons, Views.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        connBoardView = this;
        connBoardGame = findViewById(R.id.gameBoard);
        connBoardFrontView = findViewById(R.id.game_board_front);
        clock = findViewById(R.id.player_time);
        SoundEffect clickSound=new SoundEffect(this);
        //Preferences
        SharedPreferences preferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        launchSounds=preferences.getBoolean(SOUNDS,false);
        String player = preferences.getString("player1", "");
        TextView player_name = findViewById(R.id.player1_turn_label);
        player_name.setText(player);
        //Intent (get the information from newgame settings)
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        connMultiplayer = extras.getBoolean("Mode");
        modeTimer = extras.getBoolean("Timer");
        modelPiecePlayer1 = extras.getString("ModelPiece");
        player1DiscColor="Red";
        if(!connMultiplayer) player2Name="ROBOT";
        else player2Name=extras.getString("Player2Name");
        //Create the tf object based in the tensorflow algorithm
        AssetManager assetManager = getAssets();
        if (!connMultiplayer) tf = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
        player1Name = player_name.getText().toString();
        draw = getResources().getString(R.string.draw);
        wins = getResources().getString(R.string.wins);
        choosePiece(player1DiscColor,modelPiecePlayer1);
        turn();

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
                        if(connCrones!=null) connCrones.cancel();
                        Intent i= new Intent(getApplicationContext(),NewGame_Settings.class);
                        startActivity(i);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }  };
            AlertDialog.Builder builder = new AlertDialog.Builder(Connect4GameActivity.this);
            builder.setMessage("Back to menu?").setPositiveButton( getResources().getString(R.string.Yes), dialogClickListener)
                    .setTitle("CONNECT 4")
                    .setNegativeButton(getResources().getString(R.string.No), dialogClickListener).show();
        });

// ------------------------------------END CLOSE BUTTON ---------------------------------------------------
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
            builder.setMessage("Reset the game ?").setPositiveButton(getResources().getString(R.string.Yes), dialogClickListener)
                    .setTitle("CONNECT 4")
                    .setNegativeButton(getResources().getString(R.string.No), dialogClickListener).show();
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

    /**
     * In this class we are building the visual board
     */
    public void initialize(){
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

    /**
     * Class to set the disc, progressbar and name of every player depending of the number choose
     * (next feature)
     */

    public void turn(){
        if(firstTurnStatic==1) {
            ImageView disc1=findViewById(R.id.player1_disc);
            disc1.setImageResource(discColorPlayer1);
            ImageView disc2=findViewById(R.id.player2_disc);
            disc2.setImageResource(discColorPlayer2);
            ProgressBar progressBar1=findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(VISIBLE);
            ProgressBar progressBar2=findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(INVISIBLE);
            TextView name2=findViewById(R.id.player2_turn_label);
            name2.setText(player2Name);
        }else {
            ImageView disc1=findViewById(R.id.player1_disc);
            disc1.setImageResource(discColorPlayer1);
            ImageView disc2=findViewById(R.id.player2_disc);
            disc2.setImageResource(discColorPlayer2);
            ProgressBar progressBar1= findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(INVISIBLE);
            ProgressBar progressBar2=findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(VISIBLE);
           TextView name2=findViewById(R.id.player2_turn_label);
            name2.setText(player2Name);
        }

    }

    /**
     * Class to reset all the settings and visual in the board.
     */

    public void resetBoard() {
        if(connCrones!=null) connCrones.cancel();
        initialize();
        for (ImageView[] cell : connCells) {
            for (ImageView imageView : cell) {
                imageView.setImageResource(android.R.color.transparent);
            }
        }
        firstTurnStatic= connPlayer1;
        connBoardGame.setVisibility(VISIBLE);
        connBoardFrontView.setVisibility(VISIBLE);
        pause.setChecked(false);
        choosePiece(player1DiscColor,modelPiecePlayer1);
        turn();
        showWinStatus(Connect4Logic.Outcome.NOTHING, null);
    }

    /**
     * Class to block the click in the board.
     * @param value boolean
     */

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

    /**
     * Class to drop the disc in the column and set the animation
     * @param row selected the row
     * @param col selected the column
     * @param playerTurn who is the player for the color
     */
    public void dropDisc(int row, int col,int playerTurn) {
        final ImageView cell = connCells[row][col];
        float move = -(cell.getHeight() * row + cell.getHeight() + 15);
        cell.setY(move);
        cell.setImageResource(playerTurn == connPlayer1 ? discColorPlayer1 : discColorPlayer2);
        cell.animate().translationY(0).setInterpolator(new BounceInterpolator()).start();
    }

    /**
     * Get the column where the user did the click
     * @param x
     * @return column
     */

    public int colChosen(float x) {
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

    public static Connect4GameActivity getInstance(){ return connBoardView; }

    /**
     * The method to set the visual animation and deactivated the click on the board depending
     * of the user turn
     * @param playerTurn get who is the player
     */

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

    /**
     * Set the color of the player pieces
     * @param color of the piece
     * @param model in this version we put to model, but we can add more in this method
     */
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

    /**
     * My timer is a class extends of the CountDownTimer. in here we set the timer and if the time finish
     * swap the turn to the "robot"
     */
    public class MyTimer extends CountDownTimer {
        public MyTimer(long millisInFuture, long countDownInterval) { super(millisInFuture, countDownInterval); }
        @Override
        public void onFinish() {
            if(modeTimer && !connMultiplayer){
                BoardClick(false);
                connect4Controller.togglePlayer(connPlayerTurn);
                connect4Controller.robotTurn();
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


    /**
     * Override the back button of the Android to force the user to use the app buttons
     */
    @Override
    public void onBackPressed() {
        counter++;
        if(counter==3) {
            Toast.makeText(this, R.string.alert_back, Toast.LENGTH_LONG).show();
            counter=0;
        }
    }

    /**
     * This method is for set all the configuration when the player1 win, player 2 win, draw or if
     * the game continues.
     * @param outcome get the status from the Connect4Logic
     * @param winDiscs set an array for the change the visual color of the win discs
     */
    @SuppressLint("SetTextI18n")
    public void showWinStatus(Connect4Logic.Outcome outcome, ArrayList<ImageView> winDiscs) {
        SharedPreferences preferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        int p1,p2,p3;

        if (preferences.contains("P1")){
            String score1=preferences.getString("P1","");
            String [] s1=score1.split("-");
            s1[1]=s1[1].trim();
            p1=Integer.parseInt(s1[1]);
        }
        else p1=0;
        if (preferences.contains("P2")){
            String score2=preferences.getString("P2","");
            String [] s2=score2.split("-");
            s2[1]=s2[1].trim();
            p2=Integer.parseInt(s2[1]);
        }
        else p2=0;

        if (preferences.contains("Multi")){
            String scoreMulti=preferences.getString("Multi","");
            String [] s3=scoreMulti.split("-");
            s3[1]=s3[1].trim();
            p3=Integer.parseInt(s3[1]);
        }
        else p3=0;


        if (BuildConfig.DEBUG) {
            Log.e(TAG, outcome.name());
        }
        if (outcome != Connect4Logic.Outcome.NOTHING) {
            System.out.println("Checking if the status of the Connect4 game changes");
            connWinnerView.setVisibility(VISIBLE);
            if(connCrones !=null) connCrones.cancel();
            pause.setEnabled(false);
            ProgressBar progressBar1=findViewById(R.id.player1_indicator);
            progressBar1.setVisibility(INVISIBLE);
            ProgressBar progressBar2=findViewById(R.id.player2_indicator);
            progressBar2.setVisibility(INVISIBLE);

            switch (outcome) {
                case DRAW:
                    connWinnerView.setText(draw);
                    for(int r = 0; r < 6; r++){
                        ViewGroup row = (ViewGroup) ((ViewGroup) connBoardGame).getChildAt(r);
                        row.setClipChildren(false);
                        for (int c = 0; c < 7; c++){
                            ImageView imageView = (ImageView) row.getChildAt(c);
                            imageView.setOnClickListener(null);
                        }   }
                    break;
                case PLAYER1_WINS:
                    poi1=connect4Controller.getPointsPlayer1()+15;
                    connWinnerView.setText(player1Name+" "+wins);
                    for (ImageView winDisc : winDiscs) {
                        if(player1DiscColor.equals("Red"))
                        {
                            winDisc.setImageResource(R.drawable.win_red);
                        }else {
                            winDisc.setImageResource(R.drawable.win_yellow);
                        } }
                    //Saving the PLAYER 1 scores into the preferences object
                    if (p1< poi1) editor.putString("P1",player1Name+" - "+poi1);
                    if (connMultiplayer){
                        if (p3< connect4Controller.getPointsPlayer2()) editor.putString("Multi",player2Name+" - "+connect4Controller.getPointsPlayer2());
                    }
                    else {
                        if (p2< connect4Controller.getPointsPlayer2())
                            editor.putString("P2",player2Name+" - "+connect4Controller.getPointsPlayer2());
                    }
                    //End savings scores
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
                    //Either computer or the second player when in multiplayer mode
                    poi2=connect4Controller.getPointsPlayer2()+15;
                    connWinnerView.setText(player2Name+" "+wins);
                    for (ImageView winDisc : winDiscs) {
                        if(player1DiscColor.equals("Red"))
                        {
                            winDisc.setImageResource(R.drawable.win_yellow);
                        }else {
                            winDisc.setImageResource(R.drawable.win_red);
                        }

                    }
                    //Saving the PLAYER 1 scores into the preferences object
                    if (p1< poi1) editor.putString("P1",player1Name+" - "+connect4Controller.getPointsPlayer1());
                    if (connMultiplayer){
                        if (p3< poi2) editor.putString("Multi",player2Name+" - "+poi2);
                    }
                    else {
                        if (p2< poi2)
                            editor.putString("P2",player2Name+" - "+poi2);
                    }
                    //End savings scores
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
        editor.apply();
    }
}

