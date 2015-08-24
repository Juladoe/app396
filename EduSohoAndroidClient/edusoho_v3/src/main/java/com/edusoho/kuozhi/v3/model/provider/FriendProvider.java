package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.android.volley.Response;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.result.FriendResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by howzhi on 15/8/24.
 */
public class FriendProvider extends ModelProvider {

    public FriendProvider(Context context)
    {
        super(context);
    }

    public ProviderListener getSchoolApps(
            RequestUrl requestUrl, TypeToken typeToken) {

        ProviderListener responseListener = new ProviderListener(){};
        addRequest(requestUrl, typeToken, responseListener, null);
        return responseListener;
    }

    public ProviderListener getFriend(RequestUrl requestUrl, TypeToken typeToken) {
        ProviderListener responseListener = new ProviderListener(){};
        addRequest(requestUrl, typeToken, responseListener, null);
        return responseListener;
    }
}
