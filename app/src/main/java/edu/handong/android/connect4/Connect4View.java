package edu.handong.android.connect4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Our code is based in:
 * - The book "Intelligent Mobile Projects with TensorFlow" by Jeff Tang,
 *          ISBN: 978-1-78883-454-4
 * - The post "How to build your own AlphaZero AI using Python and Keras" by David Foster
 *          URL: https://medium.com/applied-data-science/how-to-build-your-own-alphazero-ai-using-python-and-keras-7f664945c188
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

    /**
     * "We used three Path instances, PBoard, PAIPieces, and PHumanPiece, to draw the board, the
     * moves the AI makes, and the moves the human makes, respectively, with different colors. The drawing
     * feature of BoardView is implemented in the onDraw method using the moveTo and lineTo methods of Path
     * and the drawPath method of Canvas."
     * @param canvas
     */

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(cBitmap, 0, 0, CanvasPaint);
        cWidth = 75.0f;
        endY = canvas.getHeight()-MARGINY;
        endX = canvas.getWidth()-MARGINX+140.0f;
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
        cPaint.setARGB(80,7, 3, 252);
        canvas.drawPath(PBoard, cPaint);
        //array to set the columns of the board
        int columnPieces[] = {0,0,0,0,0,0,0};

        /**
         * If Machine first, it starts drawing the first machine move, then the first human move, if any, and
         * alternate the drawing of the Machine's moves and the human's moves
         */
        //Conditional to create the pieces of the player or AI
        if (gActivity.getMachineFirst()) { //If the AI start
            for (int i = 0; i<gActivity.getMachineMoves().size(); i++) {
                int action = gActivity.getMachineMoves().get(i);
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

                if (i<gActivity.getMachineMoves().size()) {// The machine turn
                    action = gActivity.getMachineMoves().get(i);
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

    //The event TouchEvent, these are all the events that running when the user touch the screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gActivity.getMachineTurn()) return true;
        //get the positions that the user touched in the screen
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                // This line let limit the touch only in the boardgames
                if (y < MARGINY || x < MARGINX || y > endY || x > endX) return true;
                int column = (int)((x-MARGINX)/cWidth);
                for (int i=0; i<6; i++)
                    if (gActivity.getBoard()[35+column-7*i] == 0) {
                        gActivity.getBoard()[35+column-7*i] = game_activity.HUMAN_PIECE;
                        gActivity.getHumanMoves().add(35+column-7*i);
                        break;
                    }
                //Invalidate the whole view.
                invalidate();
                //Machine Turns
                gActivity.setMachineTurn();
                // Finished the process when the game ends
                if (gActivity.gameEnded(gActivity.getBoard())) {
                    if (gActivity.machineWon(gActivity.getBoard()))
                        gActivity.getTextView().setText("AI Won!");
                    else if (gActivity.machineLost(gActivity.getBoard()))
                        gActivity.getTextView().setText("You Won!");
                    else if (gActivity.humanDraw(gActivity.getBoard()))
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