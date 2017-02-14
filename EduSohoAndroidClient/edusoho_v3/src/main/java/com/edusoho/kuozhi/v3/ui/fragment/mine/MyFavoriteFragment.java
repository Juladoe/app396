package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.MyFavoriteAdapter;
import com.edusoho.kuozhi.v3.entity.course.LearningCourse;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 2017/2/7.
 */

public class MyFavoriteFragment extends BaseFragment {

    private RecyclerView rvContent;
    private View viewEmpty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_mine_tab);
    }

    @Override
    protected void initView(View view) {
        rvContent = (RecyclerView) view.findViewById(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));

        View viewBreakline = view.findViewById(R.id.v_breakline);
        viewBreakline.setVisibility(View.GONE);

        viewEmpty = view.findViewById(R.id.view_empty);
        viewEmpty.setVisibility(View.GONE);
        initData();
    }

    private void initData() {
        final MyFavoriteAdapter myFavoriteAdapter = new MyFavoriteAdapter(getActivity());
        rvContent.setAdapter(myFavoriteAdapter);
        CourseDetailModel.getNormalCollect(1000, 0, new ResponseCallbackListener<LearningCourse>() {
            @Override
            public void onSuccess(final LearningCourse liveCourseList) {
                myFavoriteAdapter.addDatas(liveCourseList.data);
                CourseDetailModel.getLiveCollect(1000, 0, new ResponseCallbackListener<LearningCourse>() {
                    @Override
                    public void onSuccess(LearningCourse courseList) {
                        myFavoriteAdapter.addDatas(courseList.data);
                        if (myFavoriteAdapter.getItemCount() == 0) {
                            setNoCourseDataVisible(true);
                        } else {
                            setNoCourseDataVisible(false);
                        }
                    }

                    @Override
                    public void onFailure(String code, String message) {
                        ToastUtils.show(mContext, message);
                    }
                });
            }

            @Override
            public void onFailure(String code, String message) {
                ToastUtils.show(mContext, message);
                setNoCourseDataVisible(true);
            }
        });
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

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        public View recyclerViewItem;
        public ImageView ivPic;
        public TextView tvAddNum;
        public TextView tvTitle;
        public TextView tvMore;
        public View layoutLive;
        public TextView tvLiveIcon;
        public TextView tvLive;
        public View vLine;

        public FavoriteViewHolder(View view) {
            super(view);
            recyclerViewItem = view.findViewById(R.id.llayout_favorite_content);
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            tvAddNum = (TextView) view.findViewById(R.id.tv_add_num);
            tvMore = (TextView) view.findViewById(R.id.tv_more);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            vLine = view.findViewById(R.id.v_line);
            layoutLive = view.findViewById(R.id.layout_live);
            tvLiveIcon = (TextView) view.findViewById(R.id.tv_live_icon);
            tvLive = (TextView) view.findViewById(R.id.tv_live);
        }
    }
}
