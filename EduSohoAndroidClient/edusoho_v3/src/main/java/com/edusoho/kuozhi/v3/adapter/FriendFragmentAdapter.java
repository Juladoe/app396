package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.view.EduSohoRoundCornerImage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melomelon on 2015/5/26.
 */
public class FriendFragmentAdapter extends BaseAdapter {

    private static final int MAX_TYPE_COUNT = 2;
    private static final int TYPE_HEAD = 0;
    private static final int TYPE_FRIEND = 1;

    private LayoutInflater mInflater;
    private ArrayList mList;
    private OnClickListener mOnClickListener;
    private EdusohoApp mApp;

    public FriendFragmentAdapter(Context mContext, int mResource, EdusohoApp app) {
        mList = new ArrayList();
        mInflater = LayoutInflater.from(mContext);
        mApp = app;

    }

    public void updateList() {
        notifyDataSetChanged();
    }

    public int getSectionForPosition(int position) {
        Friend friend = (Friend) (mList.get(position));
        return friend.getSortLetters().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < mList.size(); i++) {
            if (getItemViewType(i + 1) != TYPE_FRIEND) {
                continue;
            }
            Friend friend = (Friend) (mList.get(i));
            String sortStr = friend.getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return MAX_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            return TYPE_FRIEND;
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_HEAD:
                final HeadHolder headHolder;
                if (convertView == null) {
                    v = mInflater.inflate(R.layout.item_type_friend_head, null);
                    headHolder = new HeadHolder();
                    headHolder.tvSearchFriend = (TextView) v.findViewById(R.id.search_friend_btn);
                    headHolder.discussionGroup = (RelativeLayout) v.findViewById(R.id.discussion_group);
                    headHolder.service = (RelativeLayout) v.findViewById(R.id.service);
                    v.setTag(headHolder);
                } else {
                    headHolder = (HeadHolder) v.getTag();
                }
                headHolder.tvSearchFriend.setOnClickListener(mOnClickListener);
                headHolder.discussionGroup.setOnClickListener(mOnClickListener);
                headHolder.service.setOnClickListener(mOnClickListener);
                break;


            case TYPE_FRIEND:
                final ItemHolder itemHolder;
                if (convertView == null) {
                    v = mInflater.inflate(R.layout.item_type_friend, null);
                    itemHolder = new ItemHolder();
                    itemHolder.friendName = (TextView) v.findViewById(R.id.friend_name);
                    itemHolder.friendAvatar = (EduSohoRoundCornerImage) v.findViewById(R.id.friend_avatar);
                    itemHolder.teacherTag = (ImageView) v.findViewById(R.id.teacher_tag);
                    itemHolder.dividerLine = v.findViewById(R.id.divider_line);
                    itemHolder.catalog = (TextView) v.findViewById(R.id.catalog);
                    v.setTag(itemHolder);
                } else {
                    itemHolder = (ItemHolder) v.getTag();
                }

                final Friend friend = (Friend) mList.get(position - 1);
                if (position != mList.size()) {
                    if (getSectionForPosition(position - 1) != getSectionForPosition(position)) {
                        itemHolder.dividerLine.setVisibility(View.GONE);
                    } else {
                        itemHolder.dividerLine.setVisibility(View.VISIBLE);
                    }
                } else {
                    itemHolder.dividerLine.setVisibility(View.GONE);
                }

                if (CommonUtil.inArray(UserRole.ROLE_TEACHER.name(), friend.roles)) {
                    itemHolder.teacherTag.setVisibility(View.VISIBLE);
                } else {
                    itemHolder.teacherTag.setVisibility(View.GONE);
                }

                position--;
                int section = getSectionForPosition(position);
                if (position == getPositionForSection(section)) {
                    itemHolder.catalog.setVisibility(View.VISIBLE);
                    itemHolder.catalog.setText(friend.getSortLetters());
                } else {
                    itemHolder.catalog.setVisibility(View.GONE);
                }

                itemHolder.friendName.setText(friend.nickname);
                if (!TextUtils.isEmpty(friend.mediumAvatar)) {
                    ImageLoader.getInstance().displayImage(friend.mediumAvatar, itemHolder.friendAvatar, mApp.mOptions);
                } else {
                    itemHolder.friendAvatar.setImageResource(R.drawable.default_avatar);
                }
                break;
        }
        return v;
    }

    public void addSchoolList(List<SchoolApp> list) {
        list.get(0).isTop = true;
        list.get(list.size() - 1).isBottom = true;
        mList.addAll(list);
        updateList();
    }

    public void addFriendList(List<Friend> list) {
        list.get(0).isTop = true;
        list.get(list.size() - 1).isBottom = true;
        mList.addAll(list);
        updateList();
    }


    public void clearList() {
        mList.clear();
        updateList();
    }

    public void setHeadClickListener(OnClickListener onclickListener) {
        this.mOnClickListener = onclickListener;
    }

    @Override
    public int getCount() {
        return mList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return (Friend) mList.get(position - 1);
    }



    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class HeadHolder {
        TextView tvSearchFriend;
        RelativeLayout discussionGroup;
        RelativeLayout service;
    }

    private class ItemHolder {
        private EduSohoRoundCornerImage friendAvatar;
        private TextView friendName;
        private ImageView teacherTag;
        //        private LinearLayout friendTag;
        private View dividerLine;
        private TextView catalog;
    }
}
