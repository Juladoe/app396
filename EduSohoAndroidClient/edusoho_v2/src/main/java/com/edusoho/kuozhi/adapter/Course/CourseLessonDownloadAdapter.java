package com.edusoho.kuozhi.adapter.Course;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.model.status.DownLoadStatus;

import java.util.List;

/**
 * Created by howzhi on 14/12/10.
 */
public class CourseLessonDownloadAdapter
        extends RecyclerViewListBaseAdapter<LessonItem, CourseLessonDownloadAdapter.ViewHolder> {

    public SparseArray<M3U8DbModle> mLessonIds;

    public CourseLessonDownloadAdapter(Context context, int resource)
    {
        super(context, resource);
    }

    @Override
    public void addItems(List<LessonItem> list) {
        mList.addAll(list);
        int start = mList.size();
        notifyItemRangeInserted(start, list.size());
    }

    public boolean isCanClick(LessonItem lessonItem)
    {
        return mLessonIds.get(lessonItem.id) == null;
    }

    public void setLessonIds(SparseArray<M3U8DbModle> lessonIds)
    {
        this.mLessonIds = lessonIds;
    }

    public void updateLessonIds(int lessonId, M3U8DbModle m3U8DbModle)
    {
        this.mLessonIds.put(lessonId, m3U8DbModle);
        notifyDataSetChanged();
    }

    @Override
    public void addItem(LessonItem item) {
        mList.add(item);
        notifyItemInserted(mList.size() - 1);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        LessonItem lessonItem = mList.get(i);

        viewHolder.mTitle.setText(lessonItem.title);
        viewHolder.mLessonType.setCompoundDrawablesWithIntrinsicBounds(
                mContext.getResources().getDrawable(R.drawable.lesson_item_sound), null, null, null);
        viewHolder.mLessonType.setText(lessonItem.length);

        M3U8DbModle m3U8DbModle = mLessonIds.get(lessonItem.id);
        viewHolder.mDownloadCheckBox.setText("");
        if (m3U8DbModle == null) {
            viewHolder.mDownloadCheckBox.setChecked(false);
        } else {
            if (m3U8DbModle.totalNum > 0 && m3U8DbModle.totalNum == m3U8DbModle.downloadNum) {
                viewHolder.mDownloadCheckBox.setChecked(true);
                return;
            }
            viewHolder.mDownloadCheckBox.setChecked(false);
            viewHolder.mDownloadCheckBox.setText(
                    (int)(m3U8DbModle.downloadNum / (float) m3U8DbModle.totalNum * 100) + "%");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(mResource, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mLessonType;
        public TextView mTitle;
        public CheckBox mDownloadCheckBox;

        public ViewHolder(View view){
            super(view);

            mTitle = (TextView) view.findViewById(R.id.course_details_lesson_title);
            mLessonType = (TextView) view.findViewById(R.id.course_details_lesson_type);
            mDownloadCheckBox = (CheckBox) view.findViewById(R.id.course_lesson_donwload_checkbox);
        }
    }
}
