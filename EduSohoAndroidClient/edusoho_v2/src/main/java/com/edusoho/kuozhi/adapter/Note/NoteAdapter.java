package com.edusoho.kuozhi.adapter.Note;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.edusoho.kuozhi.view.ESTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).showImageOnFail(R.drawable.defaultpic).build();
    }

    @Override
    public void addItems(ArrayList<NoteInfo> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(mResource, null);
            holder = new ViewHolder();
            holder.noteImage = (ImageView) convertView.findViewById(R.id.note_image);
            holder.noteLessonTitle = (TextView) convertView.findViewById(R.id.note_lesson_title);
            holder.noteContent = (TextView) convertView.findViewById(R.id.note_content);
            holder.noteLastUpdateTime = (ESTextView) convertView.findViewById(R.id.last_update_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final NoteInfo noteInfo = mList.get(position);
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
                    Log.d(noteInfo.lessonTitle, imageUri);
                    ad.stop();
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
            holder.noteImage.setVisibility(View.VISIBLE);
        } else {
            holder.noteImage.setVisibility(View.GONE);
        }
        holder.noteImage.getLayoutParams().height = EdusohoApp.screenH / 7;
        if (noteInfo.lessonTitle == null) {
            holder.noteLessonTitle.setText("该课时已被删除");
        } else {
            holder.noteLessonTitle.setText(noteInfo.lessonTitle);
        }
        holder.noteContent.setText(Html.fromHtml(filtlerBlank(removeImgTagFromString(noteInfo.content))));
        holder.noteLastUpdateTime.setText(String.valueOf(AppUtil.getPostDays(noteInfo.noteLastUpdateTime)));
        return convertView;
    }

    public static class ViewHolder {
        ImageView noteImage;
        TextView noteLessonTitle;
        TextView noteContent;
        ESTextView noteLastUpdateTime;
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

    //去除空行
    private String filtlerBlank(String content) {
        return content.replaceAll("<p[^>]*>|</p>|<br />", "");
    }
}
