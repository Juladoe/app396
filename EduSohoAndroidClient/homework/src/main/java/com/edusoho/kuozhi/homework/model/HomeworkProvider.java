package com.edusoho.kuozhi.homework.model;

import android.content.Context;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.provider.ProviderListener;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 15/10/15.
 */
public class HomeworkProvider extends ModelProvider {

    public HomeworkProvider(Context context)
    {
        super(context);
    }

    public ProviderListener<HomeWorkModel> getHomeWork(RequestUrl requestUrl) {
        return buildSimpleGetRequest(requestUrl, new TypeToken<HomeWorkModel>(){}).build();
    }
}
