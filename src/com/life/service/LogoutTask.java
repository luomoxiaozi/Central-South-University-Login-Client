package com.life.service;

import java.util.HashMap;
import java.util.TreeMap;

import android.os.AsyncTask;
import android.os.Handler;

import com.life.util.HttpClientUtil;
import com.life.util.JsonUtil;

public class LogoutTask extends AsyncTask<String, Integer, String> {
	public static final int LOGOUTED = 23;
	public static final int LOGOUT_ERROR = 29;
	public static final int LOGOUT_FAILED = 22;
	public static final int LOGOUT_REFUSE = 21;
	public static final int LOGOUT_SUCCESS = 20;
	public static final String RESULT_CODE = "resultCode";
	private final String LOGOUT_URL = "http://61.137.86.87:8080/portalNat444/AccessServices/logout?";
	private Handler handler;

	public LogoutTask(Handler paramHandler) {
		this.handler = paramHandler;
	}

	protected String doInBackground(String[] params) {
		String ip = params[0];
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("brasAddress", "59df7586");
		paramMap.put("userIntranetAddress", ip);
		HashMap<String, String> headMap = new HashMap<String, String>();
		headMap.put("Referer",
				"http://61.137.86.87:8080/portalNat444/main2.jsp");
		try {
			return HttpClientUtil
					.doPost("http://61.137.86.87:8080/portalNat444/AccessServices/logout?",
							headMap, paramMap);
		} catch (Exception e) {
			return null;
//			e.printStackTrace();
		}
	}

	protected void onPostExecute(String paramString) {
		super.onPostExecute(paramString);
		int i;
		System.out.println("LogoutparamString:  "+paramString);
		HashMap<String, Object> localHashMap = JsonUtil.jsonToMap(paramString,
				new String[] { "resultCode" });
		// TODO
		if(handler == null) return;
		if(localHashMap==null) {
			this.handler.sendEmptyMessage(29);
		}else {
			try {
//				if(localHashMap.get("resultCode").equals(null)||localHashMap.get("resultCode").equals("")) {
//					i = 100;
//				}
//				else {
//					i = Integer.parseInt((String) localHashMap.get("resultCode"));
//				}
				
				i = Integer.parseInt((String) localHashMap.get("resultCode"));
				System.out.println("Logout_resultCode:" + i);
				switch (i) {
				case 0:
					this.handler.sendEmptyMessage(20);
					break;
				case 1:
					this.handler.sendEmptyMessage(21);
					break;
				case 2:
					this.handler.sendEmptyMessage(22);
					break;
				case 3:
					this.handler.sendEmptyMessage(23);
					break;
				default:
					this.handler.sendEmptyMessage(29);
					break;
				}
			} catch (NumberFormatException localNumberFormatException) {
//				localNumberFormatException.printStackTrace();
				this.handler.sendEmptyMessage(29);
			}
		}
	}
}