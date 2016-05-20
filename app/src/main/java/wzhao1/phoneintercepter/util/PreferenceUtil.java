package wzhao1.phoneintercepter.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wzhao1 on 16/5/18.
 */
public class PreferenceUtil {

    private static final String PREF_NAME = "zwx_info";

    public static void setSharedPreference(Context context, String key, String data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, data).apply();
    }

    public static String getSharedPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
}
