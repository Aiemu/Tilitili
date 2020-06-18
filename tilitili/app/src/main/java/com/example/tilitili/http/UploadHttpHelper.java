package com.example.tilitili.http;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;

import com.example.tilitili.data.Contants;

import java.io.File;
import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadHttpHelper {
    private UploadHttpHelper instance;
    private OkHttpClient httpClient;
    private Handler handler;

    public UploadHttpHelper(final ProgressBar progressBar) {
        handler = new Handler(Looper.getMainLooper());
        final CountingRequestBody.Listener progressListener = new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long bytesRead, long contentLength) {
                if (bytesRead >= contentLength) {
                    if (progressBar != null)
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                } else {
                    if (contentLength > 0) {
                        final int progress = (int) (((double) bytesRead / contentLength) * 100);
                        if (progressBar != null)
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.VISIBLE);
                                    progressBar.setProgress(progress);
                                }
                            });
                        if (progress >= 100) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
            }
        };
        this.httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        if (originalRequest.body() == null) {
                            return chain.proceed(originalRequest);
                        }
                        Request progressRequest = originalRequest.newBuilder()
                                .method(originalRequest.method(),
                                        new CountingRequestBody(originalRequest.body(), progressListener))
                                .build();
                        return chain.proceed(progressRequest);

                    }
                })
                .build();
    }

    public Request buildRequest(File file, int type) {
        Uri uris = Uri.fromFile(file);
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uris.toString());
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        final String fileName = file.getName();

        Log.e("filepath", file.getName() + " " + mime + " " + file);
        assert mime != null;
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", String.valueOf(type))
                .addFormDataPart("file", fileName,
                        RequestBody.create(file, MediaType.parse(mime)))
                .build();
        Request request = new Request.Builder()
                .url(Contants.API.UPLOAD_URL)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();
        return request;
    }

    public void upload(Request request, Callback callback) {
        httpClient.newCall(request).enqueue(callback);
    }
}
