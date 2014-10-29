package com.edusoho.listener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.ui.question.QuestionDetailActivity;
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
    private ProgressBar mReplyImageLoading;
    private DisplayImageOptions mOptions;

    public URLImageGetter(View v, Context context, ProgressBar progressBar) {
        this.mContainer = v;
        this.mContext = context;
        this.mReplyImageLoading = progressBar;
        mOptions = new DisplayImageOptions.Builder().delayBeforeLoading(100).cacheOnDisk(true).build();
    }

    @Override
    public Drawable getDrawable(String source) {
        final URLDrawable urlDrawable = new URLDrawable();
        if (!source.contains("http")) {
            source = QuestionDetailActivity.mHost + source;
        }

        //MyBitmapAjaxCallback myBitmapAjaxCallback = new MyBitmapAjaxCallback(urlDrawable, source, this.mContainer, this.mReplyImageLoading);
        try {
            MyImageLoadingListener myImageLoadingListener = new MyImageLoadingListener(urlDrawable, this.mContainer, this.mReplyImageLoading);
            ImageLoader.getInstance().loadImage(source, mOptions, myImageLoadingListener);
            //this.mAQuery.id(R.id.iv_tmp).image(source, false, true, 1, R.drawable.defaultpic, myBitmapAjaxCallback);
        } catch (Exception ex) {
            Log.d("imageURL--->", ex.toString());
        }
        return urlDrawable;
    }

    public class MyImageLoadingListener implements ImageLoadingListener {
        private URLDrawable mURLDrawable;
        private View mContainer;
        private ProgressBar mReplyImageLoading;

        public MyImageLoadingListener(URLDrawable d, View v, ProgressBar progressBar) {
            this.mURLDrawable = d;
            this.mContainer = v;
            this.mReplyImageLoading = progressBar;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            Bitmap bitmap = loadedImage;

            this.mReplyImageLoading.setVisibility(View.GONE);
            this.mContainer.setVisibility(View.VISIBLE);

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
            TextView tv = (TextView) this.mContainer;
            tv.setText(tv.getText());
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

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

//    public class MyBitmapAjaxCallback extends BitmapAjaxCallback {
//        private URLDrawable mURLDrawable;
//        private String mURL;
//        private View mContainer;
//        private ProgressBar mReplyImageLoading;
//
//        public MyBitmapAjaxCallback(URLDrawable d, String sourceUrl, View v, ProgressBar progressBar) {
//            this.mURLDrawable = d;
//            this.mURL = sourceUrl;
//            this.mContainer = v;
//            this.mReplyImageLoading = progressBar;
//        }
//
//        @Override
//        protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
//
//            Bitmap bitmap = URLImageGetter.this.mAQuery.getCachedImage(mURL);
//
//            this.mReplyImageLoading.setVisibility(View.GONE);
//            this.mContainer.setVisibility(View.VISIBLE);
//
//            float showMaxWidth = EdusohoApp.app.screenW * 2 / 3f;
//            float showMinWidth = EdusohoApp.app.screenW * 1 / 8f;
//            if (showMaxWidth < bitmap.getWidth()) {
//                bitmap = AppUtil.scaleImage(bitmap, showMaxWidth, 0, URLImageGetter.this.mContext);
//            } else if (showMinWidth >= bitmap.getWidth()) {
//                bitmap = AppUtil.scaleImage(bitmap, showMinWidth, 0, mContext);
//            }
//            Drawable drawable = new BitmapDrawable(bitmap);
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//            mURLDrawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//            mURLDrawable.drawable = drawable;
//            this.mContainer.postInvalidate();
//            TextView tv = (TextView) this.mContainer;
//            tv.setText(tv.getText());
//        }
//    }


}