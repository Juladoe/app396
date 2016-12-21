package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.lesson.CourseCatalogue;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;

/**
 * Created by DF on 2016/12/14.
 */
public class CourseCatalogueAdapter extends BaseAdapter {
    public int mSelect = -1;
    public CourseCatalogue courseCatalogue;
    public Context mContext;
    public boolean isJoin;
    public static final int TYPE_CHAPTER = 0;
    public static final int TYPE_SECTION = 1;
    public static final int TYPE_LESSON = 2;
    private final LayoutInflater mInflater;
    private ChapterHolder chapterHolder;
    private SectionHolder sectionHolder;
    private LessonHolder lessonHolder;
    private CourseCatalogue.LessonsBean lessonsBean;

    public CourseCatalogueAdapter(Context context, CourseCatalogue courseCatalogue, boolean isJoin) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.courseCatalogue = courseCatalogue;
        this.mContext = context;
        this.isJoin = isJoin;
    }

    public void setCourseCatalogue(CourseCatalogue courseCatalogue) {
        this.courseCatalogue = courseCatalogue;
    }

    public void changeSelected(int position) {
        if (position != mSelect) {
            mSelect = position;
            notifyDataSetChanged();
        }
    }

    public boolean isSelected(int position) {
        return mSelect != -1 && position == mSelect;
    }

    @Override
    public Object getItem(int position) {
        return courseCatalogue.getLessons().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        lessonsBean = courseCatalogue.getLessons().get(position);
        switch (getItemViewType(position)) {
            case TYPE_CHAPTER:
                convertView = mInflater.inflate(R.layout.item_chapter_catalog, null);
                chapterHolder = new ChapterHolder(convertView);
                chapterHolder.chapterTitle.setText("第" + getBigNum(lessonsBean.getNumber()) + "章" + "  " + lessonsBean.getTitle());
                break;
            case TYPE_SECTION:
                convertView = mInflater.inflate(R.layout.item_section_catalog, null);
                sectionHolder = new SectionHolder(convertView);
                sectionHolder.sectionTitle.setText("第" + getBigNum(lessonsBean.getNumber()) + "节" + "  " + lessonsBean.getTitle());
                break;
            case TYPE_LESSON:
                convertView = mInflater.inflate(R.layout.item_lesson_catalog, null);
                lessonHolder = new LessonHolder(convertView);
                //初始化控件数据
                initView(position);
                //判断课时类型
                decideKind();
                break;
        }
        return convertView;
    }

    @Override
    public int getCount() {
        if (courseCatalogue == null) {
            return 0;
        }
        return courseCatalogue.getLessons() == null ? 0 : courseCatalogue.getLessons().size();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if ("chapter".equals(courseCatalogue.getLessons().get(position).getItemType())) {
            return TYPE_CHAPTER;
        } else if ("unit".equals(courseCatalogue.getLessons().get(position).getItemType())) {
            return TYPE_SECTION;
        } else {
            return TYPE_LESSON;
        }
    }

    private void initView(int position) {
        if (!isJoin) {
            lessonHolder.lessonState.setVisibility(View.GONE);
            lessonHolder.lessonUp.setVisibility(View.GONE);
            lessonHolder.lessonDown.setVisibility(View.GONE);
        } else {
            //判断课时学习状态
            decideStatu(position);
            if (position != 0) {
                if (getItemViewType(position - 1) != TYPE_LESSON) {
                    lessonHolder.lessonUp.setVisibility(View.INVISIBLE);
                }
                if (position == courseCatalogue.getLessons().size() - 1) {
                    lessonHolder.lessonDown.setVisibility(View.INVISIBLE);
                }
            }
            if (position < courseCatalogue.getLessons().size() - 1) {
                if (getItemViewType(position + 1) != TYPE_LESSON) {
                    lessonHolder.lessonDown.setVisibility(View.INVISIBLE);
                }
                if (position == 0) {
                    lessonHolder.lessonUp.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (mSelect == position) {
            lessonHolder.lessonTitle.setTextColor(mContext.getResources().getColor(R.color.primary_color));
            lessonHolder.lessonKind.setTextColor(mContext.getResources().getColor(R.color.primary_color));
            lessonHolder.lessonTime.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        }
        lessonHolder.lessonTime.setText(lessonsBean.getLength());
        lessonHolder.lessonTitle.setText("课时:" + lessonsBean.getNumber() + " " + lessonsBean.getTitle());
        if ("0".equals(lessonsBean.getFree())) {
            lessonHolder.lessonFree.setVisibility(View.INVISIBLE);
        }
    }

    private void decideStatu(int positon) {
        if (courseCatalogue.getLearnStatuses().containsKey(lessonsBean.getId())) {
            if ("learning".equals(courseCatalogue.getLearnStatuses().get(lessonsBean.getId()))) {
                lessonHolder.lessonState.setImageResource(R.drawable.lesson_status_learning);
            } else {
                lessonHolder.lessonState.setImageResource(R.drawable.lesson_status_finish);
            }
        }
    }

    private void decideKind() {
        switch (lessonsBean.getType()) {
            case "ppt":
                lessonHolder.lessonKind.setText(R.string.catalog_lesson_ppt);
                break;
            case "audio":
                lessonHolder.lessonKind.setText(R.string.catalog_lesson_audio);
                break;
            case "text":
                lessonHolder.lessonKind.setText(R.string.catalog_lesson_text);
                break;
            case "flash":
                lessonHolder.lessonKind.setText(R.string.catalog_lesson_flash);
                break;
            case "live":
                lessonHolder.lessonKind.setText(R.string.catalog_lesson_live);
                break;
            case "video":
                lessonHolder.lessonKind.setText(R.string.catalog_lesson_video);
                break;
            case "document":
                lessonHolder.lessonKind.setText(R.string.catalog_lesson_doucument);
                break;
            case "testpaper":
                lessonHolder.lessonKind.setText(R.string.catalog_lesson_testPaper);
                break;
        }
    }

    /**
     * 处理目录中的章ChapterHolder
     */
    public static class ChapterHolder extends RecyclerView.ViewHolder {
        public TextView chapterTitle;

        public ChapterHolder(View itemView) {
            super(itemView);
            chapterTitle = (TextView) itemView.findViewById(R.id.chapter_title);
        }
    }

    /**
     * 处理目录中的节SectionHolder
     */
    public static class SectionHolder extends RecyclerView.ViewHolder {
        public TextView sectionTitle;

        public SectionHolder(View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.section_title);
        }
    }

    /**
     * 处理目录中的课时HourHolder
     */
    public static class LessonHolder extends RecyclerView.ViewHolder {
        public ImageView lessonState;
        public EduSohoNewIconView lessonKind;
        public TextView lessonTitle;
        public TextView lessonFree;
        public TextView lessonTime;
        public View lessonUp;
        public View lessonDown;

        public LessonHolder(View itemView) {
            super(itemView);
            lessonState = (ImageView) itemView.findViewById(R.id.lesson_state);
            lessonKind = (EduSohoNewIconView) itemView.findViewById(R.id.lesson_kind);
            lessonTitle = (TextView) itemView.findViewById(R.id.lesson_title);
            lessonFree = (TextView) itemView.findViewById(R.id.lesson_free);
            lessonTime = (TextView) itemView.findViewById(R.id.lesson_time);
            lessonUp = itemView.findViewById(R.id.lesson_up);
            lessonDown = itemView.findViewById(R.id.lesson_down);
        }
    }

    /**
     * 将数字转为大写的,考虑100以内
     */
    public String getBigNum(String num) {
        final String[] tag = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};
        StringBuffer stringBuffer = new StringBuffer();
        char[] cNum = num.toCharArray();
        if (cNum.length == 1) {
            stringBuffer.append(tag[Integer.valueOf(String.valueOf(cNum[0])) - 1]);
        } else {
            if (Integer.parseInt(num) % 10 == 0) {
                stringBuffer.append(tag[Integer.valueOf(String.valueOf(cNum[0])) - 1] + "十");
            } else if (Integer.parseInt(num) < 20) {
                stringBuffer.append("十" + tag[Integer.valueOf(String.valueOf(cNum[1])) - 1]);
            } else {
                stringBuffer.append(tag[Integer.valueOf(String.valueOf(cNum[0])) - 1] + "十");
                stringBuffer.append(tag[Integer.valueOf(String.valueOf(cNum[1])) - 1]);
            }
        }
        return stringBuffer.toString();
    }
}