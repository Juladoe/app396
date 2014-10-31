package com.edusoho.kuozhi.adapter.Note;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.model.Note.Note;

import java.util.ArrayList;

/**
 * Created by onewoman on 14-10-13.
 */
public class NoteListAdapter extends ListBaseAdapter<Note> {

    public NoteListAdapter(Context context, int recourse) {
        super(context, recourse);
    }

    @Override
    public void addItems(ArrayList<Note> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView noteNumber;
        TextView noteLessonTitle;
        TextView noteContent;
        if (view == null) {
            view = inflater.inflate(mResource, null);
        }
        noteNumber = (TextView) view.findViewById(R.id.lesson_note_item_number);
        noteLessonTitle = (TextView) view.findViewById(R.id.lesson_note_item_title);
        noteContent = (TextView) view.findViewById(R.id.lesson_note_item_content);

        Note note = mList.get(i);
        noteNumber.setText(String.format("课时%d", note.lessonNum));
        noteLessonTitle.setText(note.lessonTitle);
        noteContent.setText(Html.fromHtml(note.content));

        return view;
    }

}
