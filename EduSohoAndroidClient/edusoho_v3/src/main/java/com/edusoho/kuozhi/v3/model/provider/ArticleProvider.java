package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.bal.article.MenuItem;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by howzhi on 15/9/9.
 */
public class ArticleProvider extends ModelProvider {

    public ArticleProvider(Context context) {
        super(context);
    }

    public ProviderListener getMenus(RequestUrl requestUrl) {
        ProviderListener<List<MenuItem>> responseListener = new ProviderListener<List<MenuItem>>() {
        };
        addRequest(requestUrl, new TypeToken<List<MenuItem>>() {
        }, responseListener, responseListener);
        return responseListener;
    }
}
