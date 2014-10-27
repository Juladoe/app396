package com.edusoho.kuozhi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
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
    private List<ReplyModel> mSumReplyModel;

    private ListViewCache mListViewCache;

    public QuestionReplyListAdapter(Context context, Activity activity, ReplyResult replyResult, int layoutId, User user) {
        super(context, layoutId);
        mEntireReplyList = new ArrayList<EntireReply>();
        this.mActivity = activity;
        this.mUser = user;
        mSumReplyModel = new ArrayList<ReplyModel>();
        mListViewCache = new ListViewCache();
        for (ReplyModel replyModel : replyResult.data) {
            mSumReplyModel.add(replyModel);
        }
        listAddItem(mSumReplyModel);

    }

    public void setCacheClear() {
        mListViewCache.clear();
    }

    /**
     * 下拉刷新，因为带有“教师回复”，“全部回复”等标题，所以需要清空mEntireReplyList
     *
     * @param replyResult
     */
    public void addItem(ReplyResult replyResult) {
        for (ReplyModel replyModel : replyResult.data) {
            mSumReplyModel.add(replyModel);
        }
        mEntireReplyList.clear();
        listAddItem(mSumReplyModel);
    }

    private void listAddItem(List<ReplyModel> replyModels) {
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
        mSumReplyModel.clear();
        mEntireReplyList.clear();
    }

    @Override
    public int getCount() {
        Log.d("QuestionReplyListAdapter.getCount()-->", mEntireReplyList.size() + "");
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
        Log.d("position----->", String.valueOf(position));
        ViewHolder holder;
        View v = null;
        final EntireReply entireReply = mEntireReplyList.get(position);
        //View tmpView = mListViewCache.getOneCacheView(entireReply.replyModel.id);
//        if (tmpView == null || (tmpView != null && ((ViewHolder) tmpView.getTag()).tvReplyContent.toString().equals(entireReply.replyModel.content))) {
        if (mListViewCache.getOneCacheView(entireReply.replyModel.id) == null) {
            v = LayoutInflater.from(this.mContext).inflate(mResource, null);
            holder = new ViewHolder();
            holder.tvReplyType = (TextView) v.findViewById(R.id.tv_reply_type);
            holder.tvReplyName = (TextView) v.findViewById(R.id.tv_reply_name);
            holder.tvReplyTime = (TextView) v.findViewById(R.id.tv_reply_time);
            holder.tvReplyContent = (HtmlTextView) v.findViewById(R.id.tv_reply_content);
            holder.ivEdit = (ImageView) v.findViewById(R.id.iv_reply_edit);
            holder.pbReplyContent = (ProgressBar) v.findViewById(R.id.pb_reply_content);
            holder.mAqueryItem = new AQuery(v);
            v.setTag(holder);

            holder.tvReplyName.setText(entireReply.replyModel.user.nickname);

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

            URLImageGetter urlImageGetter = new URLImageGetter(holder.tvReplyContent, holder.mAqueryItem, mContext, holder.pbReplyContent);
            //Html.fromHtml方法不知道为什么会产生"\n\n"，所以去掉
            //entireReply.replyModel.content = "<font color='#FF0505'>text</font>";
            holder.tvReplyContent.setText(AppUtil.setHtmlContent(Html.fromHtml(AppUtil.removeHtml(entireReply.replyModel.content),
                    urlImageGetter, null)));
            mListViewCache.addCache(entireReply.replyModel.id, v);
        } else {
            v = mListViewCache.getOneCacheView(entireReply.replyModel.id);
            //holder = (ViewHolder) ListViewCache.getOneCacheView(entireReply.replyModel.id).getTag();
        }

        return v;
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

    public class ListViewCache {
        private SparseArray<View> mCacheList = new SparseArray<View>();

        public void addCache(int key, View view) {
            if (mCacheList.get(key) == null) {
                Log.d("pos", key + "");
                mCacheList.put(key, view);
            }
        }

        public View getOneCacheView(int key) {
            if (mCacheList.get(key) != null) {
                return mCacheList.get(key);
            }
            return null;
        }

        public void clear() {
            mCacheList.clear();
        }
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
