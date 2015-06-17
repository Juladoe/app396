package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.DownloadingAdapter;
import com.edusoho.kuozhi.v3.broadcast.DownloadStatusReceiver;
import com.edusoho.kuozhi.v3.model.bal.Lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.kuozhi.v3.view.EduSohoAnimWrap;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by JesseHuang on 15/6/16.
 * 我的下载
 */
public class DownloadManagerActivity extends ActionBarBaseActivity {
    private TextView mDeviceSpaceInfo;
    private TabHost tabHost;
    protected SqliteUtil mSqliteUtil;
    private View mToolsLayout;
    private ExpandableListView mDownloadingList;
    private ExpandableListView mDownloadedList;
    private DownloadingAdapter mDownloadedAdapter;
    private DownloadingAdapter mDownloadingAdapter;
    protected DownloadStatusReceiver mDownLoadStatusReceiver;
    private TextView tvSelectAll;
    private TextView tvDelete;
    protected String host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        setBackMode(BACK, "我的下载");
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
            int courseId = intent.getIntExtra(Const.COURSE_ID, 0);
            M3U8DbModle m3u8model = M3U8Util.queryM3U8Modle(
                    mContext, app.loginUser.id, lessonId, app.domain, M3U8Util.ALL);
            if (m3u8model.finish == M3U8Util.FINISH) {
                updateLocalCourseList(M3U8Util.FINISH, mDownloadedAdapter);
                updateLocalCourseList(M3U8Util.UN_FINISH, mDownloadingAdapter);
            } else {
                mDownloadingAdapter.updateProgress(lessonId, m3u8model);
            }
        }
    };

    private void initView() {
        mSqliteUtil = SqliteUtil.getUtil(mContext);
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("已下载").setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("下载中").setContent(R.id.tab2));
        TabWidget tabWidget = tabHost.getTabWidget();

        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(AppUtil.px2sp(this, getResources().getDimension(R.dimen.large_font_size)));
            tv.setTypeface(null, Typeface.NORMAL);
            tv.setTextColor(this.getResources().getColor(R.color.green_alpha));
            tabWidget.getChildAt(i).setBackgroundResource(R.drawable.register_tab_bg);
        }

        mDeviceSpaceInfo = (TextView) findViewById(R.id.download_device_info);
        mDownloadingList = (ExpandableListView) findViewById(R.id.el_downloading);
        mDownloadedList = (ExpandableListView) findViewById(R.id.el_downloaded);
        mToolsLayout = findViewById(R.id.download_tools_layout);
        tvSelectAll = (TextView) findViewById(R.id.tv_select_all);
        tvSelectAll.setOnClickListener(mSelectAllClickListener);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        tvDelete.setOnClickListener(mDeleteClickListener);

        //网校域名
        Uri hostUri = Uri.parse(app.host);
        if (hostUri != null) {
            this.host = hostUri.getHost();
        }

        loadDeviceSpaceInfo();
        LocalCourseModel finishModel = getLocalCourseList(M3U8Util.FINISH, null, null);
        mDownloadedAdapter = new DownloadingAdapter(mContext, this, finishModel.m3U8DbModles, finishModel.mLocalCourses, finishModel.mLocalLessons, DownloadingAdapter.DownloadType.DOWNLOADED);
        mDownloadedList.setAdapter(mDownloadedAdapter);


        LocalCourseModel unFinishModel = getLocalCourseList(M3U8Util.UN_FINISH, null, null);
        mDownloadingAdapter = new DownloadingAdapter(mContext, this, unFinishModel.m3U8DbModles, unFinishModel.mLocalCourses, unFinishModel.mLocalLessons, DownloadingAdapter.DownloadType.DOWNLOADING);
        mDownloadingList.setAdapter(mDownloadingAdapter);

    }

    private View.OnClickListener mSelectAllClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;
            if (tv.getText().equals("全选")) {
                tv.setText("取消");
                if (tabHost.getCurrentTabTag().equals("tab1")) {
                    mDownloadedAdapter.isSelectAll(true);
                } else {
                    mDownloadingAdapter.isSelectAll(true);
                }
            } else {
                tv.setText("全选");
                if (tabHost.getCurrentTabTag().equals("tab1")) {
                    mDownloadedAdapter.isSelectAll(false);
                } else {
                    mDownloadingAdapter.isSelectAll(false);
                }
            }
        }
    };

    private View.OnClickListener mDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (tabHost.getCurrentTabTag().equals("tab1")) {
                clearLocalCache(mDownloadedAdapter.getSelectLessonId());
                updateLocalCourseList(M3U8Util.FINISH, mDownloadedAdapter);
            } else {
                clearLocalCache(mDownloadingAdapter.getSelectLessonId());
                updateLocalCourseList(M3U8Util.UN_FINISH, mDownloadingAdapter);
            }
        }
    };

    private void showBtnLayout() {
        mToolsLayout.measure(0, 0);
        AppUtil.animForHeight(
                new EduSohoAnimWrap(mToolsLayout), 0, mToolsLayout.getMeasuredHeight(), 320);
    }

    private void hideBtnLayout() {
        AppUtil.animForHeight(
                new EduSohoAnimWrap(mToolsLayout), mToolsLayout.getHeight(), 0, 240);
    }

    protected void updateLocalCourseList(int type, DownloadingAdapter adapter) {
        LocalCourseModel model = getLocalCourseList(type, null, null);
        adapter.updateLocalData(model.mLocalCourses, model.mLocalLessons);

//            model
//            ListAdapter adapter = mDownloadedList.getAdapter();
//            if (!(adapter instanceof DownCourseListAdapter)) {
//                mListView.setAdapter(mAdapter);
//            }
//
//            HashMap<Integer, ArrayList<LessonItem>> mLocalLessons = mAdapter.updateLocalLesson(
//                    model.mLocalLessons);
//            sortLesson(mLocalLessons);
//            mAdapter.updateM3U8Model(model.m3U8DbModles);
//            mAdapter.clear();
//            mLocalCourses = model.mLocalCourses;
//            mAdapter.addItems(model.mLocalCourses);
//            mAdapter.expandAll(mListView);

    }

    private void clearLocalCache(ArrayList<Integer> ids) {
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
                service.cancleDownloadTask(id);
            }
        }

        clearVideoCache(ids);
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

    private void loadDeviceSpaceInfo() {
        long[] size = getDeviceSpaceSize();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.offline_menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.offline_menu_edit) {
            if (item.isChecked()) {
                hideBtnLayout();
                item.setTitle("编辑");
                if (tabHost.getCurrentTabTag().equals("tab1")) {
                    mDownloadedAdapter.setSelectShow(false);
                } else {
                    mDownloadingAdapter.setSelectShow(false);
                }
            } else {
                showBtnLayout();
                item.setTitle("取消");
                if (tabHost.getCurrentTabTag().equals("tab1")) {
                    mDownloadedAdapter.setSelectShow(true);
                } else {
                    mDownloadingAdapter.setSelectShow(true);
                }
            }
            item.setChecked(!item.isChecked());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public LocalCourseModel getLocalCourseList(
            int isFinish, int[] courseIds, int[] lessonIds) {
        LocalCourseModel model = new LocalCourseModel();

        final ArrayList<LessonItem> lessonItems = new ArrayList<LessonItem>();
        SqliteUtil.QueryPaser<ArrayList<LessonItem>> queryPaser;
        queryPaser = new SqliteUtil.QueryPaser<ArrayList<LessonItem>>() {
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
                queryPaser,
                "select * from data_cache where type=?" + lessonIdQuery.toString(),
                Const.CACHE_LESSON_TYPE
        );

        if (lessonItems != null) {
            int[] ids = getLessonIds(lessonItems);
            model.m3U8DbModles = M3U8Util.getM3U8ModleList(
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

    private void filterLessons(
            int isFinish, ArrayList<LessonItem> lessonItems, SparseArray<M3U8DbModle> m3U8Models) {
        Iterator<LessonItem> iterator = lessonItems.iterator();
        while (iterator.hasNext()) {
            LessonItem item = iterator.next();
            M3U8DbModle m3U8DbModle = m3U8Models.get(item.id);
            if (m3U8DbModle != null && m3U8DbModle.finish != isFinish) {
                iterator.remove();
            }
        }
    }

    public Course getLocalCourse(int courseId) {
        SqliteUtil.QueryPaser<Course> queryPaser;
        queryPaser = new SqliteUtil.QueryPaser<Course>() {
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
            public boolean isSignle() {
                return true;
            }
        };

        Course course = mSqliteUtil.query(
                queryPaser,
                "select * from data_cache where type=? and key=?",
                Const.CACHE_COURSE_TYPE,
                "course-" + courseId
        );

        return course;
    }

    public class LocalCourseModel {
        public ArrayList<Course> mLocalCourses;
        public SparseArray<M3U8DbModle> m3U8DbModles;
        public HashMap<Integer, ArrayList<LessonItem>> mLocalLessons;

        public LocalCourseModel() {
            mLocalCourses = new ArrayList<>();
            mLocalLessons = new HashMap<>();
        }
    }
}
