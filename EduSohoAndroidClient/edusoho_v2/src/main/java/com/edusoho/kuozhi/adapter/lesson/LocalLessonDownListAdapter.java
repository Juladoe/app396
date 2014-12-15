package com.edusoho.kuozhi.adapter.lesson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.Review;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 14/12/2.
 */
public class LocalLessonDownListAdapter
        extends RecyclerViewListBaseAdapter<LessonItem, LocalLessonDownListAdapter.ViewHolder> {

    private SparseArray<M3U8DbModle> mM3U8DbModles;

    public LocalLessonDownListAdapter(Context context, int resource)
    {
        super(context, resource);
    }

    @Override
    public void addItem(LessonItem item) {
        if (mList.add(item)) {
            notifyDataSetChanged();
        }
    }

    public void setM3U8Modles(SparseArray<M3U8DbModle> m3U8DbModles)
    {
        this.mM3U8DbModles = m3U8DbModles;
    }

    @Override
    public void addItems(List<LessonItem> list) {
        if (mList.addAll(list)) {
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(mResource, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        LessonItem lessonItem = mList.get(i);
        viewHolder.mLessonTitle.setText(lessonItem.title);

        long downloadSize = getLocalLessonSize(lessonItem.id);
        long totalSize = 0;
        if (mM3U8DbModles != null) {
            M3U8DbModle modle = mM3U8DbModles.get(lessonItem.id);
            if (modle != null) {
                viewHolder.mDownloadProgressBar.setMax(modle.totalNum);
                viewHolder.mDownloadProgressBar.setProgress(modle.downloadNum);
                totalSize = (int) (downloadSize * (modle.totalNum / (float) modle.downloadNum));
            }
        }

        viewHolder.mDownloadInfo.setText(
                String.format("%s/%s", formatSize(downloadSize), formatSize(totalSize)));
    }

    private long getLocalLessonSize(int lessonId)
    {
        File workSpace = EdusohoApp.getWorkSpace();
        StringBuffer dirBuilder = new StringBuffer(workSpace.getAbsolutePath());
        dirBuilder.append("/videos/")
                .append(EdusohoApp.app.domain)
                .append("/")
                .append(lessonId);
        File lessonDir = new File(dirBuilder.toString());
        long totalSize = 0;
        if (!lessonDir.exists()) {
            return totalSize;
        }
        for (File file : lessonDir.listFiles()) {
            totalSize += file.length();
        }

        return totalSize;
    }

    private String formatSize(long totalSize)
    {
        float kb = totalSize / 1024.0f / 1024.0f;
        return String.format("%.1f%s", kb, "M");
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mLessonTitle;
        public TextView mDownloadInfo;
        public ProgressBar mDownloadProgressBar;
        public ViewHolder(View view){
            super(view);

            mLessonTitle = (TextView) view.findViewById(R.id.lesson_title);
            mDownloadInfo = (TextView) view.findViewById(R.id.lesson_download_info);
            mDownloadProgressBar = (ProgressBar) view.findViewById(R.id.lesson_download_progress);
        }
    }
}
