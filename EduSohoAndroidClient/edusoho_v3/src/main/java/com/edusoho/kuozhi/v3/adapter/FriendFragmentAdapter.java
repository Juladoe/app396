package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.view.EduSohoRoundButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Melomelon on 2015/5/26.
 */
public class FriendFragmentAdapter extends BaseAdapter {

    private static final int TYPE_SCHOOL_APP = 0;
    private static final int TYPE_FRIEND = 1;

    private int schoolListSize;


    private LayoutInflater mInflater;
    private Context mContext;
    private int mResource;
    private SparseArray<View> mCacheArray;
    private boolean isCache;
    private int mListViewLayoutId;
    private ArrayList tmpSchoolList;
    private ArrayList tmpFriendList;
    private ArrayList mList;
    private OnClickListener mOnClickListener;
    private EdusohoApp mApp;

    public FriendFragmentAdapter(Context mContext, int mResource, EdusohoApp app) {
        this.mContext = mContext;
        this.mResource = mResource;
        tmpSchoolList = new ArrayList();
        tmpFriendList = new ArrayList();
        mList = new ArrayList();
        mCacheArray = new SparseArray<View>();
        mInflater = LayoutInflater.from(mContext);
        mApp = app;

    }

    public FriendFragmentAdapter(Context mContext, int mResource, boolean isCache) {
        this.mContext = mContext;
        this.mResource = mResource;
        this.isCache = isCache;
    }

    public void setSchoolListSize(int size) {
        schoolListSize = size;
    }

    public int getItemViewType(int position) {
        return position < schoolListSize + 1 ? TYPE_SCHOOL_APP : TYPE_FRIEND;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        if (position == 0) {
            final HeadHolder headHolder;
            if (mCacheArray.get(0) == null) {
                v = mInflater.inflate(mResource, null);
                headHolder = new HeadHolder();
                headHolder.searchFriendBtn = (EduSohoRoundButton) v.findViewById(R.id.search_friend_btn);
//                headHolder.addPhoneFriends = (LinearLayout) v.findViewById(R.id.item_add_phone_friend);
//                headHolder.addLessonFriends = (LinearLayout) v.findViewById(R.id.item_add_lesson_friend);
//                headHolder.addClassFriends = (LinearLayout) v.findViewById(R.id.item_add_class_friend);
                v.setTag(headHolder);
                setCacheView(0, v);
            } else {
                v = getCacheView(0);
                headHolder = (HeadHolder) v.getTag();
            }
            headHolder.searchFriendBtn.setOnClickListener(mOnClickListener);
//            headHolder.addPhoneFriends.setOnClickListener(mOnClickListener);
//            headHolder.addLessonFriends.setOnClickListener(mOnClickListener);
//            headHolder.addClassFriends.setOnClickListener(mOnClickListener);

        } else {
            switch (getItemViewType(position)) {

                case TYPE_SCHOOL_APP:
                    final SchoolAppHolder schoolAppHolder;
                    if (mCacheArray.get(position) == null) {
                        v = mInflater.inflate(R.layout.item_type_school_app, null);
                        schoolAppHolder = new SchoolAppHolder();
                        schoolAppHolder.SchoolAppName = (TextView) v.findViewById(R.id.friend_name);
                        schoolAppHolder.schoolAppAvatar = (CircleImageView) v.findViewById(R.id.friend_avatar);
                        schoolAppHolder.schoolAppTag = (LinearLayout) v.findViewById(R.id.school_app_tag);
                        v.setTag(schoolAppHolder);
                        setCacheView(position, v);
                    } else {
                        v = mCacheArray.get(position);
                        schoolAppHolder = (SchoolAppHolder) v.getTag();
                    }

                    final SchoolApp schoolApp = (SchoolApp) mList.get(position - 1);
                    if (schoolApp.isTop) {
                        schoolAppHolder.schoolAppTag.setVisibility(View.VISIBLE);
                    }
                    if (schoolApp.avatar != "") {
                        ImageLoader.getInstance().displayImage(mApp.host + "/" + schoolApp.avatar, schoolAppHolder.schoolAppAvatar, mApp.mOptions);
                    }
                    schoolAppHolder.SchoolAppName.setText(schoolApp.name);

                    break;
                case TYPE_FRIEND:
                    final ItemHolder itemHolder;
                    if (mCacheArray.get(position) == null) {
                        v = mInflater.inflate(R.layout.item_type_friend, null);
                        itemHolder = new ItemHolder();
                        itemHolder.friendName = (TextView) v.findViewById(R.id.friend_name);
                        itemHolder.friendAvatar = (CircleImageView) v.findViewById(R.id.friend_avatar);
                        itemHolder.teacherTag = (ImageView) v.findViewById(R.id.teacher_tag);
                        itemHolder.friendTag = (LinearLayout) v.findViewById(R.id.friend_item_tag);
                        v.setTag(itemHolder);
                        setCacheView(position, v);
                    } else {
                        v = mCacheArray.get(position);
                        itemHolder = (ItemHolder) v.getTag();
                    }

                    final Friend friend = (Friend) mList.get(position - 1);
                    if (friend.isTop) {
                        itemHolder.friendTag.setVisibility(View.VISIBLE);
                    }
                    if (friend.isTeacher == true) {
                        itemHolder.teacherTag.setVisibility(View.VISIBLE);
                    } else {
                        itemHolder.teacherTag.setVisibility(View.GONE);
                    }
                    itemHolder.friendName.setText(friend.nickname);
                    if (!friend.smallAvatar.equals("")) {
                        ImageLoader.getInstance().displayImage(mApp.host + "/" + friend.smallAvatar, itemHolder.friendAvatar, mApp.mOptions);
                    }else {
                        itemHolder.friendAvatar.setImageResource(R.drawable.default_avatar);
                    }
                    break;
            }

        }
        return v;
    }

    public void addSchoolList(List<SchoolApp> list) {
        list.get(0).isTop = true;
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addFriendList(List<Friend> list) {
        list.get(0).isTop = true;
        mList.addAll(list);
        notifyDataSetChanged();
    }


    public void addItems() {
        mList.addAll(tmpSchoolList);
        mList.addAll(tmpFriendList);
        notifyDataSetChanged();
    }

    public void clearList() {
        mList.clear();
    }


    public View getCacheView(int index) {
        if (!isCache) {
            return null;
        }
        return mCacheArray.get(index);
    }

    public void setCacheView(int index, View view) {
        if (!isCache) {
            return;
        }
        mCacheArray.put(index, view);
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
        Object obj = mList.get(position - 1);
        if (mList.get(position - 1) instanceof Friend) {
            return (Friend) obj;
        } else if (mList.get(position - 1) instanceof SchoolApp) {
            return (SchoolApp) obj;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setListViewLayout(int layoutId) {
        mListViewLayoutId = layoutId;
    }

    private class HeadHolder {
        EduSohoRoundButton searchFriendBtn;
        LinearLayout addPhoneFriends;
        LinearLayout addLessonFriends;
        LinearLayout addClassFriends;
    }

    private class ItemHolder {
        private CircleImageView friendAvatar;
        private TextView friendName;
        private ImageView teacherTag;
        private LinearLayout friendTag;
    }

    private class SchoolAppHolder {
        private CircleImageView schoolAppAvatar;
        private TextView SchoolAppName;
        private LinearLayout schoolAppTag;
    }
}
