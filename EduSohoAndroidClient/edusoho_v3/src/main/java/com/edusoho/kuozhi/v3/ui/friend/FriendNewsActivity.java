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
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

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
    private LoadDialog mLoadDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK,"粉丝通知");
        mList = new ArrayList<FollowerNotification>();
        setContentView(R.layout.friend_news_layout);

        newsList = (ListView)findViewById(R.id.friend_news_list);
        mAdapter = new FriendNewsAdapter(mContext,R.layout.friend_news_item);
        newsList.setAdapter(mAdapter);
        mLoadDialog = LoadDialog.create(mActivity);
        mLoadDialog.setMessage("正在载入数据");
        mLoadDialog.show();
        loadFriend();
    }

    private void loadFriend(){
        RequestUrl requestUrl = app.bindNewUrl(Const.NEW_FOLLOWER_NOTIFICATION, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        stringBuffer.append("?start=0&limit=10000&type=user-follow");
        requestUrl.url = stringBuffer.toString();

        ajaxGet(requestUrl,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                FollowerNotificationResult fnr = parseJsonValue(response,new TypeToken<FollowerNotificationResult>(){});
                for(FollowerNotification fn:fnr.data){
                    mAdapter.addItem(fn);
                }
                mLoadDialog.dismiss();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        if (!app.getNetIsConnect() && mLoadDialog != null){
            mLoadDialog.dismiss();
        }
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
                holder.avatar = (CircleImageView) convertView.findViewById(R.id.new_follower_avatar);
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
            if (fn.content.avatar != "") {
                ImageLoader.getInstance().displayImage(app.host + "/" + fn.content.avatar, holder.avatar, app.mOptions);
            }else {
                holder.avatar.setImageResource(R.drawable.default_avatar);
            }
            return convertView;
        }

        private class ItemHolder{
            CircleImageView avatar;
            TextView content;
            TextView time;
        }
    }

}
