package com.edusoho.kuozhi.ui.fragment.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.DownCourseListAdapter;
import com.edusoho.kuozhi.adapter.Course.LocalCourseListAdapter;
import com.edusoho.kuozhi.adapter.ExpandListEmptyAdapter;
import com.edusoho.kuozhi.adapter.lesson.LocalLessonDownListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.course.LessonActivity;
import com.edusoho.kuozhi.ui.course.LocalCoruseActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;
import com.edusoho.kuozhi.util.SqliteUtil;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import cn.trinea.android.common.util.FileUtils;
import cn.trinea.android.common.util.ListUtils;
import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by howzhi on 14/12/11.
 */
public class LessonDownedFragment extends BaseFragment {

    protected ExpandableListView mListView;
    protected ArrayList<Course> mLocalCourses;
    protected DownCourseListAdapter mAdapter;

    private View mToolsLayout;
    private TextView mSelectAllBtn;
    private View mDelBtn;
    protected TextView mDeviceSpaceInfo;

    public static final String FINISH = "finish";

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.offline_menu_edit, menu);
    }

    @Override
    public MessageType[] getMsgTypes() {
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(FINISH)
        };
        return messageTypes;
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        String type = message.type.type;
        if (FINISH.equals(type)) {
            updateLocalCourseList();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.offline_menu_edit) {
            if (item.isChecked()) {
                hideBtnLayout();
                item.setTitle("编辑");
                mAdapter.selectAll(LocalLessonDownListAdapter.INVISIBLE);
            } else {
                showBtnLayout();
                item.setTitle("取消");
                mAdapter.selectAll(LocalLessonDownListAdapter.UNCHECK);
            }

            item.setChecked(! item.isChecked());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showBtnLayout()
    {
        mToolsLayout.measure(0, 0);
        AppUtil.animForHeight(
                new EdusohoAnimWrap(mToolsLayout), 0, mToolsLayout.getMeasuredHeight(), 320);
    }

    private void hideBtnLayout()
    {
        AppUtil.animForHeight(
                new EdusohoAnimWrap(mToolsLayout), mToolsLayout.getHeight(), 0, 240);
    }

    private String coverM3U8Ids(ArrayList<Integer> ids)
    {
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

    private String coverLessonIds(ArrayList<Integer> ids)
    {
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

        clearVideoCache(ids);
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setContainerView(R.layout.lesson_downing_layout);
        app.startPlayCacheServer(mActivity);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mToolsLayout = view.findViewById(R.id.download_tools_layout);
        mSelectAllBtn = (TextView) view.findViewById(R.id.download_select_all_btn);
        mDelBtn = view.findViewById(R.id.download_del_btn);
        mDeviceSpaceInfo = (TextView) view.findViewById(R.id.download_device_info);
        mListView = (ExpandableListView) view.findViewById(R.id.list_view);
        mListView.setGroupIndicator(null);

        loadLocalCourseList();
        loadDeviceSpaceInfo();
        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocalCourses == null || mLocalCourses.isEmpty()) {
                    ToastUtils.show(mContext, "没有缓存的课时!");
                    return;
                }

                clearLocalCache(mAdapter.getSelectLessonId());
                updateLocalCourseList();
            }
        });

        mSelectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectAllBtn.getTag() != null) {
                    mSelectAllBtn.setTag(null);
                    mAdapter.selectAll(LocalLessonDownListAdapter.UNCHECK);
                    mSelectAllBtn.setText("全选");
                } else {
                    mSelectAllBtn.setTag(true);
                    mAdapter.selectAll(LocalLessonDownListAdapter.CHECKED);
                    mSelectAllBtn.setText("取消");
                }
            }
        });

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(
                    ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                DownCourseListAdapter adapter = (DownCourseListAdapter)
                        parent.getExpandableListAdapter();
                int status = adapter.getCheckStatus(groupPosition, childPosition);
                if (status != LocalLessonDownListAdapter.INVISIBLE) {
                    status = status == LocalLessonDownListAdapter.CHECKED ?
                            LocalLessonDownListAdapter.UNCHECK : LocalLessonDownListAdapter.CHECKED;
                    adapter.selectPositon(
                            groupPosition,
                            childPosition,
                            status
                    );
                    Log.d(null, "status " + status);
                    return true;
                }

                final LessonItem lesson = (LessonItem) adapter.getChild(groupPosition, childPosition);
                EdusohoApp.app.mEngine.runNormalPlugin(
                        LessonActivity.TAG, mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.COURSE_ID, lesson.courseId);
                                startIntent.putExtra(LessonActivity.FROM_CACHE, true);
                                startIntent.putExtra(Const.FREE, lesson.free);
                                startIntent.putExtra(Const.LESSON_ID, lesson.id);
                                startIntent.putExtra(Const.LESSON_TYPE, lesson.type);
                                startIntent.putExtra(Const.ACTIONBAT_TITLE, lesson.title);
                            }
                        }
                );
                return false;
            }
        });
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

    protected void updateLocalCourseList()
    {
        LocalCoruseActivity activity = (LocalCoruseActivity) getActivity();
        LocalCoruseActivity.LocalCourseModel model = activity.getLocalCourseList(
                M3U8Uitl.FINISH, null, null);
        if (model.mLocalCourses.isEmpty()) {
            mListView.setAdapter(getEmptyAdapter());
            return;
        } else {
            ListAdapter adapter = mListView.getAdapter();
            if (!(adapter instanceof DownCourseListAdapter)) {
                mListView.setAdapter(mAdapter);
            }

            HashMap<Integer, ArrayList<LessonItem>> mLocalLessons = mAdapter.updateLocalLesson(
                    model.mLocalLessons);
            sortLesson(mLocalLessons);
            mAdapter.updateM3U8Model(model.m3U8DbModles);
            mAdapter.clear();
            mLocalCourses = model.mLocalCourses;
            mAdapter.addItems(model.mLocalCourses);
            mAdapter.expandAll(mListView);
        }

    }

    private ExpandListEmptyAdapter getEmptyAdapter()
    {
        ExpandListEmptyAdapter adapter = new ExpandListEmptyAdapter(
                mContext,
                R.layout.course_empty_layout,
                new String[]{ "暂无已下载课时" },
                R.drawable.lesson_download_empty_icon
        );

        return adapter;
    }

    /**
     * 排序
     * @param localLessons
     */
    private void sortLesson(HashMap<Integer, ArrayList<LessonItem>> localLessons)
    {
        for (int courseId : localLessons.keySet()) {
            ArrayList<LessonItem> lessonItems = localLessons.get(courseId);

            int size = lessonItems.size();
            LessonItem[] sortList = new LessonItem[size];
            for (int i=0; i < size; i++) {
                sortList[i] = lessonItems.get(i);
            }

            Arrays.sort(sortList, new Comparator<LessonItem>() {
                @Override
                public int compare(LessonItem lhs, LessonItem rhs) {
                    return lhs.number - rhs.number;
                }
            });

            for (int i=0; i < size; i++) {
                lessonItems.set(i, sortList[i]);
            }

            localLessons.put(courseId, lessonItems);
        }
    }

    protected void loadLocalCourseList()
    {
        mAdapter = new DownCourseListAdapter(mActivity, R.layout.course_download_list_item);
        LocalCoruseActivity activity = (LocalCoruseActivity) getActivity();
        LocalCoruseActivity.LocalCourseModel model = activity.getLocalCourseList(M3U8Uitl.FINISH, null, null);

        sortLesson(model.mLocalLessons);
        mAdapter.setM3U8Modles(model.m3U8DbModles);
        mLocalCourses = model.mLocalCourses;
        mAdapter.setLocalLessons(model.mLocalLessons);
        mAdapter.addItems(mLocalCourses);

        if (mLocalCourses.isEmpty()) {
            mListView.setAdapter(getEmptyAdapter());
        } else {
            mListView.setAdapter(mAdapter);
            mAdapter.expandAll(mListView);
        }
    }
}
