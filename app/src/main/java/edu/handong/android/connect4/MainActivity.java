package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    RelativeLayout r1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setTheme(R.style.Theme_Connect4);
        setContentView(R.layout.activity_main);
        ImageButton New_Game_Button = (ImageButton)findViewById(R.id.newgame);
        //Binding the button to a listener
        New_Game_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewGame_Settings.class);
            startActivity(intent);}
        );
        ImageButton Load_Game_Button = (ImageButton)findViewById(R.id.loadgame);
        //Binding the button to a listener

        Load_Game_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, load_game.class);
            startActivity(intent);}
        );

        ImageButton Settings_Button = (ImageButton)findViewById(R.id.settings);
        //Binding the button to a listener
        Settings_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);}
        );

        ImageButton Ranking_buttons = (ImageButton)findViewById(R.id.rankings);
        //Binding the button to a listener
        Ranking_buttons.setOnClickListener(view -> {
            Intent intent = new Intent(this, RankingActivity.class);
            startActivity(intent);}
        );


    }
}