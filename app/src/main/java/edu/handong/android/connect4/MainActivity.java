package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    RelativeLayout r1;
    Context context;
    SharedPreferences preferences;
    private static final String PREF = "PlayerPref";

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
        /*ImageButton Load_Game_Button = (ImageButton)findViewById(R.id.loadgame);
        //Binding the button to a listener

        Load_Game_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, load_game.class);
            startActivity(intent);}
        );*/

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

        ImageButton bluetooth_button = (ImageButton)findViewById(R.id.multiplayer);
        //Binding the button to a listener
        bluetooth_button.setOnClickListener(view -> {
            Intent intent = new Intent(this, multiplayer.class);
            startActivity(intent);}
        );


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPref();
    }

    private void loadPref(){
        // Fetching the stored data from the SharedPreference
        SharedPreferences preferences = getSharedPreferences(PREF, MODE_PRIVATE);
        String lang,player;
        Locale locale;
        //Checking if we already have a preferred language selected
        if (preferences.contains("pref_lang")){

            lang = preferences.getString("pref_lang", "");
            if (lang.equals("French")){
                locale = new Locale("fr");
                Configuration config = getBaseContext().getResources().getConfiguration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            }
            else if (lang.equals("Spanish")){
                locale = new Locale("sp");
                Configuration config = getBaseContext().getResources().getConfiguration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            }
            else{
                locale = new Locale("en");
                Configuration config = getBaseContext().getResources().getConfiguration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            }

        }
        /*if (preferences.contains("player")){
            player = preferences.getString("player", "");
            Toast.makeText(SettingsActivity.this,player,Toast.LENGTH_SHORT).show();
            EditText player_name = (EditText) findViewById(R.id.edit_playerName);
            player_name.setText(player);
        }*/



    }
}