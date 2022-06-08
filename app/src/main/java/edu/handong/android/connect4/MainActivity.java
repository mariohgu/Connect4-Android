package edu.handong.android.connect4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    boolean launchSounds;
    boolean musicPlaying=false;
    MediaPlayer mediaPlayer;
    Intent serviceIntent;


    public int counter;

    public static final String PREF = "PlayerPref";
    public static final String TEXT = "player1";
    public static final String MUSIC = "music";
    public static final String SOUNDS = "sounds";
    public static final String PREF_LANG = "pref_lang";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setTheme(R.style.Theme_Connect4);
        setContentView(R.layout.activity_main);
        SoundEffect clickSound=new SoundEffect(this);
        SharedPreferences preferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        launchSounds=preferences.getBoolean(SOUNDS,false);

        ImageButton New_Game_Button = findViewById(R.id.newgame);
        New_Game_Button.setOnClickListener(view -> {
            if(launchSounds){
                clickSound.playSound();
            }
            Intent intent = new Intent(this, NewGame_Settings.class);
            startActivity(intent);}
        );

        ImageButton Settings_Button =findViewById(R.id.settings);
        Settings_Button.setOnClickListener(view -> {
            if(launchSounds){
                clickSound.playSound();
            }
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);}
        );

        ImageButton Ranking_buttons = findViewById(R.id.rankings);
        Ranking_buttons.setOnClickListener(view -> {
            if(launchSounds){
                clickSound.playSound();
            }
            Intent intent = new Intent(this, RankingActivity.class);
            startActivity(intent);}
        );

        /*ImageButton bluetooth_button = (ImageButton)findViewById(R.id.multiplayer);
        bluetooth_button.setOnClickListener(view -> {
            clickSound.playSound();
            Intent intent = new Intent(this, multiplayer.class);
            startActivity(intent);}
        );*/

        ImageButton exit_button = findViewById(R.id.exit_button);
        exit_button.setOnClickListener(view -> {
            if(launchSounds){
                clickSound.playSound();
            }
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (mediaPlayer!=null)
                        {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer=null;
                        }
                        MainActivity.this.finish();
                        moveTaskToBack(true);
                        finishAndRemoveTask();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Exit the app?").setPositiveButton("YES", dialogClickListener)
                    .setTitle("CONNECT 4")
                    .setNegativeButton("NO", dialogClickListener).show();

            }
        );

        ImageButton music_button = findViewById(R.id.music_button);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serviceIntent=new Intent(this, MusicService.class);
        }
        music_button.setOnClickListener(view -> {
            if(launchSounds){
                 clickSound.playSound();
            }
            if(!musicPlaying){
                playAudio();
                music_button.setBackgroundResource(R.drawable.sound_off);
                musicPlaying=true;
            }
            else {
                StopPlayService();
                music_button.setBackgroundResource(R.drawable.sound__2);
                musicPlaying=false;
            }
        });

        ImageButton twitter = findViewById(R.id.twitter);
        twitter.setOnClickListener(view -> {
            Intent maax = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/MAAX_Studio"));
            startActivity(maax);
        });
        //Loading the preferences of the application. If nothing set, then default settings are used
        loadPref();

    }

    private void StopPlayService() {
        stopService(serviceIntent);
    }

    private void playAudio() {
        startService(serviceIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadPref();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        counter++;
        if(counter==2) {
           MainActivity.this.finish();
           moveTaskToBack(true);
        }
        //clickSound.StopSound();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void loadPref(){

        SharedPreferences preferences = getSharedPreferences(PREF, MODE_PRIVATE);
        String lang;
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
                locale = new Locale("es");
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
    }


    public void onMusicStopped() {
        musicPlaying=false;
    }
}