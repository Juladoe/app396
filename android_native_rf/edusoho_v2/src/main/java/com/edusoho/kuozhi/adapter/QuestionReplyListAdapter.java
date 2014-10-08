package com.edusoho.kuozhi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Question.EntireReply;
import com.edusoho.kuozhi.model.Question.ReplyModel;
import com.edusoho.kuozhi.model.Question.ReplyResult;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.question.QuestionDetailActivity;
import com.edusoho.kuozhi.ui.question.QuestionReplyActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.HtmlTextView;
import com.edusoho.listener.URLImageGetter;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hby on 14-9-18.
 * 回复List适配器
 */
public class QuestionReplyListAdapter extends EdusohoBaseAdapter {
    private static final String TAG = "QuestionReplyListAdapter";
    public Context mContext;
    private Activity mActivity;
    private List<EntireReply> mEntireReplyList;
    private int mRecourseId;
    private User mUser;

    private List<EntireReply> mTeacherReplyList;
    private List<EntireReply> mNormalReplyList;

    private AQuery mAqueryItem;

    public QuestionReplyListAdapter(Context context, Activity activity, ReplyResult replyResult, int layoutId, User user) {
        mEntireReplyList = new ArrayList<EntireReply>();
        this.mContext = context;
        this.mActivity = activity;
        this.mRecourseId = layoutId;
        this.mUser = user;
        listAddItem(replyResult.data);
    }

    public void addItem(ReplyResult replyResult) {
        listAddItem(replyResult.data);
        //notifyDataSetChanged();
    }

    private void listAddItem(ReplyModel[] replyModels) {
        mNormalReplyList = new ArrayList<EntireReply>();
        mTeacherReplyList = new ArrayList<EntireReply>();
        boolean isNormalFirst = true;
        boolean isTeacherFirst = true;
        //如果刷新或者显示更多，考虑原来mEntireReplyList中的数据，mEntireReplyList+replyModels
        for (ReplyModel replyModel : replyModels) {
            EntireReply entireReply;
            if (replyModel.isElite == 0) {
                if (isNormalFirst) {
                    entireReply = new EntireReply(isNormalFirst, replyModel);
                    isNormalFirst = false;
                } else {
                    entireReply = new EntireReply(isNormalFirst, replyModel);
                }
                mNormalReplyList.add(entireReply);
            } else {
                if (isTeacherFirst) {
                    entireReply = new EntireReply(isTeacherFirst, replyModel);
                    isTeacherFirst = false;
                } else {
                    entireReply = new EntireReply(isTeacherFirst, replyModel);
                }
                mTeacherReplyList.add(entireReply);
            }
        }
        mEntireReplyList.addAll(mTeacherReplyList);
        mEntireReplyList.addAll(mNormalReplyList);
        Log.d(TAG, "----------------");
    }

    public void clearAdapter() {
        mEntireReplyList.clear();
    }

    @Override
    public int getCount() {
        Log.d("getCount()", String.valueOf(mEntireReplyList.size()));
        return mEntireReplyList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mEntireReplyList != null && mEntireReplyList.size() > 0) {
            return mEntireReplyList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("getView()", String.valueOf(position));
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(mRecourseId, null);
            mAqueryItem = new AQuery(convertView);
        }

        final EntireReply entireReply = mEntireReplyList.get(position);

        TextView tvReplyType = AppUtil.getViewHolder(convertView, R.id.tv_reply_type);
        TextView tvReplyName = AppUtil.getViewHolder(convertView, R.id.tv_reply_name);
        TextView tvReplyTime = AppUtil.getViewHolder(convertView, R.id.tv_reply_time);
        HtmlTextView tvReplyContent = AppUtil.getViewHolder(convertView, R.id.tv_reply_content);
        ImageView ivEdit = AppUtil.getViewHolder(convertView, R.id.iv_reply_edit);
        tvReplyName.setText(entireReply.replyModel.user.nickname);

