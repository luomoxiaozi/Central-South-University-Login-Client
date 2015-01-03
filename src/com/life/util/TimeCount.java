//package com.life.util;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import android.net.wifi.p2p.WifiP2pManager.ActionListener;
//
//public class TimeCount {
//	Date now=new Date(); 
//	public void Test() { 
//
//	now.setHours(0); 
//	now.setMinutes(0); 
//	now.setSeconds(0); 
//	Date now2=new Date(now.getTime()+1000); 
//	now=now2; 
//	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); 
//	formatter.format(now);
//
//	Timer timer=new Timer(1000,new ActionListener(){ 
//
//	public void actionPerformed(ActionEvent e) { 
//	} 
//	}); 
//	timer.start(); 
//	}
//}
