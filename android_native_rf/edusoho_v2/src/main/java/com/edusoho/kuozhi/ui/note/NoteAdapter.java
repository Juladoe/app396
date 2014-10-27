package com.edusoho.kuozhi.ui.note;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.EdusohoBaseAdapter;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;

/**
 * Created by onewoman on 14-10-13.
 */
public class NoteAdapter extends EdusohoBaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<CollectNode> collect;

    public NoteAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        this.collect = new ArrayList<CollectNode>();
    }

    public void addAllDatas(ArrayList<CollectNode> datas) {
        collect.addAll(datas);
        notifyDataSetChanged();
    }

    public void setItem(ArrayList<CollectNode> datas) {
        collect.clear();
        addAllDatas(datas);
    }

    @Override
    public int getCount() {
        return collect.size();
    }

    @Override
    public Object getItem(int i) {
        return collect.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewNoteInflate holder;
        if (view == null) {
            view = inflater.inflate(R.layout.note_inflate, null);
            holder = new ViewNoteInflate();
            holder.nodeImage = (ImageView) view.findViewById(R.id.note_image);
            holder.nodeTitle = (TextView) view.findViewById(R.id.note_title);
            holder.noteCount = (TextView) view.findViewById(R.id.note_count);
            holder.aQuery = new AQuery(view);
            view.setTag(holder);
        } else {
            holder = (ViewNoteInflate) view.getTag();
        }

        CollectNode collectNode = collect.get(i);
        int width = (int) (EdusohoApp.screenW * 0.45);
        holder.aQuery.id(R.id.note_image).image(
                collectNode.courseImage, false, true, 200, R.drawable.noram_course);
        holder.aQuery.id(R.id.note_image)
                .width(width, false)
                .height(AppUtil.getCourseListCoverHeight(width), false);
        holder.nodeTitle.setText(collectNode.courseName);
        holder.noteCount.setText("共" + collectNode.total + "篇笔记");
        return view;
    }

    public class ViewNoteInflate {
        ImageView nodeImage;
        TextView nodeTitle;
        TextView noteCount;
        AQuery aQuery;
    }
}
