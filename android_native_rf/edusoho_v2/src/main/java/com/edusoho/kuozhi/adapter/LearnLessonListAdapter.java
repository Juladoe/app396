package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.entity.LearnStatus;
import com.edusoho.kuozhi.entity.LessonsResult;
import com.edusoho.kuozhi.model.LessonItem;
import com.hb.views.PinnedSectionListView;

import java.util.ArrayList;
import java.util.HashMap;

public class LearnLessonListAdapter extends BaseAdapter
        implements PinnedSectionListView.PinnedSectionListAdapter{

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<LessonItem> mList;
    private HashMap<Integer, LearnStatus> mUserLearns;
    public int page = 0;
    public int count = 0;

    public LearnLessonListAdapter(
            Context context,
            LessonsResult result,
            int resource) {
        mList = result.lessons;
        mUserLearns = result.learnStatuses;
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public LessonItem getItem(int index) {
        return mList.get(index);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        LessonItem item = getItem(position);
        LessonItem.ItemType itemType = LessonItem.ItemType.cover(item.itemType);
        return itemType == LessonItem.ItemType.LESSON;
    }

    @Override
    public int getItemViewType(int position) {
        LessonItem item = getItem(position);
        LessonItem.ItemType itemType = LessonItem.ItemType.cover(item.itemType);
        return itemType == LessonItem.ItemType.CHAPTER ? 1 : 0;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        System.out.println("itemType->"+viewType);
        return viewType == 1;
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        LessonItem lesson = mList.get(index);

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.mTitle = (TextView) view.findViewById(R.id.course_details_lesson_title);
            holder.mLessonLayout = view.findViewById(R.id.course_details_lesson_layout);
            holder.mChapter = (TextView) view.findViewById(R.id.course_details_lesson_chapter);
            holder.mLessonType = (TextView) view.findViewById(R.id.course_details_lesson_type);
            holder.mLessonProgress = (ImageView) view.findViewById(R.id.course_details_lesson_progress);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        LessonItem.ItemType itemType = LessonItem.ItemType.cover(lesson.itemType);
        switch (itemType) {
            case LESSON:
                setLessonInfo(view, holder, lesson);
                break;
            case CHAPTER:
                setChapterInfo(view, holder, lesson);
                break;
        }

        setLessonProgress(lesson.id, holder.mLessonProgress);
        return view;
    }

    private void setChapterInfo(
            View view, ViewHolder holder, LessonItem lesson)
    {
        holder.mChapter.setVisibility(View.VISIBLE);
        if ("unit".equals(lesson.type)) {
            holder.mChapter.setPadding(AQUtility.dip2pixel(mContext, 40), 0, 0, 0);
            holder.mChapter.setText("第" + lesson.number + "节 " + lesson.title);
            view.setBackgroundColor(mContext.getResources().getColor(R.color.lesson_item_unit_bg));
        } else {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.lesson_item_chapter_bg));
            holder.mChapter.setText("第" + lesson.number + "章 " + lesson.title);
        }
    }

    private void setLessonInfo(
            View view, ViewHolder holder, LessonItem lesson)
    {
        holder.mLessonLayout.setVisibility(View.VISIBLE);
        holder.mTitle.setText(lesson.title);

        CourseLessonType type = CourseLessonType.value(lesson.type);
        int typeDrawable = 0;
        switch (type) {
            case TESTPAPER:
                typeDrawable = R.drawable.lesson_item_ppt;
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
            case TEXT:
                typeDrawable = R.drawable.lesson_item_image;
                break;
            default:
                typeDrawable = R.drawable.lesson_item_image;
        }

        holder.mLessonType.setCompoundDrawablesWithIntrinsicBounds(
                mContext.getResources().getDrawable(typeDrawable), null, null, null);
        holder.mLessonType.setText(lesson.length);
        if (lesson.free == LessonItem.FREE) {
            setFreeTextStyle(holder.mLessonType);
        }
        view.setBackgroundColor(mContext.getResources().getColor(R.color.lesson_item_lesson_bg));
    }

    private void setLessonProgress(int lessonId, ImageView statusImageView)
    {
        Log.d(null, "mUserLearns->" + mUserLearns + "  lessonId->"+ lessonId);
        LearnStatus status = mUserLearns.get(lessonId);
        if (status == null) {
            statusImageView.setImageResource(R.drawable.learn_status_normal);
            return;
        }
        switch (status){
            case finished:
                statusImageView.setImageResource(R.drawable.learn_status_learned);
                break;
            case learning:
                statusImageView.setImageResource(R.drawable.learn_status_learning);
                break;
        }
    }

    private void setFreeTextStyle(TextView textView)
    {
        StringBuilder str = new StringBuilder(textView.getText());
        int start = str.length();
        Spannable spannable = new SpannableString(str.append("(免费)"));
        spannable.setSpan(
                new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelSize(R.dimen.course_details_lesson_item_title)),
                start, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannable.setSpan(
                new ForegroundColorSpan(mContext.getResources().getColor(R.color.lesson_item_free)),
                start, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        textView.setText(spannable);
    }

    private class ViewHolder {
        public TextView mLessonType;
        public TextView mChapter;
        public TextView mTitle;
        public View mLessonLayout;
        public ImageView mLessonProgress;
    }

}
