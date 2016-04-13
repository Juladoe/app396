package com.edusoho.kuozhi.v3.plugin.appview;

import android.content.Intent;
import android.os.Bundle;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.ThreadCreateActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;

/**
 * Created by 菊 on 2016/4/11.
 */
public class ThreadCreateAction {

    private BaseActivity mActivity;

    public ThreadCreateAction(BaseActivity activity)
    {
        this.mActivity = activity;
    }

    public void invoke(final Bundle bundle) {
        mActivity.app.mEngine.runNormalPlugin("ThreadCreateActivity", mActivity.getBaseContext(), new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ThreadCreateActivity.COURSE_ID, AppUtil.parseInt(bundle.getString("courseId")));
                startIntent.putExtra(ThreadCreateActivity.LESSON_ID, AppUtil.parseInt(bundle.getString("lessonId")));
                startIntent.putExtra(ThreadCreateActivity.TYPE, bundle.getString("type"));
            }
        });
    }
}
