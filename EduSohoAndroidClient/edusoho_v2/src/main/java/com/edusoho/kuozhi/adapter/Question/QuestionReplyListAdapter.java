package com.edusoho.kuozhi.adapter.Question;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.model.Question.EntireReply;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.ReplyModel;
import com.edusoho.kuozhi.model.Question.ReplyResult;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.question.QuestionReplyActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.html.EduTagHandler;
import com.edusoho.kuozhi.view.EdusohoButton;
import com.edusoho.kuozhi.view.HtmlTextView;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hby on 14-9-18.
 * 回复List适配器
 */
public class QuestionReplyListAdapter extends ListBaseAdapter {
    private static final String TAG = "QuestionReplyListAdapter";
    /**
     * gridview内部间隙
     */
    private static final int GRIDVIEW_SPACING = 10;
    /**
     * gridview大小比例
     */
    private static final float GRIDVIEW_CONTENT_PROPORTION = 0.75f;
    private static final float GRIDVIEW_REPLY_PROPORTION = 0.6f;
    private static int mContentImageSize = 0;
    private static int mReplayImageSize = 0;
    private Activity mActivity;
    private List<EntireReply> mEntireReplyList;
    private User mUser;

    private List<EntireReply> mTeacherReplyList;
    private List<EntireReply> mNormalReplyList;
    private List<ReplyModel> mSumReplyModel;
    private QuestionDetailModel mQuestionDetailModel;
    private int mQuestionDetailLayoutId;
    private DisplayImageOptions mOptions;

    private ListViewCache mListViewCache;

    private View.OnClickListener mOnClickListener;


