package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.model.bal.article.ArticleList;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.google.gson.reflect.TypeToken;

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
            String host, String roomnNo, String token, String role, String clientId) {

        StringBuilder stringBuilder = new StringBuilder(host);
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

    public ProviderListener<LinkedHashMap> joinLiveChatRoom(
            String host, String roomnNo, String token, String role, String clientId
    ) {
        StringBuilder stringBuilder = new StringBuilder(host);
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
