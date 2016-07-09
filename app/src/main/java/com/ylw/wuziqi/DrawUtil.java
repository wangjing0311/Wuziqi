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
}
