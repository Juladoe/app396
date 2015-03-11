package com.edusoho.kuozhi.adapter.Course;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.model.LearnStatus;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.view.ESTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Melomelon on 2015/2/2.
 */
public class LiveCourseLessonAdapter
        extends RecyclerViewListBaseAdapter<LessonItem,LiveCourseLessonAdapter.BaseViewHolder> {


    private ActionBarBaseActivity mActivity;
//    private View mHeadView;
    private HashMap<Integer, LearnStatus> mUserLearns;
//    SparseArray<M3U8DbModle> mM3U8DbModles;

    public LiveCourseLessonAdapter(ActionBarBaseActivity activity, int resource) {
        super(activity, resource);
        this.mActivity = activity;
    }

    @Override
    public void addItem(LessonItem item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void updateLearnStatus(HashMap<Integer, LearnStatus> userLearns)
    {
        this.mUserLearns = userLearns;
    }

    @Override
    public void addItems(List<LessonItem> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View v = LayoutInflater.from(mContext).inflate(mResource, viewGroup, false);
        switch (type) {
            case VIEW_TYPE_HEADER:
                return null;
//                return new HeadViewHolder(mHeadView);
            case VIEW_TYPE_CONTENT:
                return new ViewHolder(v);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
//        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
        return VIEW_TYPE_CONTENT;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder vh, int i) {
//        if (getItemViewType(i) == VIEW_TYPE_HEADER) {
//            return;
//        }
        super.onBindViewHolder(vh, i);
        LessonItem lessonItem = mList.get(i);

        ViewHolder viewHolder = (ViewHolder) vh;
        LessonItem.ItemType itemType = LessonItem.ItemType.cover(lessonItem.itemType);
        switch (itemType) {
            case LESSON:
                setLessonInfo(viewHolder, lessonItem);
                break;
            case CHAPTER:
                setChapterInfo(viewHolder, lessonItem);
                break;
        }

    /*
    if (lessonItem.id == mCurrentLessonId) {
        viewHolder.itemView.setEnabled(false);
    }
    */
        setLessonProgress(lessonItem.id, viewHolder.mLessonProgress);

        viewHolder.mTitle.setText(lessonItem.title);
    }

    private void setLessonInfo(
            ViewHolder viewHolder, LessonItem lessonItem)
    {
        viewHolder.mChapter.setVisibility(View.GONE);
        viewHolder.mLessonLayout.setVisibility(View.VISIBLE);
        viewHolder.mTitle.setText(lessonItem.title);

        // 判断直播状态
        String liveStatus = getLiveStatus(lessonItem);
        Drawable drawable = null;
        if("generated".equals(lessonItem.replayStatus)){
            drawable = mContext.getResources().getDrawable(R.drawable.replay);
        }
        viewHolder.mLessonType.setCompoundDrawablesWithIntrinsicBounds(
                drawable, null, null, null);
        viewHolder.mLessonType.setText(liveStatus);

        if (!"published".equals(lessonItem.status)) {
            setFreeTextStyle(viewHolder.mLessonType, "(未发布)");
        } else if (lessonItem.free == LessonItem.FREE) {
            setFreeTextStyle(viewHolder.mLessonType, "(免费)");
        }

        viewHolder.itemView.setBackgroundColor(
                mContext.getResources().getColor(R.color.lesson_item_lesson_bg));
    }

    private String getLiveStatus(LessonItem lessonItem){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int st = Integer.parseInt(lessonItem.startTime);
        int et = Integer.parseInt(lessonItem.endTime);
        String s = "";

        String endTime = sdf.format(new Date(et*1000L));
        String startTime = sdf.format(new Date(st*1000L));
        String currentTime = sdf.format(new Date());
        int status1 = startTime.compareTo(currentTime);
        int status2 = endTime.compareTo(currentTime);

        if(status1>0){
            s = AppUtil.getLiveTime(st*1000L);
            s ="直播将于" + s + "开始";
        }else if(status1<=0 && status2>0){
            s = "正在直播";
        }else if(status2<=0){
            s = "直播已结束";
        }

        return s;

    }

    private void setLessonProgress(int lessonId, ImageView statusImageView) {
        LearnStatus status = mUserLearns.get(lessonId);
        if (status == null) {
            statusImageView.setImageResource(R.drawable.learn_status_normal);
            return;
        }
        switch (status) {
            case finished:
                statusImageView.setImageResource(R.drawable.learn_status_learned);
                break;
            case learning:
                statusImageView.setImageResource(R.drawable.learn_status_learning);
                break;
        }
    }

    private void setChapterInfo(
            ViewHolder viewHolder, LessonItem lessonItem)
    {
        viewHolder.mLessonLayout.setVisibility(View.GONE);
        viewHolder.mChapter.setVisibility(View.VISIBLE);
        if ("unit".equals(lessonItem.type)) {
            viewHolder.mChapter.setPadding(AQUtility.dip2pixel(mContext, 44), 0, 0, 0);
            viewHolder.mChapter.changeAlpha(0.54f);
            viewHolder.mChapter.setText("第" + lessonItem.number + "节 " + lessonItem.title);
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.lesson_item_unit_bg));
        } else {
            viewHolder.mChapter.changeAlpha(0.87f);
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.lesson_item_chapter_bg));
            viewHolder.mChapter.setText("第" + lessonItem.number + "章 " + lessonItem.title);
        }
    }

    private void setFreeTextStyle(TextView textView, String text) {
        StringBuilder str = new StringBuilder(textView.getText());
        int start = str.length();
        Spannable spannable = new SpannableString(str.append(text));
        spannable.setSpan(
                new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelSize(R.dimen.course_details_lesson_item_title)),
                start, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannable.setSpan(
                new ForegroundColorSpan(mContext.getResources().getColor(R.color.lesson_item_free)),
                start, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        textView.setText(spannable);
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder
    {
        public BaseViewHolder(View view) {
            super(view);
        }
    }

    public class ViewHolder extends BaseViewHolder
    {
        public TextView mLessonType;
        public ESTextView mChapter;
        public TextView mTitle;
        public View mLessonLayout;
        public ImageView mLessonProgress;
        public View mDownloadStatusBtn;
        public ImageView mReplayIcon;

        public ViewHolder(View view) {
            super(view);

            mTitle = (TextView) view.findViewById(R.id.course_details_lesson_title);
            mLessonLayout = view.findViewById(R.id.course_details_lesson_layout);
            mChapter = (ESTextView) view.findViewById(R.id.course_details_lesson_chapter);
            mLessonType = (TextView) view.findViewById(R.id.course_details_lesson_type);
            mLessonProgress = (ImageView) view.findViewById(R.id.course_details_lesson_progress);
//            mDownloadStatusBtn = view.findViewById(R.id.course_lesson_donwload_status);
        }
    }

}
