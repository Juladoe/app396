package com.edusoho.kuozhi.adapter.Course;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.Service.M3U8DownService;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
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
import com.edusoho.kuozhi.view.dialog.ExitCoursePopupDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by howzhi on 14/12/2
 */
public class DownCourseListAdapter extends BaseExpandableListAdapter {

    protected LayoutInflater inflater;
    protected int mResource;
    protected ArrayList<Course> mList;

    private DisplayImageOptions mOptions;
    private SparseArray<M3U8DbModle> mM3U8DbModles;
    private SparseArray<Integer> selectList;
    private HashMap<Integer, ArrayList<LessonItem>> mLocalLessons;
    private ActionBarBaseActivity mActivity;

    public final static int CHECKED = 0001;
    public final static int UNCHECK = 0003;
    public final static int INVISIBLE = 0004;

    public DownCourseListAdapter(ActionBarBaseActivity activity, int resource) {
        mActivity = activity;
        this.mResource = resource;
        inflater = LayoutInflater.from(mActivity);

        mList = new ArrayList<Course>();
        selectList = new SparseArray<Integer>();
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    public void clear()
    {
        mList.clear();
    }

    public void setLocalLessons(HashMap<Integer, ArrayList<LessonItem>> localLessons) {
        this.mLocalLessons = localLessons;
        for (int courseId : localLessons.keySet()) {
            ArrayList<LessonItem> items = localLessons.get(courseId);
            for (LessonItem lessonItem : items) {
                selectList.put(lessonItem.id, INVISIBLE);
            }
        }
    }

    public HashMap<Integer, ArrayList<LessonItem>> updateLocalLesson(
            HashMap<Integer, ArrayList<LessonItem>> localLessons)
    {
        this.mLocalLessons.putAll(localLessons);
        for (int courseId : localLessons.keySet()) {
            ArrayList<LessonItem> items = localLessons.get(courseId);
            for (LessonItem lessonItem : items) {
                selectList.put(lessonItem.id, INVISIBLE);
            }
        }

        return this.mLocalLessons;
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
                Iterator<Course> iterator = mList.iterator();
                while (iterator.hasNext()) {
                    Course course = iterator.next();
                    if (course.id == courseId) {
                        iterator.remove();
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

    public void addItems(ArrayList<Course> list) {
        if (mList.addAll(list)) {
            notifyDataSetChanged();
        }
    }

    @Override
    public Object getGroup(int i) {
        return mList.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Course course = mList.get(groupPosition);
        return mLocalLessons.get(course.id).get(childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mLocalLessons.get(mList.get(groupPosition).id).size();
    }

    private int getChildViewType(LessonItem lessonItem)
    {
        M3U8DbModle modle = mM3U8DbModles.get(lessonItem.id);
        return modle == null ? M3U8Uitl.UN_FINISH : modle.finish;
    }

    @Override
    public View getChildView(
            int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        Course course = mList.get(groupPosition);
        ArrayList<LessonItem> lessonItems = mLocalLessons.get(course.id);
        final LessonItem lessonItem = lessonItems.get(childPosition);
        int type = getChildViewType(lessonItem);
        ChildViewHolder viewHolder = null;
        if (view == null) {
            switch (type) {
                case M3U8Uitl.UN_FINISH:
                case M3U8Uitl.START:
                    view = inflater.inflate(R.layout.lesson_down_list_item, null);
                    viewHolder = new DowningChildViewHolder();
                    DowningChildViewHolder dch = (DowningChildViewHolder) viewHolder;
                    dch.mDownloadInfo = (TextView) view.findViewById(R.id.lesson_download_info);
                    dch.mDonwloadStatus = (CheckBox) view.findViewById(R.id.lesson_download_status);
                    dch.mDownloadProgressBar = (ProgressBar) view.findViewById(R.id.lesson_download_progress);
                    dch.mLessonTitle = (TextView) view.findViewById(R.id.lesson_title);
                    break;
                case M3U8Uitl.FINISH:
                    view = inflater.inflate(R.layout.lesson_downed_list_item, null);
                    viewHolder = new DownedChildViewHolder();
                    DownedChildViewHolder dcvh = (DownedChildViewHolder) viewHolder;
                    dcvh.mLessonTime = (TextView) view.findViewById(R.id.lesson_time);
            }
            viewHolder.mLessonTitle = (TextView) view.findViewById(R.id.lesson_title);
            viewHolder.mSelectBox = (CheckBox) view.findViewById(R.id.lesson_download_checkbox);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) view.getTag();
        }

        viewHolder.mLessonTitle.setText(lessonItem.title);
        if (mM3U8DbModles == null) {
            return view;
        }

        M3U8DbModle modle = null;
        modle = mM3U8DbModles.get(lessonItem.id);
        if (modle == null) {
            return view;
        }

        viewHolder.mSelectBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int index = selectList.indexOfKey(lessonItem.id);
                        selectList.setValueAt(index, isChecked ? CHECKED : UNCHECK);
                    }
                });

        viewHolder.mSelectBox.setChecked(selectList.get(lessonItem.id) == CHECKED);
        viewHolder.mSelectBox.setVisibility(
                selectList.get(lessonItem.id) != View.INVISIBLE ? View.VISIBLE : View.GONE);

        if (modle.finish == M3U8Uitl.FINISH) {
            DownedChildViewHolder downedChildViewHolder = (DownedChildViewHolder) viewHolder;
            downedChildViewHolder.mLessonTime.setText("时长:" + lessonItem.length);
            return view;
        }

        DowningChildViewHolder dch = (DowningChildViewHolder) viewHolder;
        dch.mDownloadProgressBar.setMax(modle.totalNum);
        dch.mDownloadProgressBar.setProgress(modle.downloadNum);

        M3U8DownService service = M3U8DownService.getService();
        if (service != null) {
            dch.mDonwloadStatus.setChecked(service.isExistsDownTask(lessonItem.id));
        }
        dch.mDonwloadStatus.setText(
                dch.mDonwloadStatus.isChecked() ? "暂停下载" : "开始下载"
        );
        dch.mDownloadInfo.setText((int)(modle.downloadNum / (float) modle.totalNum * 100) + "%");
        dch.mDonwloadStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    Log.d(null, "cancel downoload");
                    buttonView.setText("暂停下载");
                    M3U8DownService service = M3U8DownService.getService();
                    if (service == null) {
                        return;
                    }
                    service.cancleDownloadTask(lessonItem.id);
                } else {
                    Log.d(null, "continue downoload");
                    buttonView.setText("正在下载");
                    EdusohoApp app = EdusohoApp.app;
                    int offlineType = app.config.offlineType;
                    if (! app.getNetIsConnect()) {
                        ToastUtils.show(mActivity, "当前无网络连接!");
                        return;
                    }
                    if (offlineType == Const.NET_NONE) {
                        showAlertDialog("当前设置视频课时观看、下载为禁止模式!\n模式可以在设置里修改。");
                        return;
                    }
                    if (offlineType == Const.NET_WIFI && ! app.getNetIsWiFi()) {
                        showAlertDialog("当前设置视频课时观看、下载为WiFi模式!\n模式可以在设置里修改。");
                        return;
                    }
                    M3U8DownService.startDown(
                            mActivity, lessonItem.id, lessonItem.courseId, lessonItem.title);
                }
            }
        });

        return view;
    }

    @Override
    public View getGroupView(
            final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
        Course course = mList.get(groupPosition);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(mResource, null);
            viewHolder = new ViewHolder();
            viewHolder.mCourseTitle = (TextView) convertView.findViewById(R.id.course_title);
            viewHolder.mCourseLessonNum = (TextView) convertView.findViewById(R.id.course_lesson_num);
            viewHolder.mCoursePic = (ImageView) convertView.findViewById(R.id.course_pic);
            viewHolder.mExpandBtn = convertView.findViewById(R.id.course_list_expand);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArrayList<LessonItem> lessonItems = mLocalLessons.get(course.id);
        viewHolder.mCourseLessonNum.setText(String.format("缓存%d节课时", lessonItems.size()));
        viewHolder.mCourseTitle.setText(course.title);
        ImageLoader.getInstance().displayImage(course.largePicture, viewHolder.mCoursePic, mOptions);

        rotationExpandBtn(viewHolder.mExpandBtn, isExpanded);
        return convertView;
    }

    public void expandAll(ExpandableListView listView)
    {
        int size = mList.size();
        for (int i=0; i < size; i++) {
            listView.expandGroup(i);
        }
    }

    private void rotationExpandBtn(View view, boolean isExpanded)
    {
        boolean viewIsExpand;
        Object tag = view.getTag();
        viewIsExpand = tag == null ? false : (Boolean) tag;
        if (viewIsExpand == isExpanded) {
            return;
        }
        if (isExpanded) {
            AppUtil.rotation(view, -180, 0);
        } else {
            AppUtil.rotation(view, 0, -180);
        }

        view.setTag(isExpanded);
    }

    public void selectAll(int select)
    {
        int size = selectList.size();
        for (int i=0; i < size; i++) {
            selectList.setValueAt(i, select);
        }
        notifyDataSetChanged();
    }

    /*
        返回选择的id list
    */
    public ArrayList<Integer> getSelectLessonId()
    {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        int size = selectList.size();
        for (int i=0; i < size; i++) {
            if (selectList.valueAt(i) == CHECKED) {
                ids.add(selectList.keyAt(i));
            }
        }

        return ids;
    }

    public void selectPositon(int groupPosition, int childPosition, int select)
    {
        Course course = mList.get(groupPosition);
        ArrayList<LessonItem> lessonItems = mLocalLessons.get(course.id);
        int index = selectList.indexOfKey(lessonItems.get(childPosition).id);
        selectList.setValueAt(index, select);
        notifyDataSetInvalidated();
    }

    public int getCheckStatus(int groupPosition, int childPosition)
    {
        Course course = mList.get(groupPosition);
        ArrayList<LessonItem> lessonItems = mLocalLessons.get(course.id);
        return selectList.get(lessonItems.get(childPosition).id);
    }

    public class DowningChildViewHolder extends ChildViewHolder{

        public TextView mDownloadInfo;
        public ProgressBar mDownloadProgressBar;
        public CheckBox mDonwloadStatus;
        public TextView mLessonTitle;
    }

    public class ChildViewHolder {
        public TextView mLessonTitle;
        public CheckBox mSelectBox;
    }

    public class DownedChildViewHolder extends ChildViewHolder{

        public TextView mLessonTime;
    }

    public class ViewHolder {
        public TextView mCourseTitle;
        public TextView mCourseLessonNum;
        public View mExpandBtn;
        public ImageView mCoursePic;
    }

    private void showAlertDialog(String content)
    {
        PopupDialog popupDialog = PopupDialog.createMuilt(
                mActivity,
                "播放提示",
                content,
                new PopupDialog.PopupClickListener() {
                    @Override
                    public void onClick(int button) {
                        if (button == PopupDialog.OK) {
                            ExitCoursePopupDialog.createNormal(
                                    mActivity, "视频课时下载播放", new ExitCoursePopupDialog.PopupClickListener() {
                                        @Override
                                        public void onClick(int button, int position, String selStr) {
                                            if (button == ExitCoursePopupDialog.CANCEL) {
                                                return;
                                            }

                                            EdusohoApp app = EdusohoApp.app;
                                            app.config.offlineType = position;
                                            app.saveConfig();
                                        }
                                    }
                            ).show();
                        }
                    }
                });
        popupDialog.setOkText("去设置");
        popupDialog.show();
    }
}
