package com.hsdroid.moviebuff.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtilities {

    private SharedPreferences preferences;
    Context context;


    private PrefUtilities(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }


    public static PrefUtilities with(Context context) {
        return new PrefUtilities(context);
    }


    public void setIsFirstTimeLogin(String value) {

        preferences.edit().putString("key", value).apply();

    }

    public String getIsFirstTimeLogin() {

        return preferences.getString("key", "");

    }

}
