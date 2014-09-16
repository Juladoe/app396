package com.edusoho.kuozhi.ui.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.entity.LessonsResult;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.CourseDetailsLessonWidget;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.listener.LessonItemClickListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

/**
 * Created by howzhi on 14-9-15.
 */
public class LessonActivity extends ActionBarBaseActivity {

    public static final String TAG = "LessonActivity";
    public static final String CONTENT = "content";

    private String mCurrentFragment;
    private Class mCurrentFragmentClass;
    private int mCourseId;
    private int mLessonId;
    private String mTitle;
    private String mLessonListJson;

    protected MenuDrawer mMenuDrawer;
    private LessonItem mLesson;
    private CourseDetailsLessonWidget mCourseLessonView;
    private Button mResourceBtn;
    private Button mLearnBtn;
    private View mToolsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMenuDrawer();
        initView();
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

    private void loadLessonList()
    {
        Log.d(null, "mLessonListJson->" + mLessonListJson);
        mCourseLessonView = (CourseDetailsLessonWidget) LayoutInflater.from(mActivity).inflate(
                R.layout.course_details_lesson_content, null);
        mMenuDrawer.setMenuView(mCourseLessonView);

        mCourseLessonView.initLessonFromJson(mActivity, mLessonListJson);
        mCourseLessonView.setItemClickListener(
                new LessonItemClickListener(mActivity, mCourseLessonView.getLessonListJson()));
    }

    private void initView()
    {
        Intent data = getIntent();
        if (data != null) {
            mCourseId = data.getIntExtra(Const.COURSE_ID, 0);
            mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
            mTitle = data.getStringExtra(Const.ACTIONBAT_TITLE);
            mLessonListJson = data.getStringExtra(Const.LIST_JSON);
        }

        setBackMode(BACK, mTitle);
        mToolsLayout = findViewById(R.id.lesson_tools_layout);
        mResourceBtn = (Button) findViewById(R.id.lesson_resource_btn);
        mLearnBtn = (Button) findViewById(R.id.lesson_learn_btn);

        loadLessonList();
        if (mCourseId == 0 || mLessonId == 0) {
            longToast("课程数据错误！");
            return;
        }

        loadLesson();
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
        getMenuInflater().inflate(R.menu.lesson_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void loadLesson()
    {
        RequestUrl requestUrl = app.bindUrl(Const.COURSELESSON, true);
        requestUrl.setParams(new String[] {
                "courseId", mCourseId + "",
                "lessonId", mLessonId + ""
        });

        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLesson = parseJsonValue(
                        object, new TypeToken<LessonItem>(){});
                if (mLesson == null) {
                    return;
                }
                showToolsByAnim();
                CourseLessonType lessonType = CourseLessonType.value(mLesson.type);
                switch (lessonType) {
                    case TEXT:
                        loadLessonFragment("TextLessonFragment");
                        break;
                }

            }
        });
    }

    private void showToolsByAnim()
    {
        mToolsLayout.measure(0, 0);
        int height = mToolsLayout.getMeasuredHeight();
        AppUtil.animForHeight(
                new EdusohoAnimWrap(mToolsLayout), 0, height, 480);
    }

    private void loadLessonFragment(String fragmentName)
    {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = app.mEngine.runPluginWithFragment(
                fragmentName, mActivity, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putString(CONTENT, mLesson.content);
            }
        });
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();

        mCurrentFragment = fragmentName;
        mCurrentFragmentClass = fragment.getClass();
    }
}
