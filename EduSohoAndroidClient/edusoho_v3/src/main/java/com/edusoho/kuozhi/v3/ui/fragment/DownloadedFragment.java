package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.DownloadingAdapter;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.DownloadManagerActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;

/**
 * Created by JesseHuang on 15/6/22.
 */
public class DownloadedFragment extends BaseFragment {
    private ExpandableListView mListView;
    private View mToolsLayout;
    private TextView mSelectAllBtn;
    private View mDelBtn;
    private DownloadingAdapter mDownloadedAdapter;
    private DownloadManagerActivity mActivityContainer;
    public static final String FINISH = "finish";


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_downloaded);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void initView(View view) {
        mToolsLayout = view.findViewById(R.id.download_tools_layout);
        mSelectAllBtn = (TextView) view.findViewById(R.id.tv_select_all);
        mDelBtn = view.findViewById(R.id.tv_delete);
        mListView = (ExpandableListView) view.findViewById(R.id.el_downloaded);
        mActivityContainer = (DownloadManagerActivity) getActivity();
        DownloadManagerActivity.LocalCourseModel finishModel = mActivityContainer.getLocalCourseList(M3U8Util.FINISH, null, null);
        mDownloadedAdapter = new DownloadingAdapter(mContext, mActivity, finishModel.m3U8DbModles, finishModel.mLocalCourses, finishModel.mLocalLessons,
                DownloadingAdapter.DownloadType.DOWNLOADED, R.layout.item_downloaded_manager_lesson_child);
        mListView.setAdapter(mDownloadedAdapter);

        mSelectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                if (tv.getText().equals("全选")) {
                    tv.setText("取消");
                    mDownloadedAdapter.isSelectAll(true);
                } else {
                    tv.setText("全选");
                    mDownloadedAdapter.isSelectAll(false);
                }
            }
        });

        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivityContainer != null) {
                    mActivityContainer.clearLocalCache(mDownloadedAdapter.getSelectLessonId());
                    DownloadManagerActivity.LocalCourseModel model = mActivityContainer.getLocalCourseList(M3U8Util.UN_FINISH, null, null);
                    mDownloadedAdapter.updateLocalData(model.mLocalCourses, model.mLocalLessons);
                }
            }
        });

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (mToolsLayout.getVisibility() == View.GONE) {
                    final LessonItem lessonItem = mDownloadedAdapter.getChild(groupPosition, childPosition);
                    app.mEngine.runNormalPlugin(
                            LessonActivity.TAG, mContext, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    startIntent.putExtra(Const.COURSE_ID, lessonItem.courseId);
                                    startIntent.putExtra(LessonActivity.FROM_CACHE, true);
                                    startIntent.putExtra(Const.FREE, lessonItem.free);
                                    startIntent.putExtra(Const.LESSON_ID, lessonItem.id);
                                    startIntent.putExtra(Const.LESSON_TYPE, lessonItem.type);
                                    startIntent.putExtra(Const.ACTIONBAR_TITLE, lessonItem.title);
                                }
                            }
                    );
                } else {
                    mDownloadedAdapter.setItemDownloadStatus(groupPosition, childPosition);
                }
                return false;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("setOnItemClickListener", "1");
            }
        });

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (v.getTag() instanceof DownloadingAdapter.GroupPanel && mToolsLayout.getVisibility() == View.VISIBLE) {
                    DownloadingAdapter.GroupPanel gp = (DownloadingAdapter.GroupPanel) v.getTag();
                    if (parent.isGroupExpanded(groupPosition)) {
                        gp.ivIndicator.setText(getString(R.string.font_less));
                    } else {
                        gp.ivIndicator.setText(getString(R.string.font_more));
                    }
                }
                return false;
            }
        });

        for (int i = 0; i < mDownloadedAdapter.getGroupCount(); i++) {
            mListView.expandGroup(i);
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
                mDownloadedAdapter.setSelectShow(false);
            } else {
                showBtnLayout();
                item.setTitle("取消");
                mDownloadedAdapter.setSelectShow(true);
            }
            item.setChecked(!item.isChecked());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showBtnLayout() {
        mToolsLayout.setVisibility(View.VISIBLE);
    }

    private void hideBtnLayout() {
        mToolsLayout.setVisibility(View.GONE);
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
            if (mActivityContainer != null) {
                DownloadManagerActivity.LocalCourseModel model = mActivityContainer.getLocalCourseList(M3U8Util.FINISH, null, null);
                if (model.mLocalCourses.isEmpty()) {
                } else {
                    mDownloadedAdapter.updateLocalData(model.mLocalCourses, model.mLocalLessons);
                }
            }
        }
    }
}

