package com.edusoho.kuozhi.clean.module.courseset.plan;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.FlowLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by DF on 2017/3/24.
 */

public class StudyPlanAdapter extends RecyclerView.Adapter<StudyPlanAdapter.StudyPlanViewHolder>
        implements View.OnClickListener {

    private List<CourseStudyPlan> mList;
    private Context mContext;

    public StudyPlanAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList();
    }

    public void reFreshData(List<CourseStudyPlan> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public StudyPlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StudyPlanViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_plan, parent, false));
    }

    @Override
    public void onBindViewHolder(StudyPlanViewHolder holder, int position) {
        holder.mRlItem.setOnClickListener(this);
        holder.mFlayout.removeAllViews();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {

    }

    private void addFlowItem(StudyPlanViewHolder holder, String text) {
        int ranHeight = AppUtil.dp2px(mContext, 18);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ranHeight);
        lp.setMargins(0, AppUtil.dp2px(mContext, 3), AppUtil.dp2px(mContext, 10), AppUtil.dp2px(mContext, 2));
        TextView tv = new TextView(mContext);
        tv.setPadding(AppUtil.dp2px(mContext, 3), 0, AppUtil.dp2px(mContext, 3), 0);
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setLines(1);
        tv.setBackgroundResource(R.drawable.common_circular_bg);
        holder.mFlayout.addView(tv, lp);
    }

    public static class StudyPlanViewHolder extends RecyclerView.ViewHolder {

        private final View mRlItem;
        private final View mHasJoin;
        private final View mSymbol;
        private final View mIvHot;
        private final TextView mTvClassType;
        private final TextView mTvPrice;
        private final TextView mTvTask;
        private final FlowLayout mFlayout;
        private final TextView mTvVip;

        public StudyPlanViewHolder(View itemView) {
            super(itemView);
            mRlItem = itemView.findViewById(R.id.rl_item);
            mHasJoin = itemView.findViewById(R.id.tv_has_join);
            mSymbol = itemView.findViewById(R.id.tv_symbol);
            mIvHot = itemView.findViewById(R.id.iv_hot);
            mTvClassType = (TextView) itemView.findViewById(R.id.tv_class_type);
            mTvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            mTvTask = (TextView) itemView.findViewById(R.id.tv_task);
            mFlayout = (FlowLayout) itemView.findViewById(R.id.fl_service);
            mTvVip = (TextView) itemView.findViewById(R.id.tv_vip);
        }
    }
}
