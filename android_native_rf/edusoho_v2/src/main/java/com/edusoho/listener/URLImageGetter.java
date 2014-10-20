package com.edusoho.listener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.question.QuestionDetailActivity;
import com.edusoho.kuozhi.util.AppUtil;

/**
 * Created by Hby on 14-9-29.
 */
public class URLImageGetter implements Html.ImageGetter {
    private static final String TAG = "URLImageGetter";
    private View mContainer;
    private AQuery mAQuery;
    private Context mContext;
    private ProgressBar mReplyImageLoading;

    public URLImageGetter(View v, AQuery aQuery, Context context, ProgressBar progressBar) {
        this.mContainer = v;
        this.mAQuery = aQuery;
        this.mContext = context;
        this.mReplyImageLoading = progressBar;
    }

    @Override
    public Drawable getDrawable(String source) {
        URLDrawable urlDrawable = new URLDrawable();
        if (!source.contains("http")) {
            source = QuestionDetailActivity.mHost + source;
        }
        //Drawable drawable = new BitmapDrawable(mContext.getResources().openRawResource(R.drawable.defaultpic));
        MyBitmapAjaxCallback myBitmapAjaxCallback = new MyBitmapAjaxCallback(urlDrawable, source, this.mContainer, this.mReplyImageLoading);
        try {
            //Log.d(TAG, "aQuery.id(R.id.iv_tmp)-->" + source);
            //AQuery mAquery = new AQuery(mActivity);
            Log.d(TAG, "myBitmapAjaxCallback.mURL-- >" + myBitmapAjaxCallback.mURL);
            this.mAQuery.id(R.id.iv_tmp).image(source, true, true, 1, R.drawable.defaultpic, myBitmapAjaxCallback);
        } catch (Exception ex) {
            Log.d("imageURL--->", ex.toString());
        }
        return urlDrawable;
    }

    public class MyBitmapAjaxCallback extends BitmapAjaxCallback {
        private URLDrawable mURLDrawable;
        private String mURL;
        private View mContainer;
        private ProgressBar mReplyImageLoading;

        public MyBitmapAjaxCallback(URLDrawable d, String sourceUrl, View v, ProgressBar progressBar) {
            this.mURLDrawable = d;
            this.mURL = sourceUrl;
            this.mContainer = v;
            this.mReplyImageLoading = progressBar;
        }

        @Override
        protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
            Log.d(TAG, "callback-->" + url);
            Bitmap bitmap = URLImageGetter.this.mAQuery.getCachedImage(mURL);

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
            this.mContainer.invalidate();
            TextView tv = (TextView) this.mContainer;
            tv.setText(tv.getText());
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//            ((LinearLayout) tv.getParent()).setLayoutParams(layoutParams);
//            tv.getParent().requestLayout();
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