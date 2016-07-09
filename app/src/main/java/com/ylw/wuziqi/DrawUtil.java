package com.ylw.wuziqi;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.NonNull;

/**
 * author ylw
 * Created on 2016/7/8.
 * description : SurfaceTest
 */
public class DrawUtil {

    /**
     * 绘制垂直居中文本
     *
     * @param paint 画笔
     * @param rect  目标区域
     * @return left&top point
     */
    @NonNull
    public static float getCenterTextBaseLine(Paint paint, Rect rect) {
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        return ((float) rect.bottom+ rect.top - fontMetrics.bottom - fontMetrics.top) / 2 ;
    }

    public static String castAction(int actionMasked) {
        switch (actionMasked) {
            case 0:
                return "ACTION_DOWN           ";
            case 1:
                return "ACTION_UP             ";
            case 2:
                return "ACTION_MOVE           ";
            case 3:
                return "ACTION_CANCEL         ";
            case 4:
                return "ACTION_OUTSIDE        ";
            case 5:
                return "ACTION_POINTER_DOWN     ";
            case 6:
                return "ACTION_POINTER_UP       ";
            case 7:
                return "ACTION_HOVER_MOVE       ";
            case 8:
                return "ACTION_SCROLL           ";
            case 9:
                return "ACTION_HOVER_ENTER      ";
            case 10:
                return "ACTION_HOVER_EXIT       ";
            case 11:
                return "ACTION_BUTTON_PRESS     ";
            case 12:
                return "ACTION_BUTTON_RELEASE   ";
        }
        return "NONE - " + actionMasked;
    }
}
