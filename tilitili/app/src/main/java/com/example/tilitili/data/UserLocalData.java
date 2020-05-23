package com.example.tilitili.data;

import android.content.Context;
import android.text.TextUtils;

import com.example.tilitili.utils.JSONUtils;
import com.example.tilitili.utils.PreferencesUtils;

public class UserLocalData {

    public static void putUser(Context context, User user) {
        String user_json = JSONUtils.toJson(user);
        PreferencesUtils.putString(context, Contants.USER_JSON, user_json);
    }

    public static User getUser(Context context) {
        String user_json = PreferencesUtils.getString(context, Contants.USER_JSON);
        if (!TextUtils.isEmpty(user_json)) {
            return JSONUtils.fromJson(user_json, User.class);
        }
        return null;
    }

    public static void clearUser(Context context) {
        PreferencesUtils.putString(context, Contants.USER_JSON, "");
    }

}
