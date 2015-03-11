package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.LiveCourseLessonAdapter;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.LearnStatus;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.LessonsResult;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.course.CorusePaperActivity;
import com.edusoho.kuozhi.ui.fragment.course.ViewPagerBaseFragment;
import com.edusoho.kuozhi.ui.liveCourse.liveLessonFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Melomelon on 2015/2/2.
 */
public class LiveCourseLessonsFragment extends ViewPagerBaseFragment {

    private EduSohoListView mListView;
    private int mCourseId;
    private LiveCourseLessonAdapter mAdapter;

    private ArrayList<LessonItem> mLessons;
    private boolean mIsLoadLesson;

    @Override
    public EduSohoListView getListView() {
        return mListView;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_lesson_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mListView = (EduSohoListView) view.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(linearLayoutManager);
        mAdapter = new LiveCourseLessonAdapter(
                mActivity, R.layout.live_course_item);

        mListView.setEmptyString(new String[]{"暂无课时"}, R.drawable.icon_course_empty);
        mListView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCourseId = bundle.getInt(Const.COURSE_ID, 0);
        }
        loadLessons(true);
    }


    private void updateLessonStatus() {
        RequestUrl url = mActivity.app.bindUrl(Const.LEARN_STATUS, true);
        url.setParams(new String[]{
                Const.COURSE_ID, String.valueOf(mCourseId)
        });

        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                HashMap<Integer, LearnStatus> learnStatusHashMap = mActivity.parseJsonValue(
                        object, new TypeToken<HashMap<Integer, LearnStatus>>() {
                        }
                );
                if (learnStatusHashMap == null) {
                    return;
                }

                mAdapter.updateLearnStatus(learnStatusHashMap);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsLoadLesson) {
            return;
        }

        updateLessonStatus();
    }

    private void loadLessons(boolean mIsAddToken) {
        mListView.setLoadAdapter();
        mIsLoadLesson = true;
        RequestUrl url = mActivity.app.bindUrl(Const.LESSONS, mIsAddToken);
        url.setParams(new String[]{
                Const.COURSE_ID, String.valueOf(mCourseId)
        });

        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mIsLoadLesson = false;
                final LessonsResult lessonsResult = mActivity.parseJsonValue(
                        object, new TypeToken<LessonsResult>() {
                        });
                if (lessonsResult == null) {
                    return;
                }

                mLessons = lessonsResult.lessons;

                DateFormat fullFormat = new SimpleDateFormat("HH:mm:ss");
                DateFormat simpleFormat = new SimpleDateFormat("mm:ss");
                DateFormat format = simpleFormat;
                mAdapter.updateLearnStatus(lessonsResult.learnStatuses);
                mListView.pushData(lessonsResult.lessons);

                mAdapter.setOnItemClick(new RecyclerViewListBaseAdapter.RecyclerItemClick() {
                    @Override
                    public void onItemClick(Object obj, int position) {
                        showLesson((LessonItem) obj);
                    }
                });
            }
        });
    }


    private void showLesson(final LessonItem lessonItem) {
        CorusePaperActivity activity = (CorusePaperActivity) getActivity();
        final CourseDetailsResult courseDetailsResult = activity.getCourseResult();

        CourseLessonType courseLessonType = CourseLessonType.value(lessonItem.type);
        if (courseLessonType == CourseLessonType.CHAPTER
                || courseLessonType == CourseLessonType.UNIT) {
            return;
        }

        if (courseLessonType == CourseLessonType.EMPTY) {
            mActivity.longToast("客户端暂不支持此课时类型！");
            return;
        }

        if (Const.NETEASE_OPEN_COURSE.equals(lessonItem.mediaSource)) {
            mActivity.longToast("客户端暂不支持网易云视频");
            return;
        }

        if (!"published".equals(lessonItem.status)) {
            mActivity.longToast("课时尚未发布！请稍后浏览！");
            return;
        }

        if (lessonItem.free != LessonItem.FREE) {
            if (mActivity.app.loginUser == null) {
                mActivity.longToast("请登录后学习！");
                LoginActivity.start(mActivity);
                return;
            }


            if (courseDetailsResult.member == null) {
                mActivity.longToast("请加入学习！");
                return;
            }
        }

        Bundle bundle = new Bundle();
        bundle.putString(Const.ACTIONBAR_TITLE, lessonItem.title);
        bundle.putLong(liveLessonFragment.STARTTIME, Integer.valueOf(lessonItem.startTime) * 1000L);
        bundle.putLong(liveLessonFragment.ENDTIME, Integer.valueOf(lessonItem.endTime) * 1000L);
        bundle.putInt(Const.COURSE_ID, lessonItem.courseId);
        bundle.putInt(Const.LESSON_ID, lessonItem.id);
        bundle.putString(liveLessonFragment.SUMMARY, lessonItem.summary);
        bundle.putString(liveLessonFragment.REPLAYSTATUS, lessonItem.replayStatus);
        bundle.putString(FragmentPageActivity.FRAGMENT, "liveLessonFragment");
        startActivityWithBundle("FragmentPageActivity", bundle);

    }

}
