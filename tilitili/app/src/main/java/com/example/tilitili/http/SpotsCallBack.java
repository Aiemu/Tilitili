package com.example.tilitili.http;

import android.content.Context;
import android.util.Log;

import com.example.tilitili.R;

import java.util.Objects;

import dmax.dialog.SpotsDialog;
import okhttp3.Request;
import okhttp3.Response;

public abstract class SpotsCallBack<T> extends SimpleCallback<T> {

    private SpotsDialog dialog;
    private Context context;

    public SpotsCallBack(Context context) {
        super(context);
        this.context = context;
        this.initSpotsDialog();
    }

    private void initSpotsDialog() {
        dialog = (SpotsDialog) new SpotsDialog.Builder()
                .setContext(context)
                .setTheme(R.style.AppTheme_SpotsDialogCustom)
                .setMessage(R.string.loading)
                .build();
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

    @Override
    public void onFailure(Request request, Exception e) {
        Log.d("asd", Objects.requireNonNull(e.getMessage()));
        dismissDialog();
    }

    public Context getContext() {
        return context;
    }
}