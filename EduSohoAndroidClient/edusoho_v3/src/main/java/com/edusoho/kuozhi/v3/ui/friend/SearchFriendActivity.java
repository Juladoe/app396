package com.edusoho.kuozhi.v3.ui.friend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.cache.request.model.Request;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.FriendResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Melomelon on 2015/6/10.
 */

public class SearchFriendActivity extends ActionBarBaseActivity {

    public static final String NAME = "name";

    private String name;
    private SearchFriendAdapter mAdapter;

    private ListView mList;
    private ArrayList<Friend> mResultList;
    private ArrayList<Friend> mTmpList;
    private Integer[] friendIds = new Integer[15];
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friend_layout);
        mList = (ListView) findViewById(R.id.search_friend_list);
        Intent intent = getIntent();
        if(intent != null){
            name = intent.getStringExtra(NAME);
        }
        setBackMode(BACK,"搜索"+"“"+name+"”");
        mResultList = new ArrayList<Friend>();
        mAdapter = new SearchFriendAdapter();
        mList.setAdapter(mAdapter);

        loadResultFriends();
    }

    public void getRelationship(){
        for(final Friend friend:mResultList){
            RequestUrl requestUrl = setRelationParams(friend.id);
            ajaxGet(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    HashMap<String,String> relationShip =  mActivity.parseJsonValue(response, new TypeToken<HashMap<String,String>>() {
                    });
                    friend.friendship = relationShip.get("friendship");
                    mAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("VoleyError",error.toString());

                }
            });
        }
    };

    public RequestUrl setRelationParams(int toId){
        RequestUrl requestUrl = app.bindNewUrl(Const.USERS, false);
        StringBuffer sb = new StringBuffer(requestUrl.url.toString());
        sb.append(app.loginUser.id+"/"+"friendship?toId="+toId);
        requestUrl.url = sb.toString();

        return requestUrl;
    }

    public void loadResultFriends(){
        RequestUrl requestUrl = app.bindNewUrl(Const.USERS, false);
        requestUrl.setGetParams(new String[]{"q",name});
        ajaxGet(requestUrl,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                FriendResult friendResult = mActivity.parseJsonValue(response,new TypeToken<FriendResult>(){});

                if(friendResult == null){
                    //TODO 空数据页面

                }

                count = 0;
                if(friendResult.mobile != null){
                    for(Friend friend:friendResult.mobile){
                        mAdapter.addItem(friend);
                        friendIds[count] = friend.id;
                        count++;

                    }
                }
                if(friendResult.qq != null){
                    for(Friend friend:friendResult.qq){
                        if(Arrays.asList(friendIds).contains(friend.id)){
                            continue;
                        }else {
                            friendIds[count] = friend.id;
                            mAdapter.addItem(friend);
                            count++;
                        }
                    }
                }
                if(friendResult.nickname != null){
                    for(Friend friend:friendResult.nickname){
                        if(Arrays.asList(friendIds).contains(friend.id)){
                            continue;
                        }else {
                            friendIds[count] = friend.id;
                            mAdapter.addItem(friend);
                            count++;
                        }
                    }
                }
                getRelationship();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


    public class SearchFriendAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mResultList.size();
        }

        @Override
        public Object getItem(int position) {
            return mResultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder;
            if(convertView == null){
                holder = new ItemHolder();
                convertView = getLayoutInflater().inflate(R.layout.add_friend_item,null);
                holder.image = (CircleImageView) convertView.findViewById(R.id.add_friend_image);
                holder.name = (TextView) convertView.findViewById(R.id.add_friend_name);
                holder.state = (ImageView) convertView.findViewById(R.id.add_friend_state);
                convertView.setTag(holder);
            }else {
                holder = (ItemHolder) convertView.getTag();
            }

            Friend friend = mResultList.get(position);
            //TODO touxiang
            if(friend.smallAvatar == ""){
                holder.image.setImageResource(R.drawable.default_avatar);
            }else {
                ImageLoader.getInstance().displayImage(friend.smallAvatar, holder.image, mActivity.app.mOptions);
            }
            holder.name.setText(friend.nickname);
            if (friend.friendship == null){
                return convertView;
            }
            switch (friend.friendship){
                case Const.HAVE_ADD_TRUE:
                    holder.state.setImageResource(R.drawable.have_add_friend_true);
                    break;
                case Const.HAVE_ADD_FALSE:
                    holder.state.setImageResource(R.drawable.add_friend_selector);
                    break;
                case Const.HAVE_ADD_WAIT:
                    holder.state.setImageResource(R.drawable.have_add_friend_wait);
                    break;
            }
            return convertView;
        }

        public void addItem(Friend friend){
            mResultList.add(friend);
            notifyDataSetChanged();
        }

        private class ItemHolder{
            CircleImageView image;
            TextView name;
            ImageView state;
        }
    }
}
