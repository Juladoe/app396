package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.CourseLessonAdapter;
import com.edusoho.kuozhi.adapter.Course.LiveCourseLessonAdapter;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.LearnStatus;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.LessonsResult;
import com.edusoho.kuozhi.model.LiveingCourse;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.course.CorusePaperActivity;
import com.edusoho.kuozhi.ui.course.LessonActivity;
import com.edusoho.kuozhi.ui.fragment.course.CourseDownloadingFragment;
import com.edusoho.kuozhi.ui.fragment.course.ViewPagerBaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;
import com.edusoho.kuozhi.view.dialog.ExitCoursePopupDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Melomelon on 2015/2/2.
 */
public class LiveCourseLessonsFragment extends ViewPagerBaseFragment {

    private EduSohoListView mListView;
//    private TextView mLessonInfoView;
    private int mCourseId;
    private LiveCourseLessonAdapter mAdapter;
//    private View mLessonDownloadBtn;
//    private View mHeadView;

//    private SparseArray<M3U8DbModle> mM3U8DbModles;
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
                int lessonNum = 0;
                long totalTime = 0;

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


        mActivity.getCoreEngine().runNormalPlugin(
                LessonActivity.TAG, mActivity, new PluginRunCallback() {//TODO 把普通课时页面换成直播课时页面
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.COURSE_ID, lessonItem.courseId);
                        startIntent.putExtra(Const.IS_LEARN, courseDetailsResult.member != null);
                        startIntent.putExtra(Const.LESSON_ID, lessonItem.id);
                        startIntent.putExtra(Const.LESSON_TYPE, lessonItem.type);
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, lessonItem.title);
//                        startIntent.putExtra(LessonActivity.FROM_CACHE, mM3U8DbModles.indexOfKey(lesson.id) >= 0);
                    }
                }
        );
    }

}
