package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;
import android.os.Bundle;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.bal.push.CourseDiscussEntity;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.sql.CourseDiscussDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;

/**
 * Created by 菊 on 2016/4/25.
 */
public class DiscussMsgCommand extends AbstractCommand {

    public DiscussMsgCommand(Context context, V2CustomContent v2CustomContent)
    {
        super(context, v2CustomContent);
    }

    @Override
    public void invoke() {
        NotificationUtil.showCourseDiscuss(mContext, mV2CustomContent);

        Bundle bundle = new Bundle();
        bundle.putInt(Const.ADD_DISCUSS_MSG_DESTINATION, NewsFragment.HANDLE_RECEIVE_COURSE_DISCUSS_MSG);
        bundle.putSerializable(Const.GET_PUSH_DATA, mV2CustomContent);
        MessageEngine.getInstance().sendMsgToTaget(Const.ADD_COURSE_DISCUSS_MSG, bundle, NewsFragment.class);

        CourseDiscussEntity courseDiscussEntity = new CourseDiscussEntity(mV2CustomContent);
        CourseDiscussDataSource courseDiscussDataSource = new CourseDiscussDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, EdusohoApp.app.domain));
        courseDiscussDataSource.create(courseDiscussEntity);
    }
}
