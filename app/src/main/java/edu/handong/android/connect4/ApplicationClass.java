package edu.handong.android.connect4;

import android.app.Application;
import android.content.Context;

/**
 * This is class is executed right before the application is launched
 * We use it to setup a context for our app
 */
public class ApplicationClass extends Application {
    public static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
    }
}
