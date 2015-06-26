package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.model.bal.CourseLessonType;
import com.edusoho.kuozhi.v3.model.bal.Lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.bal.Lesson.LessonStatus;
import com.edusoho.kuozhi.v3.model.bal.course.TestpaperStatus;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-15.
 */
public class LessonActivity extends ActionBarBaseActivity implements MessageEngine.MessageCallback {

    public static final String TAG = "LessonActivity";
    public static final String CONTENT = "content";
    public static final String FROM_CACHE = "from_cache";
    public static final String RESULT_ID = "resultId";
    public static final String LESSON_MODEL = "lesson_MODEL";
    public static final String LESSON_JSON = "lesson_JSON";

    private String mCurrentFragment;
    private Class mCurrentFragmentClass;
    public static final int SHOW_TOOLS = 0001;
    public static final int HIDE_TOOLS = 0002;

    private int mCourseId;
    private int mLessonId;
    private String mLessonType;
    private String mTitle;
    private String mLessonListJson;
    private String mLessonJson;
    private Bundle fragmentData;
    private boolean mFromCache;
    private boolean mIsLearn;
    private LessonStatus mLessonStatus;

    private MsgHandler msgHandler;

    private static final int REQUEST_NOTE = 0010;
    private static final int REQUEST_QUESTION = 0020;

