package com.life.receiver;


import com.life.util.Config;
import com.life.util.WifiHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootCompleteReceiver extends BroadcastReceiver{
	private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
	private static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";  
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sp = context.getSharedPreferences("csunet", 0);
		Config.turnOnNet = sp.getBoolean("turnOnNet", true);
		Config.turnOffNet = sp.getBoolean("turnOffNet", true);
		WifiHelper wifiHelper = new WifiHelper(context);
		
		if(intent.getAction().equals(ACTION_BOOT)) {
			if(Config.turnOnNet) {
				System.out.println("turnOnNet: "+Config.turnOnNet);
				wifiHelper.openWifi();
			}
		}
		if(intent.getAction().equals(ACTION_SHUTDOWN)) {
			if(Config.turnOffNet) {
				System.out.println("turnOffNet: "+Config.turnOffNet);
				wifiHelper.closeWifi();
			}
		}
	}
}
