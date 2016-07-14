package com.ylw.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import static com.ylw.wuziqi.DrawUtil.getCenterTextBaseLine;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by wangjing on 2016/7/8.
 */
public class FiveBackground {

    private static final String TAG = "FiveBackground";
    private Bitmap img;
    private Canvas bCanvas;
    private final Context context;
    private Paint paint;
    private Paint paintImg;
    private float gridWidth;
    private final int hengNum = 19, shuNum = 19;
    private final int canvasW = 890, canvasH = 890;
    private float strokeWidth = 5;// 棋盘线宽

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
    PointF newPoint, oldPoint;
    private float zoom = 1;
    private final Matrix matrix;

    public FiveBackground(Context context) {
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paintImg = new Paint();
        img = Bitmap.createBitmap(canvasW, canvasH, Bitmap.Config.ARGB_8888);
        bCanvas = new Canvas(img);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);

        paintText.setTextSize(120);
        paintText.setColor(0xffEE8888);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setStyle(Paint.Style.FILL_AND_STROKE);
        rectText = new Rect(10, 10, 600, 260);

        mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1},
                0.4f, 60, 3.5f);

        gridWidth = canvasW / hengNum;
        qPan = new int[hengNum][shuNum];
        newPoint = new PointF(-10, -10);
        oldPoint = new PointF(-10, -10);
        matrix = new Matrix();

    }

    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
        rectText.set(0, 210, width, height / 3);
        if (oZoom == 1) {
            zoom = width * 0.9f / canvasW;
        }
        imgP.set((width - canvasW * zoom) / 2, (height - canvasH * zoom) / 2);
        Controller.draw();
    }

    public void draw(Canvas canvas) {
        long time = System.currentTimeMillis();
        if (!moved) {
            bCanvas.save();
            // 计算重绘区域
            if (newPoint.x > -1) {
                float x = offX + gridWidth * newPoint.x - gridWidth / 2;
                float y = offY + gridWidth * newPoint.y - gridWidth / 2;
                bCanvas.clipRect(x - strokeWidth, y - strokeWidth, x + gridWidth + strokeWidth, y + gridWidth + strokeWidth);
                x = offX + gridWidth * oldPoint.x - gridWidth / 2;
                y = offY + gridWidth * oldPoint.y - gridWidth / 2;
                bCanvas.clipRect(x - strokeWidth, y - strokeWidth, x + gridWidth + strokeWidth, y + gridWidth + strokeWidth, Region.Op.UNION);
            }
            bCanvas.drawColor(0xffffffaa);
            drawLines(bCanvas);
            drawPoint(bCanvas);

            bCanvas.restore();
        }

        canvas.drawColor(0xffffffff);

        //x y坐标同时缩放
        matrix.setScale(zoom, zoom);
        matrix.postTranslate(imgP.x, imgP.y);

        canvas.drawBitmap(img, matrix, paintImg);

        if (isOver) {
            String msg = "GAME OVER!";
            float baseLine = getCenterTextBaseLine(paintText, rectText);

            paintText.setColor(0xffEE8888);
            canvas.drawText(msg, width / 2, baseLine, paintText);

            paintText.setColor(0xff006600);
            canvas.drawText(resultStr, width / 2, baseLine + 150, paintText);
        }

        time = System.currentTimeMillis() - time;
        Log.d(TAG, "draw: time : " + time);
    }

    private void drawPoint(Canvas bCanvas) {
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(gridWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setMaskFilter(mEmboss);
        for (int i = 0; i < hengNum; i++) {
            for (int j = 0; j < shuNum; j++) {
                if (qPan[i][j] == 1) {
                    paint.setColor(0xffee0000);
                    bCanvas.drawPoint(offX + gridWidth * i, offY + gridWidth * j, paint);
                } else if (qPan[i][j] == 2) {
                    paint.setColor(0xff444444);
                    bCanvas.drawPoint(offX + gridWidth * i, offY + gridWidth * j, paint);
                }
            }
        }
        paint.setMaskFilter(null);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xff00ff00);
        float x = offX + gridWidth * newPoint.x - gridWidth / 2;
        float y = offY + gridWidth * newPoint.y - gridWidth / 2;
        bCanvas.drawRect(x, y, x + gridWidth, y + gridWidth, paint);
    }


    private void drawLines(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
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

    PointF imgP = new PointF();
    PointF downImgP = new PointF();
    PointF offsetP = new PointF();
    PointF dOffsetP = new PointF();

    PointF downP = new PointF();
    PointF downP2 = new PointF();

    float downTwoPointLength = 0;
    float oZoom = 1;
    boolean moved = false;

    int index1 = 0;
    int index2 = 0;

    float[][] p = new float[15][2];

    public void onTouch(MotionEvent event) {

        int index = event.getActionIndex();
        p[index][0] = event.getX(index);
        p[index][1] = event.getY(index);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                index1 = index;
                index2 = index;
                downP.set(p[index1][0], p[index1][1]);
                downP2.set(p[index2][0], p[index2][1]);
                downImgP.set(imgP);

                offsetP.set((downP.x + downP2.x) / 2 - imgP.x, (downP.y + downP2.y) / 2 - imgP.y);
                dOffsetP.set(offsetP);
                oZoom = zoom;
                moved = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                index1 = index2;
                index2 = index;
                downP.set(p[index1][0], p[index1][1]);
                downP2.set(p[index2][0], p[index2][1]);
                downImgP.set(imgP);
                oZoom = zoom;
                moved = true;

                offsetP.set((downP.x + downP2.x) / 2 - imgP.x, (downP.y + downP2.y) / 2 - imgP.y);
                dOffsetP.set(offsetP);
                downTwoPointLength = (float) sqrt(pow(downP.x - downP2.x, 2) + pow(downP.y - downP2.y, 2)) / zoom;

                break;
            case MotionEvent.ACTION_MOVE:
                p[index1][0] = event.getX(index1);
                p[index1][1] = event.getY(index1);
                p[index2][0] = event.getX(index2);
                p[index2][1] = event.getY(index2);

                float nowTwoPointLength = (float) sqrt(pow(p[index1][0] - p[index2][0], 2) + pow(p[index1][1] - p[index2][1], 2));
                if (nowTwoPointLength != 0 && downTwoPointLength != 0) {
                    zoom = nowTwoPointLength / downTwoPointLength;
                    offsetP.set(dOffsetP.x * zoom / oZoom, dOffsetP.y * zoom / oZoom);
                }
                imgP.set((p[index1][0] + p[index2][0]) / 2 - offsetP.x, (p[index1][1] + p[index2][1]) / 2 - offsetP.y);

                if (pow(p[index1][0] - downP.x, 2) + pow(p[index1][1] - downP.y, 2) > 20
                        || pow(p[index2][0] - downP2.x, 2) + pow(p[index2][1] - downP2.y, 2) > 20) {
                    moved = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                int count = event.getPointerCount();
                boolean change = false;
                for (int i = 0; i < count; i++) {
                    if (count == 2) {
                        if (i != index) {
                            index1 = i;
                            index2 = i;
                            change = true;
                            break;
                        }
                    } else if (index1 == index) {
                        if (i != index && i != index2) {
                            index1 = i;
                            change = true;
                            break;
                        }
                    } else if (index2 == index) {
                        if (i != index && i != index1) {
                            index2 = i;
                            change = true;
                            break;
                        }
                    }
                }
                if (change) {
                    downP.set(p[index1][0], p[index1][1]);
                    downP2.set(p[index2][0], p[index2][1]);
                    offsetP.set((downP.x + downP2.x) / 2 - imgP.x, (downP.y + downP2.y) / 2 - imgP.y);
                }
                if (index1 > index) index1--;
                if (index2 > index) index2--;
                break;
            case MotionEvent.ACTION_UP:
                if (!moved) {
                    addPoint(event.getX(), event.getY());
                } else {
                    if (!animing) {
                        oImgP.set(imgP);
                    }
                    boolean s0 = canvasW * zoom <= width;
                    boolean s1 = canvasH * zoom <= height;
                    boolean s2 = oImgP.x > 0;
                    boolean s3 = oImgP.y > 0;
                    boolean s4 = oImgP.x + canvasW * zoom < width;
                    boolean s5 = oImgP.y + canvasH * zoom < height;
                    boolean s6 = oImgP.x != (width - canvasW * zoom) / 2;
                    boolean s7 = oImgP.y != (height - canvasH * zoom) / 2;
                    if (s0 && s6) {
                        Controller.startAnim();
                    } else if (s2) {
                        Controller.startAnim();
                    } else if (s4) {
                        Controller.startAnim();
                    } else if (s1 && s7) {
                        Controller.startAnim();
                    } else if (s3) {
                        Controller.startAnim();
                    } else if (s5) {
                        Controller.startAnim();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

    }


    private boolean wait = true;

    private void addPoint(float x, float y) {
        if (animing) return;
        if (!Controller.c.withMe && wait) return;
        if (isOver) {
            restart();
            return;
        }
        float upX = x - offX * zoom - imgP.x;
        float upY = y - offY * zoom - imgP.y;
        float gzw = gridWidth * zoom;
        int numX = Math.round(upX / gzw);
        int numY = Math.round(upY / gzw);

        if (numX < 0 || numX >= hengNum || numY < 0 || numY >= shuNum) return;

        if (qPan[numX][numY] != 0) return;

        if (Controller.c.withMe) {
            if (isRed) {
                qPan[numX][numY] = 1;
            } else {
                qPan[numX][numY] = 2;
            }
            judgeWin(numX, numY, isRed);
            isRed = !isRed;
        } else {
            qPan[numX][numY] = 1;
            wait = true;
            Controller.send(numX, numY);
        }
        addPoint(numX, numY);
        judgeWin(numX, numY, true);
        Log.d(TAG, String.format("x = %d y = %d", numX, numY));

    }

    private void addPoint(int numX, int numY) {
        oldPoint.set(newPoint);
        newPoint.set(numX, numY);
    }

    String resultStr = "";

    private void judgeWin(int numX, int numY, boolean isRed) {
        if (isRed) {
            if (check(numX, numY, 1)) {
                resultStr = "红方胜!";
                gameOver();
            }
        } else {
            if (check(numX, numY, 2)) {
                resultStr = "黑方胜!";
                gameOver();
            }
        }
    }

    private void gameOver() {
        Toast.makeText(context, resultStr, Toast.LENGTH_LONG).show();
        isOver = true;
        wait = false;
        Controller.gameOver();
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

    public void start() {
        restart();
    }

    public void step(int x, int y) {
        qPan[x][y] = 2;
        addPoint(x, y);
        judgeWin(x, y, false);
        isRed = true;
        wait = false;
        moved = false;
    }

    public void restart() {
        wait = false;
        isRed = true;
        isOver = false;
        reset();
        newPoint.set(-10, -10);

        Controller.draw();
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    private void reset() {
        for (int i = 0; i < hengNum; i++) {
            for (int j = 0; j < shuNum; j++) {
                qPan[i][j] = 0;
            }
        }
    }

    private boolean animing = false;
    private PointF oImgP = new PointF();

    public void drawAnim(Canvas canvas, float value) {
        if (!animing) {
            oImgP.set(imgP);
        }
        animing = true;
        float x = oImgP.x;
        float y = oImgP.y;

        boolean s0 = canvasW * zoom <= width;
        boolean s1 = canvasH * zoom <= height;
        boolean s2 = oImgP.x > 0;
        boolean s3 = oImgP.y > 0;
        boolean s4 = oImgP.x + canvasW * zoom < width;
        boolean s5 = oImgP.y + canvasH * zoom < height;

        boolean s6 = oImgP.x != (width - canvasW * zoom) / 2;
        boolean s7 = oImgP.y != (height - canvasH * zoom) / 2;

        if (s0 && s6) {
            x = oImgP.x + ((width - canvasW * zoom) / 2 - oImgP.x) * value;
        } else if (s2) {
            x = oImgP.x + (-oImgP.x) * value;
        } else if (s4) {
            x = oImgP.x + (width - oImgP.x - canvasW * zoom) * value;
        }
        if (s1 && s7) {
            y = oImgP.y + ((height - canvasH * zoom) / 2 - oImgP.y) * value;
        } else if (s3) {
            y = oImgP.y + (-oImgP.y) * value;
        } else if (s5) {
            y = oImgP.y + (height - oImgP.y - canvasH * zoom) * value;
        }

        imgP.set(x, y);
        canvas.drawColor(0xffffffff);
        matrix.setScale(zoom, zoom);
        matrix.postTranslate(x, y);
        canvas.drawBitmap(img, matrix, paintImg);
    }

    public void animEnd() {
        animing = false;
    }
}
