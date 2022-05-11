package edu.handong.android.connect4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * This is the view class which draws the board, score and player names.
 * It receives touch events and notifies the controller
 */

public class Connect4View extends View {
    private Path PBoard, PAIPiece, PHumanPiece;
    private Paint Paint, CanvasPaint;
    private Canvas Canvas;
    private Bitmap Bitmap;
    private MainActivity mActivity;
    private float endY;
    private float cWidth;
    //Margin of the board(constants)
    private static final float MARGINX = 20.0f;
    private static final float MARGINY = 210.0f;

    public Connect4View(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (MainActivity) context;
        //setPathPaint();
    }

}