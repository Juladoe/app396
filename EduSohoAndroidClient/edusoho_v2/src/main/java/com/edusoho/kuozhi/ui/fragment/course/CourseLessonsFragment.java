package com.edusoho.kuozhi.ui.fragment.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.CourseLessonAdapter;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.LearnStatus;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.LessonsResult;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.course.CorusePaperActivity;
import com.edusoho.kuozhi.ui.course.LessonActivity;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.AppUtil;
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
 * Created by howzhi on 14/12/2.
 */
public class CourseLessonsFragment extends ViewPagerBaseFragment {

    private EduSohoListView mListView;
    private TextView mLessonInfoView;
    private int mCourseId;
    private CourseLessonAdapter mAdapter;
    private View mLessonDownloadBtn;
    private View mHeadView;

    private SparseArray<M3U8DbModle> mM3U8DbModles;
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
        mAdapter = new CourseLessonAdapter(
                mActivity, R.layout.course_details_learning_lesson_item);

        mListView.setEmptyString(new String[]{"暂无课时"}, R.drawable.icon_course_empty);
        mHeadView = initHeadView();
        mAdapter.addHeadView(mHeadView);
        mListView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCourseId = bundle.getInt(Const.COURSE_ID, 0);
        }
        loadLessons(true);
    }

    private View initHeadView() {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.course_lesson_list_headview, null);

        mLessonDownloadBtn = view.findViewById(R.id.lesson_download_btn);
        mLessonInfoView = (TextView) view.findViewById(R.id.course_lesson_totalInfo);
        return view;
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

    private void updateM3U8Modles()
    {
        if (mLessons == null) {
            return;
        }
        mM3U8DbModles = getLocalM3U8Models(mLessons);
        mAdapter.updateM3U8Models(mM3U8DbModles);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsLoadLesson) {
            return;
        }

        updateM3U8Modles();
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
                try {
                    for (LessonItem lessonItem : lessonsResult.lessons) {
                        CourseLessonType type = CourseLessonType.value(lessonItem.type);
                        if (type == CourseLessonType.VIDEO) {
                            if (lessonItem.length.split(":").length > 2) {
                                format = fullFormat;
                            } else {
                                format = simpleFormat;
                            }
                            totalTime += format.parse(lessonItem.length).getTime();
                        }

                        if (LessonItem.ItemType.cover(lessonItem.itemType) == LessonItem.ItemType.LESSON) {
                            lessonNum++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mLessonInfoView.setText(String.format(
                        "共%d个课时,视频课时总时长为%s", lessonNum, format.format(new Date(totalTime))));

                updateM3U8Modles();
                mAdapter.updateLearnStatus(lessonsResult.learnStatuses);
                mListView.pushData(lessonsResult.lessons);
                mLessonDownloadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (app.loginUser == null) {
                            LoginActivity.startForResult(mActivity);
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString(FragmentPageActivity.FRAGMENT, "CourseDownloadingFragment");
                        bundle.putString(Const.ACTIONBAR_TITLE, "下载列表");

                        CorusePaperActivity activity = (CorusePaperActivity) getActivity();
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
                        showLesson((LessonItem) obj);
                    }
                });
            }
        });
    }

    private SparseArray<M3U8DbModle> getLocalM3U8Models(ArrayList<LessonItem> lessons) {
        int length = lessons.size();
        int[] ids = new int[length];
        for (int i = 0; i < length; i++) {
            ids[i] = lessons.get(i).id;
        }

        int userId = app.loginUser != null ? app.loginUser.id : 0;
        return M3U8Uitl.getM3U8ModleList(mContext, ids, userId, app.domain, 1);
    }

    private void showLesson(final LessonItem lesson) {
        CorusePaperActivity activity = (CorusePaperActivity) getActivity();
        final CourseDetailsResult courseDetailsResult = activity.getCourseResult();

        CourseLessonType courseLessonType = CourseLessonType.value(lesson.type);
        if (courseLessonType == CourseLessonType.CHAPTER
                || courseLessonType == CourseLessonType.UNIT) {
            return;
        }

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

        if (lesson.free != LessonItem.FREE) {
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

        if (courseLessonType == CourseLessonType.VIDEO) {
            int offlineType = app.config.offlineType;
            if (offlineType == Const.NET_NONE) {
                showAlertDialog("当前设置视频课时观看、下载为禁止模式!\n模式可以在设置里修改。");
                return;
            } else if (offlineType == Const.NET_WIFI && !app.getNetIsConnect()) {
                showAlertDialog("当前设置视频课时观看、下载为wifi模式!\n模式可以在设置里修改。");
                return;
            }
        }

        mActivity.getCoreEngine().runNormalPlugin(
                LessonActivity.TAG, mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.COURSE_ID, lesson.courseId);
                        startIntent.putExtra(Const.IS_LEARN, courseDetailsResult.member != null);
                        startIntent.putExtra(Const.LESSON_ID, lesson.id);
                        startIntent.putExtra(Const.LESSON_TYPE, lesson.type);
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, lesson.title);
                        startIntent.putExtra(LessonActivity.FROM_CACHE, mM3U8DbModles.indexOfKey(lesson.id) >= 0);
                    }
                }
        );
    }

    private void showAlertDialog(String content) {
        PopupDialog popupDialog = PopupDialog.createMuilt(
                mActivity,
                "播放提示",
                content,
                new PopupDialog.PopupClickListener() {
                    @Override
                    public void onClick(int button) {
                        if (button == PopupDialog.OK) {
                            ExitCoursePopupDialog.createNormal(
                                    mActivity,
                                    "视频课时下载播放",
                                    new ExitCoursePopupDialog.PopupClickListener() {
                                        @Override
                                        public void onClick(int button, int position, String selStr) {
                                            if (button == ExitCoursePopupDialog.CANCEL) {
                                                return;
                                            }

                                            app.config.offlineType = position;
                                            app.saveConfig();
                                        }
                                    }
                            ).show();
                        }
                    }
                }
        );
        popupDialog.setOkText("去设置");
        popupDialog.show();
    }
}
