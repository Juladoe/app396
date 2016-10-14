package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.entity.lesson.Lesson;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.bal.course.CourseResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by su on 2015/12/22.
 */
public class LessonProvider extends ModelProvider {

    public LessonProvider(Context context) {
        super(context);
    }

    public ProviderListener<LessonItem> getLesson(int lessonId) {
        School school = SchoolUtil.getDefaultSchool(mContext);
        Map<String, ?> tokenMap = ApiTokenUtil.getToken(mContext);
        String token = tokenMap.get("token").toString();

        RequestUrl requestUrl = new RequestUrl(school.host + String.format(Const.LESSON, lessonId));
        requestUrl.heads.put("Auth-Token", token);

        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LessonItem>(){});

        return requestOption.build();
    }

    public ProviderListener<LinkedHashMap> getLiveRoom(int lessonId) {
        School school = SchoolUtil.getDefaultSchool(mContext);
        Map<String, ?> tokenMap = ApiTokenUtil.getToken(mContext);
        String token = tokenMap.get("token").toString();

        RequestUrl requestUrl = new RequestUrl(
                "http://124.160.104.76:10007/live?roomNo=dc7c48429a3a67ad2571e96ae5bae0c0&role=organizer&token=or5800415b62fca&nickName=%E5%92%8C%E5%B9%B3%E7%BB%B4%E6%8A%A4%E8%80%85&uid=66666&k=1476412387bf77632cf9fb1b8a9dcf25&extrRole=support&flashDebug=1&debug=1");
        requestUrl.heads.put("Auth-Token", token);

        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }
}
