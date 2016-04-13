package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by su on 2015/12/22.
 */
public class CourseProvider extends ModelProvider {

    public CourseProvider(Context context) {
        super(context);
    }

    public ProviderListener createThread(int courseId, String type, String title,String content) {
        School school = SchoolUtil.getDefaultSchool(mContext);
        Map<String, ?> tokenMap = ApiTokenUtil.getToken(mContext);
        String token = tokenMap.get("token").toString();
        RequestUrl requestUrl = new RequestUrl(school.host + Const.CREATE_THREAD);
        requestUrl.heads.put("X-Auth-Token", token);
        requestUrl.setParams(new String[] {
                "courseId", String.valueOf(courseId),
                "threadType", "course",
                "type" , type,
                "title" , title,
                "content", content
        });

        RequestOption requestOption = buildSimplePostRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }

    public ProviderListener getCourse(RequestUrl requestUrl) {
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<CourseDetailsResult>(){});

        requestOption.getRequest().setCacheUseMode(BaseVolleyRequest.ALWAYS_USE_CACHE);
        return requestOption.build();
    }
}
