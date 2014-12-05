package com.edusoho.kuozhi.adapter.Course;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.entity.LearnStatus;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.html.EduHtml;
import com.edusoho.kuozhi.view.ESTextView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseLessonAdapter
        extends RecyclerViewListBaseAdapter<LessonItem, CourseLessonAdapter.ViewHolder> {

    private ActionBarBaseActivity mActivity;
    private HashMap<Integer, LearnStatus> mUserLearns;

    public CourseLessonAdapter(ActionBarBaseActivity activity, int resource)
    {
        super(activity, resource);
        this.mActivity = activity;
    }

    @Override
    public void addItem(LessonItem item) {
        mList.add(item);
        notifyItemInserted(mList.size() - 1);
    }

    public void updateLearnStatus(HashMap<Integer, LearnStatus> userLearns)
    {
        this.mUserLearns = userLearns;
    }

    @Override
    public void addItems(List<LessonItem> list) {
        mList.addAll(list);
        notifyItemRangeInserted(mList.size() - 1 - list.size(), mList.size() - 1);
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
            ViewHolder viewHolder, LessonItem lesson)
    {
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
            case TEXT:
                typeDrawable = R.drawable.lesson_item_image;
                break;
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
        viewHolder.itemView.setBackgroundColor(
                mContext.getResources().getColor(R.color.lesson_item_lesson_bg));
    }

    private void setLessonProgress(int lessonId, ImageView statusImageView)
    {
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

    private void setChapterInfo(
            ViewHolder viewHolder, LessonItem lesson)
    {
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

    private void setFreeTextStyle(TextView textView, String text)
    {
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

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mLessonType;
        public ESTextView mChapter;
        public TextView mTitle;
        public View mLessonLayout;
        public ImageView mLessonProgress;

        public ViewHolder(View view){
            super(view);

            mTitle = (TextView) view.findViewById(R.id.course_details_lesson_title);
            mLessonLayout = view.findViewById(R.id.course_details_lesson_layout);
            mChapter = (ESTextView) view.findViewById(R.id.course_details_lesson_chapter);
            mLessonType = (TextView) view.findViewById(R.id.course_details_lesson_type);
            mLessonProgress = (ImageView) view.findViewById(R.id.course_details_lesson_progress);
        }
    }
}
