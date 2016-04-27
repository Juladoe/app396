package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;
import android.util.Log;

import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.SchoolBanner;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by howzhi on 15/9/24.
 */
public class SystemProvider extends ModelProvider {

    public SystemProvider(Context context) {
        super(context);
    }

    public ProviderListener<SchoolApp> getSchoolApp(RequestUrl requestUrl) {
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<SchoolApp>(){});

        requestOption.getRequest().setCacheUseMode(BaseVolleyRequest.ALWAYS_USE_CACHE);
        return requestOption.build();
    }

    public ProviderListener getSchoolBanners(RequestUrl requestUrl) {
        ProviderListener<List<SchoolBanner>> responseListener = new ProviderListener<List<SchoolBanner>>() {
        };
        addRequest(requestUrl, new TypeToken<List<SchoolBanner>>() {
        }, responseListener, responseListener);
        return responseListener;
    }

    public ProviderListener getImServerHosts() {
        Map<String,String> tokenMap = ApiTokenUtil.getToken(mContext);
        String token = tokenMap.get("token");
        School school = SchoolUtil.getDefaultSchool(mContext);
        RequestUrl requestUrl = new RequestUrl(school.host + "/api/im/me/login");
        requestUrl.getHeads().put("Auth-Token", token);

        final ProviderListener<LinkedHashMap> stringResponseListener = new ProviderListener<LinkedHashMap>(){};
        ProviderListener<LinkedHashMap> responseListener = new ProviderListener<LinkedHashMap>(){
        };
        addPostRequest(requestUrl, new TypeToken<LinkedHashMap>() {
        }, responseListener, responseListener);
        responseListener.success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap hashMap) {
                if (hashMap == null || !hashMap.containsKey("servers")) {
                    return;
                }
                LinkedHashMap hostList = (LinkedHashMap) hashMap.get("servers");
                stringResponseListener.onResponse(hostList);
            }
        });
        return stringResponseListener;
    }
}
