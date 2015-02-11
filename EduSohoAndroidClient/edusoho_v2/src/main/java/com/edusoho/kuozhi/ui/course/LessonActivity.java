package com.edusoho.kuozhi.ui.course;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.model.LearnStatus;
import com.edusoho.kuozhi.model.Lesson.LessonStatus;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.LessonsResult;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.Testpaper.TestpaperStatus;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.fragment.CourseLearningFragment;
import com.edusoho.kuozhi.ui.fragment.TestpaperLessonFragment;
import com.edusoho.kuozhi.ui.note.NoteReplyActivity;
import com.edusoho.kuozhi.ui.questionDeprecated.QuestionReplyActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;
import com.edusoho.kuozhi.util.SqliteUtil;
import com.edusoho.kuozhi.view.EduSohoTextBtn;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.listener.ResultCallback;
import com.edusoho.plugin.RichTextBox.RichTextBoxFragment;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.util.ArrayList;
import menudrawer.MenuDrawer;

/**
 * Created by howzhi on 14-9-15.
 */
public class LessonActivity extends ActionBarBaseActivity implements MessageEngine.MessageCallback {

    public static final String TAG = "LessonActivity";
    public static final String CONTENT = "content";
    public static final String FROM_CACHE = "from_cache";
    public static final int SHOW_TOOLS = 0001;
    public static final int HIDE_TOOLS = 0002;

    private String mCurrentFragment;
    private Class mCurrentFragmentClass;
    private int mCourseId;
    private int mLessonId;
    private String mLessonType;
    private String mTitle;
    private String mLessonListJson;
    private Bundle fragmentData;
    private boolean mFromCache;
    private boolean mIsLearn;
    private LessonStatus mLessonStatus;

    protected MenuDrawer mMenuDrawer;
    private EduSohoTextBtn mLearnBtn;
    private EduSohoTextBtn mLessonNextBtn;
    private EduSohoTextBtn mLessonPreviousBtn;
    private EduSohoTextBtn mMoreBtn;
    private View mToolsLayout;

