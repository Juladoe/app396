package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.LessonListAdapter;
import com.edusoho.kuozhi.adapter.ReviewListAdapter;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.Review;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.hb.views.PinnedSectionListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-8-27.
 */
public class CourseDetailsLessonWidget extends CourseDetailsLabelWidget {

    private PinnedSectionListView mContentView;
    private AQuery mAQuery;

    public CourseDetailsLessonWidget(Context context) {
        super(context);
    }

    public CourseDetailsLessonWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isMoveToTop(int top)
    {
        return super.isMoveToTop(top);
    }

    @Override
    protected void initView(AttributeSet attrs) {
        super.initView(attrs);
        mContentView = new PinnedSectionListView(mContext, attrs);
        mContentView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContentView.setPadding(0, 0, 0, 0);

        mAQuery = new AQuery(mContentView);
        setLoadView();
        setContentView(mContentView);
    }

    /**
     * #mark_update
     */
    public void initListHeight(ListView listView)
    {
        int totalHeight = 0;
        ListAdapter adapter = listView.getAdapter();
        int count = adapter.getCount();
        for (int i=0; i < count; i++) {
            View child = adapter.getView(i, null, this);
            child.measure(0, 0);
            totalHeight += child.getMeasuredHeight() + listView.getDividerHeight();
        }

        ViewGroup.LayoutParams lp = listView.getLayoutParams();
        lp.height = totalHeight;
        listView.setLayoutParams(lp);
    }

    public void initLesson(String courseId, final ActionBarBaseActivity mActivity)
    {
        String url = mActivity.app.bindUrl(Const.LESSONS);
        HashMap<String, String> params = mActivity.app.createParams(true, null);
        params.put("courseId", courseId);

        mActivity.ajaxPost(url, params, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLoadView.setVisibility(View.GONE);
                ArrayList<LessonItem> lessonItems = mActivity.parseJsonValue(
                        object, new TypeToken<ArrayList<LessonItem>>(){});

                if (lessonItems == null) {
                    return;
                }

                LessonListAdapter adapter = new LessonListAdapter(
                        mContext, lessonItems, null, R.layout.course_details_lesson_item);
                mContentView.setAdapter(adapter);
                initListHeight(mContentView);
            }
        });
    }
}
