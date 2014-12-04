package com.edusoho.kuozhi.adapter.Note;

import android.content.ContentProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.entity.NotificationItem;
import com.edusoho.kuozhi.model.Note.NoteInfo;
import com.edusoho.kuozhi.util.AppUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.cordova.App;
import org.w3c.dom.Text;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by onewoman on 14-10-13.
 */
public class NoteAdapter extends ListBaseAdapter<NoteInfo> {

    DisplayImageOptions mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();


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
            holder.noteImage = (ImageView) view.findViewById(R.id.note_image);
            holder.noteLessonTitle = (TextView) view.findViewById(R.id.note_lesson_title);
            holder.noteContent = (TextView) view.findViewById(R.id.note_content);
            holder.noteLastUpdateTime = (TextView) view.findViewById(R.id.last_update_time);

//            holder.aQuery = new AQuery(view);
            view.setTag(holder);
        } else {
            holder = (ViewNoteInflate) view.getTag();
        }

        NoteInfo noteInfo = mList.get(i);

        int width = (int) (EdusohoApp.screenW * 0.45);




        String url = getFirstImage(noteInfo.content);

        ImageLoader.getInstance().displayImage(url, holder.noteImage, mOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ImageView iv = (ImageView) view;
                iv.setBackgroundResource(R.drawable.loading_anim_list);
                AnimationDrawable ad = (AnimationDrawable) iv.getBackground();
                ad.start();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ImageView iv = (ImageView) view;
                AnimationDrawable ad = (AnimationDrawable) iv.getBackground();
                iv.setImageBitmap(loadedImage);
                ad.stop();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });




//        holder.aQuery.id(R.id.note_image).image(
//                noteInfo.largePicture, false, true, 200, R.drawable.noram_course);
//        holder.aQuery.id(R.id.note_image)
//                .width(width, false)
//                .height(AppUtil.getCourseListCoverHeight(width), false);


        holder.noteLessonTitle.setText(noteInfo.lessonTitle);
        holder.noteContent.setText(Html.fromHtml(noteInfo.content));
        System.out.println("content:"+Html.fromHtml(noteInfo.content));
        AppUtil appUtil = null;
        holder.noteLastUpdateTime.setText(String.valueOf(appUtil.getPostDays(noteInfo.noteLastUpdateTime)));
        //holder.noteLastUpdateTime.setText(String.valueOf(noteInfo.noteLastUpdateTime));

        return view;
    }

    public class ViewNoteInflate {
        ImageView noteImage;
        TextView noteLessonTitle;
        TextView noteContent;
        TextView noteLastUpdateTime;
        AQuery aQuery;
    }

    public String getFirstImage(String content){

        Pattern stemPattern= Pattern.compile("<img src=\"([^>\\s]+)\"[^>]*>",Pattern.DOTALL);
        Matcher matcher = stemPattern.matcher(content);

        if(!matcher.find())
        {
            content="";
        }

        while (matcher.find()) {
            content = matcher.group(1);
            break;
        }
        return content;

    }
}
