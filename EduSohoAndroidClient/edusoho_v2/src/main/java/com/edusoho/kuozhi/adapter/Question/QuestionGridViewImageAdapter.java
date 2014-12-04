package com.edusoho.kuozhi.adapter.Question;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by MyPC on 14-10-30.
 */
public class QuestionGridViewImageAdapter extends BaseAdapter {
    private Context mContext;
    private int mResourceId;
    private ArrayList<String> mImageUrlList;
    private DisplayImageOptions mOptions;
    private int mImageSize;
    private float mImageNumFontSize;

    public QuestionGridViewImageAdapter(Context context, int layoutId, ArrayList<String> list, int conImgSize, float fontSize) {
        this.mContext = context;
        this.mResourceId = layoutId;
        this.mImageUrlList = list;
        this.mImageSize = conImgSize;
        this.mImageNumFontSize = fontSize;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    @Override
    public int getCount() {
        if (mImageUrlList != null) {
            //如果图片超过3个，也只加载3个
//            if (mImageUrlList.size() > 3) {
//                return 3;
//            }
            return mImageUrlList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mImageUrlList != null) {
            return mImageUrlList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, null);
            holder = new ViewHolder();
            holder.ivContentImage = (ImageView) convertView.findViewById(R.id.iv_question_content_image);
            holder.tvImageNum = (TextView) convertView.findViewById(R.id.tv_image_num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String url = getItem(position).toString();

        holder.ivContentImage.getLayoutParams().height = mImageSize;
        holder.ivContentImage.getLayoutParams().width = mImageSize;
        holder.tvImageNum.setTextSize(mImageNumFontSize);

        if (position == 2 && mImageUrlList.size() > 3) {
            holder.tvImageNum.setText(String.format("共%d张", mImageUrlList.size()));
            holder.tvImageNum.setVisibility(View.VISIBLE);
        }

        ImageLoader.getInstance().displayImage(url, holder.ivContentImage, mOptions, new ImageLoadingListener() {
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

        holder.ivContentImage.setOnClickListener(new ImageClickListener(position, mImageUrlList));

        return convertView;
    }

    public static class ViewHolder {
        public ImageView ivContentImage;
        public TextView tvImageNum;
    }

    private class ImageClickListener implements View.OnClickListener {

        private int mIndex;
        private ArrayList<String> mImageArray;

        public ImageClickListener(int i, ArrayList<String> list) {
            this.mIndex = i;
            this.mImageArray = list;
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", mIndex);
            bundle.putStringArrayList("imageList", this.mImageArray);
            EdusohoApp.app.mEngine.runNormalPluginWithBundle("ViewPagerActivity", mContext, bundle);
        }
    }
}
