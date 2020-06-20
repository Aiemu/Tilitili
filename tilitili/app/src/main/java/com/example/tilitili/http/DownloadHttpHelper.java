package com.example.tilitili.http;

import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import com.example.tilitili.UserManagerApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DownloadHttpHelper {
    private static DownloadHttpHelper instance;
    private OkHttpClient httpClient;
    private UserManagerApplication userManagerApplication;

    public DownloadHttpHelper() {
        userManagerApplication = UserManagerApplication.getInstance();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public static DownloadHttpHelper getInstance() {
        if(instance == null) {
            instance = new DownloadHttpHelper();
        }
        return instance;
    }

    public void download(String url, int type, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("cookie", userManagerApplication.getSessionId())
                .get()
                .build();
        httpClient.newCall(request).enqueue(callback);
    }
}
