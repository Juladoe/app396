package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.AvatarLoadingListener;
import com.edusoho.kuozhi.v3.model.bal.DiscussionGroup;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melomelon on 2015/5/26.
 */
public class FriendFragmentAdapter<T extends Friend> extends BaseAdapter {

    private static final int MAX_TYPE_COUNT = 2;
    private static final int TYPE_HEAD = 0;
    private static final int TYPE_FRIEND = 1;

    private LayoutInflater mInflater;
    private ArrayList<T> mList;
    private Context mContext;
    private EdusohoApp mApp;
    private View mHeadView;

    public FriendFragmentAdapter(Context context, EdusohoApp app) {
        mList = new ArrayList();
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mApp = app;
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
                if (convertView == null) {
                    v = mHeadView == null ? new View(mContext) : mHeadView;
                }
                break;

            case TYPE_FRIEND:
                final ItemHolder itemHolder;
                if (convertView == null) {
                    v = mInflater.inflate(R.layout.item_type_friend, null);
                    itemHolder = new ItemHolder();
                    itemHolder.friendName = (TextView) v.findViewById(R.id.friend_name);
                    itemHolder.friendAvatar = (RoundedImageView) v.findViewById(R.id.friend_avatar);
                    itemHolder.teacherTag = (ImageView) v.findViewById(R.id.teacher_tag);
                    itemHolder.dividerLine = v.findViewById(R.id.divider_line);
                    itemHolder.catalog = (TextView) v.findViewById(R.id.catalog);
                    v.setTag(itemHolder);
                } else {
                    itemHolder = (ItemHolder) v.getTag();
                }

                final T friend = mList.get(position - 1);
                if (position != mList.size()) {
                    if (getSectionForPosition(position - 1) != getSectionForPosition(position)) {
                        itemHolder.dividerLine.setVisibility(View.GONE);
                    } else {
                        itemHolder.dividerLine.setVisibility(View.VISIBLE);
                    }
                } else {
                    itemHolder.dividerLine.setVisibility(View.GONE);
                }

                if (CommonUtil.inArray(UserRole.ROLE_TEACHER.name(), friend.getRoles())) {
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

                itemHolder.friendName.setText(friend.getNickname());
                String itemType;
                if (friend instanceof DiscussionGroup) {
                    itemType = PushUtil.ChatUserType.CLASSROOM;
                } else {
                    itemType = PushUtil.ChatUserType.FRIEND;
                }
                ImageLoader.getInstance().displayImage(friend.getMediumAvatar(), itemHolder.friendAvatar, mApp.mOptions, new AvatarLoadingListener(itemType));
                break;
        }
        return v;
    }

    public void addSchoolList(List<T> list) {
        list.get(0).isTop = true;
        list.get(list.size() - 1).isBottom = true;
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addFriendList(List<T> list) {
        list.get(0).isTop = true;
        list.get(list.size() - 1).setBottom(true);
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearList() {
        mList.clear();
        notifyDataSetChanged();
    }

    public void setHeadView(View headView) {
        mHeadView = headView;
    }

    @Override
    public int getCount() {
        return mList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ItemHolder {
        private RoundedImageView friendAvatar;
        private TextView friendName;
        private ImageView teacherTag;
        private View dividerLine;
        private TextView catalog;
    }
}
