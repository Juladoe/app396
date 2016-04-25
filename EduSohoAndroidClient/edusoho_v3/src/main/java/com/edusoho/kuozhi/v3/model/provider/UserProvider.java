package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.cache.request.model.Request;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 菊 on 2016/4/23.
 */
public class UserProvider extends ModelProvider {

    public UserProvider(Context context)
    {
        super(context);
    }

    public ProviderListener createConvNo(int toUserId) {
        Map<String,String> tokenMap = ApiTokenUtil.getToken(mContext);
        String token = tokenMap.get("token");

        School school = SchoolUtil.getDefaultSchool(mContext);

        RequestUrl requestUrl = new RequestUrl(school.host + "/api/im/me/conversations/" + toUserId);
        requestUrl.getHeads().put("Auth-Token", token);
        RequestOption requestOption = buildSimplePostRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        requestOption.getRequest().setCacheUseMode(BaseVolleyRequest.ALWAYS_USE_CACHE);
        return requestOption.build();
    }
}
