package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
        TextView txtGuest = findViewById(R.id.txtGuest_name);
        EditText guest = findViewById(R.id.edtGuest);
        Spinner pieces = findViewById(R.id.ballShape_spinner);

        multiplayer.setOnClickListener(view -> {
            if (multiplayer.isChecked()) {
                txtGuest.setVisibility(View.VISIBLE);
                guest.setVisibility(View.VISIBLE);
            } else {
                txtGuest.setVisibility(View.INVISIBLE);
                guest.setVisibility(View.INVISIBLE);
            }

        });
/**
        multiplayer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (multiplayer.isChecked()) {
                    txtGuest.setVisibility(View.VISIBLE);
                    guest.setVisibility(View.VISIBLE);
                } else {
                    txtGuest.setVisibility(View.INVISIBLE);
                    guest.setVisibility(View.INVISIBLE);
                }

            }
        });
*/

        //Go back to home page
        ImageButton home_Button = findViewById(R.id.newgamehome);
        home_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);}
        );
        //Advance to the actual game activity
        ImageButton game_Button = findViewById(R.id.launchgame);
        game_Button.setOnClickListener(view -> {
            String name2 = guest.getText().toString();
            String piece = pieces.getSelectedItem().toString();
            Intent intent = new Intent(this, Connect4GameActivity.class);
            intent.putExtra("Player2Name",name2);
            intent.putExtra("ModelPiece",piece);
            if(multiplayer.isChecked())
                intent.putExtra("Mode",5);
            else
                intent.putExtra("Mode",4);


            startActivity(intent);}
        );
    }
}