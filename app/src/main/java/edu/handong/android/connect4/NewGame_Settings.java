package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class NewGame_Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* implementation of localization
        Locale locale = new Locale("fr");
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        ****************************************/
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_new_game_settings);
        ToggleButton multiplayer = findViewById(R.id.multiplayer_toggleButton);
        TextView txtGuess = findViewById(R.id.txtGuest_name);
        EditText guess = findViewById(R.id.edtGuess);
        multiplayer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (multiplayer.isChecked()) {
                    txtGuess.setVisibility(View.VISIBLE);
                    guess.setVisibility(View.VISIBLE);
                } else {
                    txtGuess.setVisibility(View.INVISIBLE);
                    guess.setVisibility(View.INVISIBLE);
                }

            }
        });


        //Go back to home page
        ImageButton home_Button = findViewById(R.id.newgamehome);
        home_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);}
        );
        //Advance to the actual game activity
        ImageButton game_Button = findViewById(R.id.launchgame);
        game_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, Connect4GameActivity.class);
            if(multiplayer.isChecked())
                intent.putExtra("Mode",2);
            else
                intent.putExtra("Mode",1);


            startActivity(intent);}
        );
    }
}