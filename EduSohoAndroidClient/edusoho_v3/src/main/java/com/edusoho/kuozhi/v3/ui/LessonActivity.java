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

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.model.bal.CourseLessonType;
import com.edusoho.kuozhi.v3.model.bal.Lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.bal.Lesson.LessonStatus;
import com.edusoho.kuozhi.v3.model.bal.LessonsResult;
import com.edusoho.kuozhi.v3.model.bal.course.TestpaperStatus;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
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

    private String mCurrentFragment;
    private Class mCurrentFragmentClass;
    public static final int SHOW_TOOLS = 0001;
    public static final int HIDE_TOOLS = 0002;

    private int mCourseId;
    private int mLessonId;
    private String mLessonType;
    private String mTitle;
    private String mLessonListJson;
    private Bundle fragmentData;
    private boolean mFromCache;
    private boolean mIsLearn;
    private LessonStatus mLessonStatus;

//    protected MenuDrawer mMenuDrawer;
//    private EduSohoTextBtn mLearnBtn;
//    private EduSohoTextBtn mLessonNextBtn;
//    private EduSohoTextBtn mLessonPreviousBtn;
//    private EduSohoTextBtn mMoreBtn;
//    private View mToolsLayout;

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

//    private void changeLessonStatus(boolean isLearn) {
//        mLearnBtn.setEnabled(false);
//        RequestUrl requestUrl = app.bindUrl(
//                isLearn ? Const.LEARN_LESSON : Const.UNLEARN_LESSON, true);
//        requestUrl.setParams(new String[]{
//                Const.COURSE_ID, mCourseId + "",
//                Const.LESSON_ID, mLessonId + ""
//        });
//
//        ajaxPost(requestUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                mLearnBtn.setEnabled(true);
//                setSupportProgressBarIndeterminateVisibility(false);
//                LearnStatus result = parseJsonValue(response, new TypeToken<LearnStatus>() {
//                });
//                if (result == null) {
//                    return;
//                }
//
//                setLearnStatus(result);
//            }
//        }, null);
//    }

    private void initView() {
        try {
            Intent data = getIntent();
//            mToolsLayout = findViewById(R.id.lesson_tools_layout);
//            mLessonNextBtn = (EduSohoTextBtn) findViewById(R.id.lesson_next);
//            mLessonPreviousBtn = (EduSohoTextBtn) findViewById(R.id.lesson_previous);
//            mMoreBtn = (EduSohoTextBtn) findViewById(R.id.lesson_more_btn);
//            mLearnBtn = (EduSohoTextBtn) findViewById(R.id.lesson_learn_btn);

            if (data != null) {
                mCourseId = data.getIntExtra(Const.COURSE_ID, 0);
                mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
            }

            if (mCourseId == 0 || mLessonId == 0) {
                CommonUtil.longToast(mContext, "课程数据错误！");
                return;
            }

            RequestUrl requestUrl = app.bindUrl(Const.COURSELESSON, true);
            requestUrl.setParams(new String[]{
                    "courseId", mCourseId + "",
                    "lessonId", mLessonId + ""
            });

            ajaxPostWithLoading(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    LessonItem lessonItem = getLessonResultType(mLessonType, response);
                    if (lessonItem == null) {
                        return;
                    }
                    setBackMode(BACK, lessonItem.title);
                    mLessonItem = lessonItem;
                    loadLesson(mLessonId);
                    //switchLoadLessonContent(lessonItem);
                }
            }, null, "加载中");


        } catch (Exception ex) {
            Log.e("lessonActivity", ex.toString());
        }
    }

//    private void initRedirectBtn() {
//        if (mNextLessonItem == null) {
//            mLessonNextBtn.setEnabled(false);
//        } else {
//            mLessonNextBtn.setEnabled(true);
//        }
//        if (mPreviousLessonItem == null) {
//            mLessonPreviousBtn.setEnabled(false);
//        } else {
//            mLessonPreviousBtn.setEnabled(true);
//        }
//    }

    /**
     * 去掉mLessonListJson中不是lesson的item
     */
    private void clearLessonResult() {
        LessonsResult result = mActivity.parseJsonValue(
                mLessonListJson, new TypeToken<LessonsResult>() {
                });
        mCleanLessonList = result.lessons;

        for (int i = 0; i < mCleanLessonList.size(); i++) {
            if (!mCleanLessonList.get(i).itemType.equals("lesson")) {
                mCleanLessonList.remove(i--);
            }
        }
    }

    /**
     * 初始化前一个课时和后一个课时信息
     */
    private void initLessonResult() {
        ArrayList<LessonItem> list = mCleanLessonList;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id == mLessonId) {
                if (i == 0) {
                    mPreviousLessonItem = null;
                    mNextLessonItem = list.size() > 1 ? list.get(i + 1) : null;
                } else if (i == list.size() - 1) {
                    mPreviousLessonItem = list.get(i - 1);
                    mNextLessonItem = null;
                } else {
                    mPreviousLessonItem = list.get(i - 1);
                    mNextLessonItem = list.get(i + 1);
                }
            }
        }
    }

