package com.life.util;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {
	// ERROR //
	public static HashMap<String, Object> jsonToMap(String json, String[] keys) {
		// 对JSON字符串进行解析，得到对应的key 和 value 值 放入到 keys对应的map里头
//		System.out.println("JsonUtilparamString:   " + json);
		HashMap<String, Object> data = new HashMap<String, Object>();
		if (json == null || json.equals("")) {
			return null;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(json);
			for(int i=0;i<keys.length;i++) {
				if(keys[i].equals("account")&&(!json.contains("surplusmoney"))) {
//					System.out.println("执行到了这里。。。。。"+"   "+keys[2]);
					continue;
				}
				if(json.contains(keys[i])) {
					Object obj = jsonObject.get(keys[i]);
//					System.out.println("keys: "+keys[i]+"   "+obj);
					data.put(keys[i], obj);
				}
			}
			if(data.size()<=0) {
				return null; 
			}else {
				return data;
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
			return data;
		}
	}
}