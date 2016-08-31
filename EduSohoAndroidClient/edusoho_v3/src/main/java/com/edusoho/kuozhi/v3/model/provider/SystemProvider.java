package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.SchoolBanner;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.google.gson.reflect.TypeToken;

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

    public ProviderListener<LinkedHashMap> getIMChatConfig() {
        School school = SchoolUtil.getDefaultSchool(mContext);
        Map<String, ?> tokenMap = ApiTokenUtil.getToken(mContext);
        String token = tokenMap.get("token").toString();
        RequestUrl requestUrl = new RequestUrl(school.host + "/api/setting/app_im");
        requestUrl.heads.put("Auth-Token", token);

        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }
}
