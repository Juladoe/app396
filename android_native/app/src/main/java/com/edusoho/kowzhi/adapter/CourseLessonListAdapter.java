package com.edusoho.kowzhi.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kowzhi.R;
import com.edusoho.kowzhi.entity.CourseLessonType;
import com.edusoho.kowzhi.model.LessonItem;

public class CourseLessonListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private int mResouce;
	private Context mContext;
	private ArrayList<LessonItem> mList;
	private HashMap<Integer, String> mUserLearns;
	public int page = 0;
	public int count = 0;

	public CourseLessonListAdapter(
			Context context, LinkedHashMap<String, LessonItem> lessons, HashMap<Integer, String> userLearns, int resource) {
		mList = MapToList(lessons);
        mUserLearns = userLearns;
		mContext = context;
		mResouce = resource;
		inflater = LayoutInflater.from(context);
	}

    private ArrayList<LessonItem> MapToList(LinkedHashMap<String, LessonItem> lessonMap)
    {
        ArrayList<LessonItem> list = new ArrayList<LessonItem>();
        for (String key : lessonMap.keySet()) {
            list.add(lessonMap.get(key));
        }
        return list;
    }

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int index) {
		return mList.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int index, View view, ViewGroup vg) {
        LessonItem lesson =  mList.get(index);
		
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(mResouce, null);
			holder = new ViewHolder();
            holder.course_lesson_length = (TextView) view.findViewById(R.id.course_lesson_length);
            holder.course_lesson_number = (TextView) view.findViewById(R.id.course_lesson_number);
            holder.lesson_status_rating = (ProgressBar) view.findViewById(R.id.lesson_status_rating);
			holder.course_lesson_title = (TextView) view.findViewById(R.id.course_lesson_title);
			holder.course_lesson_typeimg = (TextView) view.findViewById(R.id.course_lesson_typeimg);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

        ViewGroup.LayoutParams lp = null;
        LessonItem.ItemType itemType = LessonItem.ItemType.cover(lesson.itemType);
        switch (itemType) {
            case LESSON:
                view.setBackgroundResource(R.color.lesson_item_bg);
                holder.course_lesson_number.setText(lesson.number + "");
                holder.course_lesson_title.setText(lesson.title);
                break;
            case CHAPTER:
                if ("unit".equals(lesson.type)) {
                    holder.course_lesson_title.setText("    第" + lesson.number + "节 " + lesson.title);
                } else {
                    holder.course_lesson_title.setText("第" + lesson.number + "章 " + lesson.title);
                }
                view.setBackgroundResource(R.color.lesson_item_bg_pressed);
                holder.course_lesson_typeimg.setVisibility(View.GONE);
                holder.course_lesson_number.setVisibility(View.GONE);
                holder.lesson_status_rating.setVisibility(View.GONE);
                break;
        }

		String type = "视频";
		int typeIcon = R.string.font_picture;
		switch (CourseLessonType.value(lesson.type)) {
			case VIDEO:
				type = "视频";
				typeIcon = R.string.font_play;
                holder.course_lesson_length.setText(lesson.length);
				break;
			case TEXT:
				type = "图文";
				typeIcon = R.string.font_picture;
				break;
			case TESTPAPER:
				type = "试卷";
				typeIcon = R.string.font_textpaper;
				break;
            case AUDIO:
                type = "音频";
                typeIcon = R.string.font_microphone;
                holder.course_lesson_length.setText(lesson.length);
                break;
		}

        holder.course_lesson_typeimg.setText(mContext.getString(typeIcon));
		String learnStatus = mUserLearns.get(lesson.id);
        if ("finished".equals(learnStatus)) {
            holder.lesson_status_rating.setProgress(10);
        } else if ("learning".equals(learnStatus)) {
            holder.lesson_status_rating.setProgress(5);
        }
		return view;
	}

	private class ViewHolder {
		public TextView course_lesson_typeimg;
		public TextView course_lesson_title;
        public ProgressBar lesson_status_rating;
        public TextView course_lesson_number;
        public TextView course_lesson_length;
	}

}
