package com.edusoho.kuozhi.adapter.Note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.model.Note.NoteInfo;
import com.edusoho.kuozhi.util.AppUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.cordova.App;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by onewoman on 14-10-13.
 */
public class NoteAdapter extends ListBaseAdapter<NoteInfo> {
    private static final String TAG = "NoteAdapter";
    private DisplayImageOptions mOptions;

    public NoteAdapter(Context context, int resource) {
        super(context, resource);
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    @Override
    public void addItems(ArrayList<NoteInfo> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(mResource, null);
            holder = new ViewHolder();
            holder.noteImage = (ImageView) convertView.findViewById(R.id.note_image);
            holder.noteLessonTitle = (TextView) convertView.findViewById(R.id.note_lesson_title);
            holder.noteContent = (TextView) convertView.findViewById(R.id.note_content);
            holder.noteLastUpdateTime = (TextView) convertView.findViewById(R.id.last_update_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NoteInfo noteInfo = mList.get(position);
        String url = getFirstImage(noteInfo.content);
        if (url != noteInfo.content) {
            ImageLoader.getInstance().displayImage(url, holder.noteImage, mOptions, new ImageLoadingListener() {
                AnimationDrawable ad;

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    ImageView iv = (ImageView) view;
                    iv.setBackgroundResource(R.drawable.loading_anim_list);
                    ad = (AnimationDrawable) iv.getBackground();
                    ad.start();
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ImageView iv = (ImageView) view;
                    iv.setImageBitmap(loadedImage);
                    ad.stop();
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        } else {
            holder.noteImage.setVisibility(View.GONE);
        }
        holder.noteImage.getLayoutParams().height = EdusohoApp.screenH / 7;
        holder.noteLessonTitle.setText(noteInfo.lessonTitle);
        holder.noteContent.setText(Html.fromHtml(removeImgTagFromString(noteInfo.content)));
        holder.noteLastUpdateTime.setText(String.valueOf(AppUtil.getPostDays(noteInfo.noteLastUpdateTime)));
        return convertView;
    }

    public class ViewHolder {
        ImageView noteImage;
        TextView noteLessonTitle;
        TextView noteContent;
        TextView noteLastUpdateTime;
    }

    /**
     * 去掉所有<Img>标签
     *
     * @param content
     * @return
     */
    private String removeImgTagFromString(String content) {
        Matcher m = Pattern.compile("(<img src=\".*?\" .>)").matcher(content);
        while (m.find()) {
            content = content.replace(m.group(1), "");
        }
        return content;
    }

    /**
     * 获取content中的第一个图片
     *
     * @param content
     * @return
     */
    public String getFirstImage(String content) {
        Pattern stemPattern = Pattern.compile("<img src=\"([^>\\s]+)\"[^>]*>", Pattern.DOTALL);
        Matcher matcher = stemPattern.matcher(content);

        while (matcher.find()) {
            content = matcher.group(1);
            break;
        }
        return content;
    }
}
