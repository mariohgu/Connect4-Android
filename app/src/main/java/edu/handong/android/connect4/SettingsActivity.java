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
    private EditText player_name;
    private Spinner language;
    private ToggleButton music;
    private ToggleButton sounds;
    private String player;

    SharedPreferences preferences;
    public static final String PREF = "PlayerPref";
    public static final String TEXT = "player1";
    public static final String MUSIC = "music";
    public static final String SOUNDS = "sounds";
    public static final String PREF_LANG = "pref_lang";

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

        player_name = findViewById(R.id.playerName);
        language = findViewById(R.id.spLanguage);
        music =  findViewById(R.id.toggle_music);
        sounds =  findViewById(R.id.toggle_sounds);

        ImageButton back_Button = findViewById(R.id.back_button);
        back_Button.setOnClickListener(view -> {
            onBackPressed();
        }
        );

        ImageButton save_Button = findViewById(R.id.btnSaveSettings);
        save_Button.setOnClickListener(view -> {
                savePref();

            }
        );

        ImageButton cancel_Button = findViewById(R.id.btnCancelSettings);
        cancel_Button.setOnClickListener(view -> {
                //Make a Pop-up to prompt the user if he is sure to cancel the changes
                Toast.makeText(SettingsActivity.this,"Cancelling the changes",Toast.LENGTH_SHORT).show();
                }
        );
        loadPref();




    }

    //Load saved preferences from SharedPreferences object instance
    public void loadPref(){
        // Fetching the stored data from the SharedPreference
        SharedPreferences preferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String lang;
        Locale locale;
        //Checking if we already have a preferred language selected
        if (preferences.contains(PREF_LANG)){

            lang = preferences.getString(PREF_LANG, "");
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
        } */
        player = preferences.getString(TEXT, "");
        player_name.setText(player);

    }


    //Save all changes related to the user settings preferences
    public void savePref(){


        preferences = getSharedPreferences(PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();


        editor.putBoolean(MUSIC,music.isChecked());
        editor.putBoolean(SOUNDS,sounds.isChecked());
        editor.putString(TEXT,player_name.getText().toString());
        editor.putString(PREF_LANG, language.getSelectedItem().toString());
        //editor.commit();
        editor.apply();
        Toast.makeText(SettingsActivity.this,"Settings saved successfully",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }




}