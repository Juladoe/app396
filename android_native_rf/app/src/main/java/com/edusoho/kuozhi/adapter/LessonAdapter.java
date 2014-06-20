package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.util.Const;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
                String title = item.title;
                if (!Const.PUBLISHED.equals(item.status)) {
                    title = "(未发布) " + title;
                }
                holder.lesson_title.setText(title);

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
                StringBuilder builder = new StringBuilder("第");
                builder.append(item.number)
                       .append(
                               "unit".equals(item.type) ? "节  " : "章  "
                       )
                       .append(item.title);

                holder.lesson_title.setText(builder.toString());
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