    public QuestionReplyListAdapter(Context context, Activity activity, ReplyResult replyResult, int layoutId, User user) {
        super(context, layoutId);
        mEntireReplyList = new ArrayList<EntireReply>();
        this.mActivity = activity;
        this.mUser = user;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
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

    public void setQuestionInfo(QuestionDetailModel model, int layoutId) {
        mQuestionDetailModel = model;
        mQuestionDetailLayoutId = layoutId;
    }

    public void setViewOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
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
        //算上详细问题内容，+1
        return mEntireReplyList.size() + 1;
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
        EntireReply entireReply = null;

        if (position != 0 && mEntireReplyList.size() > 0) {
            entireReply = mEntireReplyList.get(position - 1);
        }
        if (position == 0) {
            //第一个为问题内容，需特殊处理
            if (mListViewCache.getOneCacheView(0) == null) {
                v = LayoutInflater.from(mContext).inflate(mQuestionDetailLayoutId, null);
                final QuestionContentViewHolder qcvHolder = new QuestionContentViewHolder();
                qcvHolder.icon = (CircularImageView) v.findViewById(R.id.civ_post_pic);
                qcvHolder.tvPostName = (TextView) v.findViewById(R.id.tv_post_name);
                qcvHolder.tvPostDate = (TextView) v.findViewById(R.id.tv_post_date);
                qcvHolder.btnEdit = (EdusohoButton) v.findViewById(R.id.edu_btn_question_edit);
                qcvHolder.tvPostTitle = (TextView) v.findViewById(R.id.post_title);
                qcvHolder.tvPostContent = (TextView) v.findViewById(R.id.htv_post_content);
                ImageLoader.getInstance().displayImage(mQuestionDetailModel.user.mediumAvatar, qcvHolder.icon, mOptions);
                qcvHolder.tvPostName.setText(mQuestionDetailModel.user.nickname);
                qcvHolder.tvPostDate.setText(AppUtil.getPostDays(mQuestionDetailModel.createdTime));
                qcvHolder.tvPostTitle.setText(mQuestionDetailModel.title);

                if (!mQuestionDetailModel.content.contains("img src")) {
                    //qcvHolder.pb_loading.setVisibility(View.GONE);
                    qcvHolder.tvPostContent.setVisibility(View.VISIBLE);
                }
                qcvHolder.tvPostContent.setText(AppUtil.setHtmlContent(Html.fromHtml(AppUtil.removeHtml(
                        AppUtil.filterSpace(fitlerImgTag(mQuestionDetailModel.content))), null, null)));

                qcvHolder.btnEdit.setVisibility(
                        mUser.id == mQuestionDetailModel.userId ? View.VISIBLE: View.GONE
                );
                qcvHolder.btnEdit.setOnClickListener(mOnClickListener);

                /*-----------------添加GridView图片显示控件------------------------*/
                ArrayList<String> mUrlList = convertUrlStringList(mQuestionDetailModel.content);
                if (mUrlList.size() > 0) {
                    GridView gvImage = new GridView(mContext);
                    addGridView(gvImage, v, mUrlList.size());
                    QuestionGridViewImageAdapter qgvia = new QuestionGridViewImageAdapter(mContext, R.layout.question_item_grid_image_view,
                            mUrlList, mContentImageSize, AppUtil.px2sp(mContext, mContext.getResources().getDimension(R.dimen.question_content_image_num_font_size)));
                    gvImage.setAdapter(qgvia);
                }
                /*---------------------------------------------------------------*/

                //第一个问题内容，key==0
                mListViewCache.addCache(0, v);
            } else {
                v = mListViewCache.getOneCacheView(0);
            }
        } else if (mListViewCache.getOneCacheView(entireReply.replyModel.id) == null) {
            v = LayoutInflater.from(this.mContext).inflate(mResource, null);
            holder = new ViewHolder();
            holder.icon = (CircularImageView) v.findViewById(R.id.civ_reply_pic);
            holder.tvReplyType = (TextView) v.findViewById(R.id.tv_reply_type);
            holder.tvReplyName = (TextView) v.findViewById(R.id.tv_reply_name);
            holder.tvReplyTime = (TextView) v.findViewById(R.id.tv_reply_time);
            holder.tvReplyContent = (HtmlTextView) v.findViewById(R.id.tv_reply_content);
            holder.ivEdit = (ImageView) v.findViewById(R.id.iv_reply_edit);
            //holder.pbReplyContent = (ProgressBar) v.findViewById(R.id.pb_reply_content);
            v.setTag(holder);

            holder.tvReplyName.setText(entireReply.replyModel.user.nickname);

            if (holder.tvReplyName.getText().equals(mUser.nickname)) {
                holder.ivEdit.setVisibility(View.VISIBLE);
                //编辑回复
                final EntireReply finalEntireReply = entireReply;
                holder.ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent startIntent = new Intent(mContext, QuestionReplyActivity.class);
                        startIntent.putExtra(Const.REQUEST_CODE, Const.EDIT_REPLY);
                        startIntent.putExtra(Const.POST_ID, String.valueOf(finalEntireReply.replyModel.id));
                        startIntent.putExtra(Const.THREAD_ID, String.valueOf(finalEntireReply.replyModel.threadId));
                        startIntent.putExtra(Const.COURSE_ID, String.valueOf(finalEntireReply.replyModel.courseId));
                        startIntent.putExtra(Const.NORMAL_CONTENT, AppUtil.filterSpace(finalEntireReply.replyModel.content));
                        mActivity.startActivityForResult(startIntent, Const.EDIT_REPLY);
                    }
                });
            } else {
                holder.ivEdit.setVisibility(View.INVISIBLE);
            }

            ImageLoader.getInstance().displayImage(entireReply.replyModel.user.mediumAvatar, holder.icon, new MyImageLoadingListener());
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
                //holder.pbReplyContent.setVisibility(View.GONE);
                holder.tvReplyContent.setVisibility(View.VISIBLE);
            }

            //URLImageGetter urlImageGetter = new URLImageGetter(holder.tvReplyContent, mContext, holder.pbReplyContent);
            //Html.fromHtml方法不知道为什么会产生"\n\n"，所以去掉
            //entireReply.replyModel.content = "<font color='#FF0505'>text</font>";
            holder.tvReplyContent.setText(AppUtil.setHtmlContent(Html.fromHtml(AppUtil.removeHtml(
                    AppUtil.filterSpace(fitlerImgTag(entireReply.replyModel.content))), null, new EduTagHandler())));

            ArrayList<String> mUrlList = convertUrlStringList(entireReply.replyModel.content);
            if (mUrlList.size() > 0) {
                GridView gvImage = new GridView(mContext);
                addReplyGridView(gvImage, v, mUrlList.size());
                QuestionGridViewImageAdapter qgvia = new QuestionGridViewImageAdapter(mContext, R.layout.question_item_grid_image_view,
                        mUrlList, mReplayImageSize, AppUtil.px2sp(mContext, mContext.getResources().getDimension(R.dimen.question_reply_image_num_font_size)));
                gvImage.setAdapter(qgvia);
            }

            mListViewCache.addCache(entireReply.replyModel.id, v);
        } else {
            v = mListViewCache.getOneCacheView(entireReply.replyModel.id);
            //holder = (ViewHolder) ListViewCache.getOneCacheView(entireReply.replyModel.id).getTag();
        }

        return v;
    }

    /**
     * 回复内容中动态添加GridView
     *
     * @param gvImage
     * @param parent
     * @param imageNum
     */
    private void addReplyGridView(GridView gvImage, View parent, int imageNum) {
        LinearLayout layout = (LinearLayout) parent.findViewById(R.id.layout_reply_content);

        int horizontalSpacingNum = 2;
        if (imageNum < 3) {
            horizontalSpacingNum = imageNum % 3 - 1;
        }
        int verticalSapcingNum = (int) Math.ceil(imageNum / 3.0) - 1;

        int gridviewWidth = (int) ((EdusohoApp.screenW - 15 * 2) * GRIDVIEW_REPLY_PROPORTION + horizontalSpacingNum * GRIDVIEW_SPACING);
        int gridviewHeight = (int) ((EdusohoApp.screenW - 15 * 2) * GRIDVIEW_REPLY_PROPORTION / 3 + verticalSapcingNum * GRIDVIEW_SPACING);

        mReplayImageSize = gridviewWidth / 3;

        LinearLayout.LayoutParams gvLayout = new LinearLayout.LayoutParams(gridviewWidth,
                gridviewHeight);
        gvLayout.setMargins(0, 15, 0, 0);
        gvImage.setNumColumns(3);
        gvImage.setVerticalScrollBarEnabled(false);
        gvImage.setVerticalSpacing(GRIDVIEW_SPACING);
        gvImage.setHorizontalSpacing(GRIDVIEW_SPACING);
        gvImage.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        layout.addView(gvImage, gvLayout);

    }

    private void addGridView(GridView gvImage, View parent, int imageNum) {
        RelativeLayout rlPostInfo = (RelativeLayout) parent.findViewById(R.id.rl_post_info);
        int horizontalSpacingNum = 2;
        if (imageNum < 3) {
            horizontalSpacingNum = imageNum % 3 - 1;
        }
        int verticalSpacingNum = (int) Math.ceil(imageNum / 3.0) - 1;

        int gridviewWidth = (int) ((EdusohoApp.screenW - 15 * 2) * GRIDVIEW_CONTENT_PROPORTION + horizontalSpacingNum * GRIDVIEW_SPACING);
        int gridviewHeight = (int) ((EdusohoApp.screenW - 15 * 2) * GRIDVIEW_CONTENT_PROPORTION / 3 + verticalSpacingNum * GRIDVIEW_SPACING);

        mContentImageSize = gridviewWidth / 3;

        RelativeLayout.LayoutParams gvLayout = new RelativeLayout.LayoutParams(gridviewWidth,
                gridviewHeight);
        gvLayout.addRule(RelativeLayout.BELOW, R.id.htv_post_content);
        gvLayout.setMargins(0, 15, 0, 0);
        gvImage.setLayoutParams(gvLayout);
        gvImage.setVerticalScrollBarEnabled(false);
        gvImage.setNumColumns(3);
        gvImage.setVerticalSpacing(GRIDVIEW_SPACING);
        gvImage.setHorizontalSpacing(GRIDVIEW_SPACING);
        gvImage.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        rlPostInfo.addView(gvImage);
    }

    private static class ViewHolder {
        public CircularImageView icon;
        public TextView tvReplyType;
        public TextView tvReplyName;
        public TextView tvReplyTime;
        public HtmlTextView tvReplyContent;
        public ImageView ivEdit;
        //public ProgressBar pbReplyContent;
    }

    private static class QuestionContentViewHolder {
        public CircularImageView icon;
        public TextView tvPostName;
        public TextView tvPostDate;
        public EdusohoButton btnEdit;
        public TextView tvPostTitle;
        public TextView tvPostContent;
        //public ProgressBar pb_loading;
        //public ImageView ivImage;
//        public GridView gvImage;
    }

    public class ListViewCache {
        private SparseArray<android.view.View> mCacheList = new SparseArray<View>();

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

    public class MyImageLoadingListener implements ImageLoadingListener {

        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            //this.mImageView.setImageBitmap(loadedImage);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    }

    /**
     * 过滤回复内容，提取url
     *
     * @param content
     */
    private ArrayList<String> convertUrlStringList(String content) {
        ArrayList<String> urlLits = new ArrayList<String>();
        Matcher m = Pattern.compile("(img src=\".*?\")").matcher(content);
        while (m.find()) {
            String[] s = m.group(1).split("src=");
            String strUrl = s[1].toString().substring(1, s[1].length() - 1);
            if (!strUrl.startsWith("http://")) {
                strUrl = EdusohoApp.app.host + strUrl;
            }
            urlLits.add(strUrl);
        }
        return urlLits;
    }

    /**
     * 过滤img标签
     *
     * @param content
     * @return
     */
    private String fitlerImgTag(String content) {
        return content.replaceAll("(<img src=\".*?\" .>)", "");
        //return content.replaceAll("(<img src=\".*?\" .>)", "").replaceAll("(<p>\\n\\t</p>)", "");
    }
}
