package com.life.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WifiHelper {
	private Context context;
	private static WifiManager wifiManager;
	public static WifiStateEnum wifiStateEnum;
	
	public WifiHelper(Context paramContext) {
		this.context = paramContext;
		wifiManager = ((WifiManager) paramContext.getSystemService("wifi"));
	}
	
	/**
	 * 打开wifi
	 */
	public void openWifi() {
		if(!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
	}
	
	/**
	 * 关闭wifi
	 */
	public void closeWifi() {
		if(wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		}
	}

	private String ipToString(int paramInt) {
		return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "."
				+ (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
	}

	public String getIp() {
		if (!(isConnected()))
			return null;
		int i = wifiManager.getConnectionInfo().getIpAddress();
		if (i == 0) {
			Toast.makeText(this.context, "正在获取IP地址，请稍候", 0).show();
			return null;
		}
		return ipToString(i);
	}

	public boolean isConnected() {
		return wifiManager.isWifiEnabled();
	}

	public void openWifiSetting() {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder((Activity)context);
		localBuilder
				.setTitle("提示：")
				.setMessage(
						"数字中南客户端需要WIFI连接支持，并且需要连接到您寝室的光猫或者是接入光猫的路由器，是否打开WIFI设置进行设置？")
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
						WifiHelper.this.context.startActivity(new Intent(
								"android.settings.WIFI_SETTINGS"));
					}
				}).create();
		localBuilder.show();
	}
}