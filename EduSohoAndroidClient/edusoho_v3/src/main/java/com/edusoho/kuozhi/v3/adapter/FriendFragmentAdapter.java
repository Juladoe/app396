package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.view.EduSohoRoundButton;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Melomelon on 2015/5/26.
 */
public class FriendFragmentAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private Context mContext;
    private int mResource;
    private SparseArray<View> mCacheArray;
    private boolean isCache;
    private int mListViewLayoutId;
    private ArrayList<Friend> mList;
    private OnClickListener mOnClickListener;

    public FriendFragmentAdapter(Context mContext, int mResource) {
        this.mContext = mContext;
        this.mResource = mResource;
        mList = new ArrayList<Friend>();
        mCacheArray = new SparseArray<View>();
        mInflater = LayoutInflater.from(mContext);

    }

    public FriendFragmentAdapter(Context mContext, int mResource, boolean isCache) {
        this.mContext = mContext;
        this.mResource = mResource;
        this.isCache = isCache;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        if(position == 0){
            final HeadHolder headHolder;
            if(mCacheArray.get(0) == null){
                v = mInflater.inflate(mResource,null);
                headHolder = new HeadHolder();
                headHolder.searchFriendBtn = (EduSohoRoundButton) v.findViewById(R.id.search_friend_btn);
//                headHolder.addPhoneFriends = (LinearLayout) v.findViewById(R.id.item_add_phone_friend);
//                headHolder.addLessonFriends = (LinearLayout) v.findViewById(R.id.item_add_lesson_friend);
//                headHolder.addClassFriends = (LinearLayout) v.findViewById(R.id.item_add_class_friend);
                headHolder.qiqiuyuSever = (LinearLayout) v.findViewById(R.id.item_service_qiqiuyu);
                v.setTag(headHolder);
                setCacheView(0,v);
            }else {
                v = getCacheView(0);
                headHolder = (HeadHolder) v.getTag();
            }
            headHolder.searchFriendBtn.setOnClickListener(mOnClickListener);
//            headHolder.addPhoneFriends.setOnClickListener(mOnClickListener);
//            headHolder.addLessonFriends.setOnClickListener(mOnClickListener);
//            headHolder.addClassFriends.setOnClickListener(mOnClickListener);
            headHolder.qiqiuyuSever.setOnClickListener(mOnClickListener);

        }else{
            final ItemHolder itemHolder;
            if(mCacheArray.get(position) == null){
                v = mInflater.inflate(mListViewLayoutId,null);
                itemHolder = new ItemHolder();
                itemHolder.friendName = (TextView) v.findViewById(R.id.friend_name);
                itemHolder.friendAvatar = (CircleImageView) v.findViewById(R.id.friend_avatar);
                itemHolder.teacherTag = (ImageView) v.findViewById(R.id.teacher_tag);
                v.setTag(itemHolder);
                setCacheView(position,v);
            }else {
                v = mCacheArray.get(position);
                itemHolder = (ItemHolder) v.getTag();
            }
            if(mList.get(position-1).isTeacher == true){
                itemHolder.teacherTag.setVisibility(View.VISIBLE);
            }else {
                itemHolder.teacherTag.setVisibility(View.GONE);
            }
            itemHolder.friendName.setText(mList.get(position-1).nickname);
            itemHolder.friendAvatar.setImageResource(mList.get(position-1).avatarID);
        }
        return v;
    }

    public void addItem(Friend friend){
        mList.add(friend);
        notifyDataSetChanged();
    }

    public void addItems(List<Friend> list){
        mList.addAll(list);
    }

    public View getCacheView(int index){
        if(!isCache){
            return null;
        }
        return mCacheArray.get(index);
    }

    public void setCacheView(int index,View view){
        if(!isCache){
            return;
        }
        mCacheArray.put(index,view);
    }

    public void setHeadClickListener(OnClickListener onclickListener){
        this.mOnClickListener = onclickListener;
    }

    @Override
    public int getCount() {
        return mList.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setListViewLayout(int layoutId) {
        mListViewLayoutId = layoutId;
    }

    private class HeadHolder{
        EduSohoRoundButton searchFriendBtn;
        LinearLayout addPhoneFriends;
        LinearLayout addLessonFriends;
        LinearLayout addClassFriends;
        LinearLayout qiqiuyuSever;
    }

    private class ItemHolder{
        private CircleImageView friendAvatar;
        private TextView friendName;
        private ImageView teacherTag;
    }
}
