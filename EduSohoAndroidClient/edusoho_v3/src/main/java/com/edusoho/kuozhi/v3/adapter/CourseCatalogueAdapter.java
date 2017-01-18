package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.lesson.CourseCatalogue;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by DF on 2016/12/14.
 */
public class CourseCatalogueAdapter extends BaseAdapter {
    public int mSelect = -1;
    public CourseCatalogue courseCatalogue;
    public Context mContext;
    public boolean isJoin;
    public String chapterTitle;
    public String unitTitle;
    public static final int TYPE_CHAPTER = 0;
    public static final int TYPE_SECTION = 1;
    public static final int TYPE_LESSON = 2;
    private final LayoutInflater mInflater;
    private ChapterHolder chapterHolder;
    private SectionHolder sectionHolder;
    private LessonHolder lessonHolder;
    private CourseCatalogue.LessonsBean lessonsBean;
    private Map<String, String> learnStatuses;
    private RelativeLayout.LayoutParams params;

    public CourseCatalogueAdapter(Context context, CourseCatalogue courseCatalogue, boolean isJoin, String chapterTitle, String unitTitle) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        this.courseCatalogue = courseCatalogue;
        this.learnStatuses = courseCatalogue.getLearnStatuses();
        this.mContext = context;
        this.isJoin = isJoin;
        this.chapterTitle = chapterTitle;
        this.unitTitle = unitTitle;
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
                if (!TextUtils.isEmpty(chapterTitle)) {
                    chapterHolder.chapterTitle.setText(String.format("第%s%s:%s", lessonsBean.getNumber(), chapterTitle, lessonsBean.getTitle()));
                }else {
                    chapterHolder.chapterTitle.setText(String.format("%s", lessonsBean.getTitle()));
                }
                break;
            case TYPE_SECTION:
                convertView = mInflater.inflate(R.layout.item_section_catalog, null);
                sectionHolder = new SectionHolder(convertView);
                if (!TextUtils.isEmpty(unitTitle)) {
                    sectionHolder.sectionTitle.setText(String.format("第%s%s:%s", lessonsBean.getNumber(), unitTitle, lessonsBean.getTitle()));
                }else {
                    sectionHolder.sectionTitle.setText(String.format("%s", lessonsBean.getTitle()));
                }
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
        if ("chapter".equals(courseCatalogue.getLessons().get(position).getType())) {
            return TYPE_CHAPTER;
        } else if ("unit".equals(courseCatalogue.getLessons().get(position).getType())) {
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
        lessonHolder.lessonTime.setText(lessonsBean.getLength());
        lessonHolder.lessonTitle.setText(String.format("%s、%s", lessonsBean.getNumber(), lessonsBean.getTitle()));
        if ("1".equals(lessonsBean.getFree()) && !isJoin) {
            lessonHolder.lessonFree.setVisibility(View.VISIBLE);
        }
        if ("live".equals(lessonsBean.getType())) {
            initLiveState();
        }
        lessonHolder.lessonTitle.measure(0, 0);
        params = (RelativeLayout.LayoutParams) lessonHolder.lessonTitle.getLayoutParams();
        RelativeLayout.LayoutParams down = (RelativeLayout.LayoutParams) lessonHolder.lessonDown.getLayoutParams();
        down.height = AppUtil.dp2px(mContext, 30) > lessonHolder.lessonTitle.getMeasuredHeight() - AppUtil.dp2px(mContext, 12f) ? AppUtil.dp2px(mContext, 30)
                        : lessonHolder.lessonTitle.getMeasuredHeight();
        lessonHolder.lessonDown.setLayoutParams(down);
    }

    private void initLiveState() {
        lessonHolder.lessonTitle.setMaxEms(8);
        long time = System.currentTimeMillis() / 1000;
        String start = lessonsBean.getStartTime();
        String end = lessonsBean.getEndTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(Long.parseLong(end) * 1000);
        String startTime = sf.format(date);
        lessonHolder.liveState.setVisibility(View.VISIBLE);
        if (time <= Long.parseLong(start)) {
            startTime = startTime.split("-", 2)[1].substring(0, startTime.split("-", 2)[1].lastIndexOf(":")).replace("-", "月").replace(" ", "号 ");
            lessonHolder.liveState.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
            lessonHolder.liveState.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            lessonHolder.liveState.setText(startTime);
        } else {
            if (time > Long.parseLong(end)) {
                if ("ungenerated".equals(lessonsBean.getReplayStatus())) {
                    lessonHolder.liveState.setText(R.string.live_state_finish);
                    lessonHolder.liveState.setBackground(mContext.getResources().getDrawable(R.drawable.live_state_finish));
                } else {
                    lessonHolder.liveState.setText(R.string.live_state_replay);
                    lessonHolder.liveState.setTextColor(mContext.getResources().getColor(R.color.secondary2_color));
                    lessonHolder.liveState.setBackground(mContext.getResources().getDrawable(R.drawable.live_state_replay));
                }
            } else {
                lessonHolder.liveState.setText(R.string.live_state_ing);
                lessonHolder.liveState.setTextColor(mContext.getResources().getColor(R.color.primary_color));
                lessonHolder.liveState.setBackground(mContext.getResources().getDrawable(R.drawable.live_state_ing));
            }
        }
    }

    public void setLearnStatuses(Map<String, String> learnStatuses) {
        this.learnStatuses = learnStatuses;
        notifyDataSetChanged();
    }

    private void decideStatu(int positon) {
        if (learnStatuses != null && learnStatuses.containsKey(lessonsBean.getId())) {
            if ("learning".equals(learnStatuses.get(lessonsBean.getId()))) {
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
        public TextView liveState;
        public View lessonUp;
        public View lessonDown;

        public LessonHolder(View itemView) {
            super(itemView);
            lessonState = (ImageView) itemView.findViewById(R.id.lesson_state);
            lessonKind = (EduSohoNewIconView) itemView.findViewById(R.id.lesson_kind);
            lessonTitle = (TextView) itemView.findViewById(R.id.lesson_title);
            lessonFree = (TextView) itemView.findViewById(R.id.lesson_free);
            lessonTime = (TextView) itemView.findViewById(R.id.lesson_time);
            liveState = (TextView) itemView.findViewById(R.id.live_state);
            lessonUp = itemView.findViewById(R.id.lesson_up);
            lessonDown = itemView.findViewById(R.id.lesson_down);
        }
    }
}