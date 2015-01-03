package com.life.util;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;

public class CustomHttpClient {
	public static HttpClient getHttpClient() {
		return HolderClass.customHttpClient;
	}

	private static class HolderClass {
		private static final HttpClient customHttpClient = newInstance();

		private static HttpClient newInstance() {
			BasicHttpParams localBasicHttpParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(localBasicHttpParams,
					HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(localBasicHttpParams, "UTF-8");
			HttpProtocolParams.setUseExpectContinue(localBasicHttpParams, true);
			HttpProtocolParams
					.setUserAgent(
							localBasicHttpParams,
							"Mozilla/5.0 (compatible; MSIE 9.0; qdesk 2.5.1277.202; Windows NT 6.2; WOW64; Trident/6.0)");
			ConnManagerParams.setTimeout(localBasicHttpParams, 1000L);
			ConnManagerParams.setMaxTotalConnections(localBasicHttpParams, 800);
			HttpConnectionParams.setConnectionTimeout(localBasicHttpParams,
					6000);
			HttpConnectionParams.setSoTimeout(localBasicHttpParams, 10000);
			SchemeRegistry localSchemeRegistry = new SchemeRegistry();
			localSchemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			localSchemeRegistry.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));
			return new DefaultHttpClient(new ThreadSafeClientConnManager(
					localBasicHttpParams, localSchemeRegistry),
					localBasicHttpParams);
		}
	}
}