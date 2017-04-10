package com.edusoho.kuozhi.v3.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;

/**
 * Created by DF on 2017/2/23.
 */

public class EduHtmlHttpImageGetter implements Html.ImageGetter {
    TextView container;
    URI baseUri;
    boolean matchParentWidth;

    public EduHtmlHttpImageGetter(TextView textView) {
        this.container = textView;
        this.matchParentWidth = false;
    }

    public EduHtmlHttpImageGetter(TextView textView, String baseUrl) {
        this.container = textView;
        if (baseUrl != null) {
            this.baseUri = URI.create(baseUrl);
        }
    }

    public EduHtmlHttpImageGetter(TextView textView, String baseUrl, boolean matchParentWidth) {
        this.container = textView;
        this.matchParentWidth = matchParentWidth;
        if (baseUrl != null) {
            this.baseUri = URI.create(baseUrl);
        }
    }

    public Drawable getDrawable(String source) {
        EduHtmlHttpImageGetter.UrlDrawable urlDrawable = new EduHtmlHttpImageGetter.UrlDrawable();

        // get the actual source
        ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable, this, container, matchParentWidth);

        asyncTask.execute(source);

        // return reference to URLDrawable which will asynchronously load the image specified in the src tag
        return urlDrawable;
    }

    private static class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        private final WeakReference<UrlDrawable> drawableReference;
        private final WeakReference<EduHtmlHttpImageGetter> imageGetterReference;
        private final WeakReference<View> containerReference;
        private String source;
        private boolean matchParentWidth;
        private float scale;

        public ImageGetterAsyncTask(EduHtmlHttpImageGetter.UrlDrawable d, EduHtmlHttpImageGetter imageGetter, View container, boolean matchParentWidth) {
            this.drawableReference = new WeakReference<>(d);
            this.imageGetterReference = new WeakReference<>(imageGetter);
            this.containerReference = new WeakReference<>(container);
            this.matchParentWidth = matchParentWidth;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result == null) {
                return;
            }
            final EduHtmlHttpImageGetter.UrlDrawable urlDrawable = drawableReference.get();
            if (urlDrawable == null) {
                return;
            }
            // set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(0, 0, (int) (result.getIntrinsicWidth() * scale), (int) (result.getIntrinsicHeight() * scale));

            // change the reference of the current drawable to the result from the HTTP call
            urlDrawable.drawable = result;

            final EduHtmlHttpImageGetter imageGetter = imageGetterReference.get();
            if (imageGetter == null) {
                return;
            }
            // redraw the image by invalidating the container
            imageGetter.container.invalidate();
            // re-set text to fix images overlapping text
            imageGetter.container.setText(imageGetter.container.getText());
        }

        /**
         * Get the Drawable from URL
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                URL imageurl = fetchUrl(urlString);
                if (imageurl == null) {
                    return null;
                }
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageurl.toString());
                Drawable drawable = new BitmapDrawable(bitmap);
                scale = getScale(drawable);
                drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * scale), (int) (drawable.getIntrinsicHeight() * scale));
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }

        private float getScale(Drawable drawable) {
            View container = containerReference.get();
            if (!matchParentWidth || container == null) {
                return 1f;
            }
            float maxWidth = container.getWidth();
            float originalDrawableWidth = drawable.getIntrinsicWidth();
            return maxWidth / originalDrawableWidth;
        }

        private URL fetchUrl(String urlString) throws IOException {
            URL url;
            final EduHtmlHttpImageGetter imageGetter = imageGetterReference.get();
            if (imageGetter == null) {
                return null;
            }
            if (imageGetter.baseUri != null) {
                url = imageGetter.baseUri.resolve(urlString).toURL();
            } else {
                url = URI.create(urlString).toURL();
            }

            return url;
        }
    }

    @SuppressWarnings("deprecation")
    public class UrlDrawable extends BitmapDrawable {
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
