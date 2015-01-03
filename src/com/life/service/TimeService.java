package com.life.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class TimeService extends Service{

	private static Handler handler;
	public static void setHandler(Handler handler) {
		TimeService.handler = handler;
	}
	
	public static long count;
	
	/**
	 * 定时器
	 * 
	 * @return
	 */
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 100;
			msg.obj = count;
			handler.sendMessage(msg);
			count++;
//			System.out.println("执行完了发送MSG");
			handler.postDelayed(this, 1000);
		}
	};
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
//		System.out.println("创建了一个service");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		System.out.println("启动了一个service");
		if(intent!=null) {
			Bundle bundle = intent.getExtras();
//			System.out.println("bundle：----》"+bundle);
			count = bundle.getLong("nowTime");
//			System.out.println("count:------->"+count);
			handler.post(runnable);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(runnable);
	}

}
