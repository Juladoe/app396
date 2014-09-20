package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.LessonMaterial;
import com.edusoho.kuozhi.model.MaterialType;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.util.AppUtil;
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
    private ArrayList<Boolean> checkList;
    private OnCheckChangeListener checkChangeListener;

    public LessonMaterialAdapter(
            Context context, ArrayList<LessonMaterial> list,int resource) {
        mList = list;
        mContext = context;
        mResouce = resource;
        checkList = initCheckList();
        inflater = LayoutInflater.from(context);
        checkChangeListener = new OnCheckChangeListener();
        setMode(NORMAL);
    }

    private ArrayList<Boolean> initCheckList()
    {
        ArrayList<Boolean> list = new ArrayList<Boolean>();
        int count = mList.size();
        for (int i=0; i < count; i++) {
            list.add(false);
        }

        return list;
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

    public void setCheckAllStatus(boolean isChecked)
    {
        int count = checkList.size();
        for (int i=0; i < count; i++) {
            checkList.add(i, isChecked);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.materialCK = (CheckBox) view.findViewById(R.id.lesson_material_ck);
            holder.materialView = (EduSohoTextBtn) view.findViewById(R.id.lesson_material_title);
            holder.materialCK.setOnCheckedChangeListener(checkChangeListener);
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
        holder.materialView.setIcon(getMimeIcon(materialType));
        holder.materialCK.setChecked(checkList.get(index));
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
            case OTHER:
                icon = R.string.font_other;
                break;
        }

        return icon;
    }

    protected class ViewHolder {
        public CheckBox materialCK;
        public EduSohoTextBtn materialView;
    }

    private class OnCheckChangeListener implements CompoundButton.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int index = (Integer) compoundButton.getTag();
            checkList.remove(index);
            checkList.add(index, b);
        }
    }
}
