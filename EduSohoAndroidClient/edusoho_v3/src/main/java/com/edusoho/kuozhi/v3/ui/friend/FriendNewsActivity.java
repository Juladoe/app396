package com.edusoho.kuozhi.v3.ui.friend;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
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
import com.edusoho.kuozhi.v3.model.result.FollowResult;
import com.edusoho.kuozhi.v3.model.sys.*;
import com.edusoho.kuozhi.v3.model.sys.Error;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Melomelon on 2015/6/8.
 */
public class FriendNewsActivity extends ActionBarBaseActivity {

    public String mTitle = "添加校友";

    private ListView newsList;
    private TextView mEmptyNotice;
    private ArrayList<FollowerNotification> mList;
    private ArrayList ids = new ArrayList();
    private SparseArray<String> relations;
//    private ArrayList firstNotification = new ArrayList();
//    private ArrayList markPos = new ArrayList();
    private ArrayList existIds;

    private FriendNewsAdapter mAdapter;
    private LayoutInflater mInflater;
    private LoadDialog mLoadDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK, "好友通知");
        mList = new ArrayList<FollowerNotification>();
        setContentView(R.layout.friend_news_layout);

        newsList = (ListView) findViewById(R.id.friend_news_list);
        mEmptyNotice = (TextView) findViewById(R.id.empty_new_follower);
        mAdapter = new FriendNewsAdapter(mContext, R.layout.friend_news_item);
        newsList.setAdapter(mAdapter);
        mLoadDialog = LoadDialog.create(mActivity);
        mLoadDialog.setMessage("正在载入数据");
        mLoadDialog.show();
        loadFriend();
    }

    private void loadFriend() {
        RequestUrl requestUrl = app.bindNewUrl(Const.NEW_FOLLOWER_NOTIFICATION, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        stringBuffer.append("?start=0&limit=10000&type=user-follow");
        requestUrl.url = stringBuffer.toString();
        existIds = new ArrayList();

        ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                FollowerNotificationResult fnr = parseJsonValue(response, new TypeToken<FollowerNotificationResult>() {
                });
                setEmptyNotice(fnr.data.length);
                for (FollowerNotification fn : fnr.data) {
                    if (!existIds.contains(fn.content.userId)){
                        mAdapter.addItem(fn);
                        ids.add(fn.content.userId);
                        existIds.add(fn.content.userId);
                    }
//                    mAdapter.addItem(fn);
//                    ids.add(fn.content.userId);
                }
                getRelationship();
                mLoadDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        if (!app.getNetIsConnect() && mLoadDialog != null) {
            mLoadDialog.dismiss();
        }
    }

    public void getRelationship() {
        RequestUrl requestUrl = setRelationParams(ids);
        relations = new SparseArray<String>();
        ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] relationReults = mActivity.parseJsonValue(response, new TypeToken<String[]>() {
                });
                for (int i = 0; i < relationReults.length; i++) {
                    relations.put(i, relationReults[i]);
                }
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

        });
    }


    public RequestUrl setRelationParams(ArrayList idList) {
        RequestUrl requestUrl = app.bindNewUrl(Const.USERS, false);
        StringBuffer sb = new StringBuffer(requestUrl.url.toString());
        sb.append(app.loginUser.id + "/" + "friendship?toIds=");
        for (Object id : idList) {
            sb.append(id + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        requestUrl.url = sb.toString();
        return requestUrl;

    }

    public void setEmptyNotice(int length) {
        if (length == 0) {
            mEmptyNotice.setVisibility(View.VISIBLE);
            newsList.setVisibility(View.GONE);
        } else {
            mEmptyNotice.setVisibility(View.GONE);
            newsList.setVisibility(View.VISIBLE);
        }
    }

    private class FriendNewsAdapter extends BaseAdapter {
        private int mResource;

        private FriendNewsAdapter(Context mContext, int mResource) {
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

        public void addItem(FollowerNotification fn) {
            mList.add(fn);
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ItemHolder holder;
            if (convertView == null) {
                holder = new ItemHolder();
                convertView = mInflater.inflate(mResource, null);
                holder.content = (TextView) convertView.findViewById(R.id.news_content);
                holder.time = (TextView) convertView.findViewById(R.id.news_time);
                holder.avatar = (ImageView) convertView.findViewById(R.id.new_follower_avatar);
                holder.relation = (ImageView) convertView.findViewById(R.id.fans_relation);
                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }

            final FollowerNotification fn = mList.get(position);
            final int id = Integer.parseInt(fn.content.userId);
//            if (!(firstNotification.contains(id))){
//                firstNotification.add(id);
//                markPos.add(position);
//            }
//            if (markPos.contains(position)){
//                holder.relation.setVisibility(View.VISIBLE);
//            }else {
//                holder.relation.setVisibility(View.GONE);
//            }
            if (fn.content.opration.equals("follow")) {
                holder.content.setText("用户" + fn.content.userName + "关注了你。");
            } else {
                holder.content.setText("用户" + fn.content.userName + "取消了对你的关注。");
            }
            holder.time.setText(AppUtil.getPostDaysZero(fn.createdTime));

            if (!fn.content.avatar.equals("")) {
                ImageLoader.getInstance().displayImage(app.host + "/" + fn.content.avatar, holder.avatar, app.mOptions);
            } else {
                holder.avatar.setImageResource(R.drawable.default_avatar);
            }
            if (relations.size() != 0) {
                String relation = relations.get(position);
                if (relation.equals(Const.NO_USER)){
                    holder.relation.setVisibility(View.GONE);
                }else {
                    holder.relation.setVisibility(View.VISIBLE);
                }
                switch (relation) {
                    case Const.HAVE_ADD_TRUE:
                        holder.relation.setImageResource(R.drawable.have_add_friend_true);
                        break;
                    case Const.HAVE_ADD_ME:
                    case Const.HAVE_ADD_FALSE:
                        holder.relation.setImageResource(R.drawable.add_friend_selector);
                        break;
                    case Const.HAVE_ADD_WAIT:
                        holder.relation.setImageResource(R.drawable.have_add_friend_wait);
                        break;
                }
                if (relation.equals(Const.HAVE_ADD_FALSE) || relation.equals(Const.HAVE_ADD_ME)) {
                    holder.relation.setClickable(true);
                } else {
                    holder.relation.setClickable(false);
                }
            }

            holder.relation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RequestUrl requestUrl = app.bindNewUrl(Const.USERS, false);
                    StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
                    stringBuffer.append(fn.content.userId + "/followers");
                    requestUrl.url = stringBuffer.toString();
                    HashMap<String, String> params = requestUrl.getParams();
                    params.put("method", "follow");
                    params.put("userId", app.loginUser.id + "");
                    ajaxPost(requestUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            FollowResult followResult = mActivity.parseJsonValue(response, new TypeToken<FollowResult>() {
                            });
                            if (followResult == null) {
                                com.edusoho.kuozhi.v3.model.sys.Error error = mActivity.parseJsonValue(response, new TypeToken<Error>() {
                                });
                                CommonUtil.longToast(mContext, error.message);
                            }
                            if (followResult.success) {
                                CommonUtil.longToast(mContext, "关注用户成功");
                                app.sendMessage(Const.REFRESH_FRIEND_LIST, null);
                                getRelationship();
                            } else {
                                CommonUtil.longToast(mContext, "关注用户失败");
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                }
            });

            return convertView;
        }

        private class ItemHolder {
            ImageView avatar;
            TextView content;
            TextView time;
            ImageView relation;
        }
    }

}
