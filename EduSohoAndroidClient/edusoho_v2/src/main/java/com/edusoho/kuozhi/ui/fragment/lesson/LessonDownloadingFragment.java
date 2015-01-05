package com.edusoho.kuozhi.ui.fragment.lesson;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.LocalCourseListAdapter;
import com.edusoho.kuozhi.adapter.lesson.LocalLessonDownListAdapter;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.course.LocalCoruseActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;
import com.edusoho.kuozhi.util.SqliteUtil;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;

import java.io.File;
import java.util.ArrayList;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by howzhi on 14/12/11.
 */
public class LessonDownloadingFragment extends BaseFragment {

    protected EduSohoListView mListView;
    protected ArrayList<Course> mLocalCourses;
    protected LocalCourseListAdapter mAdapter;

    public static final String UPDATE = "update";

    private View mToolsLayout;
    private TextView mSelectAllBtn;
    private View mDelBtn;
    protected TextView mDeviceSpaceInfo;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public MessageType[] getMsgTypes() {
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(UPDATE)
        };
        return messageTypes;
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        String type = message.type.type;
        if (UPDATE.equals(type)) {
            updateLocalCourseList(
                    message.data.getInt(Const.COURSE_ID),
                    message.data.getInt(Const.LESSON_ID)
            );
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.offline_menu_edit, menu);
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
        File videosDir = new File(workSpace, "videos");
        for (int id : ids) {
            FileUtils.deleteFile(new File(videosDir, String.valueOf(id)).getAbsolutePath());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setContainerView(R.layout.lesson_downloading_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mToolsLayout = view.findViewById(R.id.download_tools_layout);
        mSelectAllBtn = (TextView) view.findViewById(R.id.download_select_all_btn);
        mDelBtn = view.findViewById(R.id.download_del_btn);
        mDeviceSpaceInfo = (TextView) view.findViewById(R.id.download_device_info);
        mListView = (EduSohoListView) view.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(linearLayoutManager);
        mListView.setEmptyString(
                new String[]{"没有正在下载视频"},
                R.drawable.lesson_download_empty_icon
        );

        mAdapter = new LocalCourseListAdapter(mActivity, R.layout.course_download_list_item);
        mListView.setAdapter(mAdapter);
        mListView.setFixHeight(EdusohoApp.screenH - mActivity.mActionBar.getHeight());

        loadDeviceSpaceInfo();
        loadLocalCourseList();

        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocalCourses == null || mLocalCourses.isEmpty()) {
                    return;
                }
                clearLocalCache(mAdapter.getSelectLessonId());
                loadLocalCourseList();
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

    protected void loadLocalCourseList()
    {
        LocalCoruseActivity activity = (LocalCoruseActivity) getActivity();
        LocalCoruseActivity.LocalCourseModel model = activity.getLocalCourseList(
                M3U8Uitl.UN_FINISH, null, null);
        mAdapter.setM3U8Modles(model.m3U8DbModles);
        mLocalCourses = model.mLocalCourses;
        mAdapter.setLocalLessons(model.mLocalLessons);
        mListView.pushData(mLocalCourses);
    }

    private void updateLocalCourseList(int courseId, int lessonId)
    {
        M3U8DbModle m3U8DbModle = M3U8Uitl.queryM3U8Modle(
                mContext, app.loginUser.id, lessonId, app.domain, M3U8Uitl.ALL);
        if (m3U8DbModle.finish == M3U8Uitl.FINISH) {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.COURSE_ID, courseId);
            bundle.putInt(Const.LESSON_ID, lessonId);
            app.sendMessage(LessonDownloadedFragment.FINISH, bundle);
        }

        mAdapter.updateM3U8Modles(courseId, lessonId, m3U8DbModle);
        mListView.refreshData(LocalCourseListAdapter.UPDATE);
    }
}