        if (tvReplyName.getText().equals(mUser.nickname)) {
            ivEdit.setVisibility(View.VISIBLE);
            //ivEdit.setOnClickListener(replyEditClickListener);
            //编辑回复
            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startIntent = new Intent(mContext, QuestionReplyActivity.class);
                    startIntent.putExtra(Const.REQUEST_CODE, Const.EDIT_REPLY);
                    startIntent.putExtra(Const.REPLY_ID, String.valueOf(entireReply.replyModel.id));
                    startIntent.putExtra(Const.THREAD_ID, String.valueOf(entireReply.replyModel.threadId));
                    startIntent.putExtra(Const.COURSE_ID, String.valueOf(entireReply.replyModel.courseId));
                    startIntent.putExtra(Const.NORMAL_CONTENT, entireReply.replyModel.content);
                    mActivity.startActivityForResult(startIntent, Const.EDIT_REPLY);
                }
            });
        } else {
            ivEdit.setVisibility(View.INVISIBLE);
        }

        tvReplyTime.setText(AppUtil.getPostDays(entireReply.replyModel.createdTime));

        if (entireReply.replyModel.isElite == 1) {
            if (entireReply.isFirstReply) {
                tvReplyType.setVisibility(View.VISIBLE);
                tvReplyType.setText("教师的答案（" + String.valueOf(this.mTeacherReplyList.size()) + "条）：");
                createDrawables(tvReplyType, R.drawable.recommend_week_label_icon);
            } else {
                tvReplyType.setVisibility(View.GONE);
            }
            tvReplyName.setTextColor(mContext.getResources().getColor(R.color.teacher_reply));
        } else {
            if (entireReply.isFirstReply) {
                tvReplyType.setVisibility(View.VISIBLE);
                tvReplyType.setText("所有的回复（" + String.valueOf(this.mNormalReplyList.size()) + "条）：");
                createDrawables(tvReplyType, R.drawable.normal_reply_tag);
            } else {
                tvReplyType.setVisibility(View.GONE);
            }
            tvReplyName.setTextColor(mContext.getResources().getColor(R.color.question_lesson));
        }

        URLImageGetter urlImageGetter = new URLImageGetter(tvReplyContent, mAqueryItem, mContext);
        //URLImageParserByAsyncTask p = new URLImageParserByAsyncTask(tvReplyContent, mContext);
        //aQuery.id(R.id.tv_reply_content).text(AppUtil.removeHtml(Html.fromHtml(entireReply.replyModel.content).toString()));
        Log.d(TAG, "Html.fromHtml-->" + entireReply.replyModel.content);
        tvReplyContent.setText(Html.fromHtml(AppUtil.removeHtml(entireReply.replyModel.content), urlImageGetter, null));
        Log.d("tvReplyContent--->", tvReplyContent.getText().toString());

        return convertView;
    }

    /**
     * 教师回复、普通回复标题tag颜色
     *
     * @param tv
     * @param drawableId
     */
    private void createDrawables(TextView tv, int drawableId) {
        Drawable drawable = mContext.getResources().getDrawable(drawableId);
        tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

//    private class URLImageGetter implements Html.ImageGetter {
//        private View mContainer;
//        private AQuery mAQuery;
//
//        public URLImageGetter(View v, AQuery aQuery) {
//            this.mContainer = v;
//            this.mAQuery = aQuery;
//        }
//
//        @Override
//        public Drawable getDrawable(String source) {
//            URLDrawable urlDrawable = new URLDrawable();
//            if (!source.contains("http")) {
//                source = QuestionDetailActivity.mHost + source;
//            }
//            //Drawable drawable = new BitmapDrawable(mContext.getResources().openRawResource(R.drawable.defaultpic));
//            MyBitmapAjaxCallback myBitmapAjaxCallback = new MyBitmapAjaxCallback(urlDrawable, source, this.mContainer);
//            try {
//                //Log.d(TAG, "aQuery.id(R.id.iv_tmp)-->" + source);
//                //AQuery mAquery = new AQuery(mActivity);
//                Log.d(TAG, "myBitmapAjaxCallback.mURL-- >" + myBitmapAjaxCallback.mURL);
//                this.mAQuery.id(R.id.iv_tmp).image(source, true, true, 1, R.drawable.defaultpic, myBitmapAjaxCallback);
//            } catch (Exception ex) {
//                Log.d("imageURL--->", ex.toString());
//            }
//            return urlDrawable;
//        }
//    }
//
//    public class MyBitmapAjaxCallback extends BitmapAjaxCallback {
//        private URLDrawable mURLDrawable;
//        private String mURL;
//        private View mContainer;
//
//        public MyBitmapAjaxCallback(URLDrawable d, String sourceUrl, View v) {
//            this.mURLDrawable = d;
//            this.mURL = sourceUrl;
//            this.mContainer = v;
//        }
//
//        @Override
//        protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
//            Log.d(TAG, "callback-->" + url);
//            Bitmap bitmap = mAqueryItem.getCachedImage(mURL);
//
//            float showMaxWidth = EdusohoApp.app.screenW * 2 / 3f;
//            float showMinWidth = EdusohoApp.app.screenW * 1 / 8f;
//            if (showMaxWidth < bitmap.getWidth()) {
//                bitmap = AppUtil.scaleImage(bitmap, showMaxWidth, 0, mContext);
//            } else if (showMinWidth >= bitmap.getWidth()) {
//                bitmap = AppUtil.scaleImage(bitmap, showMinWidth, 0, mContext);
//            }
//            Drawable drawable = new BitmapDrawable(bitmap);
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//            mURLDrawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//            mURLDrawable.drawable = drawable;
//            this.mContainer.invalidate();
//            TextView tv = (TextView) this.mContainer;
//            tv.setText(tv.getText());
////            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
////            ((LinearLayout) tv.getParent()).setLayoutParams(layoutParams);
////            tv.getParent().requestLayout();
//        }
//    }

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

    public class URLImageParserByAsyncTask implements Html.ImageGetter {
        Context c;
        View container;

        public URLImageParserByAsyncTask(View t, Context c) {
            this.c = c;
            this.container = t;
        }

        public Drawable getDrawable(String source) {
            if (!source.contains("http")) {
                source = QuestionDetailActivity.mHost + source;
            }
            URLDrawable urlDrawable = new URLDrawable();

            ImageGetterAsyncTask asyncTask =
                    new ImageGetterAsyncTask(urlDrawable);

            asyncTask.execute(source);

            return urlDrawable;
        }

        public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
            URLDrawable urlDrawable;

            public ImageGetterAsyncTask(URLDrawable d) {
                this.urlDrawable = d;
            }

            @Override
            protected Drawable doInBackground(String... params) {
                String source = params[0];
                return fetchDrawable(source);
            }

            @Override
            protected void onPostExecute(Drawable result) {
                // set the correct bound according to the result from HTTP call
                urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0
                        + result.getIntrinsicHeight());

                urlDrawable.drawable = result;

                URLImageParserByAsyncTask.this.container.invalidate();
                TextView tv = (TextView) URLImageParserByAsyncTask.this.container;
                tv.setText(tv.getText());

//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//                ((LinearLayout) tv.getParent()).setLayoutParams(layoutParams);
//                tv.getParent().requestLayout();
            }

            public Drawable fetchDrawable(String urlString) {
                try {
                    InputStream is = fetch(urlString);
                    Drawable drawable = Drawable.createFromStream(is, "src");
                    drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0
                            + drawable.getIntrinsicHeight());
                    return drawable;
                } catch (Exception e) {
                    return null;
                }
            }

            /**
             * Http获取图片
             *
             * @param urlString
             * @return
             * @throws MalformedURLException
             * @throws IOException
             */
            private InputStream fetch(String urlString) throws MalformedURLException, IOException {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet request = new HttpGet(urlString);
                HttpResponse response = httpClient.execute(request);
                return response.getEntity().getContent();
            }
        }
    }

    private Html.ImageGetter imgGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            if (!source.contains("http")) {
                source = QuestionDetailActivity.mHost + source;
            }
            Drawable drawable = new BitmapDrawable(mContext.getResources().openRawResource(R.drawable.defaultpic));
            try {
                mAqueryItem.id(R.id.iv_tmp).image(source, true, true, 1, R.drawable.defaultpic, null, AQuery.FADE_IN_NETWORK);
                Toast.makeText(mContext, "加载完成", 500).show();
                Bitmap bitmap = mAqueryItem.getCachedImage(source);
                float showMaxWidth = EdusohoApp.app.screenW * 2 / 3f;
                float showMinWidth = EdusohoApp.app.screenW * 1 / 8f;
                if (showMaxWidth < bitmap.getWidth()) {
                    bitmap = AppUtil.scaleImage(bitmap, showMaxWidth, 0, mContext);
                } else if (showMinWidth >= bitmap.getWidth()) {
                    bitmap = AppUtil.scaleImage(bitmap, showMinWidth, 0, mContext);
                }
                drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            } catch (Exception ex) {
                Log.d("imageURL--->", ex.toString());
            }
            return drawable;
        }
    };

}
