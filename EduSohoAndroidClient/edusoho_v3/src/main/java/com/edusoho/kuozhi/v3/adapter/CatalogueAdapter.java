package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.lesson.CourseCatalogue;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;

/**
 * Created by DF on 2016/12/14.
 */

public class CatalogueAdapter extends RecyclerView.Adapter {

    public CourseCatalogue courseCatalogue;
    public Context mContext;

    public static final int TYPE_CHAPTER = 0;
    public static final int TYPE_SECTION = 1;
    public static final int TYPE_LESSON = 2;

    public CatalogueAdapter(Context context , CourseCatalogue courseCatalogue){
        this.courseCatalogue = courseCatalogue;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case TYPE_CHAPTER:
                view = View.inflate(mContext, R.layout.item_chapter, null);
                return new ChapterHolder(view);
            case TYPE_SECTION:
                view = View.inflate(mContext, R.layout.item_section, null);
                return new SectionHolder(view);
            case TYPE_LESSON:
                view = View.inflate(mContext, R.layout.item_lesson, null);
                return new LessonHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CourseCatalogue.LessonsBean lessonsBean = courseCatalogue.getLessons().get(position);
        switch (getItemViewType(position)) {
            case TYPE_CHAPTER:
                ChapterHolder chapterHolder = (ChapterHolder) holder;
                chapterHolder.chapterTitle.setText("第"+getBigNum(lessonsBean.getNumber())+"章"+"  "+lessonsBean.getTitle());
                break;
            case TYPE_SECTION:
                SectionHolder sectionHolder = (SectionHolder) holder;
                sectionHolder.sectionTitle.setText("第"+getBigNum(lessonsBean.getNumber())+"节"+"  "+lessonsBean.getTitle());
                break;
            case TYPE_LESSON:
                LessonHolder lessonHolder = (LessonHolder) holder;
                lessonHolder.lessonTime.setText(lessonsBean.getLength());
                if (getItemViewType(position - 1) != TYPE_LESSON) {
                    lessonHolder.lessonUp.setVisibility(View.INVISIBLE);
                    if (position == courseCatalogue.getLessons().size()-1) {
                        lessonHolder.lessonDown.setVisibility(View.INVISIBLE);
                    }
                }
                if (position < courseCatalogue.getLessons().size()-1) {
                    if (getItemViewType(position + 1) != TYPE_LESSON) {
                        lessonHolder.lessonDown.setVisibility(View.INVISIBLE);
                    }
                }
                //判断课时类型
                //
                decideKind(lessonsBean, lessonHolder);
                //判断课时学习状态
                //
                decideStatu(lessonsBean, lessonHolder);
                //判断是否免费
                //
                decideFree(lessonsBean, lessonHolder);

                break;
        }

    }

    private void decideFree(CourseCatalogue.LessonsBean lessonsBean, LessonHolder lessonHolder) {
        if ("0".equals(lessonsBean.getFree())) {
            lessonHolder.lessonFree.setVisibility(View.INVISIBLE);
        }
    }

    private void decideStatu(CourseCatalogue.LessonsBean lessonsBean, LessonHolder lessonHolder) {
    }

    private void decideKind(CourseCatalogue.LessonsBean lessonsBean, LessonHolder lessonHolder) {
        switch (lessonsBean.getType()) {
            case "ppt":
                lessonHolder.lessonKind.setText("&#xe673;");
                break;
            case "video":
                break;
            case "document":
                break;
            case "testpaper":
                break;
        }
    }

    @Override
    public int getItemCount() {
        return courseCatalogue.getLessons() == null ? 0 : courseCatalogue.getLessons().size();
    }

    @Override
    public int getItemViewType(int position) {
        if ("chapter".equals(courseCatalogue.getLessons().get(position).getItemType())) {
            return TYPE_CHAPTER;
        }else if ("unit".equals(courseCatalogue.getLessons().get(position).getItemType())){
            return TYPE_SECTION;
        }else {
            return TYPE_LESSON;
        }
    }




    /**
     * 处理目录中的章ChapterHolder
     */
    public static class ChapterHolder extends RecyclerView.ViewHolder{
        public TextView chapterTitle;
        public ChapterHolder(View itemView) {
            super(itemView);
            chapterTitle = (TextView) itemView.findViewById(R.id.chapter_title);
        }
    }

    /**
     * 处理目录中的节SectionHolder
     */
    public static class SectionHolder extends RecyclerView.ViewHolder{
        public TextView sectionTitle;
        public SectionHolder(View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.section_title);
        }
    }

    /**
     * 处理目录中的课时HourHolder
     */
    public static class LessonHolder extends RecyclerView.ViewHolder{
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
            lessonTitle = (TextView) itemView.findViewById(R.id.lesson_free);
            lessonFree = (TextView) itemView.findViewById(R.id.lesson_free);
            lessonTime = (TextView) itemView.findViewById(R.id.lesson_free);
            lessonUp = itemView.findViewById(R.id.lesson_up);
            lessonDown = itemView.findViewById(R.id.lesson_down);
        }
    }

    /**
     * 将数字转为大写的,考虑100以内
     */
    public String getBigNum(String num){
        final String[] tag = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};
        StringBuffer stringBuffer = new StringBuffer();
        char[] cNum = num.toCharArray();
        if (cNum.length == 1) {
            stringBuffer.append(tag[Integer.valueOf(String.valueOf(cNum[0])) - 1]);
        }else {
            if (Integer.parseInt(num) % 10 == 0) {
                stringBuffer.append(tag[Integer.valueOf(String.valueOf(cNum[0])) - 1] + "十");
            }else if (Integer.parseInt(num) < 20){
                stringBuffer.append("十"+tag[Integer.valueOf(String.valueOf(cNum[1])) - 1]);
            }else {
                stringBuffer.append(tag[Integer.valueOf(String.valueOf(cNum[0])) - 1] + "十");
                stringBuffer.append(tag[Integer.valueOf(String.valueOf(cNum[1])) - 1]);
            }
        }
        return stringBuffer.toString();
    }
}
