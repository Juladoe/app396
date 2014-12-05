package com.edusoho.listener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.fragment.AboutFragment;
import com.edusoho.kuozhi.util.Const;

/**
 *
 * @author howzhi
 */
public class CourseListScrollListener implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener
{
    private ActionBarBaseActivity mActivity;

    public CourseListScrollListener(ActionBarBaseActivity activity)
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
        Course course = (Course) parent.getItemAtPosition(index);
        Bundle bundle = new Bundle();
        bundle.putInt(Const.COURSE_ID, course.id);
        bundle.putString(Const.ACTIONBAT_TITLE, course.title);
        mActivity.app.mEngine.runNormalPluginWithBundle("CorusePaperActivity", mActivity, bundle);

        /*
        final Course course = (Course) parent.getItemAtPosition(index);
        mActivity.app.mEngine.runNormalPlugin(
                CourseDetailsActivity.TAG, mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(Const.COURSE_ID, course.id);
                startIntent.putExtra(Const.ACTIONBAT_TITLE, course.title);
                startIntent.putExtra(CourseDetailsActivity.COURSE_PIC, course.largePicture);
            }
        });
        */
    }
}