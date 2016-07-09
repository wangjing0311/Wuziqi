package com.ylw.wuziqi.net;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskUtil {
 	private final static Handler h = new Handler(Looper.getMainLooper());

	public void postMainTask(Runnable run) {
		h.post(run);
	}

	public void postMainTaskDelay(Runnable run, long delayMills) {
		h.postDelayed(run, delayMills);
	}


	public static ExecutorService createSerialExecutor() {
		return Executors.newSingleThreadExecutor();
	}
}
