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
    private Paint cPaint, CanvasPaint;
    private Canvas cCanvas;
    private Bitmap cBitmap;
    private game_activity gActivity;
    private float endY;
    private float endX;
    private float cWidth;
    private float radius;
    private float distance;
    //Margin of the board(constants)
    private static final float MARGINX = 450.0f;
    private static final float MARGINY = 140.0f;

    public Connect4View(Context context, AttributeSet attrs) {
        super(context, attrs);
        gActivity = (game_activity) context;
        setPathPaint();
    }
    //Set the attributes of the figures using the Path class
    private void setPathPaint() {
        PBoard = new Path();
        PAIPiece = new Path();
        PHumanPiece = new Path();
        cPaint = new Paint();
        cPaint.setAntiAlias(true);
        cPaint.setStrokeWidth(12);
        cPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        cPaint.setStrokeJoin(Paint.Join.ROUND);
        CanvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    // Settings the size of the figures and set the each pixel is stored on 4 bytes.
    @Override
    protected void onSizeChanged(int w, int h, int o_w, int o_h) {
        super.onSizeChanged(w, h, o_w, o_h);
        cBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        cCanvas = new Canvas(cBitmap);
    }
    //OnDraw Class to create the figures
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(cBitmap, 0, 0, CanvasPaint);
        cWidth = 75.0f;
        endY = canvas.getHeight()-MARGINY;
        endX = canvas.getWidth()-MARGINX+120.0f;
        radius= 20.0f;
        distance = 2.0f;
        // create the vertical lines of the board
        for (int i=0; i<8; i++) {
            float x = MARGINX + i * cWidth;
            PBoard.moveTo(x, MARGINY);
            PBoard.lineTo(x, canvas.getHeight()-MARGINY);
        }
        // create the horizontal line of the board (base)
        PBoard.moveTo(MARGINX, canvas.getHeight() - MARGINY);
        PBoard.lineTo(MARGINX + 7 * cWidth, canvas.getHeight() - MARGINY);
        cPaint.setColor(0xFF0000FF);
        canvas.drawPath(PBoard, cPaint);

        //array to set the columns of the board
        int columnPieces[] = {0,0,0,0,0,0,0};

        //Conditional to create the pieces of the player or AI

        if (gActivity.getAIFirst()) { //If the AI start
            for (int i=0; i<gActivity.getAIMoves().size(); i++) {
                int action = gActivity.getAIMoves().get(i);
                int column = action % 7;
                float x = MARGINX + column * cWidth + cWidth / 2.0f;
                float y = canvas.getHeight()-MARGINY-cWidth*columnPieces[column]-cWidth/distance;
                PAIPiece.addCircle(x,y, radius, Path.Direction.CW);
                cPaint.setColor(0xFFFF0000);
                canvas.drawPath(PAIPiece, cPaint);
                columnPieces[column]++;

                if (i<gActivity.getHumanMoves().size()) { // The player turn
                    action = gActivity.getHumanMoves().get(i);
                    column = action % 7;
                    x = MARGINX+ column * cWidth + cWidth / 2.0f;
                    //y = distance;
                    y = canvas.getHeight()-MARGINY-cWidth*columnPieces[column]-cWidth/distance;
                    PHumanPiece.addCircle(x,y, radius, Path.Direction.CW);
                    cPaint.setColor(0xFFFFFF00);

                    canvas.drawPath(PHumanPiece, cPaint);

                    columnPieces[column]++;
                }
            }
        }
        else { //if the human player starts.
            for (int i=0; i<gActivity.getHumanMoves().size(); i++) {
                int action = gActivity.getHumanMoves().get(i);
                int column = action % 7;
                float x = MARGINX + column * cWidth + cWidth / 2.0f;
                //float y = distance;
                float y = canvas.getHeight()-MARGINY-cWidth*columnPieces[column]-cWidth/distance;
                PHumanPiece.addCircle(x,y, radius, Path.Direction.CW);
                cPaint.setColor(0xFFFFFF00);
                canvas.drawPath(PHumanPiece, cPaint);
                columnPieces[column]++;

                if (i<gActivity.getAIMoves().size()) {
                    action = gActivity.getAIMoves().get(i);
                    column = action % 7;
                    x = MARGINX + column * cWidth + cWidth / 2.0f;
                    //y = distance;
                    y = canvas.getHeight()-MARGINY-cWidth*columnPieces[column]-cWidth/distance;
                    PAIPiece.addCircle(x,y, radius, Path.Direction.CW);
                    cPaint.setColor(0xFFFF0000);
                    canvas.drawPath(PAIPiece, cPaint);

                    columnPieces[column]++;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gActivity.getAITurn()) return true;

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (y < MARGINY || x < MARGINX || y > endY || x > endX) return true;


                int column = (int)((x-MARGINX)/cWidth);

                for (int i=0; i<6; i++)
                    if (gActivity.getBoard()[35+column-7*i] == 0) {
                        gActivity.getBoard()[35+column-7*i] = game_activity.HUMAN_PIECE;
                        gActivity.getHumanMoves().add(35+column-7*i);
                        break;
                    }

                invalidate();
                gActivity.setAiTurn();
                if (gActivity.gameEnded(gActivity.getBoard())) {
                    if (gActivity.aiWon(gActivity.getBoard()))
                        gActivity.getTextView().setText("AI Won!");
                    else if (gActivity.aiLost(gActivity.getBoard()))
                        gActivity.getTextView().setText("You Won!");
                    else if (gActivity.aiDraw(gActivity.getBoard()))
                        gActivity.getTextView().setText("Draw");
                    return true;
                }
                Thread thread = new Thread(gActivity);
                thread.start();

                break;
            default:
                return false;
        }

        return true;
    }


    public void drawBoard() {
        cBitmap = Bitmap.createBitmap(cBitmap.getWidth(), cBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        cCanvas = new Canvas(cBitmap);
        CanvasPaint = new Paint(Paint.DITHER_FLAG);
        cCanvas.drawBitmap(cBitmap, 0, 0, CanvasPaint);

        setPathPaint();

        invalidate();

    }




    }