//    private void goToAnotherLesson(LessonItem lessonItem) {
//        CourseLessonType courseLessonType = CourseLessonType.value(lessonItem.type);
//        if (courseLessonType == CourseLessonType.VIDEO) {
//            int offlineType = app.config.offlineType;
//            if (offlineType == Const.NET_WIFI && !app.getNetIsWiFi()) {
//                AppUtil.showAlertDialog(
//                        mActivity,
//                        "当前设置视频课时观看、下载为wifi模式!\n模式可以在设置里修改。"
//                );
//                return;
//            }
//        }
//
//        mLessonId = lessonItem.id;
//        mLessonType = lessonItem.type;
//        initLessonResult();
//        initRedirectBtn();
//        hieToolsByAnim();
//        setTitle(lessonItem.title);
//        loadLesson(mLessonId);
//    }

    /**
     * 获取课时是否已学状态
     */
//    private void loadLessonStatus() {
//        RequestUrl requestUrl = app.bindUrl(Const.LESSON_STATUS, true);
//        requestUrl.setParams(new String[]{
//                "courseId", mCourseId + "",
//                "lessonId", mLessonId + ""
//        });
//
//        ajaxPost(requestUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                mLessonStatus = parseJsonValue(
//                        response, new TypeToken<LessonStatus>() {
//                        });
//                if (mLessonStatus.learnStatus != LearnStatus.finished) {
//                    mLessonStatus.learnStatus = LearnStatus.learning;
//                }
//                mToolsLayout.setVisibility(View.VISIBLE);
//                showToolsByAnim();
//                setLearnStatus(mLessonStatus == null ? LearnStatus.learning : mLessonStatus.learnStatus);
//            }
//        }, null);
//    }
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

//    private void bindListener() {
//        mLearnBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean isLearn;
//                if (mLearnBtn.getTag() == null) {
//                    isLearn = true;
//                } else {
//                    isLearn = (Boolean) mLearnBtn.getTag();
//                }
//                changeLessonStatus(isLearn);
//            }
//        });
//
//        mLessonNextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mNextLessonItem != null) {
//                    goToAnotherLesson(mNextLessonItem);
//                }
//            }
//        });
//
//        mLessonPreviousBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mPreviousLessonItem != null) {
//                    goToAnotherLesson(mPreviousLessonItem);
//                }
//            }
//        });
//
//        mMoreBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //showMoreBtn(view);
//            }
//        });
//    }

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
//        if (!mLessonType.equals("testpaper") && mIsLearn) {
//            loadLessonStatus();
//            bindListener();
//        }
        mLessonItem = lessonItem;
        switchLoadLessonContent(mLessonItem);
    }

    private LessonItem getLessonResultType(String lessonType, String object) {
        CourseLessonType courseLessonType = CourseLessonType.value(lessonType);
        LessonItem<String> normalLesson = null;
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
                normalLesson = parseJsonValue(
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

//    private void setLearnStatus(LearnStatus learnStatus) {
//        Resources resources = getResources();
//        switch (learnStatus) {
//            case learning:
//                mLearnBtn.setTag(true);
//                mLearnBtn.setIcon(R.string.learning_status);
//                mLearnBtn.setTextColor(resources.getColor(R.color.lesson_learn_btn_normal));
//                break;
//            case finished:
//                mLearnBtn.setTag(false);
//                mLearnBtn.setIcon(R.string.learned_status);
//                mLearnBtn.setTextColor(resources.getColor(R.color.lesson_learned_btn_normal));
//                break;
//        }
//    }

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

//    private void showToolsByAnim() {
//        mToolsLayout.measure(0, 0);
//        int height = mToolsLayout.getMeasuredHeight();
//        Log.d(null, "height->" + height);
//        AppUtil.animForHeight(
//                new EduSohoAnimWrap(mToolsLayout), 0, height, 480);
//    }
//
//    private void hieToolsByAnim() {
//        AppUtil.animForHeight(
//                new EduSohoAnimWrap(mToolsLayout), mToolsLayout.getHeight(), 0, 240);
//    }

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
