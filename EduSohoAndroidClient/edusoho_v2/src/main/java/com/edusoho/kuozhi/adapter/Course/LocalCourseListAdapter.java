package com.edusoho.kuozhi.adapter.Course;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.adapter.lesson.LocalLessonDownListAdapter;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by howzhi on 14/12/2.
 */
public class LocalCourseListAdapter
        extends RecyclerViewListBaseAdapter<Course, LocalCourseListAdapter.ViewHolder> {

    private DisplayImageOptions mOptions;
    private SparseArray<M3U8DbModle> mM3U8DbModles;
    private HashMap<Integer, ArrayList<LessonItem>> mLocalLessons;

    public final static int UPDATE = 0001;
    public final static int ADD = 0002;
    private int mMode;

    public LocalCourseListAdapter(Context context, int resource) {
        super(context, resource);
        mMode = ADD;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    public void setLocalLessons(HashMap<Integer, ArrayList<LessonItem>> localLessons) {
        this.mLocalLessons = localLessons;
    }

    public void setM3U8Modles(SparseArray<M3U8DbModle> m3U8DbModles) {
        this.mM3U8DbModles = m3U8DbModles;
    }

    public void updateM3U8Modles(int lessonId, M3U8DbModle m3U8DbModle) {
        this.mM3U8DbModles.put(lessonId, m3U8DbModle);
    }

    @Override
    public void addItem(Course item) {
        if (mList.add(item)) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void addItems(List<Course> list) {
        if (mList.addAll(list)) {
            notifyDataSetChanged();
        }
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
        Course course = mList.get(i);
        ArrayList<LessonItem> lessonItems = mLocalLessons.get(course.id);
        viewHolder.mCourseLessonNum.setText(String.format("共缓存%d节课时", lessonItems.size()));
        viewHolder.mCourseTitle.setText(course.title);
        ImageLoader.getInstance().displayImage(course.largePicture, viewHolder.mCoursePic, mOptions);
        viewHolder.mExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int height = 0;
                Object tag = viewHolder.mExpandBtn.getTag();
                boolean isExpand = tag == null ? true : (Boolean) tag;

                if (isExpand) {
                    AppUtil.rotation(v, 0, -180);
                    height = viewHolder.mChildListView.getHeight();
                    viewHolder.mChildListView.setTag(height);
                    viewHolder.mChildListView.setFixHeight(height);
                    AppUtil.animForHeight(
                            new EdusohoAnimWrap(viewHolder.mChildListView), height, 0, 300);
                } else {
                    AppUtil.rotation(v, -180, 0);
                    height = (Integer) viewHolder.mChildListView.getTag();
                    AppUtil.animForHeight(
                            new EdusohoAnimWrap(viewHolder.mChildListView), 0, height, 200);
                }

                viewHolder.mExpandBtn.setTag(!isExpand);
            }
        });

        if (mMode == UPDATE) {
            viewHolder.mChildListView.getAdapter().notifyDataSetChanged();
            return;
        }
        initExpandListView(viewHolder.mChildListView, lessonItems);
    }

    public void refreshData(int mode) {
        mMode = mode;
        notifyDataSetChanged();
    }

    private void initExpandListView(
            EduSohoListView childListView, ArrayList<LessonItem> lessonItems) {
        LocalLessonDownListAdapter adapter = new LocalLessonDownListAdapter(
                mContext, R.layout.lesson_down_list_item);
        childListView.setAdapter(adapter);
        childListView.addItemDecoration();
        adapter.setM3U8Modles(mM3U8DbModles);
        childListView.pushData(lessonItems);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mCourseTitle;
        public TextView mCourseLessonNum;
        public View mExpandBtn;
        public ImageView mCoursePic;
        public EduSohoListView mChildListView;

        public ViewHolder(View view) {
            super(view);

            mCourseTitle = (TextView) view.findViewById(R.id.course_title);
            mCourseLessonNum = (TextView) view.findViewById(R.id.course_lesson_num);
            mCoursePic = (ImageView) view.findViewById(R.id.course_pic);
            mExpandBtn = view.findViewById(R.id.course_list_expand);
            mChildListView = (EduSohoListView) view.findViewById(R.id.list_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            mChildListView.setLayoutManager(linearLayoutManager);
        }
    }
}
