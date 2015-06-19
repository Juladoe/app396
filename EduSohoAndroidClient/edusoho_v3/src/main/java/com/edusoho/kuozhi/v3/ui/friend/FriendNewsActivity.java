package com.edusoho.kuozhi.v3.ui.friend;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Melomelon on 2015/6/8.
 */
public class FriendNewsActivity extends ActionBarBaseActivity {



    public String mTitle = "添加校友";

    private ListView newsList;
    private ArrayList<Friend> mList;

    private FriendNewsAdapter mAdapter;
    private LayoutInflater mInflater;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK,"添加好友");
        mList = new ArrayList<Friend>();
        setContentView(R.layout.friend_news_layout);

        newsList = (ListView)findViewById(R.id.friend_news_list);
        mAdapter = new FriendNewsAdapter(mContext,R.layout.add_friend_item);
        newsList.setAdapter(mAdapter);
        loadFriend();
    }

    private void loadFriend(){
        mAdapter.addItem(new Friend(R.drawable.sample_avatar_1,"花非花",Const.HAVE_ADD_FALSE));
        mAdapter.addItem(new Friend(R.drawable.sample_avatar_2,"扫地神僧",Const.HAVE_ADD_TRUE));
        mAdapter.addItem(new Friend(R.drawable.sample_avatar_3,"独孤求败",Const.HAVE_ADD_FALSE));
        mAdapter.addItem(new Friend(R.drawable.sample_avatar_4,"阮玲玉",Const.HAVE_ADD_FALSE));
        mAdapter.addItem(new Friend(R.drawable.sample_avatar_5,"西门吹雪",Const.HAVE_ADD_TRUE));
        mAdapter.addItem(new Friend(R.drawable.sample_avatar_6,"虚竹",Const.HAVE_ADD_FALSE));
        mAdapter.addItem(new Friend(R.drawable.sample_avatar_7,"段誉",Const.HAVE_ADD_WAIT));
        mAdapter.addItem(new Friend(R.drawable.sample_avatar_8,"乔峰",Const.HAVE_ADD_WAIT));
        mAdapter.addItem(new Friend(R.drawable.sample_avatar_9,"风清扬",Const.HAVE_ADD_WAIT));
        mAdapter.addItem(new Friend(R.drawable.sample_avatar_10,"山鸡",Const.HAVE_ADD_WAIT));
    }


    private class FriendNewsAdapter extends BaseAdapter{
        private  int mResource;
        private FriendNewsAdapter(Context mContext,int mResource) {
            mInflater = LayoutInflater.from(mContext);
            this.mResource = mResource;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(Friend friend){
            mList.add(friend);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder;
            if(convertView == null){
                holder = new ItemHolder();
                convertView = mInflater.inflate(mResource,null);
                holder.mImage = (CircleImageView) convertView.findViewById(R.id.add_friend_image);
                holder.mName = (TextView) convertView.findViewById(R.id.add_friend_name);
                holder.mState = (ImageView) convertView.findViewById(R.id.add_friend_state);
                convertView.setTag(holder);
            }else {
                holder = (ItemHolder) convertView.getTag();
            }

            holder.mImage.setImageResource(mList.get(position).avatarID);
            holder.mName.setText(mList.get(position).name);
            if(position == 3){
                holder.mState.setImageResource(R.drawable.have_add_friend_wait);
            }else if(position%3 == 0){
                holder.mState.setImageResource(R.drawable.have_add_friend_true);
            }else {
                holder.mState.setImageResource(R.drawable.add_friend_selector);
            }
            return convertView;
        }

        private class ItemHolder{
            CircleImageView mImage;
            TextView mName;
            ImageView mState;
        }
    }

}
