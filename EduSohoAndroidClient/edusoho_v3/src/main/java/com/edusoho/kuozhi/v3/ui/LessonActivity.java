package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.bal.course.CourseLessonType;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.model.provider.CourseProvider;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.lesson.LiveLessonFragment;
import com.edusoho.kuozhi.v3.ui.fragment.video.LessonVideoPlayerFragment;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.helper.LessonMenuHelper;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
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

    private String mCurrentFragmentName;
    private Class mCurrentFragmentClass;
    private Fragment mCurrentFragment;
    public static final int SHOW_TOOLS = 0001;
    public static final int HIDE_TOOLS = 0002;

    public static final int REQUEST_LEARN = 0011;

    private int mCourseId;
    private int mLessonId;
    private String mLessonType;
    private Bundle fragmentData;
    private boolean mFromCache;

    private LessonItem mLessonItem;
    private Toolbar mToolBar;
    private TextView mToolBarTitle;
    private LessonMenuHelper mLessonMenuHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_layout);
        ActivityUtil.setStatusViewBackgroud(this, getResources().getColor(R.color.textIcons));
        fragmentData = new Bundle();
        initView();
        app.startPlayCacheServer(this);
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
            mToolBar = (Toolbar) findViewById(R.id.toolbar);
            mToolBarTitle = (TextView) findViewById(R.id.tv_toolbar_title);

            setSupportActionBar(mToolBar);
            if (data != null) {
                mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
                mCourseId = data.getIntExtra(Const.COURSE_ID, 0);
            }

            if (mCourseId == 0 || mLessonId == 0) {
                CommonUtil.longToast(mContext, "课程数据错误！");
                setBackMode(BACK, getString(R.string.lesson_default_title));
                return;
            }

            loadLesson();

        } catch (Exception ex) {
            Log.e("lessonActivity", ex.toString());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        changeScreenOrientaion(newConfig.orientation);
        super.onConfigurationChanged(newConfig);
    }

    private void changeScreenOrientaion(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            showActionBar();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ActivityUtil.setStatusViewBackgroud(this, getResources().getColor(R.color.textIcons));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideActionBar();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ActivityUtil.setStatusViewBackgroud(this, getResources().getColor(R.color.transparent));
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

    private void bindListener() {
    }

    private void initRedirectBtn() {
    }

    @Override
    public void setBackMode(String backTitle, String title) {
        super.setBackMode(backTitle, title);
        mToolBarTitle.setText(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_more) {
            mLessonMenuHelper.show(mToolBar, mToolBar.getWidth() - 96, 0);
            return true;
        }
        if (item.getItemId() == R.id.menu_share) {
            share();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lesson_activity_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_more);
        if (menuItem != null) {
            menuItem.setEnabled(mLessonItem != null);
        }
        initMenuPop(menu);
        return true;
    }

    private void initMenuPop(Menu menu) {
        MenuPop menuPop = new MenuPop(getBaseContext(), menu.getItem(0).getActionView());
        mLessonMenuHelper = new LessonMenuHelper(getBaseContext(), mLessonId, mCourseId);
        mLessonMenuHelper.initMenu(menuPop);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menu_share);
        if (menuItem != null) {
            menuItem.setEnabled(mLessonItem != null);
        }
        if ("testpaper".equals(mLessonType)) {
            MenuItem moreItem = menu.findItem(R.id.menu_more);
            if (moreItem != null) {
                moreItem.setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void loadLesson() {
        initLessonIds();
        initRedirectBtn();
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

    private void initLessonIds() {

    }

    private void loadLessonFromNet() {
        final LoadDialog loadDialog = LoadDialog.create(this);
        loadDialog.show();
        RequestUrl requestUrl = app.bindNewUrl(String.format(Const.LESSON, mLessonId), true);
        ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadDialog.dismiss();
                mLessonItem = getLessonResultType(response);
                if (mLessonItem == null) {
                    CommonUtil.longToast(mContext, getResources().getString(R.string.lesson_not_exist));
                    finish();
                    return;
                }
                invalidateOptionsMenu();
                mLessonType = mLessonItem.type;
                setBackMode(BACK, mLessonItem.title);
                if (!mLessonType.equals("testpaper")) {
                    bindListener();
                }
                switchLoadLessonContent(mLessonItem);
                mActivity.supportInvalidateOptionsMenu();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadDialog.dismiss();
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

        LessonItem lessonItem = getLessonResultType(object);
        if (lessonItem == null) {
            finish();
            return;
        }

        mLessonItem = lessonItem;
        mLessonType = mLessonItem.type;
        setBackMode(BACK, mLessonItem.title);
        if (!mLessonType.equals("testpaper")) {
            bindListener();
        }
        switchLoadLessonContent(mLessonItem);
        mActivity.supportInvalidateOptionsMenu();
    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return "localhost";
    }

    private LessonItem getLessonResultType(String object) {
        LessonItem lessonItem = handleJsonValue(
                object, new TypeToken<LessonItem>() {
                });
        if (lessonItem == null) {
            return null;
        }

        CourseLessonType courseLessonType = CourseLessonType.value(lessonItem.type);
        switch (courseLessonType) {
            case LIVE:
                fragmentData.putString(Const.ACTIONBAR_TITLE, lessonItem.title);
                fragmentData.putLong(LiveLessonFragment.STARTTIME, Integer.valueOf(lessonItem.startTime) * 1000L);
                fragmentData.putLong(LiveLessonFragment.ENDTIME, Integer.valueOf(lessonItem.endTime) * 1000L);
                fragmentData.putInt(Const.COURSE_ID, lessonItem.courseId);
                fragmentData.putInt(Const.LESSON_ID, lessonItem.id);
                fragmentData.putString(LiveLessonFragment.SUMMARY, lessonItem.summary);
                fragmentData.putString(LiveLessonFragment.REPLAYSTATUS, lessonItem.replayStatus);
                return lessonItem;
            case PPT:
                LessonItem<LinkedHashMap<String, ArrayList<String>>> pptLesson = lessonItem;
                fragmentData.putString(Const.LESSON_TYPE, "ppt");
                ArrayList<String> pptContent = pptLesson.content.get("resource");
                fragmentData.putStringArrayList(CONTENT, pptContent);
                return pptLesson;
            case TESTPAPER:
                LessonItem<LinkedHashMap> testpaperLesson = lessonItem;
                LinkedHashMap status = testpaperLesson.content;
                fragmentData.putString(Const.LESSON_TYPE, "testpaper");
                fragmentData.putInt(Const.MEDIA_ID, testpaperLesson.mediaId);
                int resultId = AppUtil.parseInt(status.get("resultId").toString());
                fragmentData.putInt(RESULT_ID, resultId);

                fragmentData.putString(Const.STATUS, status.get("status").toString());
                fragmentData.putInt(Const.LESSON_ID, testpaperLesson.id);
                fragmentData.putString(Const.ACTIONBAR_TITLE, testpaperLesson.title);
                return testpaperLesson;
            case DOCUMENT:
                LessonItem<LinkedHashMap<String, String>> documentLessonItem = lessonItem;
                fragmentData.putString(Const.LESSON_TYPE, courseLessonType.name());
                fragmentData.putString(CONTENT, documentLessonItem.content.get("previewUrl"));
                return documentLessonItem;
            case VIDEO:
                //fragmentData.putSerializable(LessonVideoPlayerFragment.PLAY_URI, lessonItem.mediaUri);
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
        fragmentTransaction.commit();

        mCurrentFragment = fragment;
        mCurrentFragmentName = fragmentName;
        mCurrentFragmentClass = fragment.getClass();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.stopPlayCacheServer();

        Bundle bundle = new Bundle();
        bundle.putString("event", "lessonStatusRefresh");
        MessageEngine.getInstance().sendMsg(WebViewActivity.SEND_EVENT, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.resumePlayCacheServer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        app.pausePlayCacheServer();
    }
}
