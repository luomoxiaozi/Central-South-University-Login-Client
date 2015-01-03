package com.life.util;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;

public class PreferenceHelper
{
  public static final String PASSWORD = "password";
  public static final String REMEMBER = "remember";
  public static final String USERNAME = "username";
  private final String PREFERS_FILE_NAME = "csunet";
  private Context context;

  public PreferenceHelper(Context paramContext)
  {
    this.context = paramContext;
  }

  public HashMap<String, Object> getPreferences()
  {
    HashMap localHashMap = new HashMap();
    SharedPreferences localSharedPreferences = this.context.getSharedPreferences("csunet", 0);
    localHashMap.put("username", localSharedPreferences.getString("username", ""));
    localHashMap.put("password", localSharedPreferences.getString("password", ""));
    localHashMap.put("lastTime", localSharedPreferences.getLong("lastTime", 0));
    localHashMap.put("isRemembered", Boolean.valueOf(localSharedPreferences.getBoolean("isRemembered", false)));
    localHashMap.put("isLogin", Boolean.valueOf(localSharedPreferences.getBoolean("isLogin", false)));
    localHashMap.put("information", localSharedPreferences.getString("information", ""));
    localHashMap.put("netOFF", Boolean.valueOf(localSharedPreferences.getBoolean("netOFF", true)));
    return localHashMap;
  }

  public void save(String paramString1, String paramString2, boolean paramBoolean,Boolean isLogin,long lastTime,String information)
  {
    SharedPreferences.Editor localEditor = this.context.getSharedPreferences("csunet", 0).edit();
    localEditor.putString("username", paramString1);
    localEditor.putString("password", paramString2);
    localEditor.putBoolean("isRemembered", paramBoolean);
    localEditor.putBoolean("isLogin", isLogin);
    localEditor.putLong("lastTime", lastTime);
    localEditor.putString("information", information);
    System.out.println("保存的时候："+paramBoolean);
    localEditor.commit();
  }
}