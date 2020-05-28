package com.example.tilitili;

import android.app.Application;

import com.example.tilitili.data.User;
import com.example.tilitili.data.UserLocalData;
import com.facebook.drawee.backends.pipeline.Fresco;

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
        Fresco.initialize(this);
    }

    private void initUser() {
        this.user = UserLocalData.getUser(this);
    }

    public User getUser() {
        return user;
    }

    public void putUser(User user) {
        this.user = user;
        UserLocalData.putUser(this, user);
    }

    public void clearUser() {
        this.user = null;
        UserLocalData.clearUser(this);
    }

}
