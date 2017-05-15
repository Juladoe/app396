package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.bean.TaskResultEnum;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishHelper;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.bal.course.CourseLessonType;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.model.provider.CourseProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.lesson.LiveLessonFragment;
import com.edusoho.kuozhi.v3.ui.fragment.video.LessonVideoPlayerFragment;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.server.CacheServerFactory;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import cn.trinea.android.common.util.DigestUtils;
import cn.trinea.android.common.util.FileUtils;

/**
 * Created by howzhi on 14-9-15.
 */
public class LessonActivity extends ActionBarBaseActivity implements MessageEngine.MessageCallback {

    public static final String TAG = "LessonActivity";
    public static final String CONTENT = "content";
    public static final String FROM_CACHE = "from_cache";
    public static final String LESSON_IDS = "lesson_ids";
    public static final String RESULT_ID = "resultId";
    public static final String MEMBER_STATE = "member_state";
    public static final String COURSE_TASK = "course_task";
    public static final String COURSE = "course";
    public static final int SHOW_TOOLS = 1;
    public static final int HIDE_TOOLS = 2;

    public static final int REQUEST_LEARN = 9;

    private int mCourseId;
    private int mLessonId;
    private boolean mIsMember;
    private CourseTask mCourseTask;
    private CourseProject mCourseProject;
    private String mLessonType;
    private Bundle fragmentData;
    private boolean mFromCache;