    private Handler msgHandler;

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
        msgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SHOW_TOOLS:
                        showToolsByAnim();
                        break;
                    case HIDE_TOOLS:
                        hieToolsByAnim();
                        break;
                }
            }
        };

        fragmentData = new Bundle();
        initView();
        app.registMsgSource(this);
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

    public LessonItem getLessonItem()
    {
        return mLessonItem;
    }

    public int getLessonId() {
        return mLessonId;
    }

    private void changeLessonStatus(boolean isLearn) {
        mLearnBtn.setEnabled(false);
        RequestUrl requestUrl = app.bindUrl(
                isLearn ? Const.LEARN_LESSON : Const.UNLEARN_LESSON, true);
        requestUrl.setParams(new String[]{
                Const.COURSE_ID, mCourseId + "",
                Const.LESSON_ID, mLessonId + ""
        });

        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLearnBtn.setEnabled(true);
                setProgressBarIndeterminateVisibility(false);
                LearnStatus result = parseJsonValue(object, new TypeToken<LearnStatus>() {
                });
                if (result == null) {
                    return;
                }

                setLearnStatus(result);
            }
        });
    }

    private void initView() {
        try {
            Intent data = getIntent();
            mToolsLayout = findViewById(R.id.lesson_tools_layout);
            mLessonNextBtn = (EduSohoTextBtn) findViewById(R.id.lesson_next);
            mLessonPreviousBtn = (EduSohoTextBtn) findViewById(R.id.lesson_previous);
            mMoreBtn = (EduSohoTextBtn) findViewById(R.id.lesson_more_btn);
            mLearnBtn = (EduSohoTextBtn) findViewById(R.id.lesson_learn_btn);

            if (data != null) {
                mCourseId = data.getIntExtra(Const.COURSE_ID, 0);
                mIsLearn = data.getBooleanExtra(Const.IS_LEARN, false);
                mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
                mTitle = data.getStringExtra(Const.ACTIONBAR_TITLE);
                mLessonType = data.getStringExtra(Const.LESSON_TYPE);
                mLessonListJson = data.getStringExtra(Const.LIST_JSON);
                mFromCache = data.getBooleanExtra(FROM_CACHE, false);
            }
            setBackMode(BACK, mTitle);

            //如果mLessonListJson==null,是从笔记本页面跳转，需要获取课程下的所有课时信息
            if (mLessonListJson == null) {
                RequestUrl url = mActivity.app.bindUrl(Const.LESSONS, true);
                url.setParams(new String[]{
                        "courseId", String.valueOf(mCourseId)
                });
                mActivity.ajaxPost(url, new ResultCallback() {
                    @Override
                    public void callback(String url, String object, AjaxStatus ajaxStatus) {
                        Log.d(null, "load LessonListJson");
                        mLessonListJson = object;
                        clearLessonResult();
                        initLessonResult();
                        initRedirectBtn();
                    }
                });
            } else {
                clearLessonResult();
                initLessonResult();
                initRedirectBtn();
            }

            if (mCourseId == 0 || mLessonId == 0) {
                longToast("课程数据错误！");
                return;
            }

            if (mFromCache) {
                Log.d(TAG, "mFromCache");
                loadLessonFromCache(mLessonId);
                return;
            }
            loadLesson(mLessonId);
        } catch (Exception ex) {
            Log.e("lessonActivity", ex.toString());
        }
    }

    private void initRedirectBtn() {
        if (mNextLessonItem == null) {
            mLessonNextBtn.setEnabled(false);
        } else {
            mLessonNextBtn.setEnabled(true);
        }
        if (mPreviousLessonItem == null) {
            mLessonPreviousBtn.setEnabled(false);
        } else {
            mLessonPreviousBtn.setEnabled(true);
        }
    }

    /**
     * 去掉mLessonListJson中不是lesson的item
     *
     * @return
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

    private void goToAnotherLesson(LessonItem lessonItem) {
        CourseLessonType courseLessonType = CourseLessonType.value(lessonItem.type);
        if (courseLessonType == CourseLessonType.VIDEO) {
            int offlineType = app.config.offlineType;
            Log.d(null, "offlineType-> " + offlineType);
            if (offlineType == Const.NET_WIFI && !app.getNetIsWiFi()) {
                AppUtil.showAlertDialog(
                        mActivity,
                        "当前设置视频课时观看、下载为wifi模式!\n模式可以在设置里修改。"
                );
                return;
            }
        }

        mLessonId = lessonItem.id;
        mLessonType = lessonItem.type;
        initLessonResult();
        initRedirectBtn();
        hieToolsByAnim();
        setTitle(lessonItem.title);
        loadLesson(mLessonId);
    }

    private void showMoreBtn(View parent) {
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.lesson_tools_more_layout, null);
        View layoutQuestion = contentView.findViewById(R.id.tv_question);
        View layoutProfile = contentView.findViewById(R.id.tv_profile);
        View layoutNote = contentView.findViewById(R.id.tv_note);
        View layoutAddQuestion = contentView.findViewById(R.id.tv_add_question);

        layoutQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLessonQuestionList();
            }
        });

        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLessonResource();
            }
        });

        layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle bundle = new Bundle();
                bundle.putString(RichTextBoxFragment.HIT, "添加笔记");
                bundle.putString(Const.ACTIONBAR_TITLE, mTitle);
                bundle.putInt(NoteReplyActivity.TYPE, NoteReplyActivity.ADD);
                bundle.putString(Const.LESSON_ID, String.valueOf(mLessonId));
                bundle.putString(Const.COURSE_ID, String.valueOf(mCourseId));
                bundle.putString(Const.NORMAL_CONTENT, "");

                app.mEngine.runNormalPluginForResult(
                        "NoteReplyActivity", mActivity, REQUEST_NOTE, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtras(bundle);
                            }
                        });
            }
        });

        layoutAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle bundle = new Bundle();
                bundle.putString(RichTextBoxFragment.HIT, "添加问题");
                bundle.putString(Const.ACTIONBAR_TITLE, "添加问答");
                bundle.putInt(Const.REQUEST_CODE, Const.EDIT_QUESTION);
                bundle.putString(Const.LESSON_ID, String.valueOf(mLessonId));
                bundle.putString(Const.COURSE_ID, String.valueOf(mCourseId));
                bundle.putString(QuestionReplyActivity.TYPE, "question");
                bundle.putString(Const.QUESTION_TITLE, "");
                bundle.putString(QuestionReplyActivity.ACTION, "add");
                bundle.putString(Const.QUESTION_CONTENT, "");
                bundle.putString("empty_text","暂无提问");
                bundle.putInt("empty_icon",R.drawable.icon_question);

                app.mEngine.runNormalPluginForResult(
                        "QuestionReplyActivity", mActivity, REQUEST_QUESTION, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtras(bundle);
                            }
                        });
            }
        });

        PopupWindow popupWindow = new PopupWindow(
                contentView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth((int)(parent.getWidth() * 1.5f));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        popupWindow.showAtLocation(
                parent, Gravity.BOTTOM, parent.getWidth() * 3 / 2, parent.getHeight());
    }

    /**
     * 获取课时是否已学状态
     */
    private void loadLessonStatus() {
        RequestUrl requestUrl = app.bindUrl(Const.LESSON_STATUS, true);
        requestUrl.setParams(new String[]{
                "courseId", mCourseId + "",
                "lessonId", mLessonId + ""
        });

        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLessonStatus = parseJsonValue(
                        object, new TypeToken<LessonStatus>() {
                        });
                if (mLessonStatus.learnStatus != LearnStatus.finished) {
                    mLessonStatus.learnStatus = LearnStatus.learning;
                }
                mToolsLayout.setVisibility(View.VISIBLE);
                showToolsByAnim();
                setLearnStatus(mLessonStatus == null ? LearnStatus.learning : mLessonStatus.learnStatus);
            }
        });
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

    private void showLessonResource() {
        if (mLessonStatus == null || !mLessonStatus.hasMaterial) {
            longToast("该课时暂无资料！");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Const.COURSE_ID, mCourseId);
        bundle.putInt(Const.LESSON_ID, mLessonId);
        app.mEngine.runNormalPluginWithBundle(
                "LessonResourceActivity", mActivity, bundle);
    }

    private void showLessonQuestionList() {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.LESSON_ID, mLessonId);
        bundle.putString(Const.LESSON_NAME, mTitle);
        app.mEngine.runNormalPluginWithBundle("LessonQuestionActivity", mActivity, bundle);
    }

    private void bindListener() {
        mLearnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLearn;
                if (mLearnBtn.getTag() == null) {
                    isLearn = true;
                } else {
                    isLearn = (Boolean) mLearnBtn.getTag();
                }
                changeLessonStatus(isLearn);
            }
        });

        mMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreBtn(view);
            }
        });

        mLessonNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNextLessonItem != null) {
                    goToAnotherLesson(mNextLessonItem);
                }
            }
        });

        mLessonPreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPreviousLessonItem != null) {
                    goToAnotherLesson(mPreviousLessonItem);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void loadLesson(int lessonId)
    {
        int userId = app.loginUser == null ? 0 : app.loginUser.id;
        M3U8DbModle m3U8DbModle = M3U8Uitl.queryM3U8Modle(
                mContext, userId, lessonId, app.domain, M3U8Uitl.FINISH);
        if (m3U8DbModle != null) {
            loadLessonFromCache(lessonId);
        } else {
            loadLessonFromNet(lessonId);
        }
    }

    private void loadLessonFromNet(int lessonId) {
        Log.d(TAG, "loadLessonFromNet " + lessonId);
        mFromCache = false;
        RequestUrl requestUrl = app.bindUrl(Const.COURSELESSON, true);
        requestUrl.setParams(new String[]{
                "courseId", mCourseId + "",
                "lessonId", lessonId + ""
        });

        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                LessonItem lessonItem = getLessonResultType(mLessonType, object);
                if (lessonItem == null) {
                    return;
                }
                if (!mLessonType.equals("testpaper") && mIsLearn) {
                    loadLessonStatus();
                    bindListener();
                }

                switchLoadLessonContent(lessonItem);
            }
        });
    }

    private void loadLessonFromCache(int lessonId) {
        Log.d(TAG, "loadLessonFromCache " + lessonId);
        mFromCache = true;
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
        if (!mLessonType.equals("testpaper") && mIsLearn) {
            loadLessonStatus();
            bindListener();
        }
        mLessonItem = lessonItem;
        switchLoadLessonContent(lessonItem);
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
                fragmentData.putInt(TestpaperLessonFragment.RESULT_ID, status.resultId);
                fragmentData.putString(Const.STATUS, status.status);
                fragmentData.putInt(Const.LESSON_ID, testpaperLesson.id);
                fragmentData.putString(Const.ACTIONBAR_TITLE, testpaperLesson.title);
                return testpaperLesson;
            case VIDEO:
            case AUDIO:
            case TEXT:
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
        return null;
    }

    /**
     * 获取本地视频列表
     * @param lessonId
     * @return
     */
    private File getLocalLesson(int lessonId)
    {
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

    private void setLearnStatus(LearnStatus learnStatus) {
        Resources resources = getResources();
        switch (learnStatus) {
            case learning:
                mLearnBtn.setTag(true);
                mLearnBtn.setIcon(R.string.learning_status);
                mLearnBtn.setTextColor(resources.getColor(R.color.lesson_learn_btn_normal));
                break;
            case finished:
                mLearnBtn.setTag(false);
                mLearnBtn.setIcon(R.string.learned_status);
                mLearnBtn.setTextColor(resources.getColor(R.color.lesson_learned_btn_normal));
                break;
        }
    }

    private void switchLoadLessonContent(LessonItem lessonItem) {
        CourseLessonType lessonType = CourseLessonType.value(lessonItem.type);
        if (Const.NETEASE_OPEN_COURSE.equals(lessonItem.mediaSource)) {
            mActivity.longToast("客户端暂不支持网易云视频");
            return;
        }

        if (lessonType == CourseLessonType.VIDEO
                && AppUtil.inArray(lessonItem.mediaSource, new String[]{"youku", "tudou"})) {
            loadLessonFragment("WebVideoLessonFragment");
            return;
        }

        StringBuilder stringBuilder = lessonType.getType();
        stringBuilder.append("LessonFragment");
        loadLessonFragment(stringBuilder.toString());
    }

    private void showToolsByAnim() {
        mToolsLayout.measure(0, 0);
        int height = mToolsLayout.getMeasuredHeight();
        Log.d(null, "height->" + height);
        AppUtil.animForHeight(
                new EdusohoAnimWrap(mToolsLayout), 0, height, 480);
    }

    private void hieToolsByAnim() {
        AppUtil.animForHeight(
                new EdusohoAnimWrap(mToolsLayout), mToolsLayout.getHeight(), 0, 240);
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
        app.sendMsgToTarget(
                CourseLearningFragment.UPDATE_LEARN_STATUS, null, CourseLearningFragment.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NOTE && resultCode == Const.OK) {
            longToast("添加笔记成功!");
            return;
        }

        if (requestCode == Const.EDIT_QUESTION && resultCode == Const.OK) {
            longToast("添加问题成功!");
            return;
        }
    }
}
