package com.example.tilitili.http;

import android.os.Handler;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;

import com.example.tilitili.UserManagerApplication;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHelper {
    private static OkHttpClient okHttpClient;
    private Gson gson;
    private Handler handler;
    private static HttpHelper httpHelper;

    private HttpHelper() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
        handler = new Handler(Looper.getMainLooper());
    }

    public static HttpHelper getInstance() {
        if (httpHelper == null) {
            httpHelper = new HttpHelper();
            return httpHelper;
        }
        return httpHelper;
    }

    public void get(String url, BaseHttpCallback callback) {
        Request request = buildRequset(url, null, HttpMethodType.GET);
        request(request, callback);
    }

    public void post(String url, Map<String, String> params, BaseHttpCallback callback) {
        Request request = buildRequset(url, params, HttpMethodType.POST);
        request(request, callback);
    }

    public void request(final Request request, final BaseHttpCallback callback) {

        callback.onBeforeRequest(request);

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(request, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (response.isSuccessful()) {
                    String result = response.body().string();
                    if (callback.type == String.class) {
                        callbackSuccess(callback, response, result);
                    } else {
                        try {
                            Object object = gson.fromJson(result, callback.type);
                            callbackSuccess(callback, response, object);
                        } catch (JsonParseException e) {
                            callbackError(callback, response, e);
                        }
                    }
                } else {
                    callbackError(callback, response, null);
                }
            }
        });
    }

    private Request buildRequset(String url, Map<String, String> params, HttpMethodType httpMethodType) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.addHeader("cookie", UserManagerApplication.getInstance().getSessionId());

        if (httpMethodType == HttpMethodType.GET) {
            builder.get();
        } else if (httpMethodType == HttpMethodType.POST) {
            RequestBody requestBody = buildFromData(params);
            builder.post(requestBody);
        }
        return builder.build();
    }

    private RequestBody buildFromData(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    private void callbackSuccess(final BaseHttpCallback callback, final Response response, final Object object) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response, object);
            }
        });
    }

    private void callbackError(final BaseHttpCallback callback, final Response response, final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (response.code() == 401) {
                    callback.onTokenError(response, 401);
                    return;
                }
                ErrorMessage errorMessage = null;
                try {
                    String error = Objects.requireNonNull(response.body()).string();
                    JSONObject jsonObject = new JSONObject(error);
                    errorMessage = new ErrorMessage(jsonObject.getString("errorMessage"), jsonObject.getInt("errorCode"), jsonObject.getString("uri"), jsonObject.getString("timestamp"));
                } catch (JSONException | IOException | NetworkOnMainThreadException e) {
                    e.printStackTrace();
                }
                errorMessage = new ErrorMessage("请重新尝试", 1, "/sad", "2312");
                callback.onError(response, errorMessage, e);
            }
        });
    }

    enum HttpMethodType {
        GET,
        POST
    }

}
