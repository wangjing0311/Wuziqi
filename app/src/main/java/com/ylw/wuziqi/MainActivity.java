package com.ylw.wuziqi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private FiveBackground fiveBackground;
    private boolean isDestroy = true;


    Controller controller;
    MotionEventTest motionEventTest = new MotionEventTest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "再来一局！", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        surfaceView = (SurfaceView) findViewById(R.id.surface);
        holder = surfaceView.getHolder();
        holder.addCallback(new HolderCallBack());

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                cancelAnim();
                if (isDestroy) return false;
                fiveBackground.onTouch(motionEvent);
                motionEventTest.onTouch(motionEvent);
                return !draw();
            }
        });

        fiveBackground = new FiveBackground(MainActivity.this);
        controller = new Controller(this, fiveBackground);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.leave();
        controller.destroy();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("退出五子棋？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                }).setNegativeButton("取消", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "自己玩");
        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "找人玩");
        mt[0] = menu.findItem(Menu.FIRST + 1).setCheckable(true).setChecked(true);
        mt[1] = menu.findItem(Menu.FIRST + 2).setCheckable(true).setChecked(false);
        return true;
    }

    MenuItem[] mt = new MenuItem[2];

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                mt[0].setChecked(true);
                mt[1].setChecked(false);
                controller.withMe(true);
                break;
            case Menu.FIRST + 2:
                mt[0].setChecked(false);
                mt[1].setChecked(true);
                controller.withMe(false);
                break;
        }

        return false;

    }

    public boolean draw() {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return true;

        fiveBackground.draw(canvas);

        // 显示
        holder.unlockCanvasAndPost(canvas);
        return false;
    }

    public void setMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private class HolderCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            int w = surfaceView.getWidth();
            int h = surfaceView.getHeight();
            fiveBackground.setWidthHeight(w, h);
            isDestroy = false;
            draw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            int w = surfaceView.getWidth();
            int h = surfaceView.getHeight();
            fiveBackground.setWidthHeight(w, h);
            draw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            isDestroy = true;
        }
    }

    private ValueAnimator anim;

    public void anim() {
        if (anim == null) {
            anim = ValueAnimator.ofFloat(0f, 1f);
            anim.setDuration(1000);
//            anim.setInterpolator(new OvershootInterpolator());
            anim.setInterpolator(new BounceInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
//                Log.d(TAG, "cur - AnimatedValue : " + animation.getAnimatedValue());
                    Canvas canvas = holder.lockCanvas();
                    if (canvas == null) return;

                    fiveBackground.drawAnim(canvas, (Float) animation.getAnimatedValue());

                    // 显示
                    holder.unlockCanvasAndPost(canvas);
                }
            });
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fiveBackground.animEnd();
                }
            });
        }
        anim.start();
    }

    public void cancelAnim() {
        if (anim != null)
            anim.cancel();
    }

}
