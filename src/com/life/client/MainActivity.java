package com.life.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.life.receiver.WifiBroadcastReceiver;
import com.life.service.LoginTask;
import com.life.service.LogoutTask;
import com.life.service.TimeService;
import com.life.util.Config;
import com.life.util.PreferenceHelper;
import com.life.util.WifiHelper;

public class MainActivity extends Activity implements View.OnClickListener{
	public static final int CONN_ERROR = 31;
	// private final int CANCEL_QUIT = 30;
	private int backCount = 0;
	private int downLoadCount = 0;
	private MyHandler handler;
	class MyHandler extends Handler {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message paramMessage) {
			int i = 1;
			int j = paramMessage.what;
			Object localObject = null;
			switch (j) {
			case 0:
				HashMap<String, String> localHashMap2 = (HashMap<String, String>) paramMessage.obj;
				MainActivity.this.showInfo(localHashMap2);
				MainActivity.this.infoText.setVisibility(0);
				MainActivity.this.showAnim(MainActivity.this.infoText);
				localObject = "登陆成功";
				i = 0;
				break;
			case 11:
				localObject = "您的密码相对简单，帐号存在被盗风险，请及时修改成强度高的密码";
				HashMap<String, String> localHashMap1 = (HashMap<String, String>) paramMessage.obj;
				MainActivity.this.showInfo(localHashMap1);
				i = 0;
				break;
			case 1:
				localObject = paramMessage.obj;
//				System.out.println("localObject: " + localObject);
				if (localObject == null) {
					localObject = "其它原因认证拒绝";
				}
				break;
			case 2:
				if(isLogin) return;
				localObject = "用户连接已存在，正在尝试下线";
				//TODO
				if(downLoadCount<4) {
					logout();
					downLoadCount++;
				}
				break;
			case 19:
				localObject = "登录失败！如多次尝试仍无法登录：建议1.重连WIFI；2.重启程序";
				break;
			case 20:
				localObject = "下线成功";
				stopService(new Intent(MainActivity.this, TimeService.class));
				nowTime = 0;
				disappearAnim(infoText);
				disappearAnim(time);
				//关闭wifi
				if(Config.netOFF) {
					wifiHelper.closeWifi();
				}
				break;
			case 21:
				localObject = "服务器拒绝请求";
				break;
			case 22:
				localObject = "下线请求执行失败";
				break;
			case 23:
				localObject = "您已经下线";
				break;
			case 29:
				localObject = "电信逗比了，直接关闭网络WiFi";
				break;
			case 31:
				localObject = "请求失败，请检查WIFI是否正确连接到寝室光猫或连接到光猫的路由器";
				break;
			case 30:
				MainActivity.this.backCount = 0;
				localObject = null;
				break;
			case 100:
				if(!wifiHelper.isConnected()) {
					i=1;
					stopService(new Intent(MainActivity.this, TimeService.class));
					break;
				}
				nowTime = (Long)paramMessage.obj*1000;
//				System.out.println("service发过来的时间nowTime:   " + nowTime);
				
				String temp = longToString(nowTime);
				time.setText("上次上网时长: "+temp);
				time.invalidate();
//				System.out.println("设置完了本次上网时间");
				return;
			case 101:
				if(isLogin) {
					localObject = "未下线关闭了wifi,下次登录等待五分钟哦";
				}
				stopService(new Intent(MainActivity.this, TimeService.class));
//				loginBtn.setText("五分钟后尝试");
//				loginBtn.setEnabled(true);
//				return;
//			case 102:
//				loginBtn.setText("未打开wifi");
//				loginBtn.setEnabled(false);
			default:
				break;
			}
			if (localObject != null)
				Toast.makeText(MainActivity.this, localObject + " ", 1).show();
			isRunning = false;
//			loginBtn.setEnabled(true);
			if (i != 0) {
				isLogin = false;
				loginBtn.setText("登陆");
				usernameText.setEnabled(true);
				passwordText.setEnabled(true);
				rememberCheck.setEnabled(true);
			} else {
				isLogin = true;
				loginBtn.setText("下线");
				usernameText.setEnabled(false);
				passwordText.setEnabled(false);
				rememberCheck.setEnabled(false);
				Intent intent = new Intent(MainActivity.this, TimeService.class);
				Bundle bundle = new Bundle();
				bundle.putLong("nowTime", nowTime);
				intent.putExtras(bundle);
				startService(intent);
				
				Calendar rightNow = Calendar.getInstance();
//				System.out.println("连接网络成功: "+longToString(rightNow.getTimeInMillis()));
				lastTime = rightNow.getTimeInMillis();
			}
		}
	};

	private TextView infoText;
	private static TextView time;
	private boolean isRemembered = false;
	private boolean isLogin;// 是否登陆成功
	private Button loginBtn;
	private EditText passwordText;
	private PreferenceHelper preferenceHelper;
	private CheckBox rememberCheck;
	private EditText usernameText;
	private WifiHelper wifiHelper;
	private long nowTime;// 总共登陆时间
	private long lastTime;// 上次登陆时间
	private boolean isRunning = false;
	private String information;

	private void initView() {
		usernameText = ((EditText) findViewById(R.id.usernameText));
		passwordText = ((EditText) findViewById(R.id.passwordText));
		loginBtn = ((Button) findViewById(R.id.loginBtn));
		loginBtn.setOnClickListener(this);
		rememberCheck = ((CheckBox) findViewById(R.id.rememberCheck));
		infoText = ((TextView) findViewById(R.id.infoText));
		time = (TextView) findViewById(R.id.time);
		rememberCheck
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(
							CompoundButton paramCompoundButton,
							boolean paramBoolean) {
						MainActivity.this.isRemembered = paramBoolean;
					}
				});
	}

	/**
	 * 登陆操作
	 */
	private void login() {
		String str3 = null;
		String str1 = this.usernameText.getText().toString().trim();
		String str2 = this.passwordText.getText().toString().trim();
		if ((TextUtils.isEmpty(str1)) || (TextUtils.isEmpty(str2))) {
			Toast.makeText(this, "帐号或密码不能为空", 0).show();
		} else {
			if (!(this.wifiHelper.isConnected())) {
				this.wifiHelper.openWifiSetting();
				return;
			}
			str3 = this.wifiHelper.getIp();
			this.loginBtn.setText("正在登录中...");
			isRunning = true;
			// 参数有：用户名、密码、IP地址
			new LoginTask(this.handler)
					.execute(new String[] { str1, str2, str3 });
		}
	}

	/**
	 * 登出操作
	 */
	private void logout() {
		String str;
		if (!(this.wifiHelper.isConnected())) {
			Toast.makeText(this, "未检测到WIFI连接", 0).show();
		} else {
			str = this.wifiHelper.getIp();
			isRunning = true;
			this.loginBtn.setText("正在下线中...");
			// 参数IP地址
			new LogoutTask(this.handler).execute(new String[] { str });
		}
	}

	/**
	 * 登陆成功，显示信息动画
	 * @param paramView
	 */
	private void showAnim(View paramView) {
		TranslateAnimation localTranslateAnimation = new TranslateAnimation(1,
				-1.0F, 1, 0.0F, 1, 0.0F, 1, 0.0F);
		localTranslateAnimation.setDuration(500L);
		localTranslateAnimation.setFillAfter(true);
		paramView.startAnimation(localTranslateAnimation);
	}
	
	/**
	 * 下线成功，信息消失动画
	 * @param paramView
	 */
	private void disappearAnim(View paramView) {
		TranslateAnimation localTranslateAnimation = new TranslateAnimation(1,
				0.0F, 1, 3.0F, 1, 0.0F, 1, 0.0F);
		localTranslateAnimation.setDuration(1000L);
		localTranslateAnimation.setFillAfter(true);
		paramView.startAnimation(localTranslateAnimation);
	}

	/**
	 * 显示用户，账号信息和余额信息等
	 * @param paramHashMap
	 */
	private void showInfo(HashMap<String, String> paramHashMap) {
		if(paramHashMap != null) {
			String str = (String) paramHashMap.get("surplusmoney");
			if (str.indexOf(".") == 0)
				str = "0" + str;
			information = " 当前帐号：" + ((String) paramHashMap.get("account"))
					+ "\r\n 截止时间：" + ((String) paramHashMap.get("lastupdate"))
					+ "\r\n 总  流  量：" + ((String) paramHashMap.get("totalflow"))
					+ " MB" + "\r\n 剩余流量："
					+ ((String) paramHashMap.get("surplusflow")) + " MB"
					+ "\r\n 账户余额：" + str + "元";
		}
		System.out.println("设置information： "+information);
		this.infoText.setText(information);
		this.infoText.setVisibility(0);
		time.setVisibility(0);
	}

	/**
	 * 将long点的数据，转换为时分秒格式的数据
	 * @param data
	 * @return
	 */
	private String longToString(Long data) {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(data);
	}

	public void onClick(View view) {
		if (isRunning)
			return;
		if (isLogin) {
			logout();
		} else {
			login();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
			this.backCount = (1 + this.backCount);
			if (this.backCount == 1) {
				Toast.makeText(this, "再按一次返回键退出", 3000).show();
				this.handler.sendEmptyMessageDelayed(30, 3000L);
				return false;
			}
			finish();
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			// 监控/拦截菜单键
			startActivity(new Intent(this, SetActivity.class));
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.main);
		initView();
		this.preferenceHelper = new PreferenceHelper(this);
		this.wifiHelper = new WifiHelper(this);
		getIntent().getSerializableExtra("data");
		handler = new MyHandler();
		TimeService.setHandler(handler);
		WifiBroadcastReceiver.setHandler(handler);
		HashMap<String, Object> localHashMap = this.preferenceHelper
				.getPreferences();
		isRemembered = ((Boolean) localHashMap.get("isRemembered"))
				.booleanValue();
		isLogin = ((Boolean) localHashMap.get("isLogin")).booleanValue();
		Config.netOFF = ((Boolean)localHashMap.get("netOFF")).booleanValue();
		System.out.println("onResumeIsRemember:  " + isRemembered
				+ "  isLogin:" + isLogin);
		if (isLogin&&wifiHelper.isConnected()) {
			this.loginBtn.setText("下线");
			this.usernameText.setText((String) localHashMap.get("username"));
			this.passwordText.setText((String) localHashMap.get("password"));
			usernameText.setEnabled(false);
			passwordText.setEnabled(false);
		    Calendar rightNow = Calendar.getInstance();
//			System.out.println("软件重新开启，上网时间为: "+longToString(rightNow.getTimeInMillis()));
			lastTime = (Long)localHashMap.get("lastTime");
			if(lastTime==0) {
				nowTime = 0;
			}else {
				nowTime = (rightNow.getTimeInMillis() - lastTime)/1000;
			}
			System.out.println("上次保留的时间为： "+longToString((Long)localHashMap.get("lastTime")));
			System.out.println("开始计时的时间： "+nowTime);
			information = (String) localHashMap.get("information");
			System.out.println("欧诺createimformation:  "+information);
			showInfo(null);
			Intent intent = new Intent(MainActivity.this, TimeService.class);
			Bundle bundle = new Bundle();
			bundle.putLong("nowTime", nowTime);
			intent.putExtras(bundle);
			startService(intent);
			if (this.isRemembered) {
				this.rememberCheck.setChecked(true);
			} else {
				this.rememberCheck.setChecked(false);
			}
		} else {
			this.loginBtn.setText("登陆");
			if (this.isRemembered) {
				this.usernameText
						.setText((String) localHashMap.get("username"));
				this.passwordText
						.setText((String) localHashMap.get("password"));
				this.rememberCheck.setChecked(true);
			} else {
				this.rememberCheck.setChecked(false);
			}
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (isRemembered || isLogin)
			this.preferenceHelper.save(this.usernameText.getText().toString()
					.trim(), this.passwordText.getText().toString().trim(),
					this.isRemembered, isLogin, lastTime,information);
		else {
			this.preferenceHelper.save("", "", false, false, 0,information);
		}
		stopService(new Intent(MainActivity.this, TimeService.class));
//		System.out.println("OnPauseIsRemember:  " + isRemembered
//				+ "  isLOgin  " + isLogin);
	}

	protected void onResume() { 
		super.onResume();
		if (!(this.wifiHelper.isConnected()))
			this.wifiHelper.openWifiSetting();
	}
}