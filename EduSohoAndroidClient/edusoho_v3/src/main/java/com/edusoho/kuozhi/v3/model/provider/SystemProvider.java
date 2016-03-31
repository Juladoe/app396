package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.SchoolBanner;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

    public ProviderListener getImServerHosts(RequestUrl requestUrl) {
        final ProviderListener<ArrayList<String>> stringResponseListener = new ProviderListener<ArrayList<String>>(){};
        ProviderListener<LinkedHashMap> responseListener = new ProviderListener<LinkedHashMap>(){
        };
        addRequest(requestUrl, new TypeToken<LinkedHashMap>() {
        }, responseListener, responseListener);
        responseListener.success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap hashMap) {
                if (hashMap == null || !hashMap.containsKey("servers")) {
                    return;
                }
                ArrayList<String> hostList = (ArrayList<String>) hashMap.get("servers");
                stringResponseListener.onResponse(hostList);
            }
        });
        return stringResponseListener;
    }
}
