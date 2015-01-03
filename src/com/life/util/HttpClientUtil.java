package com.life.util;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
	private static final String CHARSET = "utf-8";

	public static String doGet(String url) throws Exception {

		HttpClient httpClient = CustomHttpClient.getHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = httpClient.execute(httpGet);
		if (response.getStatusLine().getStatusCode() == 200) {
			return EntityUtils.toString(response.getEntity(), CHARSET);
		}
		throw new SocketException("请求响应失败");
	}

	public static String doPost(String url, HashMap<String, String> headInfo,
			HashMap<String, String> params) throws Exception {
		Set<Map.Entry<String, String>> set;
		Map.Entry<String, String> entry;
		HttpClient httpClient = CustomHttpClient.getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		if (headInfo != null) {
			set = headInfo.entrySet();
			if (set.iterator().hasNext()) {
				entry = (Map.Entry<String, String>) set.iterator().next();
				httpPost.setHeader((String) entry.getKey(),
						(String) entry.getValue());
			}
		}
		if (params != null) {
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			 Iterator<String> it = params.keySet().iterator();  
			  while (it.hasNext()){  
			      String key;  
			      key=(String)it.next();  
			      parameters.add(new BasicNameValuePair(key,
			    		  params.get(key)));
			  }  
			httpPost.setEntity(new UrlEncodedFormEntity(parameters, CHARSET));
		}
		HttpResponse response = httpClient.execute(httpPost);
		if (response.getStatusLine().getStatusCode() == 200) {
			return EntityUtils.toString(response.getEntity(), CHARSET);
		}
		throw new SocketException("请求响应失败");
	}
}