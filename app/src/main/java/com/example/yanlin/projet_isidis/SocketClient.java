package com.example.yanlin.projet_isidis;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by yanlin on 2018/1/24.
 */

public class SocketClient extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS =1000;
    private final static String LOCAL_TO_WS = "ws://vps507764.ovh.net:8080/projet_isidis/webSocketIMServer";

    private OkHttpClient okHttpClient;
    private Request request;
    private WebSocket ws;

    public SocketClient(){
        request = new Request.Builder().url(LOCAL_TO_WS).build();
        okHttpClient = new OkHttpClient();
        ws = okHttpClient.newWebSocket(request,this);
    }

    public void send(){
        okHttpClient.dispatcher().executorService().shutdown();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("#SuShiKan# hello socket");
        webSocket.close(NORMAL_CLOSURE_STATUS,"Goog Bye");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS,null);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
    }

}
