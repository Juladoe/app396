package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.MyFavoriteAdapter;
import com.edusoho.kuozhi.v3.entity.course.LearningCourse;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 2017/2/7.
 */

public class MyFavoriteFragment extends BaseFragment {

    private RecyclerView rvFavorite;
//    private View viewEmpty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_my_favorite);
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void initView(View view) {
        rvFavorite = (RecyclerView) view.findViewById(R.id.rv_content);
//        viewEmpty = view.findViewById(R.id.view_empty);
//        viewEmpty.setVisibility(View.GONE);
        rvFavorite.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initData() {
        final List<Course> favoriteCourse = new ArrayList<>();
        final MyFavoriteAdapter myFavoriteAdapter = new MyFavoriteAdapter(favoriteCourse, getActivity());
        rvFavorite.setAdapter(myFavoriteAdapter);
        CourseDetailModel.getLiveCollect(100, 0, new ResponseCallbackListener<LearningCourse>() {
            @Override
            public void onSuccess(final LearningCourse liveCourseList) {
                myFavoriteAdapter.addDatas(liveCourseList.data);
                CourseDetailModel.getNormalCollect(100, 0, new ResponseCallbackListener<LearningCourse>() {
                    @Override
                    public void onSuccess(LearningCourse courseList) {
                        myFavoriteAdapter.addDatas(courseList.data);
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
//                viewEmpty.setVisibility(View.VISIBLE);
            }
        });
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
