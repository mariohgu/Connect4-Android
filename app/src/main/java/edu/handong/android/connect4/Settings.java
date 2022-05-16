package edu.handong.android.connect4;

import android.content.Context;
import android.content.SharedPreferences;

import android.content.res.Configuration;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

import java.util.Locale;

public class Settings{

    /*public void loadPref(){
        // Fetching the stored data from the SharedPreference
        SharedPreferences preferences = getSharedPreferences(PREF, MODE_PRIVATE);
        String lang;
        Locale locale;
        //Checking if we already have a preferred language selected
        if (preferences.contains("pref_lang")){
            lang = preferences.getString("pref_lang", "");
            locale = new Locale(lang);
        }
        else{
            locale = new Locale("En");
        }
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }*/

}