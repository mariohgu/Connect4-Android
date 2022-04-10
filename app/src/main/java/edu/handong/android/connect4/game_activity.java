package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageButton;

public class game_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ImageButton game_Button = (ImageButton)findViewById(R.id.back_button);
        game_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewGame_Settings.class);
            startActivity(intent);}
        );
    }
}