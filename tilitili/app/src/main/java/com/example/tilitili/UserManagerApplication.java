package com.example.tilitili;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.example.tilitili.data.User;
import com.example.tilitili.data.UserLocalData;

public class UserManagerApplication extends Application {
    private User user;

    private static UserManagerApplication mInstance;

    public static UserManagerApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        this.initUser();
    }

    private void initUser() {
        this.user = UserLocalData.getUser(this);
    }

    public User getUser() {
        return user;
    }

    public void putUser(User user, String token) {
        this.user = user;
        UserLocalData.putUser(this, user);
        UserLocalData.putToken(this, token);
    }

    public void clearUser() {
        this.user = null;
        UserLocalData.clearUser(this);
        UserLocalData.clearToken(this);
    }

    public String getToken() {
        return UserLocalData.getToken(this);
    }
}
