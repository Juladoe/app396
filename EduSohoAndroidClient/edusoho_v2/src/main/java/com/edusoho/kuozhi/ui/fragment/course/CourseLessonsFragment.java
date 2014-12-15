package com.edusoho.kuozhi.ui.fragment.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.CourseLessonAdapter;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.LessonsResult;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.course.CorusePaperActivity;
import com.edusoho.kuozhi.ui.course.LessonActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseLessonsFragment extends BaseFragment {

    private EduSohoListView mListView;
    private TextView mLessonInfoView;
    private int mCourseId;
    private CourseLessonAdapter mAdapter;
    private View mLessonDownloadBtn;

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
        mLessonDownloadBtn = view.findViewById(R.id.lesson_download_btn);
        mLessonInfoView = (TextView) view.findViewById(R.id.course_lesson_totalInfo);
        mListView = (EduSohoListView) view.findViewById(R.id.list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(linearLayoutManager);
        mAdapter = new CourseLessonAdapter(
                mActivity, R.layout.course_details_learning_lesson_item);
        mListView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCourseId = bundle.getInt(Const.COURSE_ID, 0);
        }
        loadLessons(true);
    }

    private void loadLessons(boolean mIsAddToken)
    {
        RequestUrl url = mActivity.app.bindUrl(Const.LESSONS, mIsAddToken);
        url.setParams(new String[]{
                Const.COURSE_ID, String.valueOf(mCourseId)
        });
        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                final LessonsResult lessonsResult = mActivity.parseJsonValue(
                        object, new TypeToken<LessonsResult>(){});
                if (lessonsResult == null) {
                    return;
                }

                int lessonNum = 0;
                long totalTime = 0;
                DateFormat format = new SimpleDateFormat("mm:ss");
                try {
                    for (LessonItem lessonItem : lessonsResult.lessons) {
                        CourseLessonType type = CourseLessonType.value(lessonItem.type);
                        if (type == CourseLessonType.VIDEO) {
                            totalTime += format.parse(lessonItem.length).getTime();
                        }

                        if (LessonItem.ItemType.cover(lessonItem.itemType) == LessonItem.ItemType.LESSON) {
                            lessonNum ++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mLessonInfoView.setText(String.format(
                        "共%d个课时,视频课时总时长为%s", lessonNum, format.format(new Date(totalTime))));

                mAdapter.updateLearnStatus(lessonsResult.learnStatuses);
                mListView.pushData(lessonsResult.lessons);
                mLessonDownloadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(FragmentPageActivity.FRAGMENT, "CourseDownloadingFragment");
                        bundle.putString(Const.ACTIONBAT_TITLE, "下载列表");

                        CorusePaperActivity activity = (CorusePaperActivity)getActivity();
                        bundle.putString(
                                CourseDownloadingFragment.COURSE_JSON, app.gson.toJson(activity.getCourse()));
                        bundle.putString(
                                CourseDownloadingFragment.LIST_JSON, app.gson.toJson(lessonsResult.lessons));
                        startAcitivityWithBundle("FragmentPageActivity", bundle);
                    }
                });

                mAdapter.setOnItemClick(new RecyclerViewListBaseAdapter.RecyclerItemClick() {
                    @Override
                    public void onItemClick(Object obj, int position) {
                        showLesson((LessonItem)obj);
                    }
                });
            }
        });
    }

    private void showLesson(final LessonItem lesson)
    {
        CourseLessonType courseLessonType = CourseLessonType.value(lesson.type);
        if (courseLessonType == CourseLessonType.EMPTY) {
            mActivity.longToast("客户端暂不支持此课时类型！");
            return;
        }

        if (Const.NETEASE_OPEN_COURSE.equals(lesson.mediaSource)) {
            mActivity.longToast("客户端暂不支持网易云视频");
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

            /*
            if (!mIsLearn) {
                mActivity.longToast("请加入学习！");
                return;
            }
            */
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
                        //startIntent.putExtra(Const.LIST_JSON, mLessonListJson);
                        //startIntent.putExtra(Const.IS_LEARN, mIsLearn);
                    }
                });
    }
}
