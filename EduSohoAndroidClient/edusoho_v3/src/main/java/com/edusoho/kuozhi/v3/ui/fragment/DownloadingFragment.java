package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.DownloadingAdapter;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.DownloadManagerActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.view.EduSohoAnimWrap;

/**
 * Created by JesseHuang on 15/6/22.
 * 下载中
 */
public class DownloadingFragment extends BaseFragment {
    private ExpandableListView mListview;
    private View mToolsLayout;
    private TextView mSelectAllBtn;
    private View mDelBtn;
    private DownloadingAdapter mDownloadingAdapter;
    private DownloadManagerActivity mActivityContainer;
    public static final String UPDATE = "update";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_downloading);
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
        mListview = (ExpandableListView) view.findViewById(R.id.el_downloading);
        mActivityContainer = (DownloadManagerActivity) getActivity();
        DownloadManagerActivity.LocalCourseModel unFinishModel = mActivityContainer.getLocalCourseList(M3U8Util.UN_FINISH, null, null);
        mDownloadingAdapter = new DownloadingAdapter(mContext, mActivity, unFinishModel.m3U8DbModles, unFinishModel.mLocalCourses, unFinishModel.mLocalLessons, DownloadingAdapter.DownloadType.DOWNLOADING);
        mListview.setAdapter(mDownloadingAdapter);

        mSelectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                if (tv.getText().equals("全选")) {
                    tv.setText("取消");
                    mDownloadingAdapter.isSelectAll(true);
                } else {
                    tv.setText("全选");
                    mDownloadingAdapter.isSelectAll(false);
                }
            }
        });

        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivityContainer != null) {
                    mActivityContainer.clearLocalCache(mDownloadingAdapter.getSelectLessonId());
                    DownloadManagerActivity.LocalCourseModel model = mActivityContainer.getLocalCourseList(M3U8Util.UN_FINISH, null, null);
                    mDownloadingAdapter.updateLocalData(model.mLocalCourses, model.mLocalLessons);
                }
            }
        });
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
            updateLocalCourseList(message.data.getInt(Const.LESSON_ID));
        }
    }

    private void updateLocalCourseList(int lessonId) {
        M3U8DbModle m3u8Model = M3U8Util.queryM3U8Modle(
                mContext, app.loginUser.id, lessonId, app.domain, M3U8Util.ALL);
        if (m3u8Model.finish == M3U8Util.FINISH) {
            if (mActivityContainer != null) {
                DownloadManagerActivity.LocalCourseModel model = mActivityContainer.getLocalCourseList(M3U8Util.UN_FINISH, null, null);
                mDownloadingAdapter.updateLocalData(model.mLocalCourses, model.mLocalLessons);
                Bundle bundle = new Bundle();
                bundle.putInt(Const.LESSON_ID, lessonId);
                app.sendMessage(DownloadedFragment.FINISH, bundle);
            }
        } else {
            mDownloadingAdapter.updateProgress(lessonId, m3u8Model);
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
                mDownloadingAdapter.setSelectShow(false);
            } else {
                showBtnLayout();
                item.setTitle("取消");
                mDownloadingAdapter.setSelectShow(true);
            }
            item.setChecked(!item.isChecked());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showBtnLayout() {
        mToolsLayout.measure(0, 0);
        AppUtil.animForHeight(
                new EduSohoAnimWrap(mToolsLayout), 0, mToolsLayout.getMeasuredHeight(), 320);
    }

    private void hideBtnLayout() {
        AppUtil.animForHeight(
                new EduSohoAnimWrap(mToolsLayout), mToolsLayout.getHeight(), 0, 240);
    }
}
