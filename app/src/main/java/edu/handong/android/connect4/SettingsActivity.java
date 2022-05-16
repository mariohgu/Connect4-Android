package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    Context context;
    SharedPreferences preferences;
    private static final String PREF = "PlayerPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Load saved preferences from SharedPreferences object instance
        //loadPref();

        /* implementation of localization
        Locale locale = new Locale("fr");
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        ****************************************/
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_settings);

        ImageButton back_Button = (ImageButton)findViewById(R.id.back_button);
        back_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);}
        );

        ImageButton save_Button = (ImageButton)findViewById(R.id.btnSaveSettings);
        save_Button.setOnClickListener(view -> {
                savePref();
            }
        );

        ImageButton cancel_Button = (ImageButton)findViewById(R.id.btnCancelSettings);
        cancel_Button.setOnClickListener(view -> {
                //Make a Pop-up to prompt the user if he is sure to cancel the changes
                Toast.makeText(SettingsActivity.this,"Cancelling the changes",Toast.LENGTH_SHORT).show();
                }
        );

    }

    //Load saved preferences from SharedPreferences object instance
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


    //Save all changes related to the user settings preferences
    private void savePref(){
        EditText player_name = (EditText) findViewById(R.id.playerName);
        Spinner language = (Spinner) findViewById(R.id.spLanguage);
        ToggleButton music = (ToggleButton) findViewById(R.id.toggle_music);
        ToggleButton sounds = (ToggleButton) findViewById(R.id.toggle_sounds);

        preferences = getSharedPreferences(PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putBoolean("music",music.isChecked());
        editor.putBoolean("sounds",sounds.isChecked());
        editor.putString("player",player_name.getText().toString());
        editor.putString("pref_lang", language.getSelectedItem().toString());
        editor.commit();
        Toast.makeText(SettingsActivity.this,"Settings saved successfully",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}