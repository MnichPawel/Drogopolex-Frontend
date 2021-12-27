package com.example.drogopolex.utils;

import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    public static void resetSharedPreferences(SharedPreferences sp) {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString("token", "");
        spEditor.putString("user_id", "");
        spEditor.putBoolean("loggedIn", false);
        spEditor.apply();
    }
}
