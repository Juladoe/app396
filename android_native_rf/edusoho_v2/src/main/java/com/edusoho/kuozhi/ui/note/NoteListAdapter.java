package com.edusoho.kuozhi.ui.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.EdusohoBaseAdapter;
import com.edusoho.kuozhi.util.AppUtil;

import java.io.File;
import java.util.ArrayList;

import cn.trinea.android.common.util.DigestUtils;

/**
 * Created by onewoman on 14-10-13.
 */
public class NoteListAdapter extends EdusohoBaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<LessonList> data;
    private Context context;

    public NoteListAdapter(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        this.context = context;
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
        courseContent.setText(Html.fromHtml(data.get(i).courseContent,
                new NetImageGetter(courseContent, data.get(i).courseContent), null));
        return noteList;
    }

    private class NetImageGetter implements Html.ImageGetter {
        private TextView mTextView;
        private String html;

        public NetImageGetter(TextView textView, String html) {
            this.html = html;
            mTextView = textView;
        }

        @Override
        public Drawable getDrawable(String s) {
            Drawable drawable = null;
            AQuery aQuery = new AQuery(inflater.inflate(R.layout.notelist_inflater, null));
            File cacheDir = AQUtility.getCacheDir(context);
            String fileName = DigestUtils.md5(s);

            File file = new File(cacheDir, fileName);
            Log.d(null, "update file->" + file);
            if (file != null && file.exists()) {
                Bitmap bitmap = AppUtil.getBitmapFromFile(file);
                drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }

            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            aQuery.download(s, file, new AjaxCallback<File>() {
                @Override
                public void callback(String url, File object, AjaxStatus status) {
                    super.callback(url, object, status);
                    mTextView.setText(Html.fromHtml(html, new NetImageGetter(mTextView, html), null));
                }
            });
            return drawable;
        }
    }

}
