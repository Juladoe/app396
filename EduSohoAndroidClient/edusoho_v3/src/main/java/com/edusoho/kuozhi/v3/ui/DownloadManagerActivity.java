package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.DownloadingAdapter;
import com.edusoho.kuozhi.v3.broadcast.DownloadStatusReceiver;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.model.bal.Lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.DownloadingFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import cn.trinea.android.common.util.FileUtils;
import extensions.PagerSlidingTabStrip;

/**
 * Created by JesseHuang on 15/6/22.
 * 下载管理
 */
public class DownloadManagerActivity extends ActionBarBaseActivity {
    public static final String[] DOWNLOAD_FRAGMENTS = {"DownloadedFragment", "DownloadingFragment"};
    public static final String[] DOWNLOAD_TITLES = {"已下载", "下载中"};

    private PagerSlidingTabStrip mPagerTab;
    private ViewPager mViewPagers;
    private TextView mDeviceSpaceInfo;
    private ProgressBar pbDownloadDeviceInfo;

    private final Handler mHandler = new Handler();
    protected DownloadStatusReceiver mDownLoadStatusReceiver;
    private Drawable oldBackground = null;
    private int currentColor = R.color.action_bar_bg;
    protected SqliteUtil mSqliteUtil;
    protected String host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDownLoadStatusReceiver == null) {
            mDownLoadStatusReceiver = new DownloadStatusReceiver(mStatusCallback);
            Log.d(null, "register DownLoadStatusReceiver");
            registerReceiver(
                    mDownLoadStatusReceiver, new IntentFilter(DownloadStatusReceiver.ACTION));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDownLoadStatusReceiver != null) {
            unregisterReceiver(mDownLoadStatusReceiver);
            Log.d(null, "unregister DownLoadStatusReceiver");
        }
    }

    private DownloadStatusReceiver.StatusCallback mStatusCallback = new DownloadStatusReceiver.StatusCallback() {
        @Override
        public void invoke(Intent intent) {
            int lessonId = intent.getIntExtra(Const.LESSON_ID, 0);
            Bundle bundle = new Bundle();
            bundle.putInt(Const.LESSON_ID, lessonId);
            app.sendMessage(DownloadingFragment.UPDATE, bundle);
        }
    };

    private void initView() {
        setBackMode(BACK, "已下载课时");
        mPagerTab = (PagerSlidingTabStrip) findViewById(R.id.tab_download);
        pbDownloadDeviceInfo = (ProgressBar) findViewById(R.id.pb_download_device_info);
        mViewPagers = (ViewPager) findViewById(R.id.viewpager_download);
        mDeviceSpaceInfo = (TextView) findViewById(R.id.download_device_info);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), DOWNLOAD_TITLES, DOWNLOAD_FRAGMENTS);
        mViewPagers.setAdapter(myPagerAdapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mViewPagers.setPageMargin(pageMargin);
        mPagerTab.setViewPager(mViewPagers);

        mSqliteUtil = SqliteUtil.getUtil(mContext);

        changeColor(currentColor);
        setPageItem(DOWNLOAD_FRAGMENTS[0]);
        mViewPagers.setOffscreenPageLimit(DOWNLOAD_FRAGMENTS.length);
        loadDeviceSpaceInfo();

        //网校域名
        Uri hostUri = Uri.parse(app.host);
        if (hostUri != null) {
            this.host = hostUri.getHost();
        }
    }

    private void loadDeviceSpaceInfo() {
        long[] size = getDeviceSpaceSize();
        double percent = (double) size[1] / (double) size[0];
        pbDownloadDeviceInfo.setProgress((int) ((1 - percent) * 100));
        mDeviceSpaceInfo.setText(String.format("共%s, 剩余空间%s", AppUtil.formatSize(size[0]), AppUtil.formatSize(size[1])));
    }

    private long[] getDeviceSpaceSize() {
        long total = 0;
        long free = 0;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File sdcard = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(sdcard.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            long blockCount = stat.getBlockCount();

            total = blockCount * blockSize;
            free = blockSize * availableBlocks;
        }
        return new long[]{total, free};
    }

    private void changeColor(int newColor) {
        mPagerTab.setIndicatorColor(newColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = new ColorDrawable(0);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});

            if (oldBackground == null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                }

            } else {
                TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                }

                td.startTransition(200);
            }

            oldBackground = ld;
        }

        currentColor = newColor;
    }

    private void setPageItem(String name) {
        for (int i = 0; i < DOWNLOAD_FRAGMENTS.length; i++) {
            if (DOWNLOAD_FRAGMENTS.equals(name)) {
                mViewPagers.setCurrentItem(i);
                return;
            }
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] mTitles;
        private String[] mLists;

        public MyPagerAdapter(FragmentManager fm, String[] titles, String[] list) {
            super(fm);
            mTitles = titles;
            mLists = list;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = app.mEngine.runPluginWithFragment(mLists[i], mActivity, new PluginFragmentCallback() {
                @Override
                public void setArguments(Bundle bundle) {

                }
            });
            return fragment;
        }

        @Override
        public int getCount() {
            return mLists.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            //getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            mHandler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            mHandler.removeCallbacks(what);
        }
    };

    protected void updateLocalCourseList(int type, DownloadingAdapter adapter) {
        LocalCourseModel model = getLocalCourseList(type, null, null);
        adapter.updateLocalData(model.mLocalCourses, model.mLocalLessons);
    }

    public LocalCourseModel getLocalCourseList(
            int isFinish, int[] courseIds, int[] lessonIds) {
        LocalCourseModel model = new LocalCourseModel();

        final ArrayList<LessonItem> lessonItems = new ArrayList<LessonItem>();
        SqliteUtil.QueryParser<ArrayList<LessonItem>> queryParser;
        queryParser = new SqliteUtil.QueryParser<ArrayList<LessonItem>>() {
            @Override
            public ArrayList<LessonItem> parse(Cursor cursor) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                LessonItem item = mActivity.parseJsonValue(
                        value, new TypeToken<LessonItem>() {
                        });
                lessonItems.add(item);
                return lessonItems;
            }
        };

        StringBuffer lessonIdQuery = new StringBuffer();
        if (lessonIds != null) {
            lessonIdQuery = new StringBuffer(" and key in (");
            for (int id : lessonIds) {
                lessonIdQuery.append(id).append(",");
            }
            if (lessonIdQuery.length() > 1) {
                lessonIdQuery.deleteCharAt(lessonIdQuery.length() - 1);
            }
            lessonIdQuery.append(")");
        }
        mSqliteUtil.query(
                queryParser,
                "select * from data_cache where type=?" + lessonIdQuery.toString(),
                Const.CACHE_LESSON_TYPE
        );

        if (lessonItems != null) {
            int[] ids = getLessonIds(lessonItems);
            Collections.sort(lessonItems, new Comparator<LessonItem>() {
                @Override
                public int compare(LessonItem lhs, LessonItem rhs) {
                    if (lhs.courseId > rhs.courseId) {
                        return 1;
                    } else if (lhs.courseId == rhs.courseId) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });

            model.m3U8DbModles = M3U8Util.getM3U8ModelList(
                    mContext, ids, app.loginUser.id, this.host, isFinish);
            for (LessonItem lessonItem : lessonItems) {
                if (model.m3U8DbModles.indexOfKey(lessonItem.id) < 0) {
                    continue;
                }
                if (!model.mLocalLessons.containsKey(lessonItem.courseId)) {
                    if (courseIds == null || filterCourseId(lessonItem.courseId, courseIds)) {
                        model.mLocalCourses.add(getLocalCourse(lessonItem.courseId));
                        model.mLocalLessons.put(lessonItem.courseId, new ArrayList<LessonItem>());
                    }
                }

                ArrayList<LessonItem> lessons = model.mLocalLessons.get(lessonItem.courseId);
                if (lessons != null) {
                    lessons.add(lessonItem);
                }
            }

            filterLessons(isFinish, lessonItems, model.m3U8DbModles);
        } else {
            model.mLocalCourses.clear();
            model.mLocalLessons.clear();
        }
        return model;
    }

    private void filterLessons(
            int isFinish, ArrayList<LessonItem> lessonItems, SparseArray<M3U8DbModel> m3U8Models) {
        Iterator<LessonItem> iterator = lessonItems.iterator();
        while (iterator.hasNext()) {
            LessonItem item = iterator.next();
            M3U8DbModel m3U8DbModel = m3U8Models.get(item.id);
            if (m3U8DbModel != null && m3U8DbModel.finish != isFinish) {
                iterator.remove();
            }
        }
    }

    private Course getLocalCourse(int courseId) {
        SqliteUtil.QueryParser<Course> queryParser;
        queryParser = new SqliteUtil.QueryParser<Course>() {
            @Override
            public Course parse(Cursor cursor) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                Course course = mActivity.parseJsonValue(
                        value, new TypeToken<Course>() {
                        }
                );
                return course;
            }

            @Override
            public boolean isSingle() {
                return true;
            }
        };

        Course course = mSqliteUtil.query(
                queryParser,
                "select * from data_cache where type=? and key=?",
                Const.CACHE_COURSE_TYPE,
                "course-" + courseId
        );
        return course;
    }

    private boolean filterCourseId(int courseId, int[] courseIds) {
        for (int id : courseIds) {
            if (courseId == id) {
                return true;
            }
        }
        return false;
    }

    private int[] getLessonIds(ArrayList<LessonItem> lessons) {
        int index = 0;
        int[] ids = new int[lessons.size()];
        for (LessonItem lessonItem : lessons) {
            ids[index++] = lessonItem.id;
        }
        return ids;
    }

    public void clearLocalCache(ArrayList<Integer> ids) {
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(mContext);

        String m3u8LessonIds = coverM3U8Ids(ids);
        String cacheLessonIds = coverLessonIds(ids);
        sqliteUtil.execSQL(String.format(
                        "delete from data_cache where type='%s' and key in %s",
                        Const.CACHE_LESSON_TYPE,
                        cacheLessonIds.toString()
                )
        );
        sqliteUtil.execSQL(String.format(
                        "delete from data_m3u8 where host='%s' and lessonId in %s",
                        app.domain,
                        m3u8LessonIds.toString())
        );

        M3U8DownService service = M3U8DownService.getService();
        if (service != null) {
            service.cancelAllDownloadTask();
            for (int id : ids) {
                service.cancelDownloadTask(id);
            }
        }

        clearVideoCache(ids);
    }

    /**
     * 删除本地视频
     *
     * @param ids
     */
    private void clearVideoCache(ArrayList<Integer> ids) {
        File workSpace = EdusohoApp.getWorkSpace();
        if (workSpace == null) {
            return;
        }
        File videosDir = new File(workSpace, "videos/" + app.loginUser.id + "/" + app.domain);
        for (int id : ids) {
            FileUtils.deleteFile(new File(videosDir, String.valueOf(id)).getAbsolutePath());
        }
    }

    private String coverM3U8Ids(ArrayList<Integer> ids) {
        StringBuffer idsStr = new StringBuffer("(");
        for (int id : ids) {
            idsStr.append(id).append(",");
        }
        if (idsStr.length() > 1) {
            idsStr.deleteCharAt(idsStr.length() - 1);
        }
        idsStr.append(")");

        return idsStr.toString();
    }

    private String coverLessonIds(ArrayList<Integer> ids) {
        StringBuffer idsStr = new StringBuffer("(");
        for (int id : ids) {
            idsStr.append("'lesson-").append(id).append("',");
        }
        if (idsStr.length() > 1) {
            idsStr.deleteCharAt(idsStr.length() - 1);
        }
        idsStr.append(")");

        return idsStr.toString();
    }

    public class LocalCourseModel {
        public ArrayList<Course> mLocalCourses;
        public SparseArray<M3U8DbModel> m3U8DbModles;
        public HashMap<Integer, ArrayList<LessonItem>> mLocalLessons;

        public LocalCourseModel() {
            mLocalCourses = new ArrayList<>();
            mLocalLessons = new HashMap<>();
        }
    }
}
