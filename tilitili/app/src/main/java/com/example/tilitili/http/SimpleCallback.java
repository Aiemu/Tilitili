package com.example.tilitili.http;

import android.content.Context;
import android.content.Intent;

import com.example.tilitili.LoginActivity;
import com.example.tilitili.R;
import com.example.tilitili.UserManagerApplication;
import com.example.tilitili.utils.ToastUtils;

import okhttp3.Request;
import okhttp3.Response;

public abstract class SimpleCallback<T> extends BaseHttpCallback<T> {
    protected Context context;

    public SimpleCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onBeforeRequest(Request request) {
    }

    @Override
    public void onFailure(Request request, Exception e) {
    }

    @Override
    public void onResponse(Response response) {
    }

    @Override
    public void onTokenError(Response response, int code) {
        ToastUtils.show(context, context.getString(R.string.token_error));
        UserManagerApplication.getInstance().cleanSessionId();
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        context.startActivity(intent);
        UserManagerApplication.getInstance().clearUser();
    }
}
