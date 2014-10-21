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
import android.widget.ProgressBar;
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
public class QuestionReplyListAdapter extends ListBaseAdapter {
    private static final String TAG = "QuestionReplyListAdapter";
    private Activity mActivity;
    private List<EntireReply> mEntireReplyList;
    private User mUser;

    private List<EntireReply> mTeacherReplyList;
    private List<EntireReply> mNormalReplyList;

    public QuestionReplyListAdapter(Context context, Activity activity, ReplyResult replyResult, int layoutId, User user) {
        super(context, layoutId);
        mEntireReplyList = new ArrayList<EntireReply>();
        this.mActivity = activity;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(mRecourse, null);
            holder = new ViewHolder();
            holder.tvReplyType = (TextView) convertView.findViewById(R.id.tv_reply_type);
            holder.tvReplyName = (TextView) convertView.findViewById(R.id.tv_reply_name);
            holder.tvReplyTime = (TextView) convertView.findViewById(R.id.tv_reply_time);
            holder.tvReplyContent = (HtmlTextView) convertView.findViewById(R.id.tv_reply_content);
            holder.ivEdit = (ImageView) convertView.findViewById(R.id.iv_reply_edit);
            holder.pbReplyContent = (ProgressBar) convertView.findViewById(R.id.pb_reply_content);
            holder.mAqueryItem = new AQuery(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final EntireReply entireReply = mEntireReplyList.get(position);

        if (holder.tvReplyName.getText().equals(mUser.nickname)) {
            holder.ivEdit.setVisibility(View.VISIBLE);
            //ivEdit.setOnClickListener(replyEditClickListener);
            //编辑回复
            holder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startIntent = new Intent(mContext, QuestionReplyActivity.class);
                    startIntent.putExtra(Const.REQUEST_CODE, Const.EDIT_REPLY);
                    startIntent.putExtra(Const.POST_ID, String.valueOf(entireReply.replyModel.id));
                    startIntent.putExtra(Const.THREAD_ID, String.valueOf(entireReply.replyModel.threadId));
                    startIntent.putExtra(Const.COURSE_ID, String.valueOf(entireReply.replyModel.courseId));
                    startIntent.putExtra(Const.NORMAL_CONTENT, entireReply.replyModel.content);
                    mActivity.startActivityForResult(startIntent, Const.EDIT_REPLY);
                }
            });
        } else {
            holder.ivEdit.setVisibility(View.INVISIBLE);
        }

        holder.tvReplyTime.setText(AppUtil.getPostDays(entireReply.replyModel.createdTime));

        if (entireReply.replyModel.isElite == 1) {
            if (entireReply.isFirstReply) {
                holder.tvReplyType.setVisibility(View.VISIBLE);
                holder.tvReplyType.setText("教师的答案（" + String.valueOf(this.mTeacherReplyList.size()) + "条）：");
                createDrawables(holder.tvReplyType, R.drawable.recommend_week_label_icon);
            } else {
                holder.tvReplyType.setVisibility(View.GONE);
            }
            holder.tvReplyName.setTextColor(mContext.getResources().getColor(R.color.teacher_reply));
        } else {
            if (entireReply.isFirstReply) {
                holder.tvReplyType.setVisibility(View.VISIBLE);
                holder.tvReplyType.setText("所有的回复（" + String.valueOf(this.mNormalReplyList.size()) + "条）：");
                createDrawables(holder.tvReplyType, R.drawable.normal_reply_tag);
            } else {
                holder.tvReplyType.setVisibility(View.GONE);
            }
            holder.tvReplyName.setTextColor(mContext.getResources().getColor(R.color.question_lesson));
        }

        if (!entireReply.replyModel.content.contains("img src")) {
            holder.pbReplyContent.setVisibility(View.GONE);
            holder.tvReplyContent.setVisibility(View.VISIBLE);
        }

        long startMili = System.currentTimeMillis();

        URLImageGetter urlImageGetter = new URLImageGetter(holder.tvReplyContent, holder.mAqueryItem, mContext, holder.pbReplyContent);
        Log.d(TAG, "Html.fromHtml-->" + entireReply.replyModel.content);
        //Html.fromHtml方法不知道为什么会产生"\n\n"，所以去掉
        //entireReply.replyModel.content = "<font color='#FF0505'>text</font>";
        holder.tvReplyContent.setText(AppUtil.setHtmlContent(Html.fromHtml(AppUtil.removeHtml(entireReply.replyModel.content), urlImageGetter, null)));
        Log.d("tvReplyContent--->", holder.tvReplyContent.getText().toString());

        long endMili = System.currentTimeMillis();

        Log.d("tvReplyContent--->总耗时为：", (endMili - startMili) + "毫秒");

        return convertView;
    }

    private static class ViewHolder {
        public AQuery mAqueryItem;
        public TextView tvReplyType;
        public TextView tvReplyName;
        public TextView tvReplyTime;
        public HtmlTextView tvReplyContent;
        public ImageView ivEdit;
        public ProgressBar pbReplyContent;
    }

    @Override
    public void addItems(ArrayList list) {

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

    public class MyImageGetter implements Html.ImageGetter {
        private AQuery mAquery;

        public MyImageGetter(AQuery aQuery) {
            this.mAquery = aQuery;
        }

        @Override
        public Drawable getDrawable(String source) {
            if (!source.contains("http")) {
                source = QuestionDetailActivity.mHost + source;
            }
            Drawable drawable = new BitmapDrawable(mContext.getResources().openRawResource(R.drawable.defaultpic));
            try {
                mAquery.id(R.id.iv_tmp).image(source, true, true, 1, R.drawable.defaultpic, null, AQuery.FADE_IN_NETWORK);
                Toast.makeText(mContext, "加载完成", 500).show();
                Bitmap bitmap = mAquery.getCachedImage(source);
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
    }

}
