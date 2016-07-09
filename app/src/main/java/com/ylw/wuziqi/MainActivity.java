package com.ylw.wuziqi;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private FiveBackground fiveBackground;
    private int w, h;
    private boolean isDestroy = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.surface);
        holder = surfaceView.getHolder();
        holder.addCallback(new HolderCallBack());

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isDestroy) return false;
                fiveBackground.onTouch(motionEvent);
                if (draw()) return false;
                return true;
            }
        });
    }

    private boolean draw() {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return true;

        fiveBackground.draw(canvas);

        // 显示
        holder.unlockCanvasAndPost(canvas);
        return false;
    }


    private class HolderCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            w = surfaceView.getWidth();
            h = surfaceView.getHeight();
            fiveBackground = new FiveBackground(MainActivity.this);
            fiveBackground.setWidthHeight(w, h);
            isDestroy = false;
            draw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            draw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            isDestroy = true;
         }
    }



}
