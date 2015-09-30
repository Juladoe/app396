package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.result.CloudResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.dialog.RedirectPreViewDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by howzhi on 15/9/28.
 */
public class ChatSelectFragment extends BaseFragment {

    public static final String BODY = "body";

    private ChatSelectListAdapter mChatSelectListAdapter;
    private ListView mChatSelectListView;
    private View mSelectFrientBtn;

    private RedirectBody mRedirectBody;
    private ChatDataSource mChatDataSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.chatselect_layout);
        mChatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initBundleData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mSelectFrientBtn = view.findViewById(R.id.select_friend_btn);
        mChatSelectListView = (ListView) view.findViewById(R.id.chat_select_list);
        mChatSelectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handlerListItemClick((New)parent.getItemAtPosition(position));
            }
        });
        mSelectFrientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, "选择校友");
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "FriendSelectFragment");
                    }
                });
            }
        });

        initChatList();
    }

    private void initBundleData() {
        Bundle bundle = getArguments();
        mRedirectBody = (RedirectBody) bundle.getSerializable(BODY);
    }

    private void sendMessage(New item, RedirectBody body) {

        CustomContent customContent = createSendMsgCustomContent(item);
        Gson gson = new Gson();
        String content = gson.toJson(body);

        Chat chat = updateChatData(item.fromId, content, customContent.getCreatedTime());
        WrapperXGPushTextMessage message = updateNewsList(customContent, content);
        redirectMessageToUser(customContent, chat, message);
    }

    private Chat updateChatData(int toId, String content, int createdTime) {
        Chat chat = new Chat(app.loginUser.id, toId, app.loginUser.nickname, app.loginUser.mediumAvatar,
                content, Chat.FileType.MULTI.toString().toLowerCase(), createdTime);
        chat.direct = Chat.Direct.SEND;
        chat.setDelivery(Chat.Delivery.UPLOADING);
        chat.headimgurl = app.loginUser.mediumAvatar;
        chat.chatId = (int) mChatDataSource.create(chat);

        return chat;
    }

    private void redirectMessageToUser(CustomContent customContent, final Chat chat, WrapperXGPushTextMessage message) {
        int toId = customContent.getFromId();
        customContent.setFromId(app.loginUser.id);
        customContent.setNickname(app.loginUser.nickname);
        customContent.setImgUrl(app.loginUser.mediumAvatar);

        RequestUrl requestUrl = app.bindPushUrl(String.format(Const.SEND, app.loginUser.id, toId));
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", app.loginUser.nickname);
        params.put("type", Chat.FileType.MULTI.getName());
        params.put("content", chat.content);
        params.put("custom", new Gson().toJson(customContent));

        final Bundle bundle = new Bundle();
        bundle.putSerializable(Const.GET_PUSH_DATA, message);
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sendMessage", response);
                CloudResult result = app.parseJsonValue(response, new TypeToken<CloudResult>() {
                });

                Chat.Delivery status = Chat.Delivery.FAILED;
                if (result != null && result.getResult()) {
                    chat.id = result.id;
                    status = Chat.Delivery.SUCCESS;
                }
                updateChatStatus(chat, status, bundle);
                mActivity.finish();
                CommonUtil.longToast(mActivity, "分享成功");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtil.longToast(mActivity, "网络连接不可用请稍后再试");
                updateChatStatus(chat, Chat.Delivery.FAILED, bundle);
            }
        });
    }

    private WrapperXGPushTextMessage updateNewsList(CustomContent customContent, String content) {
        WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
        message.setTitle(customContent.getNickname());
        message.setContent(content);
        message.setCustomContentJson(new Gson().toJson(customContent));
        message.isForeground = true;

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.GET_PUSH_DATA, message);
        bundle.putInt(Const.ADD_CHAT_MSG_TYPE, NewsFragment.HANDLE_SEND_MSG);
        app.sendMsgToTarget(Const.ADD_CHAT_MSG, bundle, NewsFragment.class);

        return message;
    }

    private CustomContent createSendMsgCustomContent(New item) {
        CustomContent customContent = new CustomContent();
        customContent.setFromId(item.fromId);
        customContent.setNickname(item.title);
        customContent.setImgUrl(item.imgUrl);
        customContent.setTypeMsg(Chat.FileType.MULTI.getName());
        customContent.setCreatedTime((int) (System.currentTimeMillis() / 1000));
        customContent.setTypeBusiness(TypeBusinessEnum.FRIEND.getName());

        return customContent;
    }

    private void updateChatStatus(Chat chat, Chat.Delivery status, Bundle bundle) {
        chat.setDelivery(status);
        mChatDataSource.update(chat);
        bundle.putInt(ChatActivity.MSG_DELIVERY, status.getIndex());
        app.sendMsgToTarget(Const.UPDATE_CHAT_MSG, bundle, ChatActivity.class);
    }

    private void handlerListItemClick(final New item) {
        RedirectPreViewDialog dialog = RedirectPreViewDialog.getBuilder(mActivity)
                                        .setLayout(R.layout.redirect_preview_layout)
                                        .setTitle(mRedirectBody.title)
                                        .setBody(mRedirectBody.content)
                                        .setIconByUri(mRedirectBody.image)
                                        .build();
        dialog.show();
        dialog.setButtonClickListener(new PopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button) {
                if (button == PopupDialog.OK) {
                    sendMessage(item, mRedirectBody);
                }
            }
        });
    }

    private void initChatList() {
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<New> news = newDataSource.getNews("WHERE BELONGID = ? ORDER BY CREATEDTIME DESC", app.loginUser.id + "");
        mChatSelectListAdapter = new ChatSelectListAdapter(mContext, filterChatSelectList(news));
        mChatSelectListView.setAdapter(mChatSelectListAdapter);
    }

    private List<New> filterChatSelectList(List<New> source) {
        List<New> news = new ArrayList<>();
        String[] types = new String[] {
                PushUtil.ChatUserType.USER,
                PushUtil.ChatUserType.FRIEND,
                PushUtil.ChatUserType.TEACHER
        };
        for (New item : source) {
            if (CommonUtil.inArray(item.type, types)) {
                news.add(item);
            }
        }

        return news;
    }

    private class ChatSelectListAdapter extends BaseAdapter {

        private Context mContext;
        private List<New> mChatList;
        private DisplayImageOptions mOptions;

        public ChatSelectListAdapter(Context context, List<New> chatList) {

            mContext = context;
            mChatList = chatList;
            mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).
                    showImageForEmptyUri(R.drawable.default_avatar).
                    showImageOnFail(R.drawable.default_avatar).build();
        }

        @Override
        public int getCount() {
            return mChatList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_select_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mAvatarView = (ImageView) convertView.findViewById(R.id.chat_select_avatar);
                viewHolder.mNameView = (TextView) convertView.findViewById(R.id.chat_select_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            New item = mChatList.get(position);
            viewHolder.mNameView.setText(item.title);
            ImageLoader.getInstance().displayImage(item.getImgUrl(), viewHolder.mAvatarView, mOptions);
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public New getItem(int position) {
            return mChatList.get(position);
        }

        private class ViewHolder {
            public ImageView mAvatarView;
            public TextView mNameView;
        }
    }
}
