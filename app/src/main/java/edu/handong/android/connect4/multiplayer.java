package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.ImageButton;
import android.widget.TextView;

public class multiplayer extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        ImageButton home_Button = (ImageButton)findViewById(R.id.newgamehome);
        home_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);}
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