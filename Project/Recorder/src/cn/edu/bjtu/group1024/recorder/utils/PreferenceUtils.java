package cn.edu.bjtu.group1024.recorder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtils {
	private static final String SHARED_PREFF_LOGIN = "islogin";// 判断是否登录
	private static final String SHARED_PREFF_NAME = "loginname";// 百度登录用户名
	private static final String SHARED_PREFF_TOKEN = "token";// 存在本地token值

	private static SharedPreferences mSharedPreferences;
	private static Editor mEditor;

	public static void init(Context context) {
		mSharedPreferences = context.getSharedPreferences("group1024",
				Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}

	// 从本地获取登陆状态
	public static boolean getLoginStatus() {
		return mSharedPreferences.getBoolean(SHARED_PREFF_LOGIN, false);
	}

	// 设置本地登陆状态
	public static void setLoginStatus(boolean status) {
		mEditor.putBoolean(SHARED_PREFF_LOGIN, status);
		mEditor.commit();
	}

	// 从本地获取token
	public static String getToken() {
		return mSharedPreferences.getString(SHARED_PREFF_TOKEN, "");
	}

	// 设置本地token
	public static void setToken(String token) {
		mEditor.putString(SHARED_PREFF_TOKEN, token);
		mEditor.commit();
	}

	// 从本地获取登陆用户名
	public static String getLoginName() {
		return mSharedPreferences.getString(SHARED_PREFF_NAME, "");
	}

	// 设置本地登陆用户名
	public static void setLoginName(String name) {
		mEditor.putString(SHARED_PREFF_NAME, name);
		mEditor.commit();
	}

	// 清除所有存储的数据
	public static void clearData() {
		setLoginName("");
		setLoginStatus(false);
		setToken("");
	}
}
