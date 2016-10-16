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
                "http://pl.youku.com/playlist/m3u8?vid=XMTc2MDM5MjY4OA==&type=mp4&ts=1476606155&keyframe=0&ep=dyaTHE%252BNUM8F5ybajT8bNSmzISYIXJZ3kkyH%252FKYfBcZ%252BIezA6DPcqJ%252B1TPY%253D&sid=547660615255512b5aea7&token=2529&ctype=12&ev=1&oip=1942202945");
        requestUrl.heads.put("Auth-Token", token);

        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<LinkedHashMap>(){});

        return requestOption.build();
    }
}
