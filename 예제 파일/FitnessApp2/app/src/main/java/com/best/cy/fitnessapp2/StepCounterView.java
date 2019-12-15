package com.best.cy.fitnessapp2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.text.DecimalFormat;

public class StepCounterView extends View {

    Context mContext;
    long startTime;
    long currentTime;
    int gameFirstStart = 0;
    int gameIng = 0;
    int gameOver = 1;
    int Width, Height;
    int unitSize;
    Bitmap btnStart;
    Bitmap walkingBoy;

    int btnStart_x;
    int btnStart_y;
    int btnStart_width;
    int btnStart_height;

    int mWeight = 65;

    Bitmap btnStop;
    int btnStop_x;
    int btnStop_y;
    int btnStop_width;
    int btnStop_height;

    Bitmap btnPlus;
    int btnPlus_x;
    int btnPlus_y;
    int btnPlus_width;
    int btnPlus_height;

    Bitmap btnMinus;
    int btnMinus_x;
    int btnMinus_y;
    int btnMinus_width;
    int btnMinus_height;


    double calorie;

    Paint paint = new Paint();
    Paint paintBig = new Paint(); //큰 글씨
    Paint paintBlack = new Paint(); //몸무게 글씨
    static int walkingCount = 0;
    int elapsedTime = 0;
    int min, sec; //운동량이 1분이 넘어갈 경우 처리하기

    DecimalFormat format;

    public StepCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        setFocusable(true);
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {

        WindowManager windowMng = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowMng.getDefaultDisplay();

        Point mP = new Point();
        display.getSize(mP);
        Width = mP.x;
        Height = mP.y;

        unitSize = Width / 6;

        walkingBoy = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.walkingboy);
        walkingBoy = Bitmap.createScaledBitmap(walkingBoy, unitSize * 2, unitSize * 3, true);

        btnStart = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btnstart);
        btnStart = Bitmap.createScaledBitmap(btnStart, Width / 3, Height / 10, true);
        btnStart_x = Width / 5;
        btnStart_y = Height / 3;

        btnStart_width = btnStart.getWidth();
        btnStart_height = btnStart.getHeight();

        btnStop = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btnstop);
        btnStop = Bitmap.createScaledBitmap(btnStop, Width / 3, Height / 10, true);
        btnStop_x = Width / 5 + btnStart_width;
        btnStop_y = Height / 3;

        btnStop_width = btnStop.getWidth();
        btnStop_height = btnStop.getHeight();

        btnPlus = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btnplus);
        btnPlus = Bitmap.createScaledBitmap(btnPlus, Width / 6, Width / 6, true);
        btnPlus_x = Width / 2 + unitSize / 4;
        btnPlus_y = Height / 7;

        btnPlus_width = btnPlus.getWidth();
        btnPlus_height = btnPlus.getHeight();

        btnMinus = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btnminus);
        btnMinus = Bitmap.createScaledBitmap(btnMinus, Width / 6, Width / 6, true);
        btnMinus_x = Width / 2 + +unitSize / 4 + unitSize;
        btnMinus_y = Height / 7;

        btnMinus_width = btnMinus.getWidth();
        btnMinus_height = btnMinus.getHeight();

        startTime = System.currentTimeMillis();

        paint.setTextSize(Width / 14);
        paintBig.setTextSize(Width / 3);
        paintBlack.setTextSize(Width / 12);

        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create("", Typeface.BOLD));

        paintBlack.setColor(Color.BLACK);
        paintBlack.setTypeface(Typeface.create("", Typeface.BOLD));

        format = new DecimalFormat("0.000");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onDraw(Canvas canvas) {

        elapsedTime = (int) currentTime - (int) startTime;

        canvas.drawBitmap(walkingBoy, Width / 20, Height * 3 / 4, null);

        //몸무게 조절하기
        canvas.drawText("몸무게: " + mWeight + "kg", Width / 20, btnPlus_y + Width / 14, paintBlack);
        canvas.drawBitmap(btnPlus, btnPlus_x, btnPlus_y, null);
        canvas.drawBitmap(btnMinus, btnMinus_x, btnMinus_y, null);

        if (gameIng == 0)
            canvas.drawBitmap(btnStart, btnStart_x, btnStart_y, null);
        else if (gameIng == 1) canvas.drawBitmap(btnStop, btnStop_x, btnStop_y, null);


        if (gameIng == 1) {
            int elapsedTime = (int) currentTime - (int) startTime;
            currentTime = System.currentTimeMillis();

            if (elapsedTime / 1000 < 60)
                canvas.drawText("운동한 시간: " + elapsedTime / 1000 + " 초", Width / 20, Height / 4 + unitSize / 4, paint);
            else {
                min = elapsedTime / 1000 / 60;
                sec = (elapsedTime / 1000) % 60;
                canvas.drawText("운동한 시간: " + min + " 분 " + sec + " 초", Width / 20, Height / 4 + unitSize / 4, paint);
            }
            //Mets = 3.5
            calorie = 3.5 * 3.5 * mWeight / 200 / 120 * walkingCount;
            canvas.drawText("소비한 칼로리: " + format.format(calorie) + " kcal",
                    Width / 20, Height * 2 / 7 + unitSize / 4, paint);
        }

        if (gameOver == 0)
            canvas.drawText("" + walkingCount, Width / 20, Height * 2 / 3 + unitSize / 4, paintBig);


        if (gameFirstStart == 1) //처음부터 다음 내용이 나오는 것을 방지한다.
            if (gameOver == 1 && gameIng == 0) {
                if (elapsedTime / 1000 < 60)
                    canvas.drawText("운동한 시간: " + elapsedTime / 1000 + " 초", Width / 20, Height / 4 + unitSize / 4, paint);
                else {
                    min = elapsedTime / 1000 / 60;
                    sec = (elapsedTime / 1000) % 60;
                    canvas.drawText("운동한 시간: " + min + " 분 " + sec + " 초", Width / 20, Height / 4 + unitSize / 4, paint);
                }
                canvas.drawText("소비한 칼로리: " + format.format(calorie) + " kcal",
                        Width / 20, Height * 2 / 7 + unitSize / 4, paint);
            }

        postInvalidate();
    }               // end of onDraw


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = 0, y = 0;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            x = (int) event.getX();
            y = (int) event.getY();
        }

        if (gameIng == 0)
            if (x > btnStart_x && x < (btnStart_x + btnStart_width) && y > btnStart_y && y < (btnStart_y + btnStart_height)) {

                startTime = System.currentTimeMillis();
                gameIng = 1;
                walkingCount = 0;
                gameOver = 0;
                gameFirstStart = 1;
            }

        if (x > btnPlus_x && x < (btnPlus_x + btnPlus_width) && y > btnPlus_y && y < (btnPlus_y + btnPlus_height)) {
            mWeight += 1;
        }

        if (x > btnMinus_x && x < (btnMinus_x + btnMinus_width) && y > btnMinus_y && y < (btnMinus_y + btnMinus_height)) {
            mWeight -= 1;
        }

        if (gameIng == 1)
            if (x > btnStop_x && x < (btnStop_x + btnStop_width) && y > btnStop_y && y < (btnStop_y + btnStop_height)) {
                gameIng = 0;
                gameOver = 1;
            }

        postInvalidate();
        return true;
    }  //end of onTouchEvent

}
