package edu.handong.android.connect4;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.View;
/**
 * This class is the one responsible of producing the click sound effect whenever the user touches a button on the GUI
 * */
public class SoundEffect {
    private SoundPool soundPool;
    private int sound;

    public SoundEffect(Context context){
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
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
