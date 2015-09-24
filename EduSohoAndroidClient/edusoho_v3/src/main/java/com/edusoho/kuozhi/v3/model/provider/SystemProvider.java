package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.google.gson.reflect.TypeToken;

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
}
