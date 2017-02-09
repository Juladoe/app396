package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.course.DownloadCourse;
import com.edusoho.kuozhi.v3.ui.fragment.mine.MyVideoCacheFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 2017/2/10.
 */

public class MyVideoCacheAdapter extends RecyclerView.Adapter<MyVideoCacheFragment.VideoCacheViewHolder> {

    private Context mContext;
    private List<DownloadCourse> mList;

    public MyVideoCacheAdapter(Context context) {
        this.mContext = context;
        mList = new ArrayList<>();
    }

    public void addData(List<DownloadCourse> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public MyVideoCacheFragment.VideoCacheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_download_manager_course_group, parent, false);
        return new MyVideoCacheFragment.VideoCacheViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyVideoCacheFragment.VideoCacheViewHolder viewHolder, int position) {
        final DownloadCourse course = mList.get(position);
        ImageLoader.getInstance().displayImage(course.getPicture(), viewHolder.ivCover);
        viewHolder.tvCourseTitle.setText(course.title);
        viewHolder.ivVideoSizes.setText(getCacheSize(viewHolder.ivVideoSizes.getContext(), course.getCachedSize()));
        viewHolder.ivVideoSum.setText(String.format(
                viewHolder.ivVideoSum.getResources().getString(R.string.download_size_cached),
                course.getCachedLessonNum()
        ));

        viewHolder.tvExpiredView.setVisibility(course.isExpird() ? View.VISIBLE : View.GONE);
        if ("classroom".equals(course.source)) {
            viewHolder.tvSource.setVisibility(View.VISIBLE);
            viewHolder.tvSource.setText(AppUtil.getColorTextAfter(
                    viewHolder.tvSource.getResources().getString(R.string.download_size_course_source),
                    course.getSourceName(),
                    Color.rgb(113, 119, 125)
            ));
        } else {
            viewHolder.tvSource.setVisibility(View.GONE);
            viewHolder.tvSource.setText("");
        }
        viewHolder.rlayoutContent.setTag(position);
        viewHolder.rlayoutContent.setOnClickListener(getItemClickListener());
    }

    private View.OnClickListener getItemClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                Bundle bundle = new Bundle();
                final DownloadCourse course = mList.get(position);
                if (course.isExpird()) {
                    ToastUtils.show(mContext, R.string.download_course_expird_timeout);
                    return;
                }
                bundle.putInt(Const.COURSE_ID, course.id);
                CoreEngine.create(mContext).runNormalPluginWithBundle("DownloadManagerActivity", mContext, bundle);
            }
        };
    }

    private String getCacheSize(Context context, long size) {
        float realSize = size / 1024.0f / 1024.0f;
        if (realSize == 0) {
            return "0M";
        } else {
            return String.format("%.1f%s", realSize, "M");
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }
}
