package com.edusoho.kuozhi.v3.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.CourseDownloadAdapter;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.course.DownloadCourse;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.UtilFactory;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMember;
import com.edusoho.kuozhi.v3.model.bal.course.CourseResult;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.model.provider.CourseProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.DownloadManagerActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.RequestUtil;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.trinea.android.common.util.FileUtils;
import cn.trinea.android.common.util.ToastUtils;


/**
 * Created by suju on 17/1/10.
 */

public class MyDownloadFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View mEmptyView;
    private TextView tvEmptyText;
    private ListView mListView;
    private CourseDownloadAdapter mAdapter;
    private Map<Integer, List<LessonItem>> mLocalLessonMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View containerView = inflater.inflate(R.layout.fragment_mydownload_layout, null);
        ViewGroup parent = (ViewGroup) containerView.getParent();
        if (parent != null) {
            parent.removeView(containerView);
        }
        return containerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.listview);
        mEmptyView = view.findViewById(R.id.ll_empty);
        tvEmptyText = (TextView) view.findViewById(R.id.tv_empty_text);
        tvEmptyText.setText(getContext().getText(R.string.no_lesson_cache));
        mAdapter = new CourseDownloadAdapter(getContext());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getAppSettingProvider().getCurrentUser() == null) {
            return;
        }
        List<DownloadCourse> courseList = getLocalCourseList(M3U8Util.ALL, null, null);
        loadDataList(courseList);
        filterCourseByState(courseList);
    }

    private void loadDataList(List<DownloadCourse> courseList) {
        mAdapter.setCourseList(courseList);
        setEmptyState(mAdapter.getCount() == 0);
    }

    private void setEmptyState(boolean isEmpty) {
        mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void filterCourseByState(List<DownloadCourse> courseList) {
        CourseProvider courseProvider = new CourseProvider(getContext());
        for (final DownloadCourse course : courseList) {
            courseProvider.getMember(course.id).success(new NormalCallback<CourseMember>() {
                @Override
                public void success(CourseMember courseMember) {
                    if (mLocalLessonMap == null) {
                        return;
                    }
                    if (courseMember == null) {
                        clearLocalCache(getLessonIds(mLocalLessonMap.get(course.id)));
                        List<DownloadCourse> courseList = getLocalCourseList(M3U8Util.ALL, null, null);
                        loadDataList(courseList);
                        return;
                    }
                    if (AppUtil.parseInt(courseMember.deadline) <= 0) {
                        mAdapter.updateCourseExpirdState(courseMember.courseId, true);
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        DownloadCourse course = (DownloadCourse) parent.getItemAtPosition(position);
        if (course.isExpird()) {
            ToastUtils.show(getContext(), R.string.download_course_expird_timeout);
            return;
        }
        bundle.putInt(Const.COURSE_ID, course.id);
        CoreEngine.create(getContext()).runNormalPluginWithBundle("DownloadManagerActivity", getContext(), bundle);
    }

    public List<DownloadCourse> getLocalCourseList(
            int isFinish, int[] courseIds, int[] lessonIds) {
        List<DownloadCourse> localCourses = new ArrayList<>();
        final ArrayList<LessonItem> lessonItems = new ArrayList<>();
        SqliteUtil.QueryParser<ArrayList<LessonItem>> queryParser;
        queryParser = new SqliteUtil.QueryParser<ArrayList<LessonItem>>() {
            @Override
            public ArrayList<LessonItem> parse(Cursor cursor) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                LessonItem item = getUtilFactory().getJsonParser().fromJson(value, LessonItem.class);
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
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(getContext());
        sqliteUtil.query(
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

            User user = getAppSettingProvider().getCurrentUser();
            School school = getAppSettingProvider().getCurrentSchool();
            SparseArray<M3U8DbModel> m3U8DbModels = M3U8Util.getM3U8ModelList(
                    getContext(), ids, user.id, school.getDomain(), isFinish);
            mLocalLessonMap = new HashMap<>();
            for (LessonItem lessonItem : lessonItems) {
                if (m3U8DbModels.indexOfKey(lessonItem.id) < 0) {
                    continue;
                }
                if (!mLocalLessonMap.containsKey(lessonItem.courseId)) {
                    if (courseIds == null || filterCourseId(lessonItem.courseId, courseIds)) {
                        localCourses.add(getLocalCourse(lessonItem.courseId));
                        mLocalLessonMap.put(lessonItem.courseId, new ArrayList<LessonItem>());
                    }
                }

                List<LessonItem> lessons = mLocalLessonMap.get(lessonItem.courseId);
                if (lessons != null) {
                    lessons.add(lessonItem);
                }
            }

            for (DownloadCourse course : localCourses) {
                List<LessonItem> lessons = mLocalLessonMap.get(course.id);
                int[] cachedLessonIds = getCachedLessonIds(lessons, m3U8DbModels);
                course.setCachedLessonNum(cachedLessonIds.length);
                course.setCachedSize(getDownloadLessonListSize(cachedLessonIds));
            }
        } else {
            localCourses.clear();
            lessonItems.clear();
        }
        return localCourses;
    }

    private int[] getCachedLessonIds(List<LessonItem> lessons, SparseArray<M3U8DbModel> m3U8DbModels) {
        List<LessonItem> cachedLessons = new ArrayList<>();
        for (LessonItem lessonItem : lessons) {
            M3U8DbModel m3U8DbModel = m3U8DbModels.get(lessonItem.id);
            if (m3U8DbModel != null && m3U8DbModel.finish == M3U8Util.FINISH) {
                cachedLessons.add(lessonItem);
            }
        }

        return getLessonIds(cachedLessons);
    }

    private long getDownloadLessonListSize(int[] lessonIds) {
        long total = 0;
        for (int i = 0; i < lessonIds.length; i++) {
            total += getDownloadLessonSize(lessonIds[i]);
        }

        return total;
    }

    private long getDownloadLessonSize(int lessonId) {
        AppSettingProvider settingProvider = getAppSettingProvider();
        User user = settingProvider.getCurrentUser();
        School school = settingProvider.getCurrentSchool();
        if (user == null || school == null) {
            return 0;
        }
        File dir = getLocalM3U8Dir(user.id, school.getDomain(), lessonId);
        if (dir == null || !dir.exists()) {
            return 0;
        }

        return getCacheSize(dir);
    }

    private File getLocalM3U8Dir(int userId, String host, int lessonId) {
        File workSpace = EdusohoApp.getWorkSpace();
        if (workSpace == null) {
            return null;
        }

        StringBuffer dirBuilder = new StringBuffer(workSpace.getAbsolutePath());
        dirBuilder.append("/videos/")
                .append(userId)
                .append("/")
                .append(host)
                .append("/")
                .append(lessonId);

        File lessonDir = new File(dirBuilder.toString());
        if (!lessonDir.exists()) {
            lessonDir.mkdirs();
        }

        return lessonDir;
    }

    private long getCacheSize(File dir) {
        long totalSize = 0;
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                totalSize = totalSize + file.length();
            } else {
                totalSize = totalSize + getCacheSize(file);
            }
        }
        return totalSize;
    }

    private int[] getLessonIds(List<LessonItem> lessons) {
        int index = 0;
        int[] ids = new int[lessons.size()];
        for (LessonItem lessonItem : lessons) {
            ids[index++] = lessonItem.id;
        }
        return ids;
    }

    private DownloadCourse getLocalCourse(int courseId) {
        SqliteUtil.QueryParser<DownloadCourse> queryParser;
        queryParser = new SqliteUtil.QueryParser<DownloadCourse>() {
            @Override
            public DownloadCourse parse(Cursor cursor) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                DownloadCourse course = getUtilFactory().getJsonParser().fromJson(value, DownloadCourse.class);
                return course;
            }

            @Override
            public boolean isSingle() {
                return true;
            }
        };

        DownloadCourse course = SqliteUtil.getUtil(getContext()).query(
                queryParser,
                "select * from data_cache where type=? and key=?",
                Const.CACHE_COURSE_TYPE,
                String.format("course-%d", courseId)
        );
        return course;
    }

    private String coverM3U8Ids(int[] ids) {
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

    private String coverLessonIds(int[] ids) {
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

    public void clearLocalCache(int[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(getContext());

        String m3u8LessonIds = coverM3U8Ids(ids);
        String cacheLessonIds = coverLessonIds(ids);
        sqliteUtil.execSQL(String.format(
                "delete from data_cache where type='%s' and key in %s",
                Const.CACHE_LESSON_TYPE,
                cacheLessonIds.toString()
                )
        );

        School school = getAppSettingProvider().getCurrentSchool();
        sqliteUtil.execSQL(String.format(
                "delete from data_m3u8 where host='%s' and lessonId in %s",
                school != null ? school.getDomain() : "",
                m3u8LessonIds.toString())
        );

        sqliteUtil.execSQL(String.format(
                "delete from data_m3u8_url where lessonId in %s",
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
    private void clearVideoCache(int[] ids) {
        File workSpace = EdusohoApp.getWorkSpace();
        if (workSpace == null) {
            return;
        }
        School school = getAppSettingProvider().getCurrentSchool();
        User user = getAppSettingProvider().getCurrentUser();
        File videosDir = new File(workSpace, "videos/" + user.id + "/" + school.getDomain());
        for (int id : ids) {
            FileUtils.deleteFile(new File(videosDir, String.valueOf(id)).getAbsolutePath());
        }
    }

    private boolean filterCourseId(int courseId, int[] courseIds) {
        for (int id : courseIds) {
            if (courseId == id) {
                return true;
            }
        }
        return false;
    }

    protected UtilFactory getUtilFactory() {
        return FactoryManager.getInstance().create(UtilFactory.class);
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
