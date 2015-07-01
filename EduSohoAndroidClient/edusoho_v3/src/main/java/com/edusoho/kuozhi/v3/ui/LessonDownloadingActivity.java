package com.edusoho.kuozhi.v3.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.broadcast.DownloadStatusReceiver;
import com.edusoho.kuozhi.v3.model.bal.CourseLessonType;
import com.edusoho.kuozhi.v3.model.bal.DownloadStatus;
import com.edusoho.kuozhi.v3.model.bal.Lesson.DownLessonItem;
import com.edusoho.kuozhi.v3.model.bal.Lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.bal.Lesson.UploadFile;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.kuozhi.v3.view.EduSohoIconView;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by JesseHuang on 15/6/14.
 */
public class LessonDownloadingActivity extends ActionBarBaseActivity {
    public static final String LIST_JSON = "lessonListJsonStr";
    public static final String COURSE_JSON = "courseJsonStr";
    protected DownloadStatusReceiver mDownloadStatusReceiver;

    private TextView btnSelectAll;
    private TextView btnDownload;
    private int mCourseId;

    private ExpandableListView mListView;
    private Context mContext;
    private List<LessonItem> mLessonList;
    private Course mCourse;
    private List<LessonItem> mGroupItems = new ArrayList<>();
    private List<List<LessonItem>> mChildItems = new ArrayList<>();
    private DownloadLessonAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_downloading);
        setBackMode(BACK, "下载列表");
        mContext = this;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDownloadStatusReceiver == null) {
            mDownloadStatusReceiver = new DownloadStatusReceiver(mStatusCallback);
            registerReceiver(mDownloadStatusReceiver, new IntentFilter(DownloadStatusReceiver.ACTION));
        }
    }

    private DownloadStatusReceiver.StatusCallback mStatusCallback = new DownloadStatusReceiver.StatusCallback() {
        @Override
        public void invoke(Intent intent) {
            int lessonId = intent.getIntExtra(Const.LESSON_ID, 0);
            if (lessonId == 0) {
                return;
            }
            M3U8DbModle m3u8Model = M3U8Util.queryM3U8Modle(
                    mContext, app.loginUser.id, lessonId, app.domain, M3U8Util.ALL);
            if (mAdapter != null) {
                mAdapter.updateDownloadSign(lessonId, m3u8Model);
            }
        }
    };

    private void getCourseLessons() {
        RequestUrl url = mActivity.app.bindUrl(Const.DOWN_LESSONS, true);
        try {
            url.setParams(new String[]{
                    "courseId", String.valueOf(mCourseId)
            });

            this.ajaxPostWithLoading(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    DownLessonItem downLessonItem = mActivity.parseJsonValue(
                            response, new TypeToken<DownLessonItem>() {
                    });
                    if (downLessonItem != null) {
                        mCourse = downLessonItem.course;
                        initDownLessons(downLessonItem.lessons);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }, "获取信息...");
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    private void initDownLessons(List<LessonItem> lessonItems) {
        mLessonList = lessonItems;
        filterLesson(mLessonList);
        loadLocalLessonStatus(mLessonList);

        if (mLessonList != null) {
            initData();
            if (mGroupItems.size() < mChildItems.size()) {
                LessonItem group1 = new LessonItem();
                group1.title = "第0章";
                mGroupItems.add(0, group1);
            }
            mAdapter = new DownloadLessonAdapter(mGroupItems, mChildItems);
            mListView.setAdapter(mAdapter);
        }

        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (v.getTag() instanceof GroupPanel) {
                    GroupPanel gp = (GroupPanel) v.getTag();
                    if (parent.isGroupExpanded(groupPosition)) {
                        gp.ivIndicator.setText(getString(R.string.font_less));
                    } else {
                        gp.ivIndicator.setText(getString(R.string.font_more));
                    }
                }
                return false;
            }
        });
    }

    private void initView() {
        mListView = (ExpandableListView) findViewById(R.id.el_download);
        btnSelectAll = (TextView) findViewById(R.id.tv_select_all);
        btnSelectAll.setOnClickListener(mSelectAllClick);
        btnDownload = (TextView) findViewById(R.id.tv_download);
        btnDownload.setOnClickListener(mDownloadClick);

        Intent intent = getIntent();
        if (intent != null) {
            mCourseId = intent.getIntExtra(Const.COURSE_ID, 0);
        }

        getCourseLessons();
    }

    private void initData() {
        //节
        int unitCount = 0;
        int chapterCount = 0;
        List<LessonItem> tempArray = new ArrayList<>();
        int size = mLessonList.size();
        for (int i = 0; i < size; i++) {
            LessonItem curLessonItem = mLessonList.get(i);
            LessonItem.ItemType itemType = LessonItem.ItemType.cover(curLessonItem.itemType);
            LessonItem.ItemType type = LessonItem.ItemType.cover(curLessonItem.type);
            if (itemType.equals(LessonItem.ItemType.CHAPTER) && type.equals(LessonItem.ItemType.CHAPTER)) {
                //章节
                curLessonItem.title = String.format("第%s章 %s", ++chapterCount, curLessonItem.title);
                //如果最后一个GroupItem没有云视频课程，则删除改GroupItem
                if (mGroupItems.size() != 0 && mChildItems.size() < mGroupItems.size()) {
                    mGroupItems.remove(mGroupItems.size() - 1);
                }
                mGroupItems.add(curLessonItem);
            } else {
                if (type.equals(LessonItem.ItemType.UNIT)) {
                    curLessonItem.title = String.format("第%s节 %s", ++unitCount, curLessonItem.title);
                }
                curLessonItem.groupId = chapterCount;
                tempArray.add(curLessonItem);

                LessonItem nextLessonItem = mLessonList.get(i + 1);
                if (i + 1 == size || (nextLessonItem.itemType.toString().toUpperCase().equals(LessonItem.ItemType.CHAPTER.toString())
                        && nextLessonItem.type.toString().toUpperCase().equals(LessonItem.ItemType.CHAPTER.toString()))) {
                    mChildItems.add(tempArray);
                    tempArray = new ArrayList<>();
                }
            }
        }
        //遍历结束，检查最后一个GroupItem，如果没有云视频课程，则删除改GroupItem
        if (mGroupItems.size() != 0 && mChildItems.size() < mGroupItems.size()) {
            mGroupItems.remove(mGroupItems.size() - 1);
        }
    }

    private View.OnClickListener mDownloadClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getDeviceFreeSize() < (1024 * 1024 * 50)) {
                CommonUtil.longToast(mContext, "手机可用空间不足,不能下载!");
                return;
            }

            for (LessonItem item : mLessonList) {
                if (item.isSelected) {
                    downloadLesson(item);
                    item.isSelected = false;
                }
            }
        }
    };

    private View.OnClickListener mSelectAllClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (btnSelectAll.getVisibility() == View.VISIBLE
                    && btnSelectAll.getText().equals(getString(R.string.select_all))) {
                btnSelectAll.setText(getString(R.string.select_all_cancel));
                for (List<LessonItem> itemList : mChildItems) {
                    for (LessonItem item : itemList) {
                        if (item.m3u8Model == null) {
                            item.isSelected = true;
                        }
                    }
                }
            } else if (btnSelectAll.getVisibility() == View.VISIBLE
                    && btnSelectAll.getText().equals(getString(R.string.select_all_cancel))) {
                btnSelectAll.setText(getString(R.string.select_all));
                for (List<LessonItem> itemList : mChildItems) {
                    for (LessonItem item : itemList) {
                        if (item.m3u8Model == null) {
                            item.isSelected = false;
                        }
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    };

    private long getDeviceFreeSize() {
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

    /**
     * 过滤非云平台视频
     *
     * @param lessonItems
     */
    private void filterLesson(List<LessonItem> lessonItems) {
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

            //非章、非节，删除
            if (!lessonItem.itemType.toUpperCase().equals(LessonItem.ItemType.CHAPTER.toString())) {
                lessonItemIterator.remove();
            }
        }
    }

    /**
     * 加载已存储视频状态
     *
     * @param lessonItems
     * @return
     */
    private void loadLocalLessonStatus(final List<LessonItem> lessonItems) {
        int index = 0;
        int[] ids = new int[lessonItems.size()];
        final StringBuffer idStr = new StringBuffer("(");
        for (LessonItem lessonItem : lessonItems) {
            ids[index++] = lessonItem.id;
            idStr.append("'lesson-").append(lessonItem.id).append("',");
        }
        if (idStr.length() > 1) {
            idStr.deleteCharAt(idStr.length() - 1);
        }
        idStr.append(")");
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(mContext);

        final SparseArray<M3U8DbModle> m3U8DbModels = M3U8Util.getM3U8ModleList(
                mContext, ids, app.loginUser.id, app.domain, M3U8Util.ALL);

        SqliteUtil.QueryPaser<SparseArray<DownloadStatus>> queryPaser;
        queryPaser = new SqliteUtil.QueryPaser<SparseArray<DownloadStatus>>() {
            @Override
            public SparseArray<DownloadStatus> parse(Cursor cursor) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                LessonItem item = mActivity.parseJsonValue(
                        value, new TypeToken<LessonItem>() {
                        });
                for (LessonItem lessonItem : lessonItems) {
                    if (lessonItem.id == item.id) {
                        lessonItem.m3u8Model = m3U8DbModels.get(item.id);
                    }
                }
                return null;
            }
        };

        sqliteUtil.query(
                queryPaser,
                "select * from data_cache where type=? and key in " + idStr.toString(),
                Const.CACHE_LESSON_TYPE
        );
    }

    /**
     * 下载视频
     *
     * @param listItem
     */
    private void downloadLesson(final LessonItem listItem) {

        RequestUrl requestUrl = app.bindUrl(Const.COURSELESSON, true);
        requestUrl.setParams(new String[]{
                "courseId", String.valueOf(listItem.courseId),
                "lessonId", String.valueOf(listItem.id)
        });

        ajaxPostWithLoading(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LessonItem lessonItem = parseJsonValue(
                        response, new TypeToken<LessonItem>() {
                        });
                if (lessonItem == null) {
                    CommonUtil.longToast(mContext, "获取的视频地址不存在!");
                    return;
                }

                if (listItem.uploadFile == null) {
                    Pattern urlPattern = Pattern.compile("courses/[\\d]+/lessons/[\\d]+/media", Pattern.DOTALL);
                    if (urlPattern.matcher(lessonItem.mediaUri).find()) {
                        CommonUtil.longToast(mContext, "暂不支持本地视频下载!");
                        return;
                    }
                } else {
                    if ("local".equals(listItem.uploadFile.storage)) {
                        CommonUtil.longToast(mContext, "暂不支持本地视频下载!");
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

                M3U8DbModle m3U8DbModle = M3U8Util.saveM3U8Model(
                        mContext, lessonItem.id, app.domain, app.loginUser.id);
                M3U8DownService.startDown(
                        mContext, lessonItem.id, lessonItem.courseId, lessonItem.title);
                mAdapter.updateDownloadSign(lessonItem.id, m3U8DbModle);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, "");
    }

    private void saveCache(Context context, String type, String key, String value) {
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(context);

        Object obj = sqliteUtil.queryForObj(
                new TypeToken<Object>() {
                },
                "where type=? and key=?",
                type,
                key
        );

        if (obj == null) {
            sqliteUtil.saveLocalCache(type, key, value);
        }
    }

    public class DownloadLessonAdapter extends BaseExpandableListAdapter {
        private List<LessonItem> mGroupItems;
        private List<List<LessonItem>> mChildItems;

        public DownloadLessonAdapter(List<LessonItem> groupItems, List<List<LessonItem>> childItems) {
            mGroupItems = groupItems;
            mChildItems = childItems;
        }

        public void updateDownloadSign(int lessonId, M3U8DbModle model) {
            for (List<LessonItem> itemList : mChildItems) {
                for (LessonItem lessonItem : itemList) {
                    if (lessonItem.id == lessonId) {
                        lessonItem.m3u8Model = model;
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return mGroupItems.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mChildItems.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroupItems.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mChildItems.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupPanel groupPanel;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_course_group, null);
                groupPanel = new GroupPanel(convertView);
                convertView.setTag(groupPanel);
            } else {
                groupPanel = (GroupPanel) convertView.getTag();
            }

            LessonItem item = mGroupItems.get(groupPosition);
            groupPanel.tvGroupTitle.setText(item.title);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final ChildPanel childPanel;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lesson_child, null);
                childPanel = new ChildPanel(convertView);
                convertView.setTag(childPanel);
            } else {
                childPanel = (ChildPanel) convertView.getTag();
            }
            final LessonItem item = mChildItems.get(groupPosition).get(childPosition);
            if (LessonItem.ItemType.LESSON == LessonItem.ItemType.cover(item.itemType)) {
                childPanel.viewLessonUnitTitle.setVisibility(View.GONE);
                childPanel.viewLessonInfo.setVisibility(View.VISIBLE);
                childPanel.tvLessonTitle.setText(item.title);
                if (item.m3u8Model != null && item.m3u8Model.finish == M3U8Util.FINISH) {
                    //已下载
                    childPanel.ivDownloadSelected.setVisibility(View.GONE);
                    childPanel.tvDownloadFinish.setVisibility(View.VISIBLE);
                    childPanel.tvDownloadFinish.setText(getString(R.string.download_finish));
                } else if (item.m3u8Model != null && item.m3u8Model.finish == M3U8Util.UN_FINISH) {
                    //正在下载
                    childPanel.ivDownloadSelected.setVisibility(View.GONE);
                    childPanel.tvDownloadFinish.setVisibility(View.VISIBLE);
                    childPanel.tvDownloadFinish.setText(getString(R.string.downloading));
                } else if (item.m3u8Model != null && item.m3u8Model.finish == M3U8Util.START) {
                    //等待下载
                    childPanel.ivDownloadSelected.setVisibility(View.GONE);
                    childPanel.tvDownloadFinish.setVisibility(View.VISIBLE);
                    childPanel.tvDownloadFinish.setText(getString(R.string.wait_download));
                } else if (item.m3u8Model == null) {
                    //未下载
                    childPanel.ivDownloadSelected.setVisibility(View.VISIBLE);
                    childPanel.tvDownloadFinish.setVisibility(View.GONE);
                }

                //是否选中
                if (item.isSelected) {
                    childPanel.ivDownloadSelected.setText(getResources().getString(R.string.font_download_select));
                } else {
                    childPanel.ivDownloadSelected.setText(getResources().getString(R.string.font_download_unselect));
                }
            } else {
                childPanel.viewLessonUnitTitle.setVisibility(View.VISIBLE);
                childPanel.tvUnitTitle.setText(item.title);
                childPanel.viewLessonInfo.setVisibility(View.GONE);
            }
            childPanel.ivDownloadSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (childPanel.ivDownloadSelected.getText().equals(getString(R.string.font_download_select))) {
                        childPanel.ivDownloadSelected.setText(getString(R.string.font_download_unselect));
                        item.isSelected = false;
                    } else {
                        childPanel.ivDownloadSelected.setText(getString(R.string.font_download_select));
                        item.isSelected = true;
                    }
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    public static class GroupPanel {
        TextView tvGroupTitle;
        EduSohoIconView ivIndicator;

        public GroupPanel(View view) {
            tvGroupTitle = (TextView) view.findViewById(R.id.tv_group);
            ivIndicator = (EduSohoIconView) view.findViewById(R.id.iv_indicator);
        }
    }

    public static class ChildPanel {
        TextView tvUnitTitle;
        TextView tvLessonTitle;
        //EduSohoIconView ivDownload;
        TextView tvDownloadFinish;
        EduSohoIconView ivDownloadSelected;
        View viewLessonInfo;
        View viewLessonUnitTitle;

        public ChildPanel(View view) {
            tvUnitTitle = (TextView) view.findViewById(R.id.tv_lesson_chapter);
            tvLessonTitle = (TextView) view.findViewById(R.id.tv_lesson_name);
            tvDownloadFinish = (TextView) view.findViewById(R.id.iv_download_finish);
            //ivDownload = (EduSohoIconView) view.findViewById(R.id.iv_download);
            ivDownloadSelected = (EduSohoIconView) view.findViewById(R.id.iv_download_selected);
            viewLessonInfo = view.findViewById(R.id.rl_lesson_info);
            viewLessonUnitTitle = view.findViewById(R.id.rl_unit_title);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDownloadStatusReceiver != null) {
            unregisterReceiver(mDownloadStatusReceiver);
        }
    }
}
