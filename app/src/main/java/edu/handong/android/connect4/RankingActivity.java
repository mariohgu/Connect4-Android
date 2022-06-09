package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class RankingActivity extends AppCompatActivity {
    public int counter;
    boolean launchSounds;
    String playerName;
    public static final String PREF = "PlayerPref";
    public static final String SOUNDS = "sounds";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_ranking);
        SoundEffect clickSound=new SoundEffect(this);
        SharedPreferences preferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        launchSounds=preferences.getBoolean(SOUNDS,false);
        /**
         * computing the best scores
         */
        playerName = preferences.getString("P1","");
        TextView R1=findViewById(R.id.txtRankone);
        R1.setText(playerName);
        playerName = preferences.getString("P2","");
        TextView R2=findViewById(R.id.txtRanktwo);
        R2.setText(playerName);
        playerName = preferences.getString("Multi","");
        TextView R3=findViewById(R.id.txtRankthree);
        R3.setText(playerName);
        /**
         * End computing best scores
         */
        ImageButton game_Button = (ImageButton)findViewById(R.id.back_button);
        game_Button.setOnClickListener(view -> {
            if(launchSounds){
                clickSound.playSound();
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);}
        );

    }

    @Override
    public void onBackPressed() {
        counter++;
        if(counter==3) {
            Toast.makeText(this, R.string.alert_back, Toast.LENGTH_LONG).show();
            counter=0;
        }
    }
}