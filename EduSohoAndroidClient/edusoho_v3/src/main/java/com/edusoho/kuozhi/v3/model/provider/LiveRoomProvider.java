package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;
import com.edusoho.kuozhi.v3.model.live.Signal;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedHashMap;

/**
 * Created by howzhi on 15/9/9.
 */
public class LiveRoomProvider extends ModelProvider {

    public LiveRoomProvider(Context context) {
        super(context);
    }

    /*
        liveHost contain http://
     */
    public ProviderListener<LinkedHashMap> getLiveRoom(String liveHost, String token, String roomNo) {
        RequestUrl requestUrl = new RequestUrl(String.format("%s/rooms/%s/status", liveHost, roomNo));
        requestUrl.setHeads(new String[] {
                "Auth-Token", token
        });
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }

    public ProviderListener<LinkedHashMap> getLasterLiveNotice(String liveHost, String token, String roomNo) {
        RequestUrl requestUrl = new RequestUrl(String.format("%s/rooms/%s/public_notices", liveHost, roomNo));
        requestUrl.setHeads(new String[] {
                "Auth-Token", token
        });
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }

    public ProviderListener<LinkedHashMap> getLiveNoticeList(String liveHost, String token, String roomNo) {
        RequestUrl requestUrl = new RequestUrl(String.format("%s/rooms/%s/public_notices_history", liveHost, roomNo));
        requestUrl.setHeads(new String[] {
                "Auth-Token", token
        });
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }

    public ProviderListener<LinkedHashMap> getLiveServerTime(String liveHost, String token) {
        RequestUrl requestUrl = new RequestUrl(String.format("%s/live/timestamp", liveHost));
        requestUrl.setHeads(new String[] {
                "Auth-Token", token
        });
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }

    public ProviderListener<LinkedHashMap<String, Signal>> getLiveSignals(
            String liveHost, String token, long startTime, long endTime) {
        RequestUrl requestUrl = new RequestUrl(String.format("%s/signal", liveHost));
        requestUrl.setHeads(new String[] {
                "Auth-Token", token
        });
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap<String, Signal>>(){});

        return requestOption.build();
    }

    /*
        roomNo      | string   | 是     | 教室NO               |
        | token           | string   | 是     | 用户token           |
        | role  | string   | 是     | 用户角色          |
        | clientId | string   | 是     | 用户ID
     */
    public ProviderListener<LinkedHashMap> getLiveChatServer(
            String liveHost, String roomNo, String token) {
        RequestUrl requestUrl = new RequestUrl(String.format("%s/rooms/%s/socket_token", liveHost, roomNo));

        requestUrl.getHeads().put("Auth-Token", token);
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }

    public ProviderListener<LinkedHashMap> joinLiveChatRoom(
            String liveHost, String token, String roomNo) {

        RequestUrl requestUrl = new RequestUrl(String.format("%s/rooms/%s/socket_token", liveHost, roomNo));
        requestUrl.setHeads(new String[] {
                "Auth-Token", token
        });
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }
}
