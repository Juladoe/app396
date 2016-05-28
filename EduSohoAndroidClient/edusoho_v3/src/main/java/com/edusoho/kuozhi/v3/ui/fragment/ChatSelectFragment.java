package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.handler.ChatSendHandler;
import com.edusoho.kuozhi.v3.handler.ClassRoomChatSendHandler;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.NewDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.chatselect_layout);
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
                New item = (New)parent.getItemAtPosition(position);
                if (PushUtil.ChatUserType.CLASSROOM.equals(item.type)) {
                    //new ClassRoomChatSendHandler(mActivity, mRedirectBody).handleClick(item.fromId, item.title, item.imgUrl);
                    return;
                }
                //new ChatSendHandler(mActivity, mRedirectBody).handleClick(item.fromId, item.title, item.imgUrl);
            }
        });

        mSelectFrientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.mEngine.runNormalPluginForResult(
                        "FragmentPageActivity",
                        mActivity,
                        ChatSendHandler.REQUEST_SELECT_FRIEND,
                        new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.ACTIONBAR_TITLE, "选择校友");
                                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "FriendSelectFragment");
                                startIntent.putExtra(FriendSelectFragment.BODY, mRedirectBody);
                            }
                        });
            }
        });

        initChatList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChatSendHandler.REQUEST_SELECT_FRIEND
                && resultCode == ChatSendHandler.RESULT_SELECT_FRIEND_OK) {
            mActivity.finish();
        }
    }

    private void initBundleData() {
        Bundle bundle = getArguments();
        mRedirectBody = (RedirectBody) bundle.getSerializable(BODY);
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
                PushUtil.ChatUserType.TEACHER,
                PushUtil.ChatUserType.CLASSROOM
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
