package com.edusoho.kuozhi.ui.fragment.course;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.Service.M3U8DownService;
import com.edusoho.kuozhi.adapter.Course.CourseLessonDownloadAdapter;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.broadcast.DownLoadStatusReceiver;
import com.edusoho.kuozhi.broadcast.callback.StatusCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.Lesson.UploadFile;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.model.status.DownLoadStatus;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.fragment.lesson.LessonDownloadingFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;
import com.edusoho.kuozhi.util.SqliteUtil;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by howzhi on 14/12/8.
 */
public class CourseDownloadingFragment extends BaseFragment {

    public static final String LIST_JSON = "lessonListJsonStr";
    public static final String COURSE_JSON = "courseJsonStr";
    private EduSohoListView mListView;
    private Course mCourse;
    private CourseLessonDownloadAdapter mAdapter;
    protected DownLoadStatusReceiver mDownLoadStatusReceiver;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_downloading_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mListView = (EduSohoListView) view.findViewById(R.id.list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(linearLayoutManager);
        mListView.addItemDecoration();
        mAdapter = new CourseLessonDownloadAdapter(
                mActivity, R.layout.course_download_lesson_item);
        mListView.setEmptyString(
                new String[] { "暂无可下载视频(第三方视频不可下载)" },
                R.drawable.lesson_download_empty_icon
        );
        mListView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        String lessonListJsonStr = bundle.getString(LIST_JSON);
        String courseJsonStr = bundle.getString(COURSE_JSON);
        ArrayList<LessonItem> lessonItems = mActivity.parseJsonValue(
                lessonListJsonStr, new TypeToken<ArrayList<LessonItem>>(){});
        mCourse = mActivity.parseJsonValue(courseJsonStr, new TypeToken<Course>(){});
        if (lessonItems != null) {
            filterLesson(lessonItems);
            mAdapter.setLessonIds(loadLocalLessonStatus(lessonItems));
            mListView.pushData(lessonItems);
        }

        mAdapter.setOnItemClick(new RecyclerViewListBaseAdapter.RecyclerItemClick() {
            @Override
            public void onItemClick(Object obj, int position) {
                LessonItem lessonItem = (LessonItem)obj;
                if (!mAdapter.isCanClick(lessonItem)) {
                    return;
                }

                if (getDeviceFreeSize() < (1024 * 1024 * 50)) {
                    ToastUtils.show(mContext, "手机可用空间不足,不能下载!");
                    return;
                }

                checkLesson(lessonItem);
            }
        });
    }

    private void checkLesson(final LessonItem listItem) {
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.show();

        RequestUrl requestUrl = app.bindUrl(Const.COURSELESSON, true);
        requestUrl.setParams(new String[]{
                "courseId", String.valueOf(listItem.courseId),
                "lessonId", String.valueOf(listItem.id)
        });

        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                loadDialog.dismiss();
                LessonItem lessonItem = mActivity.parseJsonValue(
                        object, new TypeToken<LessonItem>(){});
                if (lessonItem == null) {
                    mActivity.longToast("获取的视频地址不存在!");
                    return;
                }

                if (listItem.uploadFile == null) {
                    Pattern urlPattern = Pattern.compile("courses/[\\d]+/lessons/[\\d]+/media", Pattern.DOTALL);
                    if (urlPattern.matcher(lessonItem.mediaUri).find()) {
                        mActivity.longToast("暂不支持本地视频下载!");
                        return;
                    }
                } else {
                    if ("local".equals(listItem.uploadFile.storage)) {
                        mActivity.longToast("暂不支持本地视频下载!");
                        return;
                    }
                }

                saveCache(
                        mContext,
                        Const.CACHE_LESSON_TYPE,
                        "lesson-" + lessonItem.id,
                        app.gson.toJson(lessonItem)
                );
                saveCache(
                        mContext,
                        Const.CACHE_COURSE_TYPE,
                        "course-" + mCourse.id,
                        app.gson.toJson(mCourse)
                );

                M3U8DbModle m3U8DbModle = M3U8Uitl.saveM3U8Model(
                        mContext, lessonItem.id, app.domain, app.loginUser.id);
                M3U8DownService.startDown(
                        mContext, lessonItem.id, lessonItem.courseId, lessonItem.title);
                mAdapter.updateLessonIds(lessonItem.id, m3U8DbModle);
            }
        });
    }

    private long getDeviceFreeSize()
    {
        long free = 0;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File sdcard = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(sdcard.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();

            free = blockSize * availableBlocks;
        }

        return free;
    }

    private void saveCache(Context context, String type, String key, String value)
    {
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(context);

        Object obj = sqliteUtil.queryForObj(
                new TypeToken<Object>(){},
                "where type=? and key=?",
                type,
                key
        );

        if (obj == null) {
            sqliteUtil.saveLocalCache(type, key, value);
        }
    }

    private SparseArray<M3U8DbModle> loadLocalLessonStatus(
            ArrayList<LessonItem> lessonItems)
    {
        int index = 0;
        int[] ids = new int[lessonItems.size()];
        final StringBuffer idStr = new StringBuffer("(");
        for (LessonItem lessonItem : lessonItems) {
            ids[index++] = lessonItem.id;
            idStr.append("'lesson-").append(lessonItem.id).append("',");
        }
        if (idStr.length() > 1) {
            idStr.deleteCharAt(idStr.length()-1);
        }
        idStr.append(")");
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(mContext);

        final SparseArray<M3U8DbModle> m3U8DbModles = M3U8Uitl.getM3U8ModleList(
                mContext, ids, app.loginUser.id, app.domain, M3U8Uitl.ALL);
        final SparseArray<M3U8DbModle> lessonIds = new SparseArray<M3U8DbModle>();

        SqliteUtil.QueryPaser<SparseArray<DownLoadStatus>> queryPaser;
        queryPaser = new SqliteUtil.QueryPaser<SparseArray<DownLoadStatus>>() {
            @Override
            public SparseArray<DownLoadStatus> parse(Cursor cursor) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                LessonItem item = mActivity.parseJsonValue(
                        value, new TypeToken<LessonItem>(){});
                lessonIds.put(item.id, m3U8DbModles.get(item.id));
                return null;
            }
        };

        sqliteUtil.query(
                queryPaser,
                "select * from data_cache where type=? and key in " + idStr.toString(),
                Const.CACHE_LESSON_TYPE
        );

        return lessonIds;
    }

    /**
     * 过滤视频类型，只保留云视频和本地视频
     * mediaSourece 视频类型
     * @param lessonItems
    */
    private void filterLesson(ArrayList<LessonItem> lessonItems)
    {
        Iterator<LessonItem> lessonItemIterator = lessonItems.iterator();
        while (lessonItemIterator.hasNext()) {
            LessonItem lessonItem = lessonItemIterator.next();
            CourseLessonType type = CourseLessonType.value(lessonItem.type);
            if (type == CourseLessonType.VIDEO &&
                    "published".equals(lessonItem.status) &&
                    "self".equals(lessonItem.mediaSource)) {
                UploadFile uploadFile = lessonItem.uploadFile;
                if (uploadFile == null || "cloud".equals(uploadFile.storage)) {
                    continue;
                }
            }

            lessonItemIterator.remove();
        }
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
            if (lessonId == 0) {
                return;
            }
            M3U8DbModle m3U8DbModle = M3U8Uitl.queryM3U8Modle(
                    mContext, app.loginUser.id, lessonId, app.domain, M3U8Uitl.ALL);
            mAdapter.updateLessonIds(lessonId, m3U8DbModle);
        }
    };
}
