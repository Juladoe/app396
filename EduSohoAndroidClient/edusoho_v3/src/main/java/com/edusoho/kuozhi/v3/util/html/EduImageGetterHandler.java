package com.edusoho.kuozhi.v3.util.html;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by howzhi on 14-10-29.
 */
public class EduImageGetterHandler implements Html.ImageGetter {

    private static final String TAG = "EduImageGetterHandler";
    private DisplayImageOptions mOptions;
    private Context mContext;
    private TextView mContainer;
    private int mImageSize;

    private SparseArray<String> mUrlArray;

    public EduImageGetterHandler(Context context, TextView view) {
        this.mImageSize = -1;
        this.mContainer = view;
        this.mContext = context;
        mUrlArray = new SparseArray<String>();
        mOptions = new DisplayImageOptions.Builder()
                .delayBeforeLoading(100)
                .cacheOnDisk(true)
                .build();
    }

    public EduImageGetterHandler setSize(int size) {
        mImageSize = size;
        return this;
    }

    @Override
    public Drawable getDrawable(String s) {
        if (!s.startsWith("http:")) {
            s = EdusohoApp.app.host + s;
        }
        CacheDrawable drawable = new CacheDrawable();
        try {
            ImageLoader loader = ImageLoader.getInstance();
            if (mImageSize == -1) {
                loader.loadImage(s, mOptions, new CustomImageLoadingListener(drawable));
            } else {
                ImageSize imageSize = new ImageSize(mImageSize, mImageSize);
                loader.loadImage(s, imageSize, mOptions, new CustomImageLoadingListener(drawable));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    private class CustomImageLoadingListener implements ImageLoadingListener {
        private CacheDrawable mDrawable;

        private CustomImageLoadingListener(CacheDrawable drawable) {
            this.mDrawable = drawable;
        }

        private void setBitmap(Bitmap loadedImage) {
            float showMaxWidth, showMinWidth;
            if (mImageSize == -1) {
                showMaxWidth = EdusohoApp.app.screenW * 0.9f;
                showMinWidth = EdusohoApp.app.screenW * 0.5f;
            } else {
                showMaxWidth = mImageSize * 0.9f;
                showMinWidth = mImageSize * 0.9f;
            }
            if (showMaxWidth < loadedImage.getWidth()) {
                loadedImage = AppUtil.scaleImage(loadedImage, showMaxWidth, 0);
            } else if (showMinWidth >= loadedImage.getWidth()) {
                loadedImage = AppUtil.scaleImage(loadedImage, showMinWidth, 0);
            }

            mDrawable.bitmap = loadedImage;
            mDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());
            mContainer.setText(mContainer.getText());
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            Bitmap loadedImage = ImageLoader.getInstance().loadImageSync(imageUri, mOptions);
            setBitmap(loadedImage);
            Log.d(null, "imageUri onLoadingCancelled--->" + imageUri);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            Log.d(null, "imageUri complete--->" + imageUri);
            setBitmap(loadedImage);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            Bitmap failBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.html_image_fail);
            mDrawable.bitmap = failBitmap;
            mDrawable.setBounds(0, 0, failBitmap.getWidth(), failBitmap.getHeight());
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            Log.d(null, "imageUri onLoadingStarted--->" + imageUri);
        }
    }

    private class CacheDrawable extends BitmapDrawable {
        public Bitmap bitmap;
        private Bitmap loadBitmap;

        public CacheDrawable() {
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

            canvas.drawBitmap(bitmap, 0, 0, new Paint());
        }
    }
}
