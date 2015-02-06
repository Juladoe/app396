package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.Service.DownLoadService;
import com.edusoho.kuozhi.model.Lesson.LessonResource;
import com.edusoho.kuozhi.model.LessonMaterial;
import com.edusoho.kuozhi.model.MaterialType;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EduSohoTextBtn;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-17.
 */
public class LessonMaterialAdapter extends EdusohoBaseAdapter {

    protected LayoutInflater inflater;
    protected int mResouce;
    protected Context mContext;
    protected ArrayList<LessonMaterial> mList;
    protected SparseArray<LessonResource> mDownloadStatus;
    private OnCheckChangeListener mCheckChangeListener;

    public LessonMaterialAdapter(
            Context context, ArrayList<LessonMaterial> list,int resource) {
        mList = list;
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
        setMode(NORMAL);
        mCheckChangeListener = new OnCheckChangeListener();
    }

    private void listAddItem(ArrayList<LessonMaterial> list)
    {
        mList.addAll(list);
    }

    public void addItem(ArrayList<LessonMaterial> list)
    {
        setMode(UPDATE);
        listAddItem(list);
        notifyDataSetChanged();
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

    public MaterialCKStatus getItemCheckStatus(LessonMaterial lessonMaterial)
    {
        LessonResource lessonResource = mDownloadStatus.get(lessonMaterial.id);
        return lessonResource == null || lessonResource.finish == 0
                ? MaterialCKStatus.UN_DOWNLOAD : MaterialCKStatus.DOWNLOAD;
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.materialCK = (CheckBox) view.findViewById(R.id.lesson_material_downstatus);
            holder.materialType = (TextView) view.findViewById(R.id.lesson_material_type);
            holder.materialView = (TextView) view.findViewById(R.id.lesson_material_title);
            holder.materialCK.setOnCheckedChangeListener(mCheckChangeListener);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.materialCK.setTag(index);
        switch (mMode){
            case UPDATE:
                break;
            case NORMAL:
                invaliViewData(holder, index);
        }
        return view;
    }

    public void invaliViewData(ViewHolder holder, int index)
    {
        LessonMaterial material =  mList.get(index);
        holder.materialView.setText(material.title);

        MaterialType materialType = MaterialType.value(material.fileMime);
        holder.materialType.setText(getMimeIcon(materialType));
        LessonResource lessonResource = mDownloadStatus.get(material.id);
        int visible = lessonResource == null || lessonResource.finish == 0
                ? View.VISIBLE : View.GONE;
        holder.materialCK.setVisibility(visible);
        if (lessonResource == null) {
            holder.materialCK.setVisibility(View.VISIBLE);
            return;
        }
        if (lessonResource.finish == 0) {
            if (lessonResource.download >= material.fileSize) {
                holder.materialCK.setText("");
                holder.materialCK.setVisibility(View.GONE);
                lessonResource = DownLoadService.queryDownTask(EdusohoApp.app, material.id);
                mDownloadStatus.put(material.id, lessonResource);
                return;
            }
            int percent = (int)((float) lessonResource.download / material.fileSize * 100);
            holder.materialCK.setText(percent + "%");
            holder.materialCK.setVisibility(View.VISIBLE);
        } else {
            holder.materialCK.setVisibility(View.GONE);
        }
    }

    private int getMimeIcon(MaterialType materialType)
    {
        int icon = 0;
        switch (materialType) {
            case PPT:
                icon = R.string.font_ppt;
                break;
            case IMAGE:
                icon = R.string.font_image;
                break;
            case VIDEO:
                icon = R.string.font_video;
                break;
            case DOCUMENT:
                icon = R.string.font_document;
                break;
            case AUDIO:
                icon = R.string.font_audio;
                break;
            case EMPTY:
            case OTHER:
                icon = R.string.font_other;
                break;
        }

        return icon;
    }

    public void updateItemDownloadSize(int materialId, long size)
    {
        LessonResource lessonResource = this.mDownloadStatus.get(materialId);
        if (lessonResource == null) {
            lessonResource = DownLoadService.queryDownTask(EdusohoApp.app, materialId);
        }

        lessonResource.download = (int) size;
        this.mDownloadStatus.put(materialId, lessonResource);
        notifyDataSetChanged();
    }

    public void setDownloadStatus(SparseArray<LessonResource> downloadStatus)
    {
        this.mDownloadStatus = downloadStatus;
    }

    protected class ViewHolder {
        public CheckBox materialCK;
        public TextView materialType;
        public TextView materialView;
    }

    private class OnCheckChangeListener implements CompoundButton.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            int index = (Integer) compoundButton.getTag();
            LessonMaterial material = mList.get(index);
            if (checked) {
                downLoadRes(material);
            } else {
                cacelDownRes(material);
            }
        }
    }

    private void cacelDownRes(LessonMaterial lessonMaterial)
    {
        DownLoadService service = DownLoadService.getService();
        if (service == null) {
            return;
        }
        service.cancelDownTask(lessonMaterial);
    }

    public void downLoadRes(LessonMaterial lessonMaterial) {
        EdusohoApp app = EdusohoApp.app;
        String url = String.format(
                Const.DOWNLOAD_MATERIAL,
                app.schoolHost,
                lessonMaterial.courseId,
                lessonMaterial.id,
                app.token
        );
        lessonMaterial.fileUri = url;
        DownLoadService.startDown(mContext, lessonMaterial);
    }

    public static enum MaterialCKStatus
    {
        CHECKED, UNCHECKED, ENABLE, DOWNLOAD, UN_DOWNLOAD;
    }
}
