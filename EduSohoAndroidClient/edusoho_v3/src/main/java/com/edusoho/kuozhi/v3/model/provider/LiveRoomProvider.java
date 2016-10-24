package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.model.bal.article.ArticleList;
import com.edusoho.kuozhi.v3.model.live.Signal;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by howzhi on 15/9/9.
 */
public class LiveRoomProvider extends ModelProvider {

    public LiveRoomProvider(Context context) {
        super(context);
    }

    public ProviderListener<LinkedHashMap> getLiveRoom(
            String roomnNo, String token, String role, String clientId) {

        StringBuilder stringBuilder = new StringBuilder(Const.LIVE_HOST);
        stringBuilder.append("/live/status/")
                .append(roomnNo)
                .append("?token=").append(token)
                .append("&role=").append(role)
                .append("&clientId=").append(clientId);
        RequestUrl requestUrl = new RequestUrl(stringBuilder.toString());
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }

    public ProviderListener<LinkedHashMap> getLiveServerTime() {

        StringBuilder stringBuilder = new StringBuilder(Const.LIVE_HOST);
        stringBuilder.append("/live/timestamp");
        RequestUrl requestUrl = new RequestUrl(stringBuilder.toString());
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }

    public ProviderListener<LinkedHashMap<String, Signal>> getLiveSignals(
            String roomNo, String token, String role, String clientId, long startTime, long endTime) {

        StringBuilder stringBuilder = new StringBuilder(Const.LIVE_HOST);
        stringBuilder.append("/signal")
                .append("?token=").append(token)
                .append("&roomNo=").append(roomNo)
                .append("&role=").append(role)
                .append("&endTime=").append(endTime)
                .append("&startTime=").append(startTime)
                .append("&clientId=").append(clientId);
        RequestUrl requestUrl = new RequestUrl(stringBuilder.toString());
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap<String, Signal>>(){});

        return requestOption.build();
    }

    public ProviderListener<LinkedHashMap> joinLiveChatRoom(
            String roomnNo, String token, String role, String clientId
    ) {
        StringBuilder stringBuilder = new StringBuilder(Const.LIVE_HOST);
        stringBuilder.append("/socket/join_token")
                .append("?roomNo=").append(roomnNo)
                .append("&token=").append(token)
                .append("&clientId=").append(clientId)
                .append("&role=").append(role);
        RequestUrl requestUrl = new RequestUrl(stringBuilder.toString());
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }
}
