package com.edusoho.kuozhi.v3.plugin.appview;

import android.content.Intent;
import android.os.Bundle;

import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.ThreadDiscussActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;

/**
 * Created by 菊 on 2016/4/11.
 */
public class ThreadDiscussAction {

    private BaseActivity mActivity;

    public ThreadDiscussAction(BaseActivity activity)
    {
        this.mActivity = activity;
    }

    public void invoke(final Bundle bundle) {
        mActivity.app.mEngine.runNormalPlugin("ThreadDiscussActivity", mActivity.getBaseContext(), new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ThreadDiscussActivity.COURSE_ID, AppUtil.parseInt(bundle.getString("courseId")));
                startIntent.putExtra(ThreadDiscussActivity.LESSON_ID, AppUtil.parseInt(bundle.getString("lessonId")));
                startIntent.putExtra(ThreadDiscussActivity.THREAD_ID, AppUtil.parseInt(bundle.getString("threadId")));
                startIntent.putExtra(ThreadDiscussActivity.ACTIVITY_TYPE, PushUtil.ThreadMsgType.THREAD_POST);
            }
        });
    }
}
