package com.edusoho.kuozhi.ui.course;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.entity.LearnStatus;
import com.edusoho.kuozhi.model.Lesson.LessonStatus;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.Testpaper.TestpaperStatus;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.fragment.CourseLearningFragment;
import com.edusoho.kuozhi.ui.fragment.TestpaperLessonFragment;
import com.edusoho.kuozhi.ui.note.NoteReplyActivity;
import com.edusoho.kuozhi.ui.question.QuestionReplyActivity;
import com.edusoho.kuozhi.ui.widget.CourseDetailsLessonWidget;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EduSohoTextBtn;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.kuozhi.view.EdusohoButton;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by howzhi on 14-9-15.
 */
public class LessonActivity extends ActionBarBaseActivity implements MessageEngine.MessageCallback{

    public static final String TAG = "LessonActivity";
    public static final String CONTENT = "content";
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
    private int mIsFree;
    private boolean mIsLearn;
    private LessonStatus mLessonStatus;

    protected MenuDrawer mMenuDrawer;
    private CourseDetailsLessonWidget mCourseLessonView;
    private EduSohoTextBtn mLearnBtn;
    private EduSohoTextBtn mNoteBtn;
    private EduSohoTextBtn mQuestionBtn;
    private EduSohoTextBtn mMoreBtn;
    private View mToolsLayout;

    private Handler msgHandler;

    private static final int REQUEST_NOTE = 0010;
    private static final int REQUEST_QUESTION = 0020;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        initMenuDrawer();
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

    private void initMenuDrawer()
    {
        mMenuDrawer = MenuDrawer.attach(
                mActivity, MenuDrawer.Type.OVERLAY, Position.RIGHT, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.lesson_layout);
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);

        mMenuDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == MenuDrawer.STATE_OPEN) {
                    mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
                } else if (newState == MenuDrawer.STATE_CLOSED) {
                    mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
            }
        });
    }

    public int getCourseId()
    {
        return mCourseId;
    }

    public int getLessonId()
    {
        return mLessonId;
    }

    private void changeLessonStatus(boolean isLearn)
    {
        mLearnBtn.setEnabled(false);
        setProgressBarIndeterminateVisibility(true);
        RequestUrl requestUrl = app.bindUrl(
                isLearn ? Const.LEARN_LESSON : Const.UNLEARN_LESSON, true);
        requestUrl.setParams(new String[] {
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

                mCourseLessonView.updateLessonStatus(mLessonId, result);
                setLearnStatus(result);
            }
        });
    }

    private void loadLessonList()
    {
        mCourseLessonView = (CourseDetailsLessonWidget) LayoutInflater.from(mActivity).inflate(
                R.layout.course_details_lesson_content, null);
        mMenuDrawer.setMenuView(mCourseLessonView);

        mCourseLessonView.initLessonFromJson(mActivity, mLessonListJson);
        mCourseLessonView.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mMenuDrawer.closeMenu();
                LessonItem lesson = (LessonItem) adapterView.getItemAtPosition(i);
                hieToolsByAnim();
                mLessonId = lesson.id;
                mLessonType = lesson.type;
                setTitle(lesson.title);
                loadLesson(mLessonId);
                if (!mLessonType.equals("testpaper") && mIsLearn) {
                    Log.d(null, "load status->");
                    loadLessonStatus();
                }
            }
        });
    }

    private void initView()
    {
        Intent data = getIntent();
        if (data != null) {
            mCourseId = data.getIntExtra(Const.COURSE_ID, 0);
            mIsFree = data.getIntExtra(Const.FREE, 0);
            mIsLearn = data.getBooleanExtra(Const.IS_LEARN, false);
            mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
            mTitle = data.getStringExtra(Const.ACTIONBAT_TITLE);
            mLessonType = data.getStringExtra(Const.LESSON_TYPE);
            mLessonListJson = data.getStringExtra(Const.LIST_JSON);
        }

        setBackMode(BACK, mTitle);
        mToolsLayout = findViewById(R.id.lesson_tools_layout);
        mNoteBtn = (EduSohoTextBtn) findViewById(R.id.lesson_note_btn);
        mQuestionBtn = (EduSohoTextBtn) findViewById(R.id.lesson_question_btn);
        mMoreBtn = (EduSohoTextBtn) findViewById(R.id.lesson_more_btn);
        mLearnBtn = (EduSohoTextBtn) findViewById(R.id.lesson_learn_btn);

        loadLessonList();
        if (mCourseId == 0 || mLessonId == 0) {
            longToast("课程数据错误！");
            return;
        }

        loadLesson(mLessonId);

        if (!mLessonType.equals("testpaper") && mIsLearn) {
            Log.d(null, "load status->");
            loadLessonStatus();
        }

        bindListener();
    }

    private void showMoreBtn(View parent)
    {
        ListView contentView = (ListView) LayoutInflater.from(mContext).inflate(
                R.layout.lesson_tools_more_layout, null);
        ArrayAdapter<String> moreItemAdapter = new ArrayAdapter<String>(
                mContext, R.layout.lesson_tools_more_list_item, new String[]{ "资料" });
        contentView.setAdapter(moreItemAdapter);
        contentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        showLessonResource();
                        break;
                }
            }
        });

        PopupWindow popupWindow = new PopupWindow(
                contentView, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(parent.getWidth());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        popupWindow.showAtLocation(
                parent, Gravity.TOP, location[0], location[1] - (int)(mToolsLayout.getHeight() * 1.5f));
    }

    private void loadLessonStatus()
    {
        RequestUrl requestUrl = app.bindUrl(Const.LESSON_STATUS, true);
        requestUrl.setParams(new String[] {
                "courseId", mCourseId + "",
                "lessonId", mLessonId + ""
        });

        setProgressBarIndeterminateVisibility(true);
        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
                mLessonStatus = parseJsonValue(
                        object, new TypeToken<LessonStatus>(){});

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

    private void showLessonResource()
    {
        if (mLessonStatus == null || !mLessonStatus.hasMaterial)  {
            longToast("该课时暂无资料！");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Const.COURSE_ID, mCourseId);
        bundle.putInt(Const.LESSON_ID, mLessonId);
        app.mEngine.runNormalPluginWithBundle(
                "LessonResourceActivity", mActivity, bundle);
    }

    private void bindListener()
    {
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

        mQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bundle bundle = new Bundle();
                bundle.putString(Const.ACTIONBAT_TITLE, mTitle);
                bundle.putInt(Const.REQUEST_CODE, Const.EDIT_QUESTION);
                bundle.putString(Const.LESSON_ID, String.valueOf(mLessonId));
                bundle.putString(Const.COURSE_ID, String.valueOf(mCourseId));
                bundle.putString(QuestionReplyActivity.TYPE, "question");
                bundle.putString(Const.QUESTION_TITLE, "");
                bundle.putString(QuestionReplyActivity.ACTION, "add");
                bundle.putString(Const.QUESTION_CONTENT, "");

                app.mEngine.runNormalPluginForResult(
                        "QuestionReplyActivity", mActivity, REQUEST_QUESTION, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtras(bundle);
                    }
                });
            }
        });

        mNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bundle bundle = new Bundle();
                bundle.putString(Const.ACTIONBAT_TITLE, mTitle);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.lesson_menu_list) {
            mMenuDrawer.openMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsLearn) {
            getMenuInflater().inflate(R.menu.lesson_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void loadLesson(int lessonId)
    {
        RequestUrl requestUrl = app.bindUrl(Const.COURSELESSON, true);
        requestUrl.setParams(new String[] {
                "courseId", mCourseId + "",
                "lessonId", lessonId + ""
        });

        setProgressBarIndeterminateVisibility(true);
        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
                LessonItem lessonItem = getLessonResultType(mLessonType, object);
                if (lessonItem == null) {
                    return;
                }

                switchLoadLessonContent(lessonItem);
            }
        });
    }

    private LessonItem getLessonResultType(String lessonType, String object)
    {
        CourseLessonType courseLessonType = CourseLessonType.value(lessonType);
        LessonItem<String> normalLesson = null;
        switch (courseLessonType) {
            case PPT:
                LessonItem<ArrayList<String>> pptLesson = parseJsonValue(
                        object, new TypeToken<LessonItem<ArrayList<String>>>(){});
                fragmentData.putString(Const.LESSON_TYPE, "ppt");
                fragmentData.putStringArrayList(CONTENT, pptLesson.content);
                return pptLesson;
            case TESTPAPER:
                LessonItem<TestpaperStatus> testpaperLesson = parseJsonValue(
                        object, new TypeToken<LessonItem<TestpaperStatus>>(){});
                TestpaperStatus status = testpaperLesson.content;
                fragmentData.putString(Const.LESSON_TYPE, "testpaper");
                fragmentData.putInt(Const.MEDIA_ID, testpaperLesson.mediaId);
                fragmentData.putInt(TestpaperLessonFragment.RESULT_ID, status.resultId);
                fragmentData.putString(Const.STATUS, status.status);
                fragmentData.putInt(Const.LESSON_ID, testpaperLesson.id);
                fragmentData.putString(Const.ACTIONBAT_TITLE, testpaperLesson.title);
                return testpaperLesson;
            case VIDEO:
            case AUDIO:
            case TEXT:
                normalLesson = parseJsonValue(
                        object, new TypeToken<LessonItem<String>>(){});
                fragmentData.putString(Const.LESSON_TYPE, courseLessonType.name());
                fragmentData.putString(CONTENT, normalLesson.content);
                if (courseLessonType == CourseLessonType.VIDEO
                        || courseLessonType == CourseLessonType.AUDIO) {
                    //String proxyUrl = "http://localhost:5820/" + normalLesson.mediaUri;
                    fragmentData.putString(Const.MEDIA_URL, normalLesson.mediaUri);
                    fragmentData.putString(Const.HEAD_URL, normalLesson.headUrl);
                    fragmentData.putString(Const.MEDIA_SOURCE, normalLesson.mediaSource);
                    fragmentData.putInt(Const.LESSON_ID, normalLesson.id);
                    fragmentData.putInt(Const.COURSE_ID, normalLesson.courseId);
                }
                return normalLesson;
        }
        return null;
    }

    private void setLearnStatus(LearnStatus learnStatus)
    {
        Resources resources = getResources();
        switch(learnStatus) {
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

    private void switchLoadLessonContent(LessonItem lessonItem)
    {
        CourseLessonType lessonType = CourseLessonType.value(lessonItem.type);
        if (lessonType == CourseLessonType.VIDEO
                && AppUtil.inArray(lessonItem.mediaSource, new String[]{ "youku", "tudou" })) {
            loadLessonFragment("WebVideoLessonFragment");
            return;
        }

        if (Const.NETEASE_OPEN_COURSE.equals(lessonItem.mediaSource)) {
            mActivity.longToast("客户端暂不支持网易云视频");
            return;
        }

        StringBuilder stringBuilder = lessonType.getType();
        stringBuilder.append("LessonFragment");
        loadLessonFragment(stringBuilder.toString());
    }

    private void showToolsByAnim()
    {
        mToolsLayout.measure(0, 0);
        int height = mToolsLayout.getMeasuredHeight();
        Log.d(null, "height->" + height);
        AppUtil.animForHeight(
                new EdusohoAnimWrap(mToolsLayout), 0, height, 480);
    }

    private void hieToolsByAnim()
    {
        AppUtil.animForHeight(
                new EdusohoAnimWrap(mToolsLayout), mToolsLayout.getHeight(), 0, 240);
    }

    private void loadLessonFragment(String fragmentName)
    {
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
