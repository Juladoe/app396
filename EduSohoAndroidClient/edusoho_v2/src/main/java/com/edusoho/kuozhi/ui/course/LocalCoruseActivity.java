package com.edusoho.kuozhi.ui.course;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;

import com.edusoho.kuozhi.adapter.Course.LocalCourseListAdapter;
import com.edusoho.kuozhi.broadcast.DownLoadStatusReceiver;
import com.edusoho.kuozhi.broadcast.callback.StatusCallback;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.fragment.lesson.LessonDownloadingFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;
import com.edusoho.kuozhi.util.SqliteUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by howzhi on 14/12/15.
 */
public class LocalCoruseActivity extends CourseDetailsTabActivity {

    protected EduSohoListView mListView;
    protected SqliteUtil mSqliteUtil;
    protected LocalCourseListAdapter mAdapter;
    protected DownLoadStatusReceiver mDownLoadStatusReceiver;
    protected String host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (app.loginUser == null) {
            finish();
        }
        app.startPlayCacheServer(mActivity);
    }

    @Override
    protected void initIntentData() {
        titles = new String[]{ "已下载" , "正在下载"};
        fragmentArrayList = new String[]{ "LessonDownedFragment","LessonDowningFragment" };

        Intent data = getIntent();
        data.putExtra(FRAGMENT_DATA, new Bundle());

        mTitle = "已下载课时";
    }

    @Override
    protected void initView() {
        super.initView();

        //网校域名
        Uri hostUri = Uri.parse(app.host);
        if (hostUri != null) {
            this.host = hostUri.getHost();
        }

        mSqliteUtil = SqliteUtil.getUtil(mContext);
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
                        value, new TypeToken<LessonItem>(){}
                );
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
            model.m3U8DbModles = M3U8Uitl.getM3U8ModleList(
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

    private boolean filterCourseId(int courseId, int[] courseIds)
    {
        for (int id : courseIds) {
            if (courseId == id) {
                return true;
            }
        }

        return false;
    }

    private int[] getLessonIds(ArrayList<LessonItem> lessons)
    {
        int index = 0;
        int[] ids = new int[lessons.size()];
        for (LessonItem lessonItem : lessons) {
            ids[index++] = lessonItem.id;
        }

        return ids;
    }

    private void filterLessons(
            int isFinish, ArrayList<LessonItem> lessonItems, SparseArray<M3U8DbModle> m3U8Models)
    {
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
        if (mDownLoadStatusReceiver != null) {
            mActivity.unregisterReceiver(mDownLoadStatusReceiver);
            Log.d(null, "unregist DownLoadStatusReceiver");
        }
    }

    private StatusCallback mStatusCallback = new StatusCallback() {
        @Override
        public void invoke(Intent intent) {
            int lessonId = intent.getIntExtra(Const.LESSON_ID, 0);
            int courseId = intent.getIntExtra(Const.COURSE_ID, 0);
            Bundle bundle = new Bundle();
            bundle.putInt(Const.LESSON_ID, lessonId);
            bundle.putInt(Const.COURSE_ID, courseId);
            app.sendMessage(LessonDownloadingFragment.UPDATE, bundle);
        }
    };

    public class LocalCourseModel
    {
        public ArrayList<Course> mLocalCourses;
        public SparseArray<M3U8DbModle> m3U8DbModles;
        public HashMap<Integer, ArrayList<LessonItem>> mLocalLessons;

        public LocalCourseModel()
        {
            mLocalCourses = new ArrayList<Course>();
            mLocalLessons = new HashMap<Integer, ArrayList<LessonItem>>();
        }
    }
}
