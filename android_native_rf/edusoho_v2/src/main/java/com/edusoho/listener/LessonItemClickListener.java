package com.edusoho.listener;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.course.LessonActivity;
import com.edusoho.kuozhi.util.Const;

/**
 *
 * @author howzhi
 */
public class LessonItemClickListener implements AdapterView.OnItemClickListener
{
    private ActionBarBaseActivity mActivity;
    private String mLessonListJson;

    public LessonItemClickListener(ActionBarBaseActivity activity, String listJson)
    {
        mActivity = activity;
        this.mLessonListJson = listJson;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index,
                            long arg3) {
        final LessonItem lesson = (LessonItem) parent.getItemAtPosition(index);

        if (lesson.free != LessonItem.FREE && mActivity.app.loginUser == null) {
            if (mActivity.app.loginUser == null) {
                mActivity.longToast("请登录后学习！");
                LoginActivity.start(mActivity);
                return;
            }
        }
        mActivity.getCoreEngine().runNormalPlugin(
                LessonActivity.TAG, mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(Const.COURSE_ID, lesson.courseId);
                startIntent.putExtra(Const.FREE, lesson.free);
                startIntent.putExtra(Const.LESSON_ID, lesson.id);
                startIntent.putExtra(Const.LESSON_TYPE, lesson.type);
                startIntent.putExtra(Const.ACTIONBAT_TITLE, lesson.title);
                startIntent.putExtra(Const.LIST_JSON, mLessonListJson);
            }
        });
    }
}