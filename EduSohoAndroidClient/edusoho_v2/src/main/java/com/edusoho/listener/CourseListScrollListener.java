package com.edusoho.listener;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.edusoho.kuozhi.model.ClassRoom;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
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

        if (course instanceof ClassRoom) {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.ID, course.id);
            bundle.putString(Const.ACTIONBAR_TITLE, course.title);
            mActivity.app.mEngine.runNormalPluginWithBundle("ClassRoomPaperActivity", mActivity, bundle);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Const.COURSE_ID, course.id);
        bundle.putString(Const.ACTIONBAR_TITLE, course.title);
        mActivity.app.mEngine.runNormalPluginWithBundle("CoursePaperActivity", mActivity, bundle);
    }
}