package com.edusohoapp.app.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edusohoapp.app.R;
import com.edusohoapp.app.entity.ChapterItem;
import com.edusohoapp.app.entity.ChaptersResult;
import com.edusohoapp.app.entity.CourseLessonItem;
import com.edusohoapp.app.entity.CourseLessonType;
import com.edusohoapp.app.entity.RecommendSchoolItem;
import com.edusohoapp.app.model.LessonItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

public class LessonAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private boolean isDelMode = false;
    private ArrayList<LessonItem> mList;
    private int mCurrentLessonId;
    private SelectLessonCallback mSelectCB;
    private boolean isFirst = true;

    public LessonAdapter(
            Context context,
            LinkedHashMap<String, LessonItem> items,
            int resource,
            int currentLessonId,
            SelectLessonCallback selectCB
    ) {
        mSelectCB = selectCB;
        mCurrentLessonId = currentLessonId;
        mList = new ArrayList<LessonItem>();
        addLessonItem(items);
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
    }

    private void addLessonItem(LinkedHashMap<String, LessonItem> items) {
        for (String key : items.keySet()) {
            mList.add(items.get(key));
        }
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
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.lesson_bg = (ViewGroup) view.findViewById(R.id.lesson_bg);
            holder.lesson_array_right = view.findViewById(R.id.lesson_array_right);
            holder.lesson_title = (TextView) view.findViewById(R.id.lesson_title);
            holder.lesson_type_img = (TextView) view.findViewById(R.id.lesson_type_img);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        LessonItem item = mList.get(index);

        setLayoutEnable(holder.lesson_bg, true);
        String itemType = item.itemType;

        switch (LessonItem.ItemType.cover(itemType)) {
            case LESSON:
                holder.lesson_title.setText(item.title);
                int typeIcon = R.string.font_empty;
                switch (CourseLessonType.value(item.type)) {
                    case VIDEO:
                        typeIcon = R.string.font_play;
                        break;
                    case TEXT:
                        typeIcon = R.string.font_picture;
                        break;
                    case TESTPAPER:
                        typeIcon = R.string.font_textpaper;
                        break;
                    case AUDIO:
                        typeIcon = R.string.font_microphone;
                        break;
                }
                holder.lesson_bg.setBackgroundResource(R.color.lesson_menu_bg);
                holder.lesson_type_img.setText(mContext.getResources().getString(typeIcon));

                if (item.id == mCurrentLessonId) {
                    setLayoutEnable(holder.lesson_bg, false);
                    if (isFirst) {
                        mSelectCB.select(item);
                        isFirst = false;
                    }
                }
                break;
            case CHAPTER:
                holder.lesson_title.setText("第" + item.number + "章节");
                holder.lesson_array_right.setVisibility(View.GONE);
                holder.lesson_bg.setBackgroundResource(R.color.lesson_chapter_bg);
                holder.lesson_type_img.setText("");
            case UNIT:
                holder.lesson_title.setText("第" + item.number + "小节");
                holder.lesson_array_right.setVisibility(View.GONE);
                holder.lesson_bg.setBackgroundResource(R.color.lesson_chapter_bg);
                holder.lesson_type_img.setText("");
                break;
            case EMPTY:
                break;
        }

        return view;
    }

    private void setLayoutEnable(ViewGroup vg, boolean isEnable) {
        int count = vg.getChildCount();
        for (int i = 0; i < count; i++) {
            vg.getChildAt(i).setEnabled(isEnable);
        }
    }

    public void setmCurrentLessonId(int lessonId) {
        this.mCurrentLessonId = lessonId;
        notifyDataSetInvalidated();
    }

    public static interface SelectLessonCallback {
        public void select(LessonItem lesson);
    }

    private class ViewHolder {
        public ViewGroup lesson_bg;
        public View lesson_array_right;
        public TextView lesson_title;
        public TextView lesson_type_img;
    }

}
