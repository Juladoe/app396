package com.edusoho.kuozhi.v3.adapter.article;

import android.content.Context;
import android.util.Log;
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
        mArcicleChatList.addAll(0, articles);
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

        int childCount = getChildrenCount(groupPosition);
        convertView = getViewByPosition(convertView, childPosition, childCount);
        changeChildViewByPosition(convertView, childPosition, childCount);

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        Article article = getChild(groupPosition, childPosition);
        Log.d("childPosition", String.format("title:%s  pos:%d v:%s", article.title, childPosition, convertView));
        viewHolder.mTitleView.setText(article.title);
        ImageLoader.getInstance().displayImage(article.picture, viewHolder.mImgView, mOptions);
        return convertView;
    }

    private void changeChildViewByPosition(View view, int childPosition, int childCount) {
        if (childCount == 1) {
            view.setBackgroundResource(R.drawable.article_list_item_single_bg);
        } else if (childCount > 1 && childPosition == 0) {
            view.setBackgroundResource(R.drawable.article_list_item_large_bg);
        } else {
            int res = ( childPosition == (childCount - 1) ) ?
                    R.drawable.article_list_item_bottom : R.drawable.article_list_item_mid_bg;
            view.setBackgroundResource(res);
        }
    }

    private View getViewByPosition(View view, int childPosition, int childCount) {
        ViewHolder viewHolder =
                view != null ? (ViewHolder) view.getTag() : new ViewHolder();

        int viewType = -1;
        if (childCount == 1) {
            viewType = 0;
        } else if (childCount > 1 && childPosition == 0) {
            viewType = 1;
        } else {
            viewType = 2;
        }

        if (view != null && viewHolder.type == viewType) {
            Log.d("childPosition-", String.format("childCount:%d  childPosition:%d vt:%d", childCount, childPosition, viewHolder.type));
            return view;
        }

        viewHolder = new ViewHolder();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case 0:
            case 1:
                viewHolder.type = viewType;
                view = layoutInflater.inflate(R.layout.article_list_item_large, null);
                break;
            case 2:
                viewHolder.type = viewType;
                view = layoutInflater.inflate(R.layout.article_list_item_normal, null);
        }

        viewHolder.mImgView = (ImageView) view.findViewById(R.id.article_item_img);
        viewHolder.mTitleView = (TextView) view.findViewById(R.id.article_item_text);
        view.setTag(viewHolder);

        Log.d("childPosition", String.format("childCount:%d  childPosition:%d vt:%d", childCount, childPosition, viewHolder.type));
        return view;
    }

    private class ViewHolder {

        public int type;
        public TextView mTitleView;
        public ImageView mImgView;

        public ViewHolder()
        {
            this.type = -1;
        }
    }
}
