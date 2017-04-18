package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.v3.handler.ClassRoomChatSendHandler;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.DiscussionGroup;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.provider.DiscussionGroupProvider;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.model.result.DiscussionGroupResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.friend.FriendComparator;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import cn.trinea.android.common.util.ToastUtils;

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
        Friend friend = (Friend) mFriendAdapter.getItem(position);
        RedirectBody redirectBody = getShowRedirectBody(friend.getNickname(), friend.getMediumAvatar());
        ClassRoomChatSendHandler chatSendHandler = new ClassRoomChatSendHandler(mActivity, redirectBody, position);
        chatSendHandler.handleClick(mSendMessageHandlerCallback);
    }

    private NormalCallback<Integer> mSendMessageHandlerCallback = new NormalCallback<Integer>() {
        @Override
        public void success(Integer index) {
            final Friend friend = (Friend) mFriendAdapter.getItem(index);
            ConvEntity convEntity = IMClient.getClient().getConvManager()
                    .getConvByTypeAndId(friend.getType(), friend.id);
            if (convEntity == null) {
                if (Destination.CLASSROOM.equals(friend.getType())) {
                    createClassRoomConvNoEntity(friend);
                } else if (Destination.COURSE.equals(friend.getType())) {
                    createCourseConvNoEntity(friend);
                }
                return;
            }
            sendMsg(friend.id, convEntity.getConvNo(), convEntity.getType(), friend.getNickname());
        }
    };

    private void validToSendMessage(final ConvEntity convEntity) {
        final LoadDialog loadDialog = LoadDialog.create(getActivity());
        loadDialog.show();
        new IMProvider(mContext).joinIMConvNo(convEntity.getId(), convEntity.getType())
                .success(new NormalCallback<LinkedHashMap>() {
                    @Override
                    public void success(LinkedHashMap data) {
                        loadDialog.dismiss();
                        if (data == null || !data.containsKey("convNo")) {
                            ToastUtils.show(mContext, "发送失败");
                            return;
                        }
                        sendMsg(convEntity.getId(), data.get("convNo").toString(), convEntity.getType(), convEntity.getTargetName());
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                loadDialog.dismiss();
                ToastUtils.show(mContext, "发送失败");
            }
        });
    }

    private void createCourseConvNoEntity(Friend friend) {
        final Course course = new Course();
        course.middlePicture = friend.getMediumAvatar();
        course.title = friend.getNickname();
        course.id = friend.id;

        final LoadDialog loadDialog = LoadDialog.create(getActivity());
        loadDialog.show();
        new IMProvider(mContext).joinIMConvNo(course.id, Destination.COURSE)
                .success(new NormalCallback<LinkedHashMap>() {
                    @Override
                    public void success(LinkedHashMap data) {
                        loadDialog.dismiss();
                        if (data == null || !data.containsKey("convNo")) {
                            ToastUtils.show(mContext, "发送失败");
                            return;
                        }
                        mConvNo = data.get("convNo").toString();
                        new IMProvider(mContext).createConvInfoByCourse(mConvNo, course)
                                .success(new NormalCallback<ConvEntity>() {
                                    @Override
                                    public void success(ConvEntity convEntity) {
                                        sendMsg(convEntity.getId(), mConvNo, convEntity.getType(), convEntity.getTargetName());
                                    }
                                });
                    }
                }).fail(new NormalCallback<VolleyError>() {
                    @Override
                    public void success(VolleyError obj) {
                        loadDialog.dismiss();
                        ToastUtils.show(mContext, "发送失败");
                    }
        });
    }

    private void createClassRoomConvNoEntity(Friend friend) {
        final Classroom classroom = new Classroom();
        classroom.middlePicture = friend.getMediumAvatar();
        classroom.title = friend.getNickname();
        classroom.id = friend.id;

        final LoadDialog loadDialog = LoadDialog.create(getActivity());
        loadDialog.show();
        new IMProvider(mContext).joinIMConvNo(classroom.id, Destination.CLASSROOM)
                .success(new NormalCallback<LinkedHashMap>() {
                    @Override
                    public void success(LinkedHashMap data) {
                        loadDialog.dismiss();
                        if (data == null || !data.containsKey("convNo")) {
                            ToastUtils.show(mContext, "发送失败");
                            return;
                        }
                        mConvNo = data.get("convNo").toString();
                        new IMProvider(mContext).createConvInfoByClassRoom(mConvNo, classroom.id, classroom)
                                .success(new NormalCallback<ConvEntity>() {
                                    @Override
                                    public void success(ConvEntity convEntity) {
                                        sendMsg(convEntity.getId(), mConvNo, convEntity.getType(), convEntity.getTargetName());
                                    }
                                });
                    }
                }).fail(new NormalCallback<VolleyError>() {
                    @Override
                    public void success(VolleyError obj) {
                        loadDialog.dismiss();
                        ToastUtils.show(mContext, "发送失败");
                    }
        });
    }

}
