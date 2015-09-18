package com.edusoho.kuozhi.v3.adapter.article;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.article.Article;
import com.edusoho.kuozhi.v3.model.bal.article.ArticleChat;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 15/9/17.
 */
public class ArticleCardAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private DisplayImageOptions mOptions;
    private List<ArticleChat> mArcicleChatList;

    public ArticleCardAdapter(Context context)
    {
        this(context, new ArrayList<ArticleChat>());
    }

    public ArticleCardAdapter(Context context, ArrayList<ArticleChat> articles)
    {
        this.mContext = context;
        this.mArcicleChatList = articles;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).
                showImageForEmptyUri(R.drawable.defaultpic).
                showImageOnFail(R.drawable.defaultpic).build();
    }

    public void addArticleChats(ArrayList<ArticleChat> articles) {
        mArcicleChatList.addAll(articles);
        notifyDataSetChanged();
    }

    public void addArticleChat(ArticleChat articleChat) {
        mArcicleChatList.add(articleChat);
        notifyDataSetChanged();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupCount() {
        return mArcicleChatList.size();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.article_list_item_time, null);
        }

        TextView textView = (TextView) convertView;
        ArticleChat articleChat = mArcicleChatList.get(groupPosition);
        int createdTime = articleChat.createdTime;
        String time = "";
        if (groupPosition > 0) {
            ArticleChat prevArticleChat = getGroup(groupPosition - 1);
            if (createdTime - prevArticleChat.createdTime > 60 * 5) {
                time = AppUtil.convertMills2Date(((long) articleChat.createdTime) * 1000);
            }
        } else {
            time = AppUtil.convertMills2Date(((long) articleChat.createdTime) * 1000);
        }
        textView.setText(time);
        return convertView;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public ArticleChat getGroup(int groupPosition) {
        return mArcicleChatList.get(groupPosition);
    }

    @Override
    public Article getChild(int groupPosition, int childPosition) {
        return mArcicleChatList.get(groupPosition).articleList.get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mArcicleChatList.get(groupPosition).articleList.size();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getViewByPosition(groupPosition, childPosition);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        Article article = getChild(groupPosition, childPosition);
        viewHolder.mTitleView.setText(article.title);
        ImageLoader.getInstance().displayImage(article.picture, viewHolder.mImgView, mOptions);
        return convertView;
    }

    private View getViewByPosition(int groupPosition, int childPosition) {
        int count = getChildrenCount(groupPosition);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = null;
        if (count > 1 && childPosition == 0) {
            view = layoutInflater.inflate(R.layout.article_list_item_large, null);
            view.setBackgroundResource(R.drawable.article_list_item_large_bg);
        } else {
            view = layoutInflater.inflate(R.layout.article_list_item_normal, null);
            int res = ( childPosition == (count - 1) ) ?
                    R.drawable.article_list_item_bottom : R.drawable.article_list_item_mid_bg;
            view.setBackgroundResource(res);
        }

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mImgView = (ImageView) view.findViewById(R.id.article_item_img);
        viewHolder.mTitleView = (TextView) view.findViewById(R.id.article_item_text);
        view.setTag(viewHolder);

        return view;
    }

    private class ViewHolder {

        public TextView mTitleView;
        public ImageView mImgView;
    }
}
