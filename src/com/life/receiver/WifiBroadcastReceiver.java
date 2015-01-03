package com.life.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

import com.life.service.LoginTask;
import com.life.util.Config;
import com.life.util.WifiHelper;
import com.life.util.WifiStateEnum;

public class WifiBroadcastReceiver extends BroadcastReceiver{

	private static Handler handler;
	private static SharedPreferences sp;
	public static void setHandler(Handler handler) {
		WifiBroadcastReceiver.handler = handler;
	}
	public static SharedPreferences getSP() {
		return sp;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("csunet", 0);
		Config.netON = sp.getBoolean("netON", true);
		Config.netOFF = sp.getBoolean("netOFF", true);
		String username = sp.getString("username", null);
		String password = sp.getString("password", null);
		String ipAddress="";
		// 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()))
        {
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra)
            {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                WifiHelper.wifiStateEnum = WifiStateEnum.DISCONNECTED;
                switch (networkInfo.getState())
                {
                case CONNECTED:
                    Log.e("APActivity", "CONNECTED");
                    WifiHelper.wifiStateEnum = WifiStateEnum.CONNECTED;
                    if(!Config.netON) return;
                    System.out.println("netOn: "+Config.netON);
                    //启动连接
                    WifiHelper wifiHelper = new WifiHelper(context);
                    ipAddress = wifiHelper.getIp();
                    System.out.println("执行到了str: "+ipAddress);
                    System.out.println("用户名："+username+"  密码："+password);
                    if(username!=null&&password!=null) {
                    	 new LoginTask(handler)
     					.execute(new String[] { username, password, ipAddress });
                    	 System.out.println("执行到了这里username");
                    }
                    break;
                case CONNECTING:
                    Log.e("APActivity", "CONNECTING");
                    WifiHelper.wifiStateEnum = WifiStateEnum.CONNECTING;
                    break;
                case DISCONNECTED:
//                	sp.edit().putBoolean("isLogin", false).commit();
                    Log.e("APActivity", "DISCONNECTED");
                    WifiHelper.wifiStateEnum = WifiStateEnum.DISCONNECTED;
                    if(handler!=null) {
                    	if(Config.netOFF) handler.sendEmptyMessage(101);
                    }
                    break;
                case DISCONNECTING:
                    Log.e("APActivity", "DISCONNECTING");
                    WifiHelper.wifiStateEnum = WifiStateEnum.DISCONNECTING;
                    break;
                case SUSPENDED:
                    Log.e("APActivity", "SUSPENDED");
                    WifiHelper.wifiStateEnum = WifiStateEnum.SUSPENDED;
                    break;
                case UNKNOWN:
                    Log.e("APActivity", "UNKNOWN");
                    WifiHelper.wifiStateEnum = WifiStateEnum.UNKNOWN;
                    break;
                default:
                    break;
                }
            }
        }
        
        // 这个监听wifi的打开与关闭，与wifi的连接无关
//        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction()))
//        {
//            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
//            switch (wifiState)
//            {
//            case WifiManager.WIFI_STATE_ENABLED:
//                Log.e("APActivity", "WIFI_STATE_ENABLED");
//                break;
//            case WifiManager.WIFI_STATE_ENABLING:
//                Log.e("APActivity", "WIFI_STATE_ENABLING");
//                break;
//            case WifiManager.WIFI_STATE_DISABLED:
//                Log.e("APActivity", "WIFI_STATE_DISABLED");
//                if(handler!=null) {
//                	handler.sendEmptyMessage(102);
//                }
//                break;
//            case WifiManager.WIFI_STATE_DISABLING:
//                Log.e("APActivity", "WIFI_STATE_DISABLING");
//                break;
//            }
//        }
        
        
	}
	
}
