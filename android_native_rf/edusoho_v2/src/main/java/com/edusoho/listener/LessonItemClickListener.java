package com.edusoho.listener;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.entity.CourseLessonType;
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
    private boolean mIsLearn;

    public LessonItemClickListener(
            ActionBarBaseActivity activity, String listJson, boolean isLearn)
    {
        mIsLearn = isLearn;
        mActivity = activity;
        this.mLessonListJson = listJson;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index,
                            long arg3) {
        final LessonItem lesson = (LessonItem) parent.getItemAtPosition(index);
        CourseLessonType courseLessonType = CourseLessonType.value(lesson.type);
        if (courseLessonType == CourseLessonType.EMPTY) {
            mActivity.longToast("客户端暂不支持此课时类型！");
            return;
        }
        if (!"published".equals(lesson.status)) {
            mActivity.longToast("课时尚未发布！请稍后浏览！");
            return;
        }
        if (lesson.free != LessonItem.FREE ) {
            if (mActivity.app.loginUser == null) {
                mActivity.longToast("请登录后学习！");
                LoginActivity.start(mActivity);
                return;
            }

            if (!mIsLearn) {
                mActivity.longToast("请加入学习！");
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
                startIntent.putExtra(Const.IS_LEARN, mIsLearn);
            }
        });
    }
}