package com.edusoho.kuozhi.adapter.lesson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.Service.M3U8DownService;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;
import com.edusoho.kuozhi.view.dialog.ExitCoursePopupDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by howzhi on 14/12/2.
 */
public class LocalLessonDownListAdapter
        extends RecyclerViewListBaseAdapter<LessonItem, LocalLessonDownListAdapter.BaseViewHoler> {

    private SparseArray<M3U8DbModle> mM3U8DbModles;
    private SparseArray<Integer> selectList;

    public final static int CHECKED = 0001;
    public final static int UNCHECK = 0003;
    public final static int INVISIBLE = 0004;
    private ActionBarBaseActivity mActivity;

    public LocalLessonDownListAdapter(ActionBarBaseActivity activity, int resource)
    {
        super(activity, resource);
        mActivity = activity;
        selectList = new SparseArray<Integer>();
    }

    @Override
    public void addItem(LessonItem item) {
        if (mList.add(item)) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void clear() {
        selectList.clear();
        super.clear();
    }

    public void setM3U8Modles(SparseArray<M3U8DbModle> m3U8DbModles)
    {
        this.mM3U8DbModles = m3U8DbModles;
    }

    @Override
    public void addItems(List<LessonItem> list) {
        if (mList.addAll(list)) {
            int size = mList.size();
            for (int i=0; i < size; i++) {
                selectList.put(i, INVISIBLE);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public BaseViewHoler onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = null;
        switch (i) {
            case M3U8Uitl.UN_FINISH:
            case M3U8Uitl.START:
                v = LayoutInflater.from(mContext).inflate(mResource, viewGroup, false);
                return new ViewHolder(v);
            case M3U8Uitl.FINISH:
                v = LayoutInflater.from(mContext).inflate(
                        R.layout.lesson_downed_list_item, viewGroup, false);
                return new DownloadedViewHolder(v);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        LessonItem lessonItem = mList.get(position);
        M3U8DbModle modle = mM3U8DbModles.get(lessonItem.id);
        return modle == null ? M3U8Uitl.UN_FINISH : modle.finish;
    }

    @Override
    public void onBindViewHolder(final BaseViewHoler vh, final int i) {
        final LessonItem lessonItem = mList.get(i);
        vh.mLessonTitle.setText(lessonItem.title);

        if (mM3U8DbModles == null) {
            return;
        }

        long downloadSize = getLocalLessonSize(lessonItem.id);
        long totalSize = 0;
        M3U8DbModle modle = null;
        modle = mM3U8DbModles.get(lessonItem.id);
        if (modle == null) {
            return;
        }

        vh.mSelectBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        selectList.setValueAt(i, isChecked ? CHECKED : UNCHECK);
                    }
                });

        vh.mSelectBox.setChecked(selectList.get(i) == CHECKED);
        vh.mSelectBox.setVisibility(
                selectList.get(i) != View.INVISIBLE ? View.VISIBLE : View.GONE);

        if (modle.finish == M3U8Uitl.FINISH) {
            super.onBindViewHolder(vh, i);
            DownloadedViewHolder dvh = (DownloadedViewHolder) vh;
            dvh.mLessonTime.setText("时长:" + lessonItem.length);
            return;
        }
        ViewHolder viewHolder = (ViewHolder) vh;
        viewHolder.mDownloadProgressBar.setMax(modle.totalNum);
        viewHolder.mDownloadProgressBar.setProgress(modle.downloadNum);

        viewHolder.mDonwloadStatus.setText(
                viewHolder.mDonwloadStatus.isChecked() ? "暂停下载" : "正在下载"
        );
        viewHolder.mDownloadInfo.setText((int)(modle.downloadNum / (float) modle.totalNum * 100) + "%");
        viewHolder.mDonwloadStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(null, isChecked + " isChecked");
                if (isChecked) {
                    buttonView.setText("暂停下载");
                    M3U8DownService service = M3U8DownService.getService();
                    if (service == null) {
                        return;
                    }
                    service.cancleDownloadTask(lessonItem.id);
                } else {
                    buttonView.setText("正在下载");
                    EdusohoApp app = EdusohoApp.app;
                    int offlineType = app.config.offlineType;
                    if (! app.getNetIsConnect()) {
                        ToastUtils.show(mContext, "当前无网络连接!");
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
                            mContext, lessonItem.id, lessonItem.courseId, lessonItem.title);
                }
            }
        });
    }

    public int getCheckStatus(int position)
    {
        return selectList.get(position);
    }

    public ArrayList<Integer> getSelectIds()
    {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        int size = selectList.size();
        for (int i=0; i < size; i++) {
            if (selectList.get(i) == CHECKED) {
                ids.add(mList.get(i).id);
            }
        }

        return ids;
    }

    public void selectPositon(int postion, int select)
    {
        selectList.setValueAt(postion, select);
        notifyDataSetChanged();
    }

    public void selectAll(int select)
    {
        int size = selectList.size();
        for (int i=0; i < size; i++) {
            selectList.setValueAt(i, select);
        }
        notifyDataSetChanged();
    }

    private long getLocalLessonSize(int lessonId)
    {
        File workSpace = EdusohoApp.getWorkSpace();
        StringBuffer dirBuilder = new StringBuffer(workSpace.getAbsolutePath());
        dirBuilder.append("/videos/")
                .append(EdusohoApp.app.domain)
                .append("/")
                .append(lessonId);
        File lessonDir = new File(dirBuilder.toString());
        long totalSize = 0;
        if (!lessonDir.exists()) {
            return totalSize;
        }
        for (File file : lessonDir.listFiles()) {
            totalSize += file.length();
        }

        return totalSize;
    }

    private String formatSize(long totalSize)
    {
        float kb = totalSize / 1024.0f / 1024.0f;
        return String.format("%.1f%s", kb, "M");
    }

    public class BaseViewHoler extends RecyclerView.ViewHolder
    {
        public TextView mLessonTitle;
        public CheckBox mSelectBox;

        public BaseViewHoler(View view) {
            super(view);

            mLessonTitle = (TextView) view.findViewById(R.id.lesson_title);
            mSelectBox = (CheckBox) view.findViewById(R.id.lesson_download_checkbox);
        }
    }

    public class ViewHolder extends BaseViewHoler
    {
        public TextView mDownloadInfo;
        public ProgressBar mDownloadProgressBar;
        public CheckBox mDonwloadStatus;

        public ViewHolder(View view){
            super(view);

            mDownloadInfo = (TextView) view.findViewById(R.id.lesson_download_info);
            mDonwloadStatus = (CheckBox) view.findViewById(R.id.lesson_download_status);
            mDownloadProgressBar = (ProgressBar) view.findViewById(R.id.lesson_download_progress);
        }
    }

    public class DownloadedViewHolder extends BaseViewHoler
    {
        public TextView mLessonTime;

        public DownloadedViewHolder(View view) {
            super(view);

            mLessonTime = (TextView) view.findViewById(R.id.lesson_time);
        }
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
