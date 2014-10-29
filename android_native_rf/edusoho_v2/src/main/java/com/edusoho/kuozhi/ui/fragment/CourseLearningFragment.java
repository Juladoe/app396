package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.LearnStatus;
import com.edusoho.kuozhi.model.Announcement;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.Member;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsTabActivity;
import com.edusoho.kuozhi.ui.widget.CourseDetailsLessonWidget;
import com.edusoho.kuozhi.ui.widget.LearnStatusWidget;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.ExitCoursePopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howzhi on 14-9-3.
 */
public class CourseLearningFragment extends BaseFragment {

    public static final int UPDATE_LEARN_STATUS = 0002;
    private String mTitle;
    private int mCourseId;

    private TextView mCourseNoticeView;
    private View mErrorLayout;
    private Button mCommitBtn;
    private LearnStatusWidget mCourseStatusView;
    private CourseDetailsLessonWidget mCourseLessonList;
    private View mBtnLayout;
    private CourseDetailsResult mCourseDetailsResult;
    private ArrayList<Announcement> mAnnouncements;
    private int noticeShowIndex;

    private Handler workHandler;
    public static final int SWITCH_SHOW_NOTICE = 0001;
    private Timer noticeTimer;
    private CourseDetailsActivity mCourseDetailsActivity;

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initCourseDetailsResult();
    }

    private void initCourseDetailsResult()
    {
        if (mCourseDetailsResult == null) {
            mCourseDetailsActivity = (CourseDetailsActivity) getActivity();
            mCourseDetailsResult = mCourseDetailsActivity.getCourseDetailsInfo();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("courseDetailsResult", mCourseDetailsResult);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        noticeTimer = new Timer();
        workHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case SWITCH_SHOW_NOTICE:
                        showNotice();
                        break;
                }
            }
        };
        setContainerView(R.layout.course_learning_fragment);
    }

    private void showNotice()
    {
        int size = mAnnouncements.size();
        if (size == 0) {
            mCourseNoticeView.setVisibility(View.GONE);
            return;
        }
        if (noticeShowIndex > (size - 1)) {
            noticeShowIndex = 0;
        }

        Announcement announcement = mAnnouncements.get(noticeShowIndex++);
        StringBuilder builder = new StringBuilder(AppUtil.coverCourseAbout(announcement.content));
        mCourseNoticeView.setText(builder);

        builder.append("\n");
        int oldLenght = builder.length();
        builder.append(announcement.createdTime);
        Spannable spannable = new SpannableString(builder);
        spannable.setSpan(
                new RelativeSizeSpan(0.5f), oldLenght, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mCourseNoticeView.setText(spannable);
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(UPDATE_LEARN_STATUS, source)
        };
        return messageTypes;
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        switch (type) {
            case UPDATE_LEARN_STATUS:
                loadLearnStatus();
                mCourseStatusView.initialiseByCourse(
                        mActivity,
                        mCourseDetailsResult.course,
                        mCourseId,
                        mCourseLessonList.getLessonListJson()
                );
                break;
        }
    }

    private void loadLearnStatus()
    {
        RequestUrl requestUrl = app.bindUrl(Const.LEARN_STATUS, true);
        requestUrl.setParams(new String[] {
                Const.COURSE_ID, mCourseId + ""
        });
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                HashMap<Integer, LearnStatus> learnStatusHashMap = mActivity.parseJsonValue(
                        object, new TypeToken<HashMap<Integer, LearnStatus>>() {
                });
                if (learnStatusHashMap == null) {
                    return;
                }
                mCourseLessonList.updateLessonStatus(learnStatusHashMap);
            }
        });
    }

    private void unLearnCourse()
    {
        ExitCoursePopupDialog.create(mActivity, new ExitCoursePopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button, String selStr) {
                if (button == ExitCoursePopupDialog.CANCEL) {
                    return;
                }

                showProgress(true);
                RequestUrl requestUrl = app.bindUrl(Const.UN_LEARN_COURSE, true);
                requestUrl.setParams(new String[]{
                        Const.COURSE_ID, mCourseId + "",
                        "reason", selStr
                });
                mActivity.ajaxPost(requestUrl, new ResultCallback() {
                    @Override
                    public void callback(String url, String object, AjaxStatus ajaxStatus) {
                        Log.d(null, "exit course->");
                        showProgress(false);
                        boolean result = mActivity.parseJsonValue(
                                object, new TypeToken<Boolean>() {
                        });

                        if (result) {
                            Bundle bundle = new Bundle();
                            bundle.putString(CourseDetailsActivity.FRAGMENT, "CourseDetailsFragment");
                            app.sendMsgToTarget(
                                    CourseDetailsActivity.RELOAD_DATA, null, CourseDetailsActivity.class);
                            app.sendMsgToTarget(
                                    CourseDetailsActivity.CHANGE_FRAGMENT, bundle, CourseDetailsActivity.class);
                            app.sendMsgToTarget(
                                    CourseDetailsActivity.SHOW_COURSE_PIC, null, CourseDetailsActivity.class);
                        } else {
                            mActivity.longToast("退出学习失败");
                        }
                    }
                });
            }
        }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.course_details_menu_exit) {
            unLearnCourse();
            return true;
        } else if (id == R.id.course_details_menu_courseinfo) {
            Course course = mCourseDetailsResult.course;
            Bundle fragmentData = new Bundle();
            fragmentData.putIntArray(
                    TeacherInfoFragment.TEACHER_ID, AppUtil.getTeacherIds(course.teachers));
            fragmentData.putSerializable(CourseInfoFragment.COURSE, course);
            fragmentData.putSerializable(CourseInfoFragment.MEMBER, mCourseDetailsResult.member);
            fragmentData.putInt(Const.COURSE_ID, course.id);
            fragmentData.putBoolean(Const.IS_STUDENT, true);

            Bundle bundle = new Bundle();
            bundle.putBundle(CourseDetailsTabActivity.FRAGMENT_DATA, fragmentData);
            bundle.putString(Const.ACTIONBAT_TITLE, "课程详情");
            bundle.putStringArray(CourseDetailsTabActivity.LISTS, Const.COURSE_INFO_FRAGMENT);
            bundle.putStringArray(CourseDetailsTabActivity.TITLES, Const.COURSE_INFO_TITLE);
            app.mEngine.runNormalPluginWithBundle(
                    "CourseDetailsTabActivity", mActivity, bundle);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Member member = mCourseDetailsResult.member;
        if (member != null && member.role == Member.Role.teacher) {
            inflater.inflate(R.menu.course_learning_menu_2, menu);
        } else {
            inflater.inflate(R.menu.course_learning_menu, menu);
        }
    }

    private void checkMemberExprie()
    {
        Member member = mCourseDetailsResult.member;
        if (member != null) {
            long deadline = member.deadline;
            if (deadline == -1) {
                mErrorLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void saveViewState(Bundle savedInstanceState) {
        super.saveViewState(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        mCourseDetailsResult = (CourseDetailsResult) savedInstanceState.getSerializable(
                "courseDetailsResult");
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mErrorLayout = view.findViewById(R.id.course_details_error_layout);
        mCommitBtn = (Button) view.findViewById(R.id.course_details_commit_btn);
        mBtnLayout = view.findViewById(R.id.course_details_btn_layouts);
        mCourseLessonList = (CourseDetailsLessonWidget) view.findViewById(R.id.course_learning_lessonlist);
        mCourseStatusView = (LearnStatusWidget) view.findViewById(R.id.course_learning_status_widget);
        mCourseNoticeView = (TextView) view.findViewById(R.id.course_learning_notice);

        checkMemberExprie();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(Const.ACTIONBAT_TITLE);
            mCourseId = bundle.getInt(Const.COURSE_ID);
        }

        setTitle(mTitle);
        initCourseAnnouncement();

        mCourseLessonList.hideTitle();
        mCourseLessonList.setIsLearn(true);
        mCourseLessonList.initLesson(mCourseId, mActivity, true);
        mCourseLessonList.onShow();
        showBtnLayout(mBtnLayout);

        mCourseStatusView.initialiseByCourse(
                mActivity,
                mCourseDetailsResult.course,
                mCourseId,
                mCourseLessonList.getLessonListJson()
        );
        app.sendMsgToTarget(
                CourseDetailsActivity.HIDE_COURSE_PIC, null, CourseDetailsActivity.class);
        mCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAcitivity("FragmentPageActivity", new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "ReviewInfoFragment");
                        startIntent.putExtra(Const.COURSE_ID, mCourseId);
                        startIntent.putExtra(Const.IS_STUDENT, true);
                        startIntent.putExtra(Const.ACTIONBAT_TITLE, "课程评价");
                        startIntent.putExtra(ReviewInfoFragment.COURSE, mCourseDetailsResult.course);
                    }
                });
            }
        });
    }

    private void initCourseAnnouncement()
    {
        RequestUrl url = app.bindUrl(Const.COURSE_NOTICE, true);
        url.setParams(new String[]{
                "courseId", mCourseId + ""
        });
        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mAnnouncements = mActivity.parseJsonValue(
                        object, new TypeToken<ArrayList<Announcement>>(){});

                if (mAnnouncements == null) {
                    return;
                }

                loadAnnouncements();
            }
        });
    }

    private void loadAnnouncements()
    {
        noticeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = workHandler.obtainMessage(SWITCH_SHOW_NOTICE);
                message.obj = null;
                message.sendToTarget();
            }
        }, 0, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
