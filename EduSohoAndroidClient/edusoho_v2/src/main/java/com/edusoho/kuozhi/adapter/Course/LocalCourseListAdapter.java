package com.edusoho.kuozhi.adapter.Course;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.adapter.lesson.LocalLessonDownListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.course.LessonActivity;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by howzhi on 14/12/2
 */
public class LocalCourseListAdapter
        extends RecyclerViewListBaseAdapter<Course, LocalCourseListAdapter.ViewHolder> {

    private DisplayImageOptions mOptions;
    private SparseArray<M3U8DbModle> mM3U8DbModles;
    private SparseArray<EduSohoListView> childLists;
    private HashMap<Integer, ArrayList<LessonItem>> mLocalLessons;
    private ActionBarBaseActivity mActivity;

    public LocalCourseListAdapter(ActionBarBaseActivity activity, int resource) {
        super(activity, resource);
        mActivity = activity;
        mMode = ADD;
        childLists = new SparseArray<EduSohoListView>();
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    public void setLocalLessons(HashMap<Integer, ArrayList<LessonItem>> localLessons) {
        this.mLocalLessons = localLessons;
    }

    public void updateLocalLesson(HashMap<Integer, ArrayList<LessonItem>> localLessons)
    {
        this.mLocalLessons.putAll(localLessons);
    }

    public void setM3U8Modles(SparseArray<M3U8DbModle> m3U8DbModles) {
        this.mM3U8DbModles = m3U8DbModles;
    }

    public void updateM3U8Model(SparseArray<M3U8DbModle> m3U8DbModles)
    {
        int size = m3U8DbModles.size();
        for (int i=0; i<size; i++) {
            this.mM3U8DbModles.put(m3U8DbModles.keyAt(i), m3U8DbModles.valueAt(i));
        }
    }

    @Override
    public void clear() {
        this.childLists.clear();
        super.clear();
    }

    public void updateM3U8Modles(int courseId, int lessonId, M3U8DbModle m3U8DbModle) {
        if (m3U8DbModle.finish == M3U8Uitl.FINISH) {
            ArrayList<LessonItem> items = mLocalLessons.get(courseId);
            if (items == null) {
                return;
            }
            int pos = findLessonById(items, lessonId);
            if (pos >= 0) {
                items.remove(pos);
            }
            if (items.isEmpty()) {
                mLocalLessons.remove(courseId);
                //移除课程
                int length = mList.size();
                for (int i=0; i < length; i++) {
                    Course course = mList.get(i);
                    if (course.id == courseId) {
                        mList.remove(i);
                    }
                }
            }
            return;
        }
        this.mM3U8DbModles.put(lessonId, m3U8DbModle);
    }

    private int findLessonById(ArrayList<LessonItem> items, int lessonId)
    {
        int length = items.size();
        for (int i=0; i < length; i++) {
            LessonItem item = items.get(i);
            if (item.id == lessonId) {
                return i;
            }
        }
        return -1;
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
        viewHolder.mCourseLessonNum.setText(String.format("缓存%d节课时", lessonItems.size()));
        viewHolder.mCourseTitle.setText(course.title);
        ImageLoader.getInstance().displayImage(course.largePicture, viewHolder.mCoursePic, mOptions);
        viewHolder.mExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int height = 0;
                Object tag = viewHolder.mExpandBtn.getTag();
                boolean isExpand = tag == null ? true : (Boolean) tag;
                if (isExpand) {
                    AppUtil.rotation(v, -180, 0);
                    height = viewHolder.mChildListView.getHeight();
                    viewHolder.mChildListView.setTag(height);
                    viewHolder.mChildListView.setFixHeight(height);
                    AppUtil.animForHeight(
                            new EdusohoAnimWrap(viewHolder.mChildListView), height, 0, 300);
                } else {
                    AppUtil.rotation(v, 0, -180);
                    viewHolder.mChildListView.setFixHeight(-1);
                    height = (Integer) viewHolder.mChildListView.getTag();
                    AppUtil.animForHeight(
                            new EdusohoAnimWrap(viewHolder.mChildListView), 0, height, 200);
                }

                viewHolder.mExpandBtn.setTag(!isExpand);
            }
        });

        initExpandListView(viewHolder.mChildListView, lessonItems);
        childLists.put(i, viewHolder.mChildListView);
    }

    public void refreshData(int mode) {
        mMode = mode;
        notifyDataSetChanged();
    }

    private void initExpandListView(
            final EduSohoListView childListView, ArrayList<LessonItem> lessonItems) {
        LocalLessonDownListAdapter adapter =
                (LocalLessonDownListAdapter) childListView.getAdapter();
        childListView.clear();
        adapter.setM3U8Modles(mM3U8DbModles);
        adapter.addItems(lessonItems);
        //childListView.pushData(lessonItems);
    }

    public void selectAll(int select)
    {
        int size = childLists.size();
        for (int i=0; i < size; i++) {
            LocalLessonDownListAdapter adapter = (LocalLessonDownListAdapter)
                    childLists.get(i).getAdapter();
            adapter.selectAll(select);
        }
    }

    /*
        返回选择的id list
    */
    public ArrayList<Integer> getSelectLessonId()
    {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        int size = childLists.size();
        for (int i=0; i < size; i++) {
            LocalLessonDownListAdapter adapter = (LocalLessonDownListAdapter)
                    childLists.get(i).getAdapter();
            ids.addAll(adapter.getSelectIds());
        }

        return ids;
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
            LocalLessonDownListAdapter adapter = new LocalLessonDownListAdapter(
                    mActivity, R.layout.lesson_down_list_item);
            mChildListView.setAdapter(adapter);
            mChildListView.addItemDecoration();
            //mChildListView.setIsSetHeight(true);
            mChildListView.setFixHeight(-1);

            adapter.setOnItemClick(new RecyclerItemClick() {
                @Override
                public void onItemClick(Object obj, int position) {
                    LocalLessonDownListAdapter adapter = (LocalLessonDownListAdapter)
                            mChildListView.getAdapter();
                    int status = adapter.getCheckStatus(position);
                    if (status != LocalLessonDownListAdapter.INVISIBLE) {
                        status = status == LocalLessonDownListAdapter.CHECKED ?
                                LocalLessonDownListAdapter.UNCHECK : LocalLessonDownListAdapter.CHECKED;
                        adapter.selectPositon(
                                position,
                                status
                        );
                        return;
                    }
                    final LessonItem lesson = (LessonItem) obj;
                    EdusohoApp.app.mEngine.runNormalPlugin(
                            LessonActivity.TAG, mContext, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    startIntent.putExtra(Const.COURSE_ID, lesson.courseId);
                                    startIntent.putExtra(LessonActivity.FROM_CACHE, true);
                                    startIntent.putExtra(Const.FREE, lesson.free);
                                    startIntent.putExtra(Const.LESSON_ID, lesson.id);
                                    startIntent.putExtra(Const.LESSON_TYPE, lesson.type);
                                    startIntent.putExtra(Const.ACTIONBAT_TITLE, lesson.title);
                                }
                            }
                    );
                }
            });
        }
    }
}
