package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class multiplayer extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        SoundEffect clickSound=new SoundEffect(this);

        ImageButton home_Button = (ImageButton)findViewById(R.id.newgamehome);
        home_Button.setOnClickListener(view -> {
            clickSound.playSound();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);}
        );

        ImageButton multi_Button = (ImageButton)findViewById(R.id.launchgame);
        multi_Button.setOnClickListener(view -> {
            clickSound.playSound();

            Intent intent = new Intent(this, Connect4GameActivity.class);
            startActivity(intent);}
        );

        ToggleButton bluetooth_Button = findViewById(R.id.bluetooth_toggleButton);
        bluetooth_Button.setOnClickListener(view -> {
            clickSound.playSound();
            }
        );
        loadPref();

    }

   private void loadPref(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PlayerPref", Context.MODE_PRIVATE);
        String player = preferences.getString("player1", "");
        TextView player_name = findViewById(R.id.playerName);
        player_name.setText(player);

        }


}