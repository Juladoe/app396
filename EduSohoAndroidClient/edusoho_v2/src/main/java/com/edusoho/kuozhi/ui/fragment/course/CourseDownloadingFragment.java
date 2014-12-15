package com.edusoho.kuozhi.ui.fragment.course;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.Service.M3U8DownService;
import com.edusoho.kuozhi.adapter.Course.CourseLessonDownloadAdapter;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.status.DownLoadStatus;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.SqliteUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by howzhi on 14/12/8.
 */
public class CourseDownloadingFragment extends BaseFragment {

    public static final String LIST_JSON = "lessonListJsonStr";
    public static final String COURSE_JSON = "courseJsonStr";
    private EduSohoListView mListView;
    private Course mCourse;
    private CourseLessonDownloadAdapter mAdapter;

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
                M3U8DownService.startDown(
                        mContext, lessonItem.id, lessonItem.courseId, lessonItem.title);
                mAdapter.updateLessonIds(lessonItem.id, DownLoadStatus.STARTING);
            }
        });
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

    private SparseArray<DownLoadStatus> loadLocalLessonStatus(
            ArrayList<LessonItem> lessonItems)
    {
        final StringBuffer ids = new StringBuffer("(");
        for (LessonItem lessonItem : lessonItems) {
            ids.append("'lesson-").append(lessonItem.id).append("',");
        }
        if (ids.length() > 1) {
            ids.deleteCharAt(ids.length()-1);
        }
        ids.append(")");
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(mContext);
        final SparseArray<DownLoadStatus> lessonIds = new SparseArray<DownLoadStatus>();

        SqliteUtil.QueryPaser<SparseArray<DownLoadStatus>> queryPaser;
        queryPaser = new SqliteUtil.QueryPaser<SparseArray<DownLoadStatus>>() {
            @Override
            public SparseArray<DownLoadStatus> parse(Cursor cursor) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                LessonItem item = mActivity.parseJsonValue(
                        value, new TypeToken<LessonItem>(){});
                lessonIds.put(item.id, DownLoadStatus.value(0));
                return null;
            }
        };

        sqliteUtil.query(
                queryPaser,
                "select * from data_cache where type=? and key in " + ids.toString(),
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
                    ! AppUtil.inArray(lessonItem.mediaSource, new String[]{"youku", "tudou"})) {
                continue;
            }
            lessonItemIterator.remove();
        }
    }
}
