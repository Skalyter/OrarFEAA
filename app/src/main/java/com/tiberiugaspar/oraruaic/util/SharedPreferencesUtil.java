package com.tiberiugaspar.oraruaic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUtil {
    public static String getSharedPreference(Context applicationContext, String key, String defaultValue) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(applicationContext);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void saveSharedPreference(Context applicationContext, String key, String value) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
