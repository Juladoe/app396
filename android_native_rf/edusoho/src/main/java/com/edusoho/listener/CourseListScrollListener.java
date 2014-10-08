package com.edusoho.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.ui.course.CourseInfoActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoListView;

/**
 *
 * @author howzhi
 */
public class CourseListScrollListener implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener
{
    private Activity mActivity;

    public CourseListScrollListener(Activity activity)
    {
        mActivity = activity;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index,
                            long arg3) {

        Object object = parent.getItemAtPosition(index);
        if (object instanceof Course) {
            final Course course = (Course) object;
            EdusohoApp.app.mEngine.runNormalPluginForResult(
                    "CourseInfoActivity", mActivity, Const.COURSEINFO_REQUEST, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra("courseId", course.id);
                    startIntent.putExtra("largePicture", course.largePicture);
                    startIntent.putExtra("courseTitle", course.title);
                }
            });
        }
    }
}