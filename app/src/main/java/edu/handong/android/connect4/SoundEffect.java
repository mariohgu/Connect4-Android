package edu.handong.android.connect4;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.View;

public class SoundEffect {
    private SoundPool soundPool;
    private int sound;

    public SoundEffect(Context context){
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {*/
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
       // }

        sound = soundPool.load(context, R.raw.click, 1);
    }

    public void playSound(){
        soundPool.play(sound, 1,1,0,0,1);
    }


    public void StopSound(){
        soundPool.release();
        soundPool = null;
    }
}
