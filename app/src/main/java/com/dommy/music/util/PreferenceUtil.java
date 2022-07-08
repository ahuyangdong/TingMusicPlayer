package com.dommy.music.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtil {

    public static final String spName = "TingMuiscPlayer";

    public static void set(Context context, String key, String val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString(key, val);
        editor.commit();//提交修改
    }

    public static void setBoolean(Context context, String key, boolean val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putBoolean(key, val);
        editor.commit();//提交修改
    }

    public static String get(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(spName, Activity.MODE_PRIVATE);
        String r = preferences.getString(key, "");
        return r;
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(spName, Activity.MODE_PRIVATE);
        boolean b = preferences.getBoolean(key, false);
        return b;
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(spName, Activity.MODE_PRIVATE);
        boolean b = preferences.getBoolean(key, defaultValue);
        return b;
    }

    public static void setInt(Context context, String key, int val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt(key, val);
        editor.commit();//提交修改
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(spName, Activity.MODE_PRIVATE);
        return preferences.getInt(key, defaultValue);
    }

    public static void setFloat(Context context, String key, float val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putFloat(key, val);
        editor.commit();//提交修改
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(spName, Activity.MODE_PRIVATE);
        return preferences.getFloat(key, defaultValue);
    }

}
