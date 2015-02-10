package com.edusoho.kuozhi.adapter.QuestionNew;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by onewoman on 2014/12/30.
 */
public class QuestionReplyAdapter extends ListBaseAdapter<String> {
    private DisplayImageOptions mDisplayImageOptions;

    public QuestionReplyAdapter(Context context, int resource) {
        super(context, resource);
        //test
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();
    }

    @Override
    public void addItems(ArrayList<String> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int postion, View view, ViewGroup viewGroup) {
        View mQuestionReplyLoadView;
        final ImageView imageView;
        if (view == null) {
            view = inflater.inflate(mResource, null);
        }
        imageView = (ImageView) view.findViewById(R.id.question_answer_content_image);
        mQuestionReplyLoadView = view.findViewById(R.id.load_layout);

        String imgUrl = mList.get(postion);
        ImageLoader.getInstance().displayImage(imgUrl, imageView, mDisplayImageOptions);
        imageView.setOnClickListener(new ImageClickListener(postion, mList) {
        });
        mQuestionReplyLoadView.setVisibility(View.GONE);
        return view;
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
