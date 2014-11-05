package com.edusoho.listener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.util.AppUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Hby on 14-9-29.
 */
public class URLImageGetter implements Html.ImageGetter {
    private static final String TAG = "URLImageGetter";
    private View mContainer;
    private Context mContext;
    private DisplayImageOptions mOptions;

    public URLImageGetter(View v, Context context) {
        this.mContainer = v;
        this.mContext = context;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    @Override
    public Drawable getDrawable(String source) {
        final URLDrawable urlDrawable = new URLDrawable();
        if (!source.contains("http")) {
            source = EdusohoApp.app.host + source;
        }
        try {
            MyImageLoadingListener myImageLoadingListener = new MyImageLoadingListener(urlDrawable, this.mContainer);
            ImageLoader.getInstance().loadImage(source, mOptions, myImageLoadingListener);
        } catch (Exception ex) {
            Log.d("imageURL--->", ex.toString());
        }
        return urlDrawable;
    }

    public class MyImageLoadingListener implements ImageLoadingListener {
        private URLDrawable mURLDrawable;
        private View mContainer;
        private ProgressBar mReplyImageLoading;

        public MyImageLoadingListener(URLDrawable d, View v) {
            this.mURLDrawable = d;
            this.mContainer = v;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            refreshImageView(loadedImage, mURLDrawable);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            //暂时先考虑多图同路径就触发这个方法，可能也有其他情况。
            Bitmap bitmap = BitmapFactory.decodeFile(ImageLoader.getInstance().getDiskCache().get(imageUri).getPath());
            refreshImageView(bitmap, mURLDrawable);
        }
    }

    private void refreshImageView(Bitmap loadedImage, URLDrawable mURLDrawable) {
        Bitmap bitmap = loadedImage;

        float showMaxWidth = EdusohoApp.app.screenW * 2 / 3f;
        float showMinWidth = EdusohoApp.app.screenW * 1 / 8f;
        if (showMaxWidth < bitmap.getWidth()) {
            bitmap = AppUtil.scaleImage(bitmap, showMaxWidth, 0, URLImageGetter.this.mContext);
        } else if (showMinWidth >= bitmap.getWidth()) {
            bitmap = AppUtil.scaleImage(bitmap, showMinWidth, 0, mContext);
        }
        Drawable drawable = new BitmapDrawable(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mURLDrawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mURLDrawable.drawable = drawable;
        this.mContainer.postInvalidate();
        if (this.mContainer instanceof TextView) {
            TextView tv = (TextView) this.mContainer;
            tv.setText(tv.getText());
        } else if (this.mContainer instanceof EditText) {
            EditText et = (EditText) this.mContainer;
            et.setText(et.getText());
        }

    }

    public class URLDrawable extends BitmapDrawable {
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}