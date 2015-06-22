package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.DownloadingAdapter;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.DownloadManagerActivity1;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.view.EduSohoAnimWrap;

/**
 * Created by JesseHuang on 15/6/22.
 */
public class DownloadedFragment extends BaseFragment {
    private ExpandableListView mListview;
    private View mToolsLayout;
    private TextView mSelectAllBtn;
    private View mDelBtn;
    private DownloadingAdapter mDownloadedAdapter;
    private DownloadManagerActivity1 mActivityContainer;
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
        if (mActivityContainer != null) {
            app.startPlayCacheServer(mActivityContainer);
        }
    }

    @Override
    protected void initView(View view) {
        mToolsLayout = view.findViewById(R.id.download_tools_layout);
        mSelectAllBtn = (TextView) view.findViewById(R.id.tv_select_all);
        mDelBtn = view.findViewById(R.id.tv_delete);
        mListview = (ExpandableListView) view.findViewById(R.id.el_downloaded);
        mActivityContainer = (DownloadManagerActivity1) getActivity();
        DownloadManagerActivity1.LocalCourseModel finishModel = mActivityContainer.getLocalCourseList(M3U8Util.FINISH, null, null);
        mDownloadedAdapter = new DownloadingAdapter(mContext, mActivity, finishModel.m3U8DbModles, finishModel.mLocalCourses, finishModel.mLocalLessons, DownloadingAdapter.DownloadType.DOWNLOADED);
        mListview.setAdapter(mDownloadedAdapter);

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
                    DownloadManagerActivity1.LocalCourseModel model = mActivityContainer.getLocalCourseList(M3U8Util.UN_FINISH, null, null);
                    mDownloadedAdapter.updateLocalData(model.mLocalCourses, model.mLocalLessons);
                }
            }
        });

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
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
        mToolsLayout.measure(0, 0);
        AppUtil.animForHeight(
                new EduSohoAnimWrap(mToolsLayout), 0, mToolsLayout.getMeasuredHeight(), 320);
    }

    private void hideBtnLayout() {
        AppUtil.animForHeight(
                new EduSohoAnimWrap(mToolsLayout), mToolsLayout.getHeight(), 0, 240);
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
                DownloadManagerActivity1.LocalCourseModel model = mActivityContainer.getLocalCourseList(M3U8Util.FINISH, null, null);
                if (model.mLocalCourses.isEmpty()) {
                    //mListview.setAdapter(getEmptyAdapter());
                    return;
                } else {
                    mDownloadedAdapter.updateLocalData(model.mLocalCourses, model.mLocalLessons);
                }
            }
        }
    }
}

