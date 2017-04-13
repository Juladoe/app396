package com.edusoho.kuozhi.clean.module.courseset.plan;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.view.FlowLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by DF on 2017/3/24.
 */

public class StudyPlanAdapter extends RecyclerView.Adapter<StudyPlanAdapter.StudyPlanViewHolder>
        implements View.OnClickListener {

    private List<CourseProject> mList;
    private List<VipInfo> mVipInfos;
    private Context mContext;
    private int maxIndex = -1;

    public StudyPlanAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList();
        this.mVipInfos = new ArrayList();
    }

    public void reFreshData(List<CourseProject> list, List<VipInfo> mVipInfos) {
        this.mList = list;
        this.mVipInfos = mVipInfos;
        int num = 0;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).studentNum > num) {
                num = mList.get(i).studentNum;
                maxIndex = i;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public StudyPlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StudyPlanViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_plan, parent, false));
    }

    @Override
    public void onBindViewHolder(StudyPlanViewHolder holder, int position) {
        CourseProject courseStudyPlan = mList.get(position);
        holder.mRlItem.setOnClickListener(this);
        holder.mFlayout.removeAllViews();
        holder.mClassType.setText(courseStudyPlan.title);
        holder.mTask.setText(String.format(mContext.getString(R.string.course_task_num), courseStudyPlan.taskNum));
        loadPrice(holder, courseStudyPlan);
        loadService(holder, courseStudyPlan);
        loadHot(holder, position);
        loadVip(holder, courseStudyPlan);
        holder.mRlItem.setTag(position);
        holder.mRlItem.setOnClickListener(getOnClickListener());
    }

    private void loadPrice(StudyPlanViewHolder holder, CourseProject courseStudyPlan) {
        if ("1".equals(courseStudyPlan.isFree)) {
            holder.mPrice.setText(R.string.free_course_project);
            holder.mPrice.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
        } else {
            holder.mPrice.setText(String.format("¥ %.2f", courseStudyPlan.price));
            holder.mPrice.setTextColor(ContextCompat.getColor(mContext, R.color.secondary_color));
        }
    }

    private void loadService(StudyPlanViewHolder holder, CourseProject courseStudyPlan) {
        if (courseStudyPlan.services.length != 0) {
            holder.mService.setVisibility(View.VISIBLE);
            holder.mFlayout.setVisibility(View.VISIBLE);
            addFlowItem(holder, courseStudyPlan.services);
        } else {
            holder.mService.setVisibility(View.GONE);
            holder.mFlayout.setVisibility(View.GONE);
        }
    }

    private void loadHot(StudyPlanViewHolder holder, int position) {
        if (mList.size() > 1) {
            if (maxIndex == position) {
                holder.mHot.setVisibility(View.VISIBLE);
            } else {
                holder.mHot.setVisibility(View.GONE);
            }
        } else {
            holder.mHot.setVisibility(View.GONE);
        }
    }

    private void loadVip(StudyPlanViewHolder holder, CourseProject courseStudyPlan) {
        holder.mVip.setVisibility(View.GONE);
        for (int i = 0; i < mVipInfos.size(); i++) {
            VipInfo vipInfo = mVipInfos.get(i);
            if (courseStudyPlan.vipLevelId == vipInfo.id) {
                holder.mVip.setVisibility(View.VISIBLE);
                holder.mVip.setText(String.format(mContext.getString(R.string.vip_free), vipInfo.name));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {

    }

    private void addFlowItem(StudyPlanViewHolder holder, CourseProject.Service[] services) {
        int ranHeight = AppUtil.dp2px(mContext, 16);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ranHeight);
        lp.setMargins(0, 0, AppUtil.dp2px(mContext, 10), 0);
        for (int i = 0; i < services.length; i++) {
            TextView serviceTextView = new TextView(mContext);
            serviceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            serviceTextView.setTextColor(mContext.getResources().getColor(R.color.primary_color));
            serviceTextView.setText(services[i].short_name);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = CommonUtil.dip2px(mContext, 10);
            serviceTextView.setLayoutParams(lp);
            serviceTextView.setBackgroundResource(R.drawable.course_project_services);

            holder.mFlayout.addView(serviceTextView, lp);
        }
    }

    public View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                CourseProjectActivity.launch(mContext, mList.get(position).id);
            }
        };
    }

    public static class StudyPlanViewHolder extends RecyclerView.ViewHolder {

        private final View mRlItem;
        private final View mHot;
        private final TextView mClassType;
        private final TextView mPrice;
        private final TextView mTask;
        private final FlowLayout mFlayout;
        private final TextView mVip;
        private final View mService;

        public StudyPlanViewHolder(View itemView) {
            super(itemView);
            mRlItem = itemView.findViewById(R.id.rl_item);
            mHot = itemView.findViewById(R.id.iv_hot);
            mClassType = (TextView) itemView.findViewById(R.id.tv_class_type);
            mPrice = (TextView) itemView.findViewById(R.id.tv_price);
            mTask = (TextView) itemView.findViewById(R.id.tv_task);
            mService = itemView.findViewById(R.id.tv_service);
            mFlayout = (FlowLayout) itemView.findViewById(R.id.fl_service);
            mVip = (TextView) itemView.findViewById(R.id.tv_vip);
        }
    }
}
