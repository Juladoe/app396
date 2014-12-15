package com.edusoho.kuozhi.ui.fragment.lesson;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.LocalCourseListAdapter;
import com.edusoho.kuozhi.broadcast.DownLoadStatusReceiver;
import com.edusoho.kuozhi.broadcast.callback.StatusCallback;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;
import com.edusoho.kuozhi.util.SqliteUtil;
import com.google.gson.reflect.TypeToken;

import org.apache.cordova.App;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by howzhi on 14/12/11.
 */
public class LessonDownloadedFragment extends BaseFragment {

    private EduSohoListView mListView;
    private SqliteUtil mSqliteUtil;
    private ArrayList<Course> mLocalCourses;
    private SparseArray<M3U8DbModle> m3U8DbModles;
    private HashMap<Integer, ArrayList<LessonItem>> mLocalLessons;
    private LocalCourseListAdapter mAdapter;
    private DownLoadStatusReceiver mDownLoadStatusReceiver;

    private TextView mDeviceSpaceInfo;
    private String host;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.offline_menu_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.offline_menu_edit) {
            if (mLocalCourses == null || mLocalCourses.isEmpty()) {
                return true;
            }
            clearAllLocalCache();
            mListView.clear();
            loadLocalCourseList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearAllLocalCache() {
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(mContext);
        sqliteUtil.delete(
                "data_cache",
                "type=?",
                new String[]{Const.CACHE_LESSON_TYPE}
        );
        clearVideoCache();
    }

    private void clearVideoCache() {
        File workSpace = EdusohoApp.getWorkSpace();
        if (workSpace == null) {
            return;
        }
        File videosDir = new File(workSpace, "videos");
        FileUtils.deleteFile(videosDir.getAbsolutePath());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSqliteUtil = SqliteUtil.getUtil(mContext);
        Uri hostUri = Uri.parse(app.host);
        if (hostUri != null) {
            this.host = hostUri.getHost();
        }
        setContainerView(R.layout.lesson_downloading_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mDeviceSpaceInfo = (TextView) view.findViewById(R.id.download_device_info);
        mListView = (EduSohoListView) view.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(linearLayoutManager);
        mListView.setEmptyString(
                new String[]{"没有已下载视频"},
                R.drawable.lesson_download_empty_icon
        );

        mAdapter = new LocalCourseListAdapter(mContext, R.layout.course_download_list_item);
        mListView.setAdapter(mAdapter);
        mListView.setFixHeight(EdusohoApp.screenH - mActivity.mActionBar.getHeight());

        mLocalCourses = new ArrayList<Course>();
        mLocalLessons = new HashMap<Integer, ArrayList<LessonItem>>();

        loadDeviceSpaceInfo();
        loadLocalCourseList();
    }

    private long[] getDeviceSpaceSize()
    {
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

    private void loadDeviceSpaceInfo()
    {
        long[] size = getDeviceSpaceSize();
        mDeviceSpaceInfo.setText(
                String.format(
                        "共%s, 剩余空间%s",
                        AppUtil.formatSize(size[0]),
                        AppUtil.formatSize(size[1])
                )
        );
    }

    private void loadLocalCourseList() {
        final ArrayList<LessonItem> list = new ArrayList<LessonItem>();
        SqliteUtil.QueryPaser<ArrayList<LessonItem>> queryPaser;
        queryPaser = new SqliteUtil.QueryPaser<ArrayList<LessonItem>>() {
            @Override
            public ArrayList<LessonItem> parse(Cursor cursor) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                LessonItem item = mActivity.parseJsonValue(
                        value, new TypeToken<LessonItem>() {
                });
                list.add(item);
                return list;
            }
        };

        ArrayList<LessonItem> lessonItems = mSqliteUtil.query(
                queryPaser,
                "select * from data_cache where type=?",
                Const.CACHE_LESSON_TYPE
        );

        if (lessonItems != null) {
            int index = 0;
            int[] ids = new int[lessonItems.size()];
            for (LessonItem lessonItem : lessonItems) {
                if (!mLocalLessons.containsKey(lessonItem.courseId)) {
                    mLocalCourses.add(getLocalCourse(lessonItem.courseId));
                    mLocalLessons.put(lessonItem.courseId, new ArrayList<LessonItem>());
                }

                ArrayList<LessonItem> lessons = mLocalLessons.get(lessonItem.courseId);
                lessons.add(lessonItem);
                ids[index++] = lessonItem.id;
            }

            m3U8DbModles = M3U8Uitl.getM3U8ModleList(mContext, ids, this.host);
            mAdapter.setM3U8Modles(m3U8DbModles);
        } else {
            mLocalCourses.clear();
            mLocalLessons.clear();
        }

        mAdapter.setLocalLessons(mLocalLessons);
        mListView.pushData(mLocalCourses);
    }

    private Course getLocalCourse(int courseId) {
        SqliteUtil.QueryPaser<Course> queryPaser;
        queryPaser = new SqliteUtil.QueryPaser<Course>() {
            @Override
            public Course parse(Cursor cursor) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                Course course = mActivity.parseJsonValue(
                        value, new TypeToken<Course>() {
                });
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

    @Override
    public void onResume() {
        super.onResume();
        if (mDownLoadStatusReceiver == null) {
            mDownLoadStatusReceiver = new DownLoadStatusReceiver(mStatusCallback);
            Log.d(null, "regist DownLoadStatusReceiver");
            mActivity.registerReceiver(
                    mDownLoadStatusReceiver, new IntentFilter(DownLoadStatusReceiver.ACTION));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(mDownLoadStatusReceiver);
        Log.d(null, "unregist DownLoadStatusReceiver");
    }

    private StatusCallback mStatusCallback = new StatusCallback() {
        @Override
        public void invoke(Intent intent) {
            int lessonId = intent.getIntExtra(Const.LESSON_ID, 0);
            M3U8DbModle m3U8DbModle = M3U8Uitl.queryM3U8Modle(mContext, lessonId, host);
            mAdapter.updateM3U8Modles(lessonId, m3U8DbModle);
            mAdapter.refreshData(LocalCourseListAdapter.UPDATE);
        }
    };

}
