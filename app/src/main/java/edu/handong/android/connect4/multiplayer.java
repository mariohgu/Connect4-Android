package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class multiplayer extends AppCompatActivity {
    private static final String PREF = "PlayerPref";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPref();
        setContentView(R.layout.activity_multiplayer);
    }

    private void loadPref(){
        // Fetching the stored data from the SharedPreference
        SharedPreferences preferences = getSharedPreferences(PREF, MODE_PRIVATE);

        if (preferences.contains("player")){
            String player = (String) preferences.getString("player", "");
            TextView player_name = (TextView) findViewById(R.id.playerName);
            //player_name.setText("Test ecriture");
        }



    }
}