    private LessonItem mLessonItem;
    private LessonItem mPreviousLessonItem;
    private LessonItem mNextLessonItem;
    private ArrayList<LessonItem> mCleanLessonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_layout);
        msgHandler = new MsgHandler(this);
        fragmentData = new Bundle();
        initView();
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        switch (type) {
            case SHOW_TOOLS:
                msgHandler.obtainMessage(SHOW_TOOLS).sendToTarget();
                break;
            case HIDE_TOOLS:
                msgHandler.obtainMessage(HIDE_TOOLS).sendToTarget();
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(HIDE_TOOLS, source)
        };
        return messageTypes;
    }

    public int getCourseId() {
        return mCourseId;
    }

    public LessonItem getLessonItem() {
        return mLessonItem;
    }

    public int getLessonId() {
        return mLessonId;
    }

    private void initView() {
        try {
            Intent data = getIntent();

            if (data != null) {
                mLessonItem = (LessonItem) data.getSerializableExtra(LESSON_MODEL);
                mLessonJson = data.getStringExtra(LESSON_JSON);
            }


            if (mLessonItem == null) {
                CommonUtil.longToast(mContext, "课程数据错误！");
                return;
            }

            mLessonItem = getLessonResultType(mLessonItem.type, mLessonJson);
            if (mLessonItem == null) {
                return;
            }
            setBackMode(BACK, mLessonItem.title);
            loadLesson(mLessonId);
        } catch (Exception ex) {
            Log.e("lessonActivity", ex.toString());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            msgHandler.obtainMessage(SHOW_TOOLS).sendToTarget();
            showActionBar();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            msgHandler.obtainMessage(HIDE_TOOLS).sendToTarget();
            hideActionBar();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(null, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(null, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void loadLesson(int lessonId) {
        int userId = app.loginUser == null ? 0 : app.loginUser.id;
        M3U8DbModle m3U8DbModle = M3U8Util.queryM3U8Modle(
                mContext, userId, lessonId, app.domain, M3U8Util.FINISH);
        if (m3U8DbModle != null) {
            loadLessonFromCache(lessonId);
        } else {
            loadLessonFromNet();
        }
    }

    private void loadLessonFromNet() {
        switchLoadLessonContent(mLessonItem);
    }

    private void loadLessonFromCache(int lessonId) {
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(mContext);
        String object = sqliteUtil.query(
                String.class,
                "value",
                "select * from data_cache where type=? and key=?",
                Const.CACHE_LESSON_TYPE,
                "lesson-" + lessonId
        );

        LessonItem lessonItem = getLessonResultType(mLessonType, object);
        if (lessonItem == null) {
            return;
        }

        mLessonItem = lessonItem;
        switchLoadLessonContent(mLessonItem);
    }

    private LessonItem getLessonResultType(String lessonType, String object) {
        CourseLessonType courseLessonType = CourseLessonType.value(lessonType);
        switch (courseLessonType) {
            case PPT:
                LessonItem<ArrayList<String>> pptLesson = parseJsonValue(
                        object, new TypeToken<LessonItem<ArrayList<String>>>() {
                        });
                fragmentData.putString(Const.LESSON_TYPE, "ppt");
                fragmentData.putStringArrayList(CONTENT, pptLesson.content);
                return pptLesson;
            case TESTPAPER:
                LessonItem<TestpaperStatus> testpaperLesson = parseJsonValue(
                        object, new TypeToken<LessonItem<TestpaperStatus>>() {
                        });
                TestpaperStatus status = testpaperLesson.content;
                fragmentData.putString(Const.LESSON_TYPE, "testpaper");
                fragmentData.putInt(Const.MEDIA_ID, testpaperLesson.mediaId);
                fragmentData.putInt(RESULT_ID, status.resultId);
                fragmentData.putString(Const.STATUS, status.status);
                fragmentData.putInt(Const.LESSON_ID, testpaperLesson.id);
                fragmentData.putString(Const.ACTIONBAR_TITLE, testpaperLesson.title);
                return testpaperLesson;
            case VIDEO:
            case AUDIO:
            case TEXT:
            default:
                LessonItem<String> normalLesson = parseJsonValue(
                        object, new TypeToken<LessonItem<String>>() {
                        });
                if (mFromCache) {
                    normalLesson.mediaUri = "http://localhost:8800/playlist/" + mLessonId;
                }
                fragmentData.putString(Const.LESSON_TYPE, courseLessonType.name());
                fragmentData.putString(CONTENT, normalLesson.content);
                if (courseLessonType == CourseLessonType.VIDEO
                        || courseLessonType == CourseLessonType.AUDIO) {
                    fragmentData.putString(Const.MEDIA_URL, normalLesson.mediaUri);
                    fragmentData.putBoolean(FROM_CACHE, mFromCache);
                    fragmentData.putString(Const.HEAD_URL, normalLesson.headUrl);
                    fragmentData.putString(Const.MEDIA_SOURCE, normalLesson.mediaSource);
                    fragmentData.putInt(Const.LESSON_ID, normalLesson.id);
                    fragmentData.putInt(Const.COURSE_ID, normalLesson.courseId);
                }
                return normalLesson;
        }
    }

    /**
     * 获取本地视频列表
     *
     * @param lessonId
     * @return
     */
    private File getLocalLesson(int lessonId) {
        File workSpace = EdusohoApp.getWorkSpace();
        if (workSpace == null) {
            return null;
        }

        StringBuffer dirBuilder = new StringBuffer(workSpace.getAbsolutePath());
        dirBuilder.append("/videos/")
                .append(app.domain)
                .append("/")
                .append(lessonId);

        return new File(dirBuilder.toString(), "play.m3u8");
    }

    private void switchLoadLessonContent(LessonItem lessonItem) {
        CourseLessonType lessonType = CourseLessonType.value(lessonItem.type);
        if (Const.NETEASE_OPEN_COURSE.equals(lessonItem.mediaSource)) {
            CommonUtil.longToast(mContext, "客户端暂不支持网易云视频");
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
        fragmentTransaction.commit();

        mCurrentFragment = fragmentName;
        mCurrentFragmentClass = fragment.getClass();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO 发送H5页面学习状态
//        app.sendMsgToTarget(
//                CourseLearningFragment.UPDATE_LEARN_STATUS, null, CourseLearningFragment.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NOTE && resultCode == Const.OK) {
            CommonUtil.longToast(mContext, "添加笔记成功!");
            return;
        }

        if (requestCode == Const.EDIT_QUESTION && resultCode == Const.OK) {
            CommonUtil.longToast(mContext, "添加问题成功!");
            return;
        }
    }

    private boolean getM3U8Cache(int lessonId) {
        M3U8DbModle model = M3U8Util.queryM3U8Modle(mContext, app.loginUser.id, lessonId, app.domain, M3U8Util.FINISH);
        return model != null;
    }

    public static class MsgHandler extends Handler {
        WeakReference<LessonActivity> mWeakReference;
        LessonActivity mActivity;

        public MsgHandler(LessonActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWeakReference != null) {
                mActivity = mWeakReference.get();
            }
            switch (msg.what) {
                case SHOW_TOOLS:
                    //mActivity.showToolsByAnim();
                    break;
                case HIDE_TOOLS:
                    //mActivity.hieToolsByAnim();
                    break;
            }
        }
    }
}
