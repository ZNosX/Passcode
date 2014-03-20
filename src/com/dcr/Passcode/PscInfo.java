package com.dcr.Passcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class PscInfo {

	public static String getPIN(String key, Context context) {
		SharedPreferences sharePrefs = context.getSharedPreferences(
				"passcode", Context.MODE_PRIVATE);
		return sharePrefs.getString(key, null);
	}

	public static void savePIN(String key, String value,
			Context context) {
		SharedPreferences sharePrefs = context.getSharedPreferences(
				"passcode", Context.MODE_PRIVATE);
		Editor editor = sharePrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getMsg(String key, Context context) {
		SharedPreferences sharePrefs = context.getSharedPreferences(
				"MSG", Context.MODE_PRIVATE);
		return sharePrefs.getString(key, null);
	}

	public static void saveMsg(String key, String value,
			Context context) {
		SharedPreferences sharePrefs = context.getSharedPreferences(
				"MSG", Context.MODE_PRIVATE);
		Editor editor = sharePrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
}