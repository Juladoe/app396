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
public class CourseCatalogueAdapter extends RecyclerView.Adapter<CourseCatalogueAdapter.ViewHolder> {

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
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.courseCatalogue = courseCatalogue;
        this.learnStatuses = courseCatalogue.getLearnStatuses();
        this.mContext = context;
        this.isJoin = isJoin;
        this.chapterTitle = chapterTitle;
        this.unitTitle = unitTitle;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.render(courseCatalogue.getLessons().get(position), chapterTitle);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_CHAPTER:
                return new ChatperViewHolder(mInflater.inflate(R.layout.item_chapter_catalog, null));
            case TYPE_SECTION:
                return new UnitViewHolder(mInflater.inflate(R.layout.item_section_catalog, null));
        }
        return new LessonViewHolder(mInflater.inflate(R.layout.item_lesson_catalog, null));
    }


    @Override
    public int getItemCount() {
        if (courseCatalogue == null) {
            return 0;
        }
        return courseCatalogue.getLessons() == null ? 0 : courseCatalogue.getLessons().size();
    }

    protected static abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
        }

        protected abstract void render(CourseCatalogue.LessonsBean lesson, String customTitle);
    }

    protected static class LessonViewHolder extends ViewHolder {

        private ImageView lessonState;
        private EduSohoNewIconView lessonKind;
        private TextView lessonTitle;
        private TextView lessonFree;
        private TextView lessonTime;
        private TextView liveState;
        private View lessonUp;
        private View lessonDown;

        public LessonViewHolder(View view) {
            super(view);
            lessonState = (ImageView) itemView.findViewById(R.id.lesson_state);
            lessonKind = (EduSohoNewIconView) itemView.findViewById(R.id.lesson_kind);
            lessonTitle = (TextView) itemView.findViewById(R.id.lesson_title);
            lessonFree = (TextView) itemView.findViewById(R.id.lesson_free);
            lessonTime = (TextView) itemView.findViewById(R.id.lesson_time);
            liveState = (TextView) itemView.findViewById(R.id.live_state);
            lessonUp = itemView.findViewById(R.id.lesson_up);
            lessonDown = itemView.findViewById(R.id.lesson_down);
        }

        @Override
        protected void render(CourseCatalogue.LessonsBean lesson, String customTitle) {
            lessonTitle.setText(lesson.getTitle());
        }
    }
    protected static class ChatperViewHolder extends ViewHolder {

        public TextView chapterTitle;

        public ChatperViewHolder(View view) {
            super(view);
            chapterTitle = (TextView) itemView.findViewById(R.id.chapter_title);
        }

        @Override
        protected void render(CourseCatalogue.LessonsBean lesson, String customTitle) {
            if (!TextUtils.isEmpty(customTitle)) {
                chapterTitle.setText(String.format("第%s%s:%s", lesson.getNumber(), customTitle, lesson.getTitle()));
            }else {
                chapterTitle.setText(String.format("%s", lesson.getTitle()));
            }
        }
    }

    protected static class UnitViewHolder extends ViewHolder {

        public TextView sectionTitle;

        public UnitViewHolder(View view) {
            super(view);
            sectionTitle = (TextView) itemView.findViewById(R.id.section_title);
        }

        @Override
        protected void render(CourseCatalogue.LessonsBean lesson, String customTitle) {
            if (!TextUtils.isEmpty(customTitle)) {
                sectionTitle.setText(String.format("第%s%s:%s", lesson.getNumber(), customTitle, lesson.getTitle()));
            }else {
                sectionTitle.setText(String.format("%s", lesson.getTitle()));
            }
        }
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
            if (courseCatalogue.getLessons().size() == 1) {
                lessonHolder.lessonUp.setVisibility(View.INVISIBLE);
                lessonHolder.lessonDown.setVisibility(View.INVISIBLE);
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
    public static class ChapterHolder{
        public TextView chapterTitle;

        public ChapterHolder(View itemView) {
            chapterTitle = (TextView) itemView.findViewById(R.id.chapter_title);
        }
    }

    /**
     * 处理目录中的节SectionHolder
     */
    public static class SectionHolder{
        public TextView sectionTitle;

        public SectionHolder(View itemView) {
            sectionTitle = (TextView) itemView.findViewById(R.id.section_title);
        }
    }

    /**
     * 处理目录中的课时HourHolder
     */
    public static class LessonHolder{
        public ImageView lessonState;
        public EduSohoNewIconView lessonKind;
        public TextView lessonTitle;
        public TextView lessonFree;
        public TextView lessonTime;
        public TextView liveState;
        public View lessonUp;
        public View lessonDown;

        public LessonHolder(View itemView) {
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