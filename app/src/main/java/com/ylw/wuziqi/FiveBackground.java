package com.ylw.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import static com.ylw.wuziqi.DrawUtil.getCenterTextBaseLine;
import static java.lang.Math.pow;

/**
 * Created by wangjing on 2016/7/8.
 */
public class FiveBackground {

    private static final String TAG = "FiveBackground";
    private final Bitmap img;
    private final Canvas bCanvas;
    private final Context context;
    private Paint paint;
    private float w, h;
    private Paint paintImg;
    private final int hengNum = 30, shuNum = 30;
    private final int canvasW = 1900, canvasH = 1900;

    boolean isRed = true;
    private float offX;
    private float offY;

    int[][] qPan;
    private boolean isOver;
    private Paint paintText;
    private Rect rectText;
    private int width;
    private int height;
    private MaskFilter mEmboss;

    public FiveBackground(Context context) {
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paintImg = new Paint();
        img = Bitmap.createBitmap(canvasW, canvasH, Bitmap.Config.ARGB_8888);
        bCanvas = new Canvas(img);
        this.w = canvasW;
        this.h = canvasH;

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);

        paintText.setTextSize(120);
        paintText.setColor(0xffEE8888);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setStyle(Paint.Style.STROKE);
        rectText = new Rect(10, 10, 600, 260);

        mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1},
                0.4f, 60, 3.5f);

        qPan = new int[hengNum][shuNum];

    }

    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
        rectText.set(0, 210, width, height / 3);

    }

    public void draw(Canvas canvas) {

        drawBg(canvas);

        if (pow(curP.x - downP.x, 2) + pow(curP.y - downP.y, 2) < 20) {
            drawLines(bCanvas);
            drawPoint(bCanvas);
        }

        canvas.drawBitmap(img, imgP.x, imgP.y, paintImg);

        if (isOver) {
            String msg = "GAME OVER!";
            float baseLine = getCenterTextBaseLine(paintText, rectText);

            paintText.setColor(0xffEE8888);
            canvas.drawText(msg, width / 2, baseLine, paintText);

            paintText.setColor(0xff006600);
            canvas.drawText(resultStr, width / 2, baseLine + 150, paintText);
        }

    }


    private void drawBg(Canvas canvas) {
        paint.setColor(0xffffffff);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(6);
        canvas.drawRect(0, 0, w, h, paint);
    }

    private void drawPoint(Canvas bCanvas) {
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(60);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setMaskFilter(mEmboss);
        for (int i = 0; i < hengNum; i++) {
            for (int j = 0; j < shuNum; j++) {
                if (qPan[i][j] == 1) {
                    paint.setColor(Color.RED);
                    bCanvas.drawPoint(offX + gridWidth * i, offY + gridWidth * j, paint);
                } else if (qPan[i][j] == 2) {
                    paint.setColor(0xff444444);
                    bCanvas.drawPoint(offX + gridWidth * i, offY + gridWidth * j, paint);
                }
            }
        }
        paint.setMaskFilter(null);

    }

    private float gridWidth = 60;


    private void drawLines(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xff8888ff);

        float qpw = (hengNum - 1) * gridWidth;
        float qph = (shuNum - 1) * gridWidth;

        offX = (canvasW - qpw) / 2;
        offY = (canvasH - qph) / 2;

        float lineX = 0, lineY = 0;

        for (int i = 0; i < shuNum; i++) {//while (lineY <= h-offY)
            canvas.drawLine(offX, offY + lineY, canvasW - offX, offY + lineY, paint);
            lineY += gridWidth;
        }
        for (int i = 0; i < hengNum; i++) {//while (lineX <= w-offX)
            canvas.drawLine(offX + lineX, offY, offX + lineX, canvasH - offY, paint);
            lineX += gridWidth;
        }
    }

    PointF curP = new PointF();
    PointF downP = new PointF();
    PointF imgP = new PointF();
    PointF offsetP = new PointF();

    public void onTouch(MotionEvent event) {
        curP.set(event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downP.set(event.getX(), event.getY());
                offsetP.set(downP.x - imgP.x, downP.y - imgP.y);
                break;
            case MotionEvent.ACTION_MOVE:
                imgP.set(curP.x - offsetP.x, curP.y - offsetP.y);
                break;
            case MotionEvent.ACTION_UP:
                if (pow(curP.x - downP.x, 2) + pow(curP.y - downP.y, 2) < 20) {
                    addPoint();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
    }


    private void addPoint() {
        if (isOver) return;
        float upX = curP.x - imgP.x - offX;
        float upY = curP.y - imgP.y - offY;
        int numX = Math.round(upX / gridWidth);
        int numY = Math.round(upY / gridWidth);

        if (numX < 0 || numX >= hengNum || numY < 0 || numY >= shuNum) return;

        if (qPan[numX][numY] != 0) return;

        if (isRed) {
            qPan[numX][numY] = 1;
        } else {
            qPan[numX][numY] = 2;
        }
        judgeWin(numX, numY, isRed);
        isRed = !isRed;

        Log.d(TAG, String.format("x = %d y = %d", numX, numY));

    }

    String resultStr = "";

    private void judgeWin(int numX, int numY, boolean isRed) {
        if (isRed) {
            if (check(numX, numY, 1)) {
                resultStr = "红方胜!";
                Toast.makeText(context, resultStr, Toast.LENGTH_LONG).show();
                isOver = true;
            }
        } else {
            if (check(numX, numY, 2)) {
                resultStr = "黑方胜!";
                Toast.makeText(context, resultStr, Toast.LENGTH_LONG).show();
                isOver = true;
            }
        }
    }


    private boolean check(int i, int j, int color) {
        int i1 = i - 4;
        int j1 = j - 4;
        int j2 = j + 4;
        int n1 = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        for (int k = 0; k < 9; j2--) {
            n1 = check(i1, j) == color ? n1 + 1 : 0;
            n2 = check(i, j1) == color ? n2 + 1 : 0;
            n3 = check(i1, j2) == color ? n3 + 1 : 0;
            n4 = check(i1, j1) == color ? n4 + 1 : 0;
            if ((n1 > 4) || (n2 > 4) || (n3 > 4) || (n4 > 4)) {
                if (n1 > 4) {
                    for (int i11 = 0; i11 < 5; i11++) {
                        this.qPan[0][i11] = (i1--);
                        this.qPan[1][i11] = j;
                    }
                } else if (n2 > 4) {
                    for (int i11 = 0; i11 < 5; i11++) {
                        this.qPan[0][i11] = i;
                        this.qPan[1][i11] = (j1--);
                    }
                } else if (n3 > 4) {
                    for (int i11 = 0; i11 < 5; i11++) {
                        this.qPan[0][i11] = (i1--);
                        this.qPan[1][i11] = (j2++);
                    }
                } else if (n4 > 4) {
                    for (int i11 = 0; i11 < 5; i11++) {
                        this.qPan[0][i11] = (i1--);
                        this.qPan[1][i11] = (j1--);
                    }
                }
                return true;
            }
            k++;
            i1++;
            j1++;
        }
        return false;
    }

    private int check(int i1, int j1) {
        if ((i1 > -1) && (i1 < hengNum) && (j1 > -1) && (j1 < shuNum)) {
            return this.qPan[i1][j1];
        }
        return 0;
    }
}
