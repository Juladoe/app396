package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

/**
 * Created by DF on 2017/2/9.
 */

public class CourseDiscussProvider extends ModelProvider {

    public CourseDiscussProvider(Context context) {
        super(context);
    }

    public ProviderListener<DiscussDetail> getCourseDiscuss(boolean isCourse, int mCourseId) {
        RequestUrl requestUrl = EdusohoApp.app.bindNewUrl(String.format(isCourse ? Const.LESSON_DISCUSS : Const.CLASS_DISCUSS, mCourseId, mCourseId,0), true);
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<DiscussDetail>(){});
        return requestOption.build();
    }
}
