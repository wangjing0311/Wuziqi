package com.ylw.wuziqi.net;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskUtil {
 	private final static Handler h = new Handler(Looper.getMainLooper());

	public static void postMainTask(Runnable run) {
		h.post(run);
	}

	public static void postMainTaskDelay(Runnable run, long delayMills) {
		h.postDelayed(run, delayMills);
	}


	public static ExecutorService createSerialExecutor() {
		return Executors.newSingleThreadExecutor();
	}
}
