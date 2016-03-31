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

import java.util.List;

import cn.trinea.android.common.util.DigestUtils;


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

    private List<String> mHostList;
    private int mCurrentHostIndex;
    private String mToken;
    private String mConvNo;
    private String mClientName;
    private String mClientId;
    private PingManager mPingManager;
    private Future<WebSocket> mWebSocketFuture;
    private Receiver mReceiver;
    private AsyncHttpClient.WebSocketConnectCallback mWebSocketConnectCallback;

    private PingManager.PingCallback mPingCallback = new PingManager.PingCallback() {
        @Override
        public void onPing() {
            Log.d(TAG, "onPing");
            ping();
        }
    };

    public ImServer() {
        this.mPingManager = new PingManager();
        this.mPingManager.setPingCallback(mPingCallback);
        //this.mConvNo = "b6565ecacef7fd0f3ea1fab66e7b3a49";
    }

    public List<String> getHost() {
        return mHostList;
    }

    public void setReceiver(Receiver receiver) {
        this.mReceiver = receiver;
    }

    public void clear() {
        mPingManager.stop();
    }

    public void initWithHost(List<String> host) {
        this.mHostList = host;
        this.mCurrentHostIndex = 0;
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

    public void onReceiveMessage(String msg) {
        if (mReceiver == null) {
            return;
        }
        mReceiver.onReceive(msg);
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
                mPingManager.start();
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

    private void createJoinToken(String userId, String clientId) {
        int time = (int) System.currentTimeMillis();

        String token = String.format("%s:%s:%s:%d:%s:%s", userId, clientId, clientId, time, clientId);
    }

    private void getJoinToken() {
    }

    public void joinConversation(String clientId, String nickname, final String convNo) {
        this.mClientName = nickname;
        this.mConvNo = convNo;

        String url = String.format("http://im-rpc.han.dev.qiqiuyun.cn:8081/tmp.php?act=getJoinToken&clientId=%s&no=%s", clientId, convNo);
        AsyncHttpClient.getDefaultInstance().executeString(new AsyncHttpGet(url), new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, String s) {
                mToken = s.substring(1, s.length() - 1);

                send(new String[] {
                        "cmd", "add",
                        "convNo", convNo,
                        "token", mToken
                });
            }
        });
    }

    private void loginImServer() {
        if (mCurrentHostIndex > mHostList.size()) {
            return;
        }
        String host = mHostList.get(mCurrentHostIndex++);
        connectWebsocket(host);
    }

    private void connectWebsocket(String host) {
        Log.d(TAG, host);
        mWebSocketFuture =  AsyncHttpClient.getDefaultInstance().websocket(
                host  + "&clientName=" + mClientName,
                null,
                getWebSocketConnectCallback()
        );
    }

    public Future<WebSocket> getSocket() {
        return mWebSocketFuture;
    }

    public static interface Receiver
    {
        public void onReceive(String msg);
    }
}
