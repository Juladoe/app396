package com.edusoho.kuozhi.util.html;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.util.AppUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by howzhi on 14-10-29.
 */
public class EduImageGetterHandler implements Html.ImageGetter {

    private static final String TAG = "EduImageGetterHandler";
    private DisplayImageOptions mOptions;
    private Context mContext;
    private TextView mContainer;

    public EduImageGetterHandler(Context context, TextView view)
    {
        this.mContainer = view;
        this.mContext = context;
        mOptions = new DisplayImageOptions.Builder().delayBeforeLoading(100).cacheOnDisk(true).build();
    }

    @Override
    public Drawable getDrawable(String s) {
        CacheDrawable drawable = new CacheDrawable();
        try{
            ImageLoader.getInstance().loadImage(s, mOptions, new CustomImageLoadingListener(drawable));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    private class CustomImageLoadingListener implements ImageLoadingListener
    {
        private CacheDrawable mDrawable;

        private CustomImageLoadingListener(CacheDrawable drawable)
        {
            this.mDrawable = drawable;
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            float showMaxWidth = EdusohoApp.app.screenW * 2 / 3f;
            float showMinWidth = EdusohoApp.app.screenW * 1 / 8f;
            if (showMaxWidth < loadedImage.getWidth()) {
                loadedImage = AppUtil.scaleImage(loadedImage, showMaxWidth, 0, mContext);
            } else if (showMinWidth >= loadedImage.getWidth()) {
                loadedImage = AppUtil.scaleImage(loadedImage, showMinWidth, 0, mContext);
            }

            mDrawable.bitmap = loadedImage;
            mDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());
            mContainer.setText(mContainer.getText());
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            Bitmap failBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.html_image_fail);
            mDrawable.bitmap = failBitmap;
            mDrawable.setBounds(0, 0, failBitmap.getWidth(), failBitmap.getHeight());
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
        }
    }

    private class CacheDrawable extends BitmapDrawable
    {
        private Bitmap loadBitmap;
        public Bitmap bitmap;

        public CacheDrawable()
        {
            loadBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.html_image_loading);
            setBounds(0, 0, loadBitmap.getWidth(), loadBitmap.getHeight());
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (bitmap == null) {
                canvas.drawBitmap(loadBitmap, 0, 0, new Paint());
                return;
            }

            Log.d(null, "draw-->" + bitmap);
            canvas.drawBitmap(bitmap, 0, 0, new Paint());
        }
    }
}
