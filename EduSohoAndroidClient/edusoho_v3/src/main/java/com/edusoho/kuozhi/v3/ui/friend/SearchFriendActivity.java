package com.edusoho.kuozhi.v3.ui.friend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;

import java.util.ArrayList;

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

    public void loadResultFriends(){
        //TODO
//        RequestUrl requestUrl = app.bindUrl(Const.SEARCH_FRIEND,false);
//        requestUrl.setParams(new String[]{
//                "mobile",name,
//                "qq",name,
//                "nickname",name
//        });
//        setProgressBarIndeterminate(true);
//        ajaxGet(requestUrl,new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                setProgressBarIndeterminateVisibility(false);
//
//            }
//        },new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
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

        public void addItem(Friend friend){
            mResultList.add(friend);
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
            holder.image.setImageResource(mResultList.get(position).avatarID);
            holder.name.setText(mResultList.get(position).name);
            switch (mResultList.get(position).state){
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

        private class ItemHolder{
            CircleImageView image;
            TextView name;
            ImageView state;
        }
    }
}
