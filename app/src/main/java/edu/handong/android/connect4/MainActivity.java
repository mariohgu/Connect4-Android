package edu.handong.android.connect4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    RelativeLayout r1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Connect4);
        setContentView(R.layout.activity_main);
        //r1=findViewById(R.id.RelativeLayout1);
        //r1.setBackgroundResource(R.drawable.background);
    }
}