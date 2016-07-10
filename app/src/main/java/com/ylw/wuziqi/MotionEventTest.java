package com.ylw.wuziqi;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import static com.ylw.wuziqi.DrawUtil.castAction;

/**
 * author ylw
 * Created on 2016/7/10.
 * description : Wuziqi
 */
public class MotionEventTest {
    private static final String TAG = "MotionEventTest";


    public void onTouch(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                break;
            default:

                String s = " Point ID : ";
                int c = e.getPointerCount();
                for (int i = 0; i < c; i++) {
                    s += e.getPointerId(i) + ", ";
                }
                s += "Index : " + e.getActionIndex();
                Log.d(TAG, "Action : " + castAction(e.getActionMasked()) + s);
                break;
        }
    }
}
