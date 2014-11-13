package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.LearnLessonListAdapter;
import com.edusoho.kuozhi.adapter.lesson.LessonEmptyAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.LearnStatus;
import com.edusoho.kuozhi.entity.LessonsResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.LessonItemClickListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import views.PinnedSectionListView;

/**
 * Created by howzhi on 14-8-27.
 */
public class CourseDetailsLessonWidget extends CourseDetailsLabelWidget {

    private ActionBarBaseActivity mActivity;
    protected PinnedSectionListView mContentView;
    private boolean isInitHeight;
    private int mCourseId;
    private AQuery mAQuery;
    private boolean isLearn;
    private boolean mIsAddToken;
    private String mLessonListJson;
    private HashMap<Integer, LearnStatus> learnStatuses;
    private LearnLessonListAdapter mAdapter;
    private String[] mEmptyText = new String[]{ "课程暂无课时内容" };;

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
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CourseDetailsLabelWidget);
        isInitHeight = ta.getBoolean(R.styleable.CourseDetailsLabelWidget_isInitHeight, false);

        mContentView = new PinnedSectionListView(mContext, null);
        mContentView.setDivider(null);
        mContentView.setSelector(getResources().getDrawable(R.drawable.normal_list_select));
        mContentView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

    @Override
    public void onShow() {
        loadLessons();
    }

    private void loadLessons()
    {
        RequestUrl url = mActivity.app.bindUrl(Const.LESSONS, mIsAddToken);
        url.setParams(new String[]{
                "courseId", mCourseId + ""
        });
         mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLoadView.setVisibility(View.GONE);
                mLessonListJson = object;
                parseDataToView();
            }
        });
    }

    private void parseDataToView()
    {
        LessonsResult result = mActivity.parseJsonValue(
                mLessonListJson, new TypeToken<LessonsResult>(){});
        if (result == null) {
            return;
        }

        learnStatuses = result.learnStatuses;
        setAdapter(result);
        if (isInitHeight) {
            initListHeight(mContentView);
        }
    }

    public void setIsLearn(boolean isLearn) {
        this.isLearn = isLearn;
    }

    public LearnStatus getLearnStatus(int lessonId)
    {
        return learnStatuses.get(lessonId);
    }

    public void updateLessonStatus(HashMap<Integer, LearnStatus> newStatus)
    {
        learnStatuses = newStatus;
        mAdapter.updateLearnStatusList(learnStatuses);
    }

    public void updateLessonStatus(int index, LearnStatus status)
    {
        learnStatuses.put(index, status);
        mAdapter.updateLearnStatusList(learnStatuses);
    }

    public String getLessonListJson()
    {
        return mLessonListJson;
    }

    protected void setAdapter(LessonsResult result)
    {
        if (result.lessons.isEmpty()) {
            mContentView.setAdapter(getEmptyLayoutAdapter());
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContentView.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            mContentView.setLayoutParams(layoutParams);
            return;
        }
        mAdapter = new LearnLessonListAdapter(
                mContext, result, R.layout.course_details_learning_lesson_item);
        mContentView.setAdapter(mAdapter);
        mContentView.setOnItemClickListener(new LessonItemClickListener(mActivity, mLessonListJson, isLearn));
    }

    public void setItemClickListener(AdapterView.OnItemClickListener itemClickListener)
    {
        mContentView.setOnItemClickListener(itemClickListener);
    }

    public void initLessonFromJson(
            ActionBarBaseActivity activity, String json)
    {
        mActivity = activity;
        mLoadView.setVisibility(View.GONE);
        mLessonListJson = json;
        parseDataToView();
    }

    public void initLesson(
            int courseId, ActionBarBaseActivity activity, boolean isAddToken)
    {
        mCourseId = courseId;
        mActivity = activity;
        this.mIsAddToken = isAddToken;
    }

    protected ListAdapter getEmptyLayoutAdapter()
    {
        LessonEmptyAdapter arrayAdapter = new LessonEmptyAdapter(
                mContext, mEmptyText, R.layout.course_empty_layout);
        return arrayAdapter;
    }
}
