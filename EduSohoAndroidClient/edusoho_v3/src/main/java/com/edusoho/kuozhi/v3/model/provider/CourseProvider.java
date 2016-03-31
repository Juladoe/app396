package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by su on 2015/12/22.
 */
public class CourseProvider extends ModelProvider {

    public CourseProvider(Context context) {
        super(context);
    }

    public ProviderListener getCourse(RequestUrl requestUrl) {
        RequestOption requestOption = buildSimplePostRequest(
                requestUrl, new TypeToken<CourseDetailsResult>(){});

        requestOption.getRequest().setCacheUseMode(BaseVolleyRequest.ALWAYS_USE_CACHE);
        return requestOption.build();
    }
}
