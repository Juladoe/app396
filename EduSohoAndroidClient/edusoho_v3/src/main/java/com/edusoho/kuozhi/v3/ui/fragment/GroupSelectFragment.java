package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.edusoho.kuozhi.v3.handler.ChatSendHandler;
import com.edusoho.kuozhi.v3.handler.ClassRoomChatSendHandler;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.DiscussionGroup;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.provider.DiscussionGroupProvider;
import com.edusoho.kuozhi.v3.model.result.DiscussionGroupResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.friend.FriendComparator;
import com.edusoho.kuozhi.v3.util.Const;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by howzhi on 15/11/3.
 */
public class GroupSelectFragment extends FriendSelectFragment {

    private DiscussionGroupProvider mDiscussionGroupProvider;

    @Override
    public String getTitle() {
        return "选择讨论组";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mGroupSelectBtn.setVisibility(View.GONE);
    }

    @Override
    protected void initFriendListData() {
        RequestUrl requestUrl = app.bindNewUrl(Const.DISCUSSION_GROUP, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        stringBuffer.append("?start=0&limit=10000/");
        requestUrl.url = stringBuffer.toString();
        mDiscussionGroupProvider.getClassrooms(requestUrl).success(
                new NormalCallback<DiscussionGroupResult>() {
                    @Override
                    public void success(DiscussionGroupResult result) {
                        if (result.resources.length != 0) {
                            List<DiscussionGroup> list = Arrays.asList(result.resources);
                            setChar(list);
                            Collections.sort(list, new FriendComparator());
                            mFriendAdapter.addFriendList(list);
                        }
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DiscussionGroup discussionGroup = (DiscussionGroup) parent.getItemAtPosition(position);
        ClassRoomChatSendHandler chatSendHandler = new ClassRoomChatSendHandler(mActivity, mRedirectBody);
        chatSendHandler.setFinishCallback(new NormalCallback() {
            @Override
            public void success(Object obj) {
                mActivity.setResult(ChatSendHandler.RESULT_SELECT_FRIEND_OK);
            }
        });
        chatSendHandler.handleClick(discussionGroup.getId(), discussionGroup.getNickname(), discussionGroup.getMediumAvatar());
    }
}
