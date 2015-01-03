package com.life.client;

import com.life.util.Config;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.KeyEvent;

public class SetActivity extends PreferenceActivity implements OnPreferenceClickListener,  
OnPreferenceChangeListener  {

	CheckBoxPreference netON_selection_pref;
	CheckBoxPreference netOFF_selection_pref;
	CheckBoxPreference turnOnNet_selection_pref;
	CheckBoxPreference turnOffNet_selection_pref;
	private SharedPreferences sp;
	private static String TAG = "HelloPreference";  

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 加载第一个布局
		addPreferencesFromResource(R.xml.preferencescreentest_one);
		netON_selection_pref = (CheckBoxPreference)findPreference("netON_selection_pref");
		netOFF_selection_pref = (CheckBoxPreference)findPreference("netOFF_selection_pref");
		turnOnNet_selection_pref = (CheckBoxPreference)findPreference("turnOnNet_selection_pref");
		turnOffNet_selection_pref = (CheckBoxPreference)findPreference("turnOffNet_selection_pref");
		
		netON_selection_pref.setOnPreferenceClickListener(this);
		netOFF_selection_pref.setOnPreferenceClickListener(this);
		turnOnNet_selection_pref.setOnPreferenceClickListener(this);
		turnOffNet_selection_pref.setOnPreferenceClickListener(this);
		// 得到我们的存储Preferences值的对象，然后对其进行相应操作  
        sp = this.getSharedPreferences("csunet", 0);	
	}
	
	
	// 对控件进行的一些操作  
    private void operatePreference(Preference preference) {  
    	Editor editor = sp.edit();
        if (preference == netON_selection_pref){   
        	Config.netON = netON_selection_pref.isChecked();
        	editor.putBoolean("netON", Config.netON);
        	Log.i(TAG, " 打开WiFi自动上网： "+ Config.netON);  
        }
        if (preference.getKey().equals("netOFF_selection_pref")){  
        	Config.netOFF = netOFF_selection_pref.isChecked();
        	editor.putBoolean("netOFF", Config.netOFF);
            Log.i(TAG, " 下线自动关闭wifi：  "+Config.netOFF);  
        }
        if (preference == turnOnNet_selection_pref){   
        	Config.turnOnNet = turnOnNet_selection_pref.isChecked();
        	editor.putBoolean("turnOnNet", Config.turnOnNet);
            Log.i(TAG, " 开机自动上网：   "+ Config.turnOnNet);  
        }
        if (preference.getKey().equals("turnOffNet_selection_pref")) { 
        	Config.turnOffNet = turnOffNet_selection_pref.isChecked();
        	editor.putBoolean("turnOffNet", Config.turnOffNet);
        	Log.i(TAG, " 关机自动关闭wifi：   "+ Config.turnOffNet);  
        }
        editor.commit();
    }  

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		return false;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		operatePreference(preference);
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 监控/拦截菜单键
			startActivity(new Intent(this, MainActivity.class));
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			SetActivity.this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
