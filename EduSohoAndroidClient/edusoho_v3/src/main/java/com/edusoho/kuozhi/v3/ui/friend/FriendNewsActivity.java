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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.FollowerNotification;
import com.edusoho.kuozhi.v3.model.bal.FollowerNotificationResult;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Melomelon on 2015/6/8.
 */
public class FriendNewsActivity extends ActionBarBaseActivity {



    public String mTitle = "添加校友";

    private ListView newsList;
    private ArrayList<FollowerNotification> mList;

    private FriendNewsAdapter mAdapter;
    private LayoutInflater mInflater;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK,"粉丝通知");
        mList = new ArrayList<FollowerNotification>();
        setContentView(R.layout.friend_news_layout);

        newsList = (ListView)findViewById(R.id.friend_news_list);
        mAdapter = new FriendNewsAdapter(mContext,R.layout.friend_news_item);
        newsList.setAdapter(mAdapter);
        loadFriend();
    }

    private void loadFriend(){
        RequestUrl requestUrl = app.bindNewUrl(Const.NEW_FOLLOWER_NOTIFICATION, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        stringBuffer.append("?start=0&limit=1000&type=user-follow");
        requestUrl.url = stringBuffer.toString();

        ajaxGet(requestUrl,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                FollowerNotificationResult fnr = parseJsonValue(response,new TypeToken<FollowerNotificationResult>(){});
                for(FollowerNotification fn:fnr.data){
                    mAdapter.addItem(fn);
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
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

        public void addItem(FollowerNotification fn){
            mList.add(fn);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder;
            if(convertView == null){
                holder = new ItemHolder();
                convertView = mInflater.inflate(mResource,null);
                holder.content = (TextView) convertView.findViewById(R.id.news_content);
                holder.time = (TextView) convertView.findViewById(R.id.news_time);
                convertView.setTag(holder);
            }else {
                holder = (ItemHolder) convertView.getTag();
            }

            FollowerNotification fn = mList.get(position);

            if (fn.content.opration.equals("follow")){
                holder.content.setText("用户"+fn.content.userName+"关注了你。");
            }else {
                holder.content.setText("用户"+fn.content.userName+"取消了对你的关注。");
            }
            holder.time.setText(AppUtil.getPostDaysZero(fn.createdTime));
            return convertView;
        }

        private class ItemHolder{
            TextView content;
            TextView time;
        }
    }

}
