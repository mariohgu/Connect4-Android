package edu.handong.android.connect4;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class BackgroundSoundService extends IntentService {
    public static final String PREF = "PlayerPref";
    public static final String MUSIC = "music";

    MediaPlayer mediaPlayer;
    public BackgroundSoundService() {
        super("BackgroundSoundService");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mediaPlayer = MediaPlayer.create(this, R.raw.happy_lullaby);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferences = getSharedPreferences(PREF, MODE_PRIVATE);
        boolean music = preferences.getBoolean(MUSIC, false);


        Toast.makeText(BackgroundSoundService.this,"inside service",Toast.LENGTH_SHORT).show();

        if (intent != null) {
            if (music && (this.mediaPlayer.isPlaying()==false)){
                Toast.makeText(BackgroundSoundService.this,"music ON and player was OFF",Toast.LENGTH_SHORT).show();
                this.mediaPlayer.setLooping(true); // Set looping
                this.mediaPlayer.setVolume(100, 100);
                this.mediaPlayer.start();
            }
            else if (music && this.mediaPlayer.isPlaying()){
                Toast.makeText(BackgroundSoundService.this,"music ON and player was ON",Toast.LENGTH_SHORT).show();

            }
            else if ((music==false)&&this.mediaPlayer.isPlaying()){
                Toast.makeText(BackgroundSoundService.this,"music OFF and player was ON",Toast.LENGTH_SHORT).show();
                this.mediaPlayer.stop();
            }
            else{
                if (!music)Toast.makeText(BackgroundSoundService.this,"music OFF and mediaPlayer was OFF",Toast.LENGTH_SHORT).show();
                this.mediaPlayer.stop();
            }
        }

    }


}