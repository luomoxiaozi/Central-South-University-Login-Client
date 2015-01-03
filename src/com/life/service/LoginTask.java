package com.life.service;

import java.util.Calendar;
import java.util.HashMap;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.life.receiver.WifiBroadcastReceiver;
import com.life.util.HttpClientUtil;
import com.life.util.JsonUtil;
import com.life.util.RSAhelper;

public class LoginTask extends AsyncTask<String, Integer, String> {
	public static final String ACCOUNT = "account";
	public static final int CONN_EXIST = 2;
	public static final int EASY_PASSWORD = 11;
	public static final int ERROR = 19;
	public static final String LAST_UPDATE = "lastupdate";
	public static final int NAME_PWD_EXIST_REFUSE = 1;
	public static final String RESULT_CODE = "resultCode";
	public static final String RESULT_DESCRIBE = "resultDescribe";
	public static final int SUCCESS = 0;
	public static final String SURPLUS_FLOW = "surplusflow";
	public static final String SURPLUS_MONEY = "surplusmoney";
	public static final String TOTAL_FLOW = "totalflow";
	private final String LOGIN_URL = "http://61.137.86.87:8080/portalNat444/AccessServices/login";
	private Handler handler;

	public LoginTask(Handler paramHandler) {
		this.handler = paramHandler;
	}

	protected String doInBackground(String[] params) {
		// 用户名、密码、IP地址
		try {
			String username = params[0];
			String password = params[1];
			String ip = params[2];
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("accountID", username + "@zndx.inter");
			paramMap.put("password", RSAhelper.helper(password));
			paramMap.put("brasAddress", "59df7586");
			paramMap.put("userIntranetAddress", ip);
			HashMap<String, String> headMap = new HashMap<String, String>();
			headMap.put("Referer",
					"http://61.137.86.87:8080/portalNat444/index.jsp");
			String str4 = HttpClientUtil
					.doPost("http://61.137.86.87:8080/portalNat444/AccessServices/login",
							headMap, paramMap);
			return str4;
		} catch (Exception e) {
			return null;
		}
		
	}

	/**
	 * 将doInBackground函数的返回值，当做参数传进去
	 */
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Message msg;
		HashMap<String, Object> data = JsonUtil.jsonToMap(result, new String[] {
				"resultCode", "resultDescribe", "account", "lastupdate",
				"totalflow", "surplusflow", "surplusmoney" });
		if(data == null&&handler!=null) this.handler.sendEmptyMessage(31);
		else {
			int resultCode;
			try {
				System.out.println("resultCode: "+data.get("resultCode"));
				System.out.println("resultDescribe: "+data.get("resultDescribe"));
				resultCode = Integer.parseInt((String) data.get("resultCode"));
				System.out.println("LoginIn接收的handler："+handler);
				if(resultCode==0&&handler == null) {
					SharedPreferences sp = WifiBroadcastReceiver.getSP();
					Calendar rightNow = Calendar.getInstance();
					Editor editor = sp.edit();
					editor.putBoolean("isLogin", true);
					editor.putLong("lastTime", rightNow.getTimeInMillis());
					editor.commit();
					System.out.println("执行了，设置登录状态为true"+rightNow.getTimeInMillis());
					return;
				}
				if(handler == null) return;
				msg = this.handler.obtainMessage();
				switch (resultCode) {
				case 0:
					msg.what = 0;
					msg.obj = data;
					this.handler.sendMessage(msg);
					break;
				case 11:
					msg.what = 11;
					msg.obj = data;
					this.handler.sendMessage(msg);
					break;
				case 1:
					msg.what = 1;
					if(!data.get("resultDescribe").equals(null)&&!data.get("resultDescribe").equals(""))
					msg.obj = data.get("resultDescribe");
					this.handler.sendMessage(msg);
					break;
				case 2:
					this.handler.sendEmptyMessage(2);
					break;
				default:
					this.handler.sendEmptyMessage(19);
					break;
				}
			} catch (NumberFormatException localNumberFormatException) {
				this.handler.sendEmptyMessage(31);
			}
		}
	}
}