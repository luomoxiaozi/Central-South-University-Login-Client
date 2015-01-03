package com.life.util;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  Administrator
 * @version  [版本号, 2014-3-10]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public enum WifiStateEnum
{
	CONNECTED, //连接上WiFi
	CONNECTING, //正在连接WiFi
	DISCONNECTED, //未连接WiFi
	DISCONNECTING, //正在取消连接WiFi
	SUSPENDED,//废除状态
	UNKNOWN;//未知状态
	
    
    private static final String[] WIFI_STATE_STR;
    
    public String toString()
    {
        return WIFI_STATE_STR[this.ordinal()];
    }
    
    static
    {
    	WIFI_STATE_STR = new String[] {"连接上WiFi", "正在连接WiFi", "未连接WiFi", "正在取消连接WiFi","废除状态","未知状态"};
    }
    
}
