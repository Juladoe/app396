package com.edusoho.kuozhi.clean.module.courseset.plan;

import android.content.Context;
import android.graphics.Paint;
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

public class CourseProjectsAdapter extends RecyclerView.Adapter<CourseProjectsAdapter.CourseProjectViewHolder>{

    private static final String IS_FREE = "1";

    private List<CourseProject> mList;
    private List<VipInfo> mVipInfos;
    private Context mContext;
    private int maxIndex = -1;

    CourseProjectsAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList<>();
        this.mVipInfos = new ArrayList<>();
    }

    void reFreshData(List<CourseProject> list, List<VipInfo> mVipInfos) {
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
    public CourseProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CourseProjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_plan, parent, false));
    }

    @Override
    public void onBindViewHolder(CourseProjectViewHolder holder, int position) {
        CourseProject courseProject = mList.get(position);
        holder.mFlayout.removeAllViews();
        holder.mClassType.setText(courseProject.title);
        holder.mTask.setText(String.format(mContext.getString(R.string.course_task_num), courseProject.taskNum));
        loadPrice(holder, courseProject);
        loadService(holder, courseProject);
        loadHot(holder, position);
        loadVip(holder, courseProject);
        holder.mRlItem.setTag(position);
        holder.mRlItem.setOnClickListener(getOnClickListener());
    }

    private void loadPrice(CourseProjectViewHolder holder, CourseProject courseProject) {
        if (IS_FREE.equals(courseProject.isFree)) {
            holder.mDiscount.setVisibility(View.GONE);
            holder.mOriginalPrice.setVisibility(View.GONE);
            holder.mPrice.setText(R.string.free_course_project);
            holder.mPrice.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
        } else {
            holder.mPrice.setTextColor(ContextCompat.getColor(mContext, R.color.secondary_color));
            holder.mPrice.setText(String.format(mContext.getString(R.string.yuan_symbol), courseProject.price));
            if (courseProject.price == courseProject.originPrice) {
                holder.mDiscount.setVisibility(View.GONE);
                holder.mOriginalPrice.setVisibility(View.GONE);
                return;
            }
            holder.mDiscount.setVisibility(View.VISIBLE);
            holder.mOriginalPrice.setVisibility(View.VISIBLE);
            holder.mOriginalPrice.setText(String.format(mContext.getString(R.string.yuan_symbol), courseProject.originPrice));
            holder.mOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void loadService(CourseProjectViewHolder holder, CourseProject courseProject) {
        if (courseProject.services.length != 0) {
            holder.mService.setVisibility(View.VISIBLE);
            holder.mFlayout.setVisibility(View.VISIBLE);
            addFlowItem(holder, courseProject.services);
        } else {
            holder.mService.setVisibility(View.GONE);
            holder.mFlayout.setVisibility(View.GONE);
        }
    }

    private void loadHot(CourseProjectViewHolder holder, int position) {
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

    private void loadVip(CourseProjectViewHolder holder, CourseProject courseProject) {
        holder.mVip.setVisibility(View.GONE);
        for (int i = 0; i < mVipInfos.size(); i++) {
            VipInfo vipInfo = mVipInfos.get(i);
            if (courseProject.vipLevelId == vipInfo.id) {
                holder.mVip.setVisibility(View.VISIBLE);
                holder.mVip.setText(String.format(mContext.getString(R.string.vip_free), vipInfo.name));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void addFlowItem(CourseProjectViewHolder holder, CourseProject.Service[] services) {
        int ranHeight = AppUtil.dp2px(mContext, 16);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ranHeight);
        lp.setMargins(0, 0, AppUtil.dp2px(mContext, 10), 0);
        for (CourseProject.Service service : services) {
            TextView serviceTextView = new TextView(mContext);
            serviceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            serviceTextView.setTextColor(ContextCompat.getColor(mContext, R.color.primary_color));
            serviceTextView.setText(service.shortName);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = CommonUtil.dip2px(mContext, 10);
            serviceTextView.setLayoutParams(lp);
            serviceTextView.setBackgroundResource(R.drawable.course_project_services);

            holder.mFlayout.addView(serviceTextView, lp);
        }
    }

    private View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                CourseProjectActivity.launch(mContext, mList.get(position).id);
            }
        };
    }

    static class CourseProjectViewHolder extends RecyclerView.ViewHolder {

        private final View mRlItem;
        private final View mHot;
        private final View mDiscount;
        private final TextView mClassType;
        private final TextView mPrice;
        private final TextView mOriginalPrice;
        private final TextView mTask;
        private final FlowLayout mFlayout;
        private final TextView mVip;
        private final View mService;

        CourseProjectViewHolder(View itemView) {
            super(itemView);
            mDiscount = itemView.findViewById(R.id.tv_discount);
            mOriginalPrice = (TextView) itemView.findViewById(R.id.tv_price_old);
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
