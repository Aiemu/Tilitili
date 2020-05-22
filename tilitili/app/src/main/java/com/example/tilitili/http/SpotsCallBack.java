package com.example.tilitili.http;

import android.content.Context;

import com.example.tilitili.R;

import okhttp3.Request;
import okhttp3.Response;
import dmax.dialog.SpotsDialog;

public abstract class SpotsCallBack<T> extends SimpleCallback<T> {

    private SpotsDialog dialog;

    public SpotsCallBack(Context context) {
        super(context);
        this.initSpotsDialog();
    }

    private void initSpotsDialog() {
        dialog = (SpotsDialog) new SpotsDialog.Builder().setContext(context).setMessage(R.string.loading).build();
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public void setMessage(int resId) {
        dialog.setMessage(context.getString(resId));
    }

    @Override
    public void onBeforeRequest(Request request) {
        showDialog();
    }

    @Override
    public void onResponse(Response response) {
        dismissDialog();
    }
}