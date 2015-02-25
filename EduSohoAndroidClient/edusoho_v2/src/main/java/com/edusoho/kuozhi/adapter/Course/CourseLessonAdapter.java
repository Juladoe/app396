package com.edusoho.kuozhi.adapter.Course;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.model.LearnStatus;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.view.ESTextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseLessonAdapter
        extends RecyclerViewListBaseAdapter<LessonItem, CourseLessonAdapter.BaseViewHolder> {

    private ActionBarBaseActivity mActivity;
    private View mHeadView;
    private HashMap<Integer, LearnStatus> mUserLearns;
    SparseArray<M3U8DbModle> mM3U8DbModles;

    public CourseLessonAdapter(ActionBarBaseActivity activity, int resource) {
        super(activity, resource);
        this.mActivity = activity;
    }

    @Override
    public void addItem(LessonItem item) {
        mList.add(item);
        notifyItemInserted(mList.size() - 1);
    }

    public void updateM3U8Models(SparseArray<M3U8DbModle> m3U8DbModles)
    {
        this.mM3U8DbModles = m3U8DbModles;
    }

    public void updateLearnStatus(HashMap<Integer, LearnStatus> userLearns)
    {
        this.mUserLearns = userLearns;
    }

    public void addHeadView(View headView)
    {
        mHeadView = headView;
        mList.add(0, null);
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
                return new HeadViewHolder(mHeadView);
            case VIEW_TYPE_CONTENT:
                return new ViewHolder(v);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder vh, int i) {
        if (getItemViewType(i) == VIEW_TYPE_HEADER) {
            return;
        }
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
        setLessonProgress(lessonItem.id, viewHolder.mLessonProgress);

        viewHolder.mTitle.setText(lessonItem.title);
    }

    private void setLessonInfo(
            ViewHolder viewHolder, LessonItem lesson)
    {
        viewHolder.mChapter.setVisibility(View.GONE);
        viewHolder.mLessonLayout.setVisibility(View.VISIBLE);
        viewHolder.mTitle.setText(lesson.title);

        CourseLessonType type = CourseLessonType.value(lesson.type);
        int typeDrawable = 0;
        switch (type) {
            case TESTPAPER:
                typeDrawable = R.drawable.lesson_item_testpaper;
                break;
            case PPT:
                typeDrawable = R.drawable.lesson_item_ppt;
                break;
            case VIDEO:
                typeDrawable = R.drawable.lesson_item_video;
                break;
            case AUDIO:
                typeDrawable = R.drawable.lesson_item_sound;
                break;
            case DOCUMENT:
                typeDrawable = R.drawable.lesson_item_document;
                break;
            case TEXT:
            default:
                typeDrawable = R.drawable.lesson_item_image;
        }

        viewHolder.mLessonType.setCompoundDrawablesWithIntrinsicBounds(
                mContext.getResources().getDrawable(typeDrawable), null, null, null);
        viewHolder.mLessonType.setText(lesson.length);

        if (!"published".equals(lesson.status)) {
            setFreeTextStyle(viewHolder.mLessonType, "(未发布)");
        } else if (lesson.free == LessonItem.FREE) {
            setFreeTextStyle(viewHolder.mLessonType, "(免费)");
        }

        if (mM3U8DbModles != null && mM3U8DbModles.indexOfKey(lesson.id) >= 0) {
            viewHolder.mDownloadStatusBtn.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mDownloadStatusBtn.setVisibility(View.GONE);
        }
        viewHolder.itemView.setBackgroundColor(
                mContext.getResources().getColor(R.color.lesson_item_lesson_bg));
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
            ViewHolder viewHolder, LessonItem lesson)
    {
        viewHolder.mLessonLayout.setVisibility(View.GONE);
        viewHolder.mChapter.setVisibility(View.VISIBLE);
        if ("unit".equals(lesson.type)) {
            viewHolder.mChapter.setPadding(AQUtility.dip2pixel(mContext, 44), 0, 0, 0);
            viewHolder.mChapter.changeAlpha(0.54f);
            viewHolder.mChapter.setText("第" + lesson.number + "节 " + lesson.title);
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.lesson_item_unit_bg));
        } else {
            viewHolder.mChapter.changeAlpha(0.87f);
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.lesson_item_chapter_bg));
            viewHolder.mChapter.setText("第" + lesson.number + "章 " + lesson.title);
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

        public ViewHolder(View view) {
            super(view);

            mTitle = (TextView) view.findViewById(R.id.course_details_lesson_title);
            mLessonLayout = view.findViewById(R.id.course_details_lesson_layout);
            mChapter = (ESTextView) view.findViewById(R.id.course_details_lesson_chapter);
            mLessonType = (TextView) view.findViewById(R.id.course_details_lesson_type);
            mLessonProgress = (ImageView) view.findViewById(R.id.course_details_lesson_progress);
            mDownloadStatusBtn = view.findViewById(R.id.course_lesson_donwload_status);
        }
    }

    public class HeadViewHolder extends BaseViewHolder
    {
        public HeadViewHolder(View view){
            super(view);
        }
    }
}
