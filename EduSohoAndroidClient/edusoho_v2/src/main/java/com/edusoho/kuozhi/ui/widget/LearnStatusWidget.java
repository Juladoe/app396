package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.LasterLearnStatus;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.course.LessonActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14-8-14.
 */
public class LearnStatusWidget extends FrameLayout {

    private Context mContext;
    private View mLoadView;
    private ActionBarBaseActivity mActivity;
    private View mContainer;

    private ImageView mCoursePicView;
    private TextView mLasterCourse;
    private TextView mLasterProgressNumber;
    private ProgressBar mLasterProgress;
    private View mStartLearnBtn;

    public LearnStatusWidget(Context context) {
        super(context);
        mContext = context;
    }

    public LearnStatusWidget(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private void initView(android.util.AttributeSet attrs)
    {
        mLoadView = initLoadView();
        addView(mLoadView);

        mContainer = initLoadContainer();
        addView(mContainer);

        mStartLearnBtn = mContainer.findViewById(R.id.learn_status_widget_learnBtn);
        mCoursePicView = (ImageView) mContainer.findViewById(R.id.learn_status_widget_coursepic);
        mLasterCourse = (TextView) mContainer.findViewById(R.id.learn_status_widget_lastLearnCourse);
        mLasterProgressNumber = (TextView) mContainer.findViewById(R.id.learn_status_widget_learnProgressNum);
        mLasterProgress = (ProgressBar) mContainer.findViewById(R.id.learn_status_widget_learnProgress);
    }

    private View initLoadContainer()
    {
        return LayoutInflater.from(mContext).inflate(R.layout.learn_status_widget_layout, null);
    }

    private View initLoadView()
    {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout, null);
        loadView.findViewById(R.id.load_text).setVisibility(View.GONE);
        return loadView;
    }

    public void initialise(
            ActionBarBaseActivity activity)
    {
        mActivity = activity;
        RequestUrl requestUrl = mActivity.app.bindUrl(Const.LASTER_LEARN_COURSE, true);
        Log.d(null, "LearnStatusWidget->initialise");
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                LasterLearnStatus<Course> lasterLearnStatus = mActivity.parseJsonValue(
                        object, new TypeToken<LasterLearnStatus<Course>>(){});
                if (lasterLearnStatus == null) {
                    setVisibility(View.GONE);
                    return;
                }

                mLoadView.setVisibility(View.GONE);
                if (getVisibility() == GONE) {
                    setVisibility(View.VISIBLE);
                }

                final Course course = lasterLearnStatus.data;
                LasterLearnStatus.Progress progress = lasterLearnStatus.progress;
                initWidgetView(course, progress);
            }
        });
    }

    public void initialiseByCourse(
            ActionBarBaseActivity activity,
            final Course course,
            int courseId,
            final String lessonJson
    )
    {
        mActivity = activity;
        RequestUrl requestUrl = mActivity.app.bindUrl(Const.LASTER_LEARN_LESSON, true);
        requestUrl.setParams(new String[] {
                "courseId", courseId + ""
        });
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                LasterLearnStatus<LessonItem<String>> lasterLearnStatus = mActivity.parseJsonValue(
                        object, new TypeToken<LasterLearnStatus<LessonItem<String>>>(){});
                if (lasterLearnStatus == null || lasterLearnStatus.data == null) {
                    setVisibility(GONE);
                    return;
                }

                mLoadView.setVisibility(View.GONE);
                //setVisibility(View.VISIBLE);

                LasterLearnStatus.Progress progress = lasterLearnStatus.progress;
                initWidgetView(course, lasterLearnStatus.data, progress, lessonJson);
            }
        });
    }

    private void initWidgetView(final Course course, LasterLearnStatus.Progress progress)
    {
        AQuery aQuery = new AQuery(mContainer);
        aQuery.id(R.id.learn_status_widget_coursepic).image(
                course.largePicture, false, true, 0, R.drawable.noram_course
        );
        mLasterCourse.setText("上次您学习到课程:" + course.title);
        mLasterProgressNumber.setText("课程学习进度:" + progress.percent);

        mLasterProgress.setMax(progress.total);
        mLasterProgress.setProgress(progress.number);

        mStartLearnBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(Const.COURSE_ID, course.id);
                bundle.putString(Const.ACTIONBAR_TITLE, course.title);
                mActivity.app.mEngine.runNormalPluginWithBundle("CorusePaperActivity", mActivity, bundle);
            }
        });
    }

    private void initWidgetView(
            final Course course,
            final LessonItem<String> lesson,
            LasterLearnStatus.Progress progress,
            final String lessonJson
    )
    {
        AQuery aQuery = new AQuery(mContainer);
        aQuery.id(R.id.learn_status_widget_coursepic).image(
                course.largePicture, false, true, 0, R.drawable.noram_course
        );

        mLasterCourse.setText("最近您学习到课时:" + lesson.title);
        mLasterProgressNumber.setText("课程学习进度:" + progress.percent);

        mLasterProgress.setMax(progress.total);
        mLasterProgress.setProgress(progress.number);

        mStartLearnBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.getCoreEngine().runNormalPlugin(
                        LessonActivity.TAG, mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.COURSE_ID, lesson.courseId);
                        startIntent.putExtra(Const.FREE, lesson.free);
                        startIntent.putExtra(Const.LESSON_ID, lesson.id);
                        startIntent.putExtra(Const.LESSON_TYPE, lesson.type);
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, lesson.title);
                        startIntent.putExtra(Const.LIST_JSON, lessonJson);
                    }
                });
            }
        });
    }
}
