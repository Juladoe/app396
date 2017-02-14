package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.MyVideoCacheAdapter;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CourseCacheHelper;
import com.edusoho.kuozhi.v3.util.M3U8Util;

/**
 * Created by JesseHuang on 2017/2/10.
 */

public class MyVideoCacheFragment extends BaseFragment {

    private RecyclerView rvContent;
    private View viewEmpty;
    private CourseCacheHelper mCourseCacheHelper;
    private MyVideoCacheAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_mine_tab);
    }

    @Override
    protected void initView(View view) {
        rvContent = (RecyclerView) view.findViewById(R.id.rv_content);
        viewEmpty = view.findViewById(R.id.view_empty);

        View viewBreakline = view.findViewById(R.id.v_breakline);
        viewBreakline.setVisibility(View.GONE);

        viewEmpty.setVisibility(View.GONE);
        rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initData() {
        mAdapter = new MyVideoCacheAdapter(mContext);
        rvContent.setAdapter(mAdapter);
    }

    private void loadData() {
        User user = getAppSettingProvider().getCurrentUser();
        if (user == null) {
            return;
        }
        School school = getAppSettingProvider().getCurrentSchool();
        mCourseCacheHelper = new CourseCacheHelper(getContext(), school.getDomain(), user.id);

        mAdapter.setData(mCourseCacheHelper.getLocalCourseList(M3U8Util.ALL, null, null));
        if (mAdapter.getItemCount() == 0) {
            setNoCourseDataVisible(true);
        } else {
            setNoCourseDataVisible(false);
        }
    }

    private void setNoCourseDataVisible(boolean visible) {
        if (visible) {
            viewEmpty.setVisibility(View.VISIBLE);
            rvContent.setVisibility(View.GONE);
        } else {
            viewEmpty.setVisibility(View.GONE);
            rvContent.setVisibility(View.VISIBLE);
        }
    }

    public static class VideoCacheViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivCover;
        public TextView tvCourseTitle;
        public TextView tvSource;
        public TextView ivVideoSum;
        public TextView ivVideoSizes;
        public TextView tvExpiredView;
        public View rlayoutContent;

        public VideoCacheViewHolder(View view) {
            super(view);
            ivCover = (ImageView) view.findViewById(R.id.iv_avatar);
            tvCourseTitle = (TextView) view.findViewById(R.id.tv_course_title);
            ivVideoSum = (TextView) view.findViewById(R.id.tv_video_sum);
            ivVideoSizes = (TextView) view.findViewById(R.id.tv_video_size);
            tvSource = (TextView) view.findViewById(R.id.tv_download_source);
            tvExpiredView = (TextView) view.findViewById(R.id.tv_download_expird);
            rlayoutContent = view.findViewById(R.id.rlayout_cache_layout);
        }
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
