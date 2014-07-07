package com.edusoho.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.ui.course.CourseInfoActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoListView;

/**
 *
 * @author howzhi
 */
public class CourseListScrollListener implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener
{
    private View mParent;
    private View course_more_btn;
    private EdusohoListView mListView;
    private Activity mContext;

    public CourseListScrollListener(Activity context, EdusohoListView listView)
    {
        mContext = context;
        mListView = listView;
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

        Intent intent = new Intent(mContext, CourseInfoActivity.class);
        intent.putExtra("courseId", course.id);
        intent.putExtra("largePicture", course.largePicture);
        intent.putExtra("courseTitle", course.title);
        mContext.startActivityForResult(intent, Const.COURSEINFO_REQUEST);
    }
}