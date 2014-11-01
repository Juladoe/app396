package com.edusoho.kuozhi.adapter.Note;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.model.Note.NoteInfo;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;

/**
 * Created by onewoman on 14-10-13.
 */
public class NoteAdapter extends ListBaseAdapter<NoteInfo> {

    public NoteAdapter(Context context, int resouce) {
        super(context, resouce);
    }

    @Override
    public void addItems(ArrayList<NoteInfo> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewNoteInflate holder;
        if (view == null) {
            view = inflater.inflate(mResource, null);
            holder = new ViewNoteInflate();
            holder.nodeImage = (ImageView) view.findViewById(R.id.note_image);
            holder.nodeTitle = (TextView) view.findViewById(R.id.note_title);
            holder.noteCount = (TextView) view.findViewById(R.id.note_count);
            holder.aQuery = new AQuery(view);
            view.setTag(holder);
        } else {
            holder = (ViewNoteInflate) view.getTag();
        }

        NoteInfo noteInfo = mList.get(i);

        int width = (int) (EdusohoApp.screenW * 0.45);
        holder.aQuery.id(R.id.note_image).image(
                noteInfo.largePicture, false, true, 200, R.drawable.noram_course);
        holder.aQuery.id(R.id.note_image)
                .width(width, false)
                .height(AppUtil.getCourseListCoverHeight(width), false);
        holder.nodeTitle.setText(noteInfo.courseTitle);
        holder.noteCount.setText("共" + noteInfo.noteNum + "篇笔记");
        return view;
    }

    public class ViewNoteInflate {
        ImageView nodeImage;
        TextView nodeTitle;
        TextView noteCount;
        AQuery aQuery;
    }
}
