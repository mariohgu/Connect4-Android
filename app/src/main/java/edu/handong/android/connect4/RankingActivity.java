package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;


public class RankingActivity extends AppCompatActivity {
    public int counter;
    boolean launchSounds;
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