package com.edusoho.kuozhi.adapter.Note;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.EdusohoBaseAdapter;
import com.edusoho.kuozhi.model.Note.LessonList;
import com.edusoho.listener.URLImageGetter;

import java.util.ArrayList;

/**
 * Created by onewoman on 14-10-13.
 */
public class NoteListAdapter extends EdusohoBaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<LessonList> data;
    private Context context;
    private ProgressBar progressBar;

    public NoteListAdapter(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        this.context = context;
        progressBar = new ProgressBar(this.context);
        data = new ArrayList<LessonList>();
    }

    public void addAllDatas(ArrayList<LessonList> datas) {
        data.addAll(datas);
        notifyDataSetChanged();
    }

    public void setItem(ArrayList<LessonList> datas) {
        data.clear();
        addAllDatas(datas);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewGroup noteList;
        TextView courseNum;
        TextView courseTitle;
        TextView courseContent;
        if (view == null) {
            noteList = (ViewGroup) inflater.inflate(R.layout.notelist_inflater, null);
        } else
            noteList = (ViewGroup) view;

        courseNum = (TextView) noteList.findViewById(R.id.course);
        courseTitle = (TextView) noteList.findViewById(R.id.course_title);
        courseContent = (TextView) noteList.findViewById(R.id.note_course_content);

        courseNum.setText(data.get(i).courseNum);
        courseTitle.setText(data.get(i).courseTitle);
        courseContent.setText(Html.fromHtml(data.get(i).courseContent,new URLImageGetter(courseContent,context,progressBar), null));
        return noteList;
    }

//    public Html.ImageGetter imgGetter = new Html.ImageGetter() {
//        @Override
//        public Drawable getDrawable(String source) {
//            if (!source.contains("http")) {
//                source = activity.app.host + source;
//            }
//            Drawable drawable = null;
//            try {
//                Bitmap bitmap = BitmapFactory.decodeFile(ImageLoader.getInstance().getDiskCache().get(source).getPath());
//                float showWidth = EdusohoApp.app.screenW * 0.8f;
//                if (showWidth < bitmap.getHeight()) {
//                    bitmap = AppUtil.scaleImage(bitmap, showWidth, 0, activity.getBaseContext());
//                }
//                drawable = new BitmapDrawable(bitmap);
//
//                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//            } catch (Exception ex) {
//                Log.d("imageURL--->", ex.toString());
//            }
//
//            return drawable;
//        }
//    };

}
