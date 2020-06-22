package com.example.tilitili.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketHelper {
    private static OkHttpClient sClient;
    private static WebSocket sWebSocket;

    public static WebSocket startRequest(String url, WebSocketListener socketListener) {
        if (sClient == null) {
            sClient = new OkHttpClient();
        }
        if (sWebSocket == null) {
            Request request = new Request.Builder().url(url).build();
            sWebSocket = sClient.newWebSocket(request, socketListener);
        }
        return sWebSocket;
    }

}
