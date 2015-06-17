package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.Lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoIconView;
import com.edusoho.kuozhi.v3.view.dialog.ExitCoursePopupDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 15/6/16.
 */
public class DownloadingAdapter extends BaseExpandableListAdapter {
    private Context mContex;
    private ActionBarBaseActivity mActivity;
    private SparseArray<M3U8DbModle> m3u8ModelList;
    private List<Course> mGroupItems;
    private List<List<LessonItem>> mChildItems;
    private boolean mSelectedShow = false;
    private DownloadType mType;

    public DownloadingAdapter(Context ctx, ActionBarBaseActivity activity, SparseArray<M3U8DbModle> m3u8List,
                              List<Course> groupItems, HashMap<Integer, ArrayList<LessonItem>> mLocalLessons, DownloadType type) {
        mContex = ctx;
        mActivity = activity;
        m3u8ModelList = m3u8List;
        mGroupItems = groupItems;

        List<List<LessonItem>> lessonItems = new ArrayList<>();
        Iterator iterator = mLocalLessons.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ArrayList<LessonItem> itemList = (ArrayList<LessonItem>) entry.getValue();
            lessonItems.add(itemList);
        }
        mChildItems = lessonItems;
        mType = type;
    }

    public void updateLocalData(List<Course> groupItems, HashMap<Integer, ArrayList<LessonItem>> mLocalLessons) {
        mGroupItems = groupItems;
        List<List<LessonItem>> lessonItems = new ArrayList<>();
        Iterator iterator = mLocalLessons.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ArrayList<LessonItem> itemList = (ArrayList<LessonItem>) entry.getValue();
            lessonItems.add(itemList);
        }
        mChildItems = lessonItems;
        notifyDataSetChanged();
    }

    public void updateProgress(int lessonId, M3U8DbModle model) {
        m3u8ModelList.put(lessonId, model);
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mGroupItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildItems.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildItems.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupPanel groupPanel;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContex).inflate(R.layout.item_download_manager_course_group, null);
            groupPanel = new GroupPanel(convertView);
            convertView.setTag(groupPanel);
        } else {
            groupPanel = (GroupPanel) convertView.getTag();
        }

        final Course course = mGroupItems.get(groupPosition);
        ImageLoader.getInstance().displayImage(course.middlePicture, groupPanel.ivAvatar, mActivity.app.mOptions);
        groupPanel.tvCourseTitle.setText(course.title);
        groupPanel.ivVideoSum.setText(String.format("视频 %s", mChildItems.get(groupPosition).size()));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildPanel childPanel;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContex).inflate(R.layout.item_download_manager_lesson_child, null);
            childPanel = new ChildPanel(convertView, mType);
            convertView.setTag(childPanel);
        } else {
            childPanel = (ChildPanel) convertView.getTag();
        }
        final LessonItem lessonItem = mChildItems.get(groupPosition).get(childPosition);
        childPanel.tvLessonTitle.setText(lessonItem.title);
        if (mType == DownloadType.DOWNLOADED) {
            childPanel.tvVideoLength.setText(AppUtil.convertCNTime(lessonItem.length));
        } else {
            final M3U8DbModle model = m3u8ModelList.get(lessonItem.id);
//            switch (model.finish) {
//                case M3U8Util.UN_FINISH:
//                    childPanel.ivDownloadSign.setText(mContex.getString(R.string.font_downloading));
//                    break;
//                case M3U8Util.START:
//                    childPanel.ivDownloadSign.setText(mContex.getString(R.string.font_wait_downloading));
//                    break;
//            }
            childPanel.ivDownloadSign.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    M3U8DownService service = M3U8DownService.getService();
                    if (childPanel.ivDownloadSign.getText().equals(mContex.getString(R.string.font_downloading))) {
                        childPanel.ivDownloadSign.setText(mContex.getString(R.string.font_stop_downloading));
                        if (service != null) {
                            service.cancleDownloadTask(lessonItem.id);
                        }
                    } else {
                        if (!mActivity.app.getNetIsConnect()) {
                            ToastUtils.show(mActivity, "当前无网络连接!");
                            return;
                        }
                        int offlineType = mActivity.app.config.offlineType;
                        if (offlineType == Const.NET_NONE) {
                            showAlertDialog("当前设置视频课时观看、下载为禁止模式!\n模式可以在设置里修改。");
                            return;
                        }
                        if (offlineType == Const.NET_WIFI && !mActivity.app.getNetIsWiFi()) {
                            showAlertDialog("当前设置视频课时观看、下载为WiFi模式!\n模式可以在设置里修改。");
                            return;
                        }
                        childPanel.ivDownloadSign.setText(mContex.getString(R.string.font_downloading));
                        M3U8DownService.startDown(
                                mActivity, lessonItem.id, lessonItem.courseId, lessonItem.title);
                    }
                }
            });
            childPanel.tvProgress.setText((int) (model.downloadNum / (float) model.totalNum * 100) + "%");
        }
        //选择框是否显示
        if (mSelectedShow) {
            childPanel.ivDownloadSelected.setVisibility(View.VISIBLE);
            if (lessonItem.isSelected) {
                childPanel.ivDownloadSelected.setText(mContex.getString(R.string.font_download_select));
            } else {
                childPanel.ivDownloadSelected.setText(mContex.getString(R.string.font_download_unselect));
            }
            childPanel.ivDownloadSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (childPanel.ivDownloadSelected.getText().equals(mContex.getString(R.string.font_download_unselect))) {
                        childPanel.ivDownloadSelected.setText(mContex.getString(R.string.font_download_select));
                        lessonItem.isSelected = true;
                    } else {
                        childPanel.ivDownloadSelected.setText(mContex.getString(R.string.font_download_unselect));
                        lessonItem.isSelected = false;
                    }
                }
            });
        } else {
            childPanel.ivDownloadSelected.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setSelectShow(boolean b) {
        this.mSelectedShow = b;
        notifyDataSetChanged();
    }

    public void isSelectAll(boolean b) {
        for (List<LessonItem> lessonItems : mChildItems) {
            for (LessonItem item : lessonItems) {
                item.isSelected = b;
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getSelectLessonId() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (List<LessonItem> lessonItems : mChildItems) {
            for (LessonItem item : lessonItems) {
                if (item.isSelected) {
                    ids.add(item.id);
                }
            }
        }
        return ids;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public static class GroupPanel {
        public ImageView ivAvatar;
        public TextView tvCourseTitle;
        public EduSohoIconView ivIndicator;
        public TextView ivVideoSum;
        public TextView ivVideoSizes;

        public GroupPanel(View view) {
            ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
            tvCourseTitle = (TextView) view.findViewById(R.id.tv_course_title);
            ivIndicator = (EduSohoIconView) view.findViewById(R.id.iv_indicator);
            ivVideoSum = (TextView) view.findViewById(R.id.tv_video_sum);
            ivVideoSizes = (TextView) view.findViewById(R.id.tv_video_size);
        }
    }

    public static class ChildPanel {
        public EduSohoIconView ivDownloadSelected;
        public TextView tvLessonTitle;
        public View viewDownloadProgress;
        public EduSohoIconView ivDownloadSign;
        public TextView tvProgress;
        public TextView tvVideoLength;

        public ChildPanel(View view, DownloadType type) {
            ivDownloadSelected = (EduSohoIconView) view.findViewById(R.id.iv_download_selected);
            tvLessonTitle = (TextView) view.findViewById(R.id.tv_lesson_title);
            viewDownloadProgress = view.findViewById(R.id.rl_progress);
            ivDownloadSign = (EduSohoIconView) view.findViewById(R.id.iv_download_sign);
            tvProgress = (TextView) view.findViewById(R.id.tv_progress);
            tvVideoLength = (TextView) view.findViewById(R.id.tv_video_length);

            if (DownloadType.DOWNLOADED == type) {
                tvVideoLength.setVisibility(View.VISIBLE);
                ivDownloadSign.setVisibility(View.GONE);
                tvProgress.setVisibility(View.GONE);
            } else {
                tvVideoLength.setVisibility(View.GONE);
                ivDownloadSign.setVisibility(View.VISIBLE);
                tvProgress.setVisibility(View.VISIBLE);
            }
        }
    }

    public enum DownloadType {
        DOWNLOADING, DOWNLOADED
    }

    private void showAlertDialog(String content) {
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
