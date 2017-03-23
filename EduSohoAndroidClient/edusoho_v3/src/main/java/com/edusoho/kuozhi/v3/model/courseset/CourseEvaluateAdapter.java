package com.edusoho.kuozhi.v3.model.courseset;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.course.CourseReview;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DF on 2017/3/23.
 */

public class CourseEvaluateAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<CourseReview> mList;
    private View mItem;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE     = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE     = 2;

    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    public CourseEvaluateAdapter(Context mContext) {
        this.mContext = mContext;
        this.mList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            mItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.foot_item, parent, false);
            return new FooterViewHolder(mItem);
        }else {
            mItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unjoin_evaluate, parent, false);
            return new EvaluateViewHolder(mItem);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EvaluateViewHolder) {
            CourseReview courseReview = mList.get(position);
            EvaluateViewHolder evaluateViewHolder = (EvaluateViewHolder) holder;
            // TODO: 2017/3/23     评论来自哪个计划
            evaluateViewHolder.mTvName.setText(courseReview.getUser().nickname);
            evaluateViewHolder.mTvTime.setText(CommonUtil.convertWeekTime(courseReview.getCreatedTime()));
            evaluateViewHolder.mTVDesc.setText(courseReview.getContent());
            evaluateViewHolder.mRvStar.setRating((int) Double.parseDouble(courseReview.getRating()));
            ImageLoader.getInstance().displayImage(courseReview.getUser().getMediumAvatar(), evaluateViewHolder.mIvUserIcon);
        } else {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            switch (mLoadMoreStatus) {
                case PULLUP_LOAD_MORE:
                    footerViewHolder.mTvLoadText.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footerViewHolder.mTvLoadText.setText("正加载更多...");
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = mList.size();
        return count == 0 ? 0 : count + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private static class EvaluateViewHolder extends RecyclerView.ViewHolder{

        private ImageView mIvUserIcon;
        private TextView mTvName;
        private TextView mTvTime;
        private TextView mTvFrom;
        private TextView mTVDesc;
        private ReviewStarView mRvStar;

        public EvaluateViewHolder(View itemView) {
            super(itemView);
            mIvUserIcon = (ImageView) itemView.findViewById(R.id.iv_user_icon);
            mTvName = (TextView) itemView.findViewById(R.id.tv_evaluate_name);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_evaluate_time);
            mTvFrom = (TextView) itemView.findViewById(R.id.tv_evaluate_from);
            mTVDesc = (TextView) itemView.findViewById(R.id.tv_evaluate_desc);
            mRvStar = (ReviewStarView) itemView.findViewById(R.id.rv_evaluate_star);
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLoadLayout;
        private TextView mTvLoadText;

        private FooterViewHolder(View itemView) {
            super(itemView);
            mLoadLayout = (LinearLayout) itemView.findViewById(R.id.ll_load);
            mTvLoadText =  (TextView) itemView.findViewById(R.id.tv_load);
        }
    }
}
