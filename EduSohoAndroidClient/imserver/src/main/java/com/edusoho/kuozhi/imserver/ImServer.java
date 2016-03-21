package com.edusoho.kuozhi.imserver;

import android.util.Log;
import com.edusoho.kuozhi.imserver.command.CommandFactory;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.WritableCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by su on 2016/3/17.
 */
public class ImServer {

    private static final String TAG = "ImServer";

    private String[] pingCmd = {
            "cmd" , "ping"
    };

    private String[] addCmd = {
            "cmd" , "ping",
            "toId"
    };

    private String mHost;
    private String mToken;
    private String mConvNo;
    private String mClientId;
    private PingManager mPingManager;
    private Future<WebSocket> mWebSocketFuture;
    private AsyncHttpClient.WebSocketConnectCallback mWebSocketConnectCallback;

    private PingManager.PingCallback mPingCallback = new PingManager.PingCallback() {
        @Override
        public void onPing() {
            ping();
        }
    };

    public ImServer() {
        this.mPingManager = new PingManager();
        this.mPingManager.setPingCallback(mPingCallback);
        this.mConvNo = "b6565ecacef7fd0f3ea1fab66e7b3a49";
    }

    public void initWithHost(String host) {
        this.mHost = host;
        loginImServer();
    }

    public PingManager getPingManager() {
        return mPingManager;
    }

    public void sendMessage(String msg) {
        send(new String[] {
                "cmd" , "send",
                "toId" , "all",
                "convNo" , mConvNo,
                "msg", msg
        });
    }

    public void ping() {
        send(pingCmd);
    }

    private void send(String[] params) {
        try {
            JSONObject msgObj = new JSONObject();
            for (int i = 0; i < params.length; i = i + 2) {
                msgObj.put(params[i], params[i + 1]);
            }
            mWebSocketFuture.tryGet().send(msgObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private AsyncHttpClient.WebSocketConnectCallback getWebSocketConnectCallback() {
        return new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                }
                Log.d(TAG, "onCompleted:" + webSocket);
                mPingManager.start();
                webSocket.setEndCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception e) {
                        e.printStackTrace();
                    }
                });
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        Log.d(TAG, "re:" + s);
                        handleCmd(s);
                    }
                });
                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception e) {
                        Log.d(TAG, "close");
                    }
                });

                webSocket.setWriteableCallback(new WritableCallback() {
                    @Override
                    public void onWriteable() {
                        Log.d(TAG, "onWriteable");
                    }
                });
                webSocket.setPongCallback(new WebSocket.PongCallback() {
                    @Override
                    public void onPongReceived(String s) {
                        Log.d(TAG, "pone " + s);
                    }
                });
                getJoinToken();
            }
        };
    }

    private void handleCmd(String cmdStr) {
        try {
            JSONObject jsonObject = new JSONObject(cmdStr);
            String cmd = jsonObject.optString("cmd");
            CommandFactory.getInstance().create(this, cmd).invoke(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJoinToken() {
        String url = String.format("http://im-rpc.han.dev.qiqiuyun.cn:8081/tmp.php?act=getJoinToken&clientId=%s&no=%s", mClientId, mConvNo);
        AsyncHttpClient.getDefaultInstance().executeString(new AsyncHttpGet(url), new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, String s) {
                mToken = s.substring(1, s.length() - 1);
                Log.d(TAG, s);
                addConversation();
            }
        });
    }

    private void addConversation() {
        send(new String[] {
                "cmd", "add",
                "convNo", mConvNo,
                "token", mToken
        });
    }

    private void parseClientId(String serverUrl) {
        int firstTokenIndex = serverUrl.indexOf("token=");
        if (firstTokenIndex > 0) {
            String token = serverUrl.substring(firstTokenIndex);
            String[] splits = token.split(":");
            if (splits != null && splits.length > 2) {
                mClientId = splits[1];
            }
        }
    }

    private void loginImServer() {

        String url = String.format("http://trymob3.edusoho.cn/api/me/im/login");
        AsyncHttpRequest request = new AsyncHttpGet(url);
        request.addHeader("Auth-Token", "amgt5sd48r48oog08cks88oosw04k4g");
        AsyncHttpClient.getDefaultInstance().executeJSONObject(request, new AsyncHttpClient.JSONObjectCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, JSONObject jsonObject) {
                try {
                    JSONArray serviceArray = jsonObject.getJSONArray("servers");
                    mHost = serviceArray.getString(0);
                    parseClientId(mHost);
                    connectWebsocket();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void connectWebsocket() {
        Log.d(TAG, mHost);
        mWebSocketFuture =  AsyncHttpClient.getDefaultInstance().websocket(
                mHost  + "&clientName=suju3",
                null,
                getWebSocketConnectCallback()
        );
    }

    public Future<WebSocket> getSocket() {
        return mWebSocketFuture;
    }
}
