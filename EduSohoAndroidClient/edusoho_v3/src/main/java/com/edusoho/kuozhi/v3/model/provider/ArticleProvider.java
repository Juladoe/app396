package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.model.bal.article.ArticleList;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by howzhi on 15/9/9.
 */
public class ArticleProvider extends ModelProvider {

    public ArticleProvider(Context context) {
        super(context);
    }

    public ProviderListener getMenus(RequestUrl requestUrl) {
        return addSimpleGetRequest(requestUrl, new TypeToken<List<LinkedHashMap>>(){});
    }

    public ProviderListener getArticles(RequestUrl requestUrl) {
        return addSimpleGetRequest(requestUrl, new TypeToken<ArticleList>(){});
    }
}