    private LessonItem mLessonItem;
    private Toolbar mToolBar;
    private TextView mToolBarTitle;
    private View mLoadView;
    private ESIconView mBack;
    private TextView mTaskFinish;
    private TaskFinishHelper mTaskFinishHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_layout);
        ActivityUtil.setStatusViewBackgroud(this, getResources().getColor(R.color.primary_color));
        fragmentData = new Bundle();
        initView();
        startCacheServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        CacheServerFactory.getInstance().resume();
        mTaskFinishHelper.onInvoke();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CacheServerFactory.getInstance().pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTaskFinishHelper.onDestroyTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bundle bundle = new Bundle();
        bundle.putString("event", "lessonStatusRefresh");
        MessageEngine.getInstance().sendMsg(WebViewActivity.SEND_EVENT, bundle);
    }

    private void initView() {
        try {
            Intent data = getIntent();
            mToolBar = (Toolbar) findViewById(R.id.toolbar);
            mBack = (ESIconView) findViewById(R.id.iv_back);
            mLoadView = findViewById(R.id.load_layout);
            mToolBarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
            mTaskFinish = (TextView) findViewById(R.id.tv_finish_task);

            setSupportActionBar(mToolBar);
            if (data != null) {
                mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
                mCourseId = data.getIntExtra(Const.COURSE_ID, 0);
                mIsMember = data.getBooleanExtra(LessonActivity.MEMBER_STATE, false);
                mCourseTask = (CourseTask) data.getSerializableExtra(COURSE_TASK);
                mCourseProject = (CourseProject) data.getSerializableExtra(COURSE);
            }

            mToolBarTitle.setText(mCourseTask.title);
            if (mIsMember) {
                mTaskFinish.setVisibility(View.VISIBLE);
                setTaskFinishButtonBackground(mCourseTask);
            } else {
                mTaskFinish.setVisibility(View.GONE);
            }

            loadLesson();

            mBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        finish();
                    }
                }
            });

            TaskFinishHelper.Builder builder = new TaskFinishHelper.Builder()
                    .setCourseId(mCourseProject.id)
                    .setCourseTask(mCourseTask)
                    .setEnableFinish(mCourseProject.enableFinish);

            mTaskFinishHelper = new TaskFinishHelper(builder, this)
                    .setActionListener(new TaskFinishHelper.ActionListener() {
                        @Override
                        public void onFinish(TaskEvent taskEvent) {
                            EventBus.getDefault().postSticky(new MessageEvent<>(mCourseTask, MessageEvent.FINISH_TASK_SUCCESS));
                            mCourseTask.result = taskEvent.result;
                            setTaskFinishButtonBackground(mCourseTask);
                            TaskFinishDialog.newInstance(taskEvent, mCourseTask)
                                    .show(getSupportFragmentManager(), "mTaskFinishDialog");
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });

            mTaskFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsMember && (mCourseTask.result == null || TaskResultEnum.START.toString().equals(mCourseTask.result.status))) {
                        finishTask();
                    }
                }
            });

        } catch (Exception ex) {
            Log.e("lessonActivity", ex.toString());
        }
    }

    private void setTaskFinishButtonBackground(CourseTask courseTask) {
        if (courseTask.result != null && TaskResultEnum.FINISH.toString().equals(courseTask.result.status)) {
            mTaskFinish.setTextColor(mContext.getResources().getColor(R.color.disabled2_hint_color));
            mTaskFinish.setCompoundDrawablesWithIntrinsicBounds(R.drawable.task_finish_left_icon, 0, 0, 0);
            mTaskFinish.setBackground(getResources().getDrawable(R.drawable.task_finish_button_bg));
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTaskFinish.setTextColor(mContext.getResources().getColor(R.color.disabled2_hint_color));
                mTaskFinish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mTaskFinish.setBackground(getResources().getDrawable(R.drawable.task_unfinish_button_bg));
            } else {
                mTaskFinish.setTextColor(mContext.getResources().getColor(R.color.primary_font_color));
                mTaskFinish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mTaskFinish.setBackground(getResources().getDrawable(R.drawable.task_unfinish_button_grey_bg));
            }
        }
    }

    public void finishTask() {
        mTaskFinishHelper.finish();
    }

    public void stickyFinish() {
        mTaskFinishHelper.stickyFinish();
    }

    private void startCacheServer() {
        User user = getAppSettingProvider().getCurrentUser();
        School school = getAppSettingProvider().getCurrentSchool();
        if (user == null || school == null) {
            return;
        }
        CacheServerFactory.getInstance().start(getBaseContext(), school.host, user.id);
    }

    protected void share() {
        final LoadDialog loadDialog = LoadDialog.create(this);
        loadDialog.show();
        new CourseProvider(getBaseContext()).getCourse(mCourseId)
                .success(new NormalCallback<CourseDetailsResult>() {
                    @Override
                    public void success(CourseDetailsResult courseDetailsResult) {
                        loadDialog.dismiss();
                        if (courseDetailsResult == null || courseDetailsResult.course == null) {
                            return;
                        }
                        final Course course = courseDetailsResult.course;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String shareUrl = String.format("%s/course/%d", app.host, mCourseId);
                                ShareTool shareTool = new ShareTool(
                                        mActivity, shareUrl, course.title, mLessonItem.title, course.middlePicture);
                                shareTool.shardCourse();
                            }
                        });
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                loadDialog.dismiss();
            }
        });
    }

    private void setLoadViewState(boolean isShow) {
        mLoadView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        changeScreenOrientation(newConfig.orientation);
        super.onConfigurationChanged(newConfig);
    }

    private void changeScreenOrientation(int orientation) {
        invalidateOptionsMenu();
        setTaskFinishButtonBackground(mCourseTask);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ActivityUtil.setStatusViewBackgroud(this, getResources().getColor(R.color.primary_color));
            mToolBar.setBackgroundColor(getResources().getColor(R.color.textIcons));
            mBack.setTextColor(getResources().getColor(R.color.primary_font_color));
            mToolBarTitle.setTextColor(getResources().getColor(R.color.textSecondary));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ActivityUtil.setStatusViewBackgroud(this, getResources().getColor(R.color.transparent));
            mToolBar.setBackgroundColor(getResources().getColor(R.color.transparent));
            mBack.setTextColor(getResources().getColor(R.color.disabled2_hint_color));
            mToolBarTitle.setTextColor(getResources().getColor(R.color.textIcons));
        }
    }

    @Override
    public void showActionBar() {
        getSupportActionBar().show();
    }

    @Override
    public void hideActionBar() {
        getSupportActionBar().hide();
    }

    private void loadLesson() {
        int userId = app.loginUser == null ? 0 : app.loginUser.id;
        M3U8DbModel m3U8DbModel = M3U8Util.queryM3U8Model(
                mContext, userId, mLessonId, app.domain, M3U8Util.FINISH);
        mFromCache = m3U8DbModel != null;
        if (mFromCache) {
            try {
                loadLessonFromCache();
            } catch (RuntimeException e) {
                loadLessonFromNet();
            }
            return;
        }
        loadLessonFromNet();
    }

    private void loadLessonFromNet() {
        setLoadViewState(true);
        RequestUrl requestUrl = app.bindNewUrl(String.format(Const.LESSON, mCourseTask.id), true);
        ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (isFinishing()) {
                    return;
                }
                setLoadViewState(false);
                mLessonItem = getLessonResultType(response);
                if (mLessonItem == null) {
                    CommonUtil.longToast(mContext, getResources().getString(R.string.lesson_not_exist));
                    finish();
                    return;
                }
                mCourseId = mLessonItem.courseId;
                mLessonType = mLessonItem.type;
                switchLoadLessonContent(mLessonItem);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setLoadViewState(false);
            }
        });

    }

    private void loadLessonFromCache() {
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(mContext);
        String object = sqliteUtil.query(
                String.class,
                "value",
                "select * from data_cache where type=? and key=?",
                Const.CACHE_LESSON_TYPE,
                "lesson-" + mLessonId
        );

        setLoadViewState(false);
        LessonItem lessonItem = getLessonResultType(object);
        if (lessonItem == null) {
            finish();
            return;
        }

        mLessonItem = lessonItem;
        mLessonType = mLessonItem.type;
        setBackMode(BACK, mLessonItem.title);
        switchLoadLessonContent(mLessonItem);
    }

    private LessonItem getLessonResultType(String object) {
        LessonItem lessonItem = handleJsonValue(
                object, new TypeToken<LessonItem>() {
                });
        if (lessonItem == null) {
            return null;
        }

        CourseLessonType courseLessonType = CourseLessonType.value(lessonItem.type);
        fragmentData.putInt(Const.COURSE_ID, lessonItem.courseId);
        fragmentData.putInt(Const.LESSON_ID, lessonItem.id);
        switch (courseLessonType) {
            case LIVE:
                fragmentData.putString(Const.ACTIONBAR_TITLE, lessonItem.title);
                fragmentData.putLong(LiveLessonFragment.STARTTIME, Integer.valueOf(lessonItem.startTime) * 1000L);
                fragmentData.putLong(LiveLessonFragment.ENDTIME, Integer.valueOf(lessonItem.endTime) * 1000L);
                fragmentData.putString(LiveLessonFragment.SUMMARY, lessonItem.summary);
                fragmentData.putString(LiveLessonFragment.REPLAYSTATUS, lessonItem.replayStatus);
                return lessonItem;
            case PPT:
                LessonItem<LinkedTreeMap<String, ArrayList<String>>> pptLesson = lessonItem;
                fragmentData.putString(Const.LESSON_TYPE, "ppt");
                ArrayList<String> pptContent = pptLesson.content.get("resource");
                fragmentData.putStringArrayList(CONTENT, pptContent);
                return pptLesson;
            case TESTPAPER:
                LessonItem<LinkedTreeMap> testpaperLesson = lessonItem;
                LinkedTreeMap status = testpaperLesson.content;
                fragmentData.putString(Const.LESSON_TYPE, "testpaper");
                fragmentData.putInt(Const.MEDIA_ID, testpaperLesson.mediaId);
                int resultId = AppUtil.parseInt(status.get("resultId").toString());
                fragmentData.putInt(RESULT_ID, resultId);

                fragmentData.putString(Const.STATUS, status.get("status").toString());
                fragmentData.putInt(Const.LESSON_ID, testpaperLesson.id);
                fragmentData.putString(Const.ACTIONBAR_TITLE, testpaperLesson.title);
                return testpaperLesson;
            case DOCUMENT:
                LessonItem<LinkedTreeMap<String, String>> documentLessonItem = lessonItem;
                fragmentData.putString(Const.LESSON_TYPE, courseLessonType.name());
                fragmentData.putString(CONTENT, documentLessonItem.content.get("previewUrl"));
                return documentLessonItem;
            case VIDEO:
                if (!TextUtils.isEmpty(lessonItem.mediaUri)) {
                    Uri uri = Uri.parse(lessonItem.mediaUri);
                    lessonItem.mediaUri = String.format("%s://%s%s", uri.getScheme(), uri.getHost(), uri.getPath());
                }
                if (!TextUtils.isEmpty(lessonItem.headUrl)) {
                    Uri headUri = Uri.parse(lessonItem.mediaUri);
                    lessonItem.headUrl = String.format("%s://%s%s", headUri.getScheme(), headUri.getHost(), headUri.getPath());
                }
            case AUDIO:
            case TEXT:
            default:
                LessonItem<String> normalLesson = lessonItem;
                if (mFromCache) {
                    if (lessonItem.mediaUri.contains("getLocalVideo")) {
                        StringBuffer dirBuilder = new StringBuffer(EdusohoApp.getWorkSpace().getAbsolutePath());
                        dirBuilder.append("/videos/")
                                .append(app.loginUser.id)
                                .append("/")
                                .append(app.domain)
                                .append("/")
                                .append(mLessonId).append("/").append(DigestUtils.md5(lessonItem.mediaUri));
                        if (FileUtils.isFileExist(dirBuilder.toString())) {
                            normalLesson.mediaUri = dirBuilder.toString();
                        } else {
                            CommonUtil.longToast(mContext, "视频文件不存在");
                            return null;
                        }
                    } else {
                        normalLesson.mediaUri = String.format("http://%s:8800/playlist/%d.m3u8", "localhost", mLessonId);
                    }
                }
                fragmentData.putString(Const.LESSON_TYPE, courseLessonType.name());
                fragmentData.putString(CONTENT, normalLesson.content);
                if (courseLessonType == CourseLessonType.VIDEO
                        || courseLessonType == CourseLessonType.AUDIO) {
                    fragmentData.putString(LessonVideoPlayerFragment.PLAY_URI, normalLesson.mediaUri);
                    fragmentData.putBoolean(FROM_CACHE, mFromCache);
                    fragmentData.putString(Const.HEAD_URL, normalLesson.headUrl);
                    fragmentData.putString(Const.MEDIA_SOURCE, normalLesson.mediaSource);
                    fragmentData.putInt(Const.LESSON_ID, normalLesson.id);
                    fragmentData.putInt(Const.COURSE_ID, normalLesson.courseId);
                    fragmentData.putString(Const.LESSON_NAME, normalLesson.title);
                    fragmentData.putString(Const.VIDEO_TYPE, normalLesson.mediaStorage);
                    fragmentData.putString(Const.CLOUD_VIDEO_CONVERT_STATUS, normalLesson.mediaConvertStatus);
                }
                return normalLesson;
        }
    }

    private void switchLoadLessonContent(LessonItem lessonItem) {
        CourseLessonType lessonType = CourseLessonType.value(lessonItem.type);

        if ("flash".equals(lessonItem.type) || CommonUtil.inArray(lessonItem.mediaSource,
                new String[]{Const.NETEASE_OPEN_COURSE, Const.QQ_OPEN_COURSE})) {
            CommonUtil.longToast(mContext, "客户端暂不支持该课时！");
            return;
        }
        if (lessonType == CourseLessonType.VIDEO
                && !"self".equals(lessonItem.mediaSource)) {
            loadLessonFragment("WebVideoLessonFragment");
            return;
        }

        StringBuilder stringBuilder = lessonType.getType();
        stringBuilder.append("LessonFragment");
        loadLessonFragment(stringBuilder.toString());
    }

    private void loadLessonFragment(String fragmentName) {
        Log.d(null, "fragmentName->" + fragmentName);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = app.mEngine.runPluginWithFragment(
                fragmentName, mActivity, new PluginFragmentCallback() {
                    @Override
                    public void setArguments(Bundle bundle) {
                        bundle.putAll(fragmentData);
                    }
                });
        fragmentTransaction.replace(R.id.lesson_content, fragment);
        fragmentTransaction.setCustomAnimations(
                FragmentTransaction.TRANSIT_FRAGMENT_FADE, FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void finish() {
        super.finish();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.lesson_content);
        if (fragment != null) {
            mFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }

        CacheServerFactory.getInstance().stop();
    }

}
