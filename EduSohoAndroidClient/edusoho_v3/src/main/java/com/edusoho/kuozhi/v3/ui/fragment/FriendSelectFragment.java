package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.FriendFragmentAdapter;
import com.edusoho.kuozhi.v3.handler.ChatSendHandler;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.provider.FriendProvider;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.result.FriendResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.friend.CharacterParser;
import com.edusoho.kuozhi.v3.ui.friend.FriendComparator;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.SideBar;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by howzhi on 15/9/30.
 */
public class FriendSelectFragment extends BaseFragment {

    public static final String BODY = "body";
    private TextView mCurrentFriendTagView;
    private SideBar mSidebar;
    private ListView mFriendListView;

    private RedirectBody mRedirectBody;
    private FriendProvider mFriendProvider;
    private FriendFragmentAdapter mFriendAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.frient_select_layout);
        ModelProvider.init(mContext, this);
    }

    private void initBundleData() {
        Bundle bundle = getArguments();
        mRedirectBody = (RedirectBody) bundle.getSerializable(BODY);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initBundleData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mFriendListView = (ListView) mContainerView.findViewById(R.id.friends_list);
        mSidebar = (SideBar) mContainerView.findViewById(R.id.sidebar);
        mCurrentFriendTagView = (TextView) mContainerView.findViewById(R.id.dialog);

        mFriendAdapter = new FriendFragmentAdapter(mContext, app);
        mFriendListView.setAdapter(mFriendAdapter);

        mSidebar.setTextView(mCurrentFriendTagView);
        mSidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChangedListener(String string) {
                int postion = mFriendAdapter.getPositionForSection(string.charAt(0));
                if (postion != -1) {
                    mFriendListView.setSelection(postion + 1);
                }
            }
        });
        mFriendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = (Friend) parent.getItemAtPosition(position);
                ChatSendHandler chatSendHandler = new ChatSendHandler(mActivity, mRedirectBody);
                chatSendHandler.setFinishCallback(new NormalCallback() {
                    @Override
                    public void success(Object obj) {
                        mActivity.setResult(ChatSendHandler.RESULT_SELECT_FRIEND_OK);
                    }
                });
                chatSendHandler.handleClick(friend.id, friend.nickname, friend.mediumAvatar);
            }
        });

        initFriendListData();
    }

    private void initFriendListData() {
        RequestUrl requestUrl = app.bindNewUrl(Const.MY_FRIEND, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        stringBuffer.append("?start=0&limit=10000/");
        requestUrl.url = stringBuffer.toString();
        mFriendProvider.getFriend(requestUrl).success(new NormalCallback<FriendResult>() {
            @Override
            public void success(FriendResult result) {
                if (result.data.length != 0) {
                    List<Friend> list = Arrays.asList(result.data);
                    setChar(list);
                    Collections.sort(list, new FriendComparator());
                    mFriendAdapter.addFriendList(list);
                }
            }
        });
    }

    private void setChar(List<Friend> list) {
        for (Friend friend : list) {
            String pinyin = CharacterParser.getInstance().getSelling(friend.nickname);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                friend.setSortLetters(sortString.toUpperCase());
            } else {
                friend.setSortLetters("#");
            }
        }
    }
}
