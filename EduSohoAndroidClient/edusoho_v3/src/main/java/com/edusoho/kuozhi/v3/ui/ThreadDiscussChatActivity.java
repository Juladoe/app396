package com.edusoho.kuozhi.v3.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.entity.IMUploadEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.entity.message.Source;
import com.edusoho.kuozhi.imserver.managar.IMMessageManager;
import com.edusoho.kuozhi.imserver.ui.MessageListFragment;
import com.edusoho.kuozhi.imserver.ui.entity.AudioBody;
import com.edusoho.kuozhi.imserver.ui.entity.PushUtil;
import com.edusoho.kuozhi.imserver.ui.listener.IMessageDataProvider;
import com.edusoho.kuozhi.imserver.ui.listener.MessageControllerListener;
import com.edusoho.kuozhi.imserver.ui.util.AudioUtil;
import com.edusoho.kuozhi.imserver.util.MessageEntityBuildr;
import com.edusoho.kuozhi.imserver.util.MsgDbHelper;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.push.CourseThreadPostResult;
import com.edusoho.kuozhi.v3.model.bal.thread.CourseThreadPostEntity;
import com.edusoho.kuozhi.v3.model.bal.thread.PostThreadResult;
import com.edusoho.kuozhi.v3.model.provider.ThreadProvider;
import com.edusoho.kuozhi.v3.model.provider.UserProvider;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.chat.AbstractIMChatActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by suju on 16/9/6.
 */
public class ThreadDiscussChatActivity extends AbstractIMChatActivity implements IMessageDataProvider {

    private static final String CONV_NO_FORMAT = "%s-%s";
    public static final String LESSON_ID = "lesson_id";
    public static final String THREAD_TYPE = "threadType";
    public static final String THREAD_TARGET_TYPE = "thread_target_type";
    public static final String THREAD_TARGET_ID = "thread_target_id";
    public static final String IMAGE_FORMAT = "<img alt=\"\" src=\"%s\" />";

    protected ThreadProvider mThreadProvider;
    /**
     * ask,answer
     */

    //讨论组问答类型:question, discuss
    private String mThreadType;

    //讨论组目标id:课程或者班级id
    private int mThreadTargetId;
    private int mLessonId;

    //讨论组目标类型:course 或classroom
    private String mThreadTargetType;
    private LinkedHashMap mThreadInfo;
    private List<MessageEntity> mMessageEntityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThreadProvider = new ThreadProvider(mContext);
    }

    @Override
    protected View createView() {
        return LayoutInflater.from(mContext).inflate(R.layout.activity_thread_discuss_layout, null);
    }

    @Override
    public void onAttachedToWindow() {
        if (mThreadInfo == null) {
            getThreadInfo(new NormalCallback<LinkedHashMap>() {
                @Override
                public void success(LinkedHashMap linkedHashMap) {
                    if (linkedHashMap == null
                            || !linkedHashMap.containsKey("id")
                            || !linkedHashMap.containsKey("type")) {
                        CommonUtil.shortToast(mContext, "获取讨论组信息失败");
                        return;
                    }
                    mThreadInfo = linkedHashMap;
                    setBackMode(BACK, mThreadInfo.get("title").toString());
                    initHeaderInfo(mThreadInfo);
                    initThreadPostList();
                }
            });
            return;
        }
        initHeaderInfo(mThreadInfo);
        initThreadPostList();
    }

    protected void initParams() {
        super.initParams();
        Intent dataIntent = getIntent();

        mTargetType = dataIntent.getStringExtra(THREAD_TYPE);
        mThreadTargetType = dataIntent.getStringExtra(THREAD_TARGET_TYPE);
        mThreadTargetId = dataIntent.getIntExtra(THREAD_TARGET_ID, 0);
        mLessonId = dataIntent.getIntExtra(LESSON_ID, 0);
    }

    @Override
    protected void createTargetRole(String type, int rid, final MessageControllerListener.RoleUpdateCallback callback) {
        if (Destination.USER.equals(type)) {
            new UserProvider(mContext).getUserInfo(rid)
                    .success(new NormalCallback<User>() {
                        @Override
                        public void success(User user) {
                            Role role = new Role();
                            role.setRid(user.id);
                            role.setAvatar(user.mediumAvatar);
                            role.setType(getTargetType());
                            role.setNickname(user.nickname);
                            callback.onCreateRole(role);
                        }
                    });
            return;
        }
        if (mThreadInfo != null) {
            Role role = new Role();
            role.setRid(AppUtil.parseInt(mThreadInfo.get("id").toString()));
            role.setType(mThreadInfo.get("type").toString());
            role.setNickname(mThreadInfo.get("title").toString());
            callback.onCreateRole(role);
            return;
        }
        getThreadInfo(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap linkedHashMap) {
                if (linkedHashMap == null) {
                    return;
                }
                mThreadInfo = linkedHashMap;
                Role role = new Role();
                role.setRid(AppUtil.parseInt(linkedHashMap.get("id").toString()));
                role.setType(linkedHashMap.get("type").toString());
                role.setNickname(linkedHashMap.get("title").toString());
                callback.onCreateRole(role);
            }
        });
    }

    @Override
    protected Promise createChatConvNo() {
        final Promise promise = new Promise();
        User currentUser = getAppSettingProvider().getCurrentUser();
        if (currentUser == null || currentUser.id == 0) {
            ToastUtils.show(getBaseContext(), "用户未登录");
            promise.resolve(null);
            return promise;
        }

        getThreadInfo(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap linkedHashMap) {
                if (linkedHashMap == null
                        || !linkedHashMap.containsKey("id")
                        || !linkedHashMap.containsKey("type")) {
                    CommonUtil.shortToast(mContext, "获取讨论组信息失败");
                    return;
                }

                mThreadInfo = linkedHashMap;
                promise.resolve(String.format(CONV_NO_FORMAT, linkedHashMap.get("id"), linkedHashMap.get("type")));
            }
        });

        return promise;
    }

    private void getThreadInfo(NormalCallback<LinkedHashMap> normalCallback) {
        if ("course".equals(mThreadTargetType)) {
            mThreadProvider.getCourseThreadInfo(mTargetId, mThreadTargetId)
                    .success(normalCallback)
                    .fail(normalCallback);
            return;
        }

        if ("classroom".equals(mThreadTargetType)) {
            mThreadProvider.getClassRoomThreadInfo(mTargetId).success(normalCallback);
            return;
        }
        normalCallback.success(null);
    }

    private List<MessageEntity> coverPostListToMessageEntity(List<CourseThreadPostEntity> postList) {
        List<MessageEntity> messageEntityList = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            MessageEntity messageEntity = createMessageEntityBy(i, postList.get(i));
            messageEntityList.add(messageEntity);
        }

        return messageEntityList;
    }

    @Override
    protected MessageListFragment createFragment() {
        MessageListFragment messageListFragment = super.createFragment();
        messageListFragment.setIMessageDataProvider(this);
        return messageListFragment;
    }

    @Override
    public MessageEntity createMessageEntity(MessageBody messageBody) {
        MessageEntity messageEntity = new MessageEntityBuildr()
                .addUID(messageBody.getMessageId())
                .addConvNo(messageBody.getConvNo())
                .addToId(String.valueOf(messageBody.getDestination().getId()))
                .addToName(messageBody.getDestination().getNickname())
                .addFromId(String.valueOf(messageBody.getSource().getId()))
                .addFromName(messageBody.getSource().getNickname())
                .addCmd("message")
                .addStatus(MessageEntity.StatusType.FAILED)
                .addMsg(messageBody.toJson())
                .addTime((int) (messageBody.getCreatedTime() / 1000))
                .builder();

        messageEntity.setId(mMessageEntityList.size());
        mMessageEntityList.add(messageEntity);
        return messageEntity;
    }

    @Override
    public void sendMessage(String convNo, MessageBody messageBody) {
        switch (messageBody.getType()) {
            case PushUtil.ChatMsgType.TEXT:
                sendMessageToServer(messageBody.getMid(), messageBody.getBody());
                break;
            case PushUtil.ChatMsgType.IMAGE:
                sendMessageToServer(messageBody.getMid(), String.format(IMAGE_FORMAT, messageBody.getBody()));
                break;
            case PushUtil.ChatMsgType.AUDIO:
                sendMessageToServer(messageBody.getMid(), messageBody.getBody());
        }
    }

    @Override
    public IMMessageManager getMessageManager() {
        return mIMMessageManager;
    }

    @Override
    public void updateConvEntity(String convNo, Role role) {
    }

    @Override
    public List<MessageEntity> getMessageList(String convNo, int start) {
        return mMessageEntityList;
    }

    private void sendMessageToServer(final int position, String content) {
        String threadType = "course".equals(mThreadTargetType) ? "course" : "common";
        mThreadProvider.createThreadPost(mThreadTargetType, mThreadTargetId, threadType, mTargetId, content)
                .success(new NormalCallback<PostThreadResult>() {
                    @Override
                    public void success(PostThreadResult threadResult) {
                        if (threadResult != null) {
                            MessageEntity messageEntity = mMessageEntityList.get(position);
                            if (messageEntity != null) {
                                messageEntity.setStatus(MessageEntity.StatusType.SUCCESS);
                                mMessageListFragment.updateListByEntity(messageEntity);
                            }
                        }
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError error) {
            }
        });
    }

    private String parseBodyType(String body) {
        if (body.contains("<img")) {
            return PushUtil.ChatMsgType.IMAGE;
        }

        AudioBody audioBody = AudioUtil.getAudioBody(body);
        if (audioBody != null) {
            return PushUtil.ChatMsgType.AUDIO;
        }

        return PushUtil.ChatMsgType.TEXT;
    }

    private String getBodyContent(String body) {
        if (body.contains("<img")) {
            Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
            Matcher m = p.matcher(body);
            while (m.find()) {
                String path = m.group(1);
                if (path != null && !path.startsWith("http://")) {
                    School school = getAppSettingProvider().getCurrentSchool();
                    return school.host + path;
                }
                return path;
            }
        }

        return AppUtil.coverCourseAbout(body);
    }

    private MessageEntity createMessageEntityBy(int position, CourseThreadPostEntity postEntity) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(position);
        CourseThreadPostEntity.UserEntity user = postEntity.user;
        messageEntity.setFromId(String.valueOf(user.id));
        messageEntity.setFromName(user.nickname);
        messageEntity.setToId("all");
        messageEntity.setToName(mThreadInfo.get("title").toString());

        String messageType = parseBodyType(postEntity.content);
        MessageBody messageBody = new MessageBody(MessageBody.VERSION, messageType, getBodyContent(postEntity.content));
        messageBody.setDestination(new Destination(
                AppUtil.parseInt(mThreadInfo.get("id").toString()), mThreadInfo.get("type").toString()));
        messageBody.setSource(new Source(user.id, Destination.USER));
        messageEntity.setMsg(messageBody.toJson());
        messageEntity.setTime((int) AppUtil.convertTimeZone2Millisecond(postEntity.createdTime));

        messageEntity.setStatus(MessageEntity.StatusType.SUCCESS);
        return messageEntity;
    }

    private void initThreadPostList() {
        mThreadProvider.getThreadPost(mThreadTargetType, mTargetId).success(new NormalCallback<CourseThreadPostResult>() {
            @Override
            public void success(CourseThreadPostResult threadPostResult) {
                if (threadPostResult != null && threadPostResult.resources != null) {
                    List<CourseThreadPostEntity> posts = threadPostResult.resources;
                    Collections.reverse(posts);
                    mMessageEntityList = coverPostListToMessageEntity(posts);

                    attachMessageListFragment();
                }
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                CommonUtil.shortToast(mContext, "获取讨论组回复列表失败");
                finish();
            }
        });
    }

    private String getFromInfoTime(String time) {
        try {
            time = time.replace("T", " ");
            Date timeDate = new SimpleDateFormat("yyyy-MM-dd").parse(time);
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd");
            return timeFormat.format(timeDate);
        } catch (Exception e) {
        }

        return "";
    }

    private void fillThreaLabelData(LinkedHashMap threadInfo) {
        String type = threadInfo.get("type").toString();
        TextView labelView = (TextView) findViewById(R.id.tdh_label);
        if ("question".equals(type)) {
            labelView.setText("问答");
            labelView.setTextColor(getResources().getColor(R.color.thread_type_question));
            labelView.setBackgroundResource(R.drawable.thread_type_question_label);
        } else {
            labelView.setText("话题");
            labelView.setTextColor(getResources().getColor(R.color.thread_type_discuss));
            labelView.setBackgroundResource(R.drawable.thread_type_discuss_label);
        }
        TextView titleView = (TextView) findViewById(R.id.tdh_title);
        titleView.setText(threadInfo.get("title").toString());
        TextView timeView = (TextView) findViewById(R.id.tdh_time);
        timeView.setText(getFromInfoTime(threadInfo.get("createdTime").toString()));

        TextView contentView = (TextView) findViewById(R.id.tdh_content);
        contentView.setText(AppUtil.coverCourseAbout(threadInfo.get("content").toString()));

        LinkedHashMap<String, String> user = (LinkedHashMap<String, String>) threadInfo.get("user");
        TextView nicknameView = (TextView) findViewById(R.id.tdh_nickname);
        nicknameView.setText(user.get("nickname"));
        ImageView userAvatar = (ImageView) findViewById(R.id.tdh_avatar);
        ImageLoader.getInstance().displayImage(user.get("avatar"), userAvatar);
    }

    private void initHeaderInfo(LinkedHashMap threadInfo) {
        fillThreaLabelData(threadInfo);
        if ("course".equals(mThreadTargetType)) {
            initThreadInfoByCourse(threadInfo);
            return;
        }
        if ("classroom".equals(mThreadTargetType)) {
            initThreadInfoByClassRoom(threadInfo);
            return;
        }
    }

    private void initThreadInfoByClassRoom(LinkedHashMap threadInfo) {
        LinkedHashMap<String, String> course = (LinkedHashMap<String, String>) threadInfo.get("target");
        TextView fromCourseView = (TextView) findViewById(R.id.tdh_from_course);
        fromCourseView.setText(String.format("来自班级:《%s》", course.get("title")));
        fromCourseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        String.format(Const.MOBILE_WEB_COURSE, mTargetId)
                );
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
            }
        });
    }

    private void initThreadInfoByCourse(LinkedHashMap threadInfo) {
        LinkedHashMap<String, String> course = (LinkedHashMap<String, String>) threadInfo.get("course");
        TextView fromCourseView = (TextView) findViewById(R.id.tdh_from_course);
        fromCourseView.setText(String.format("来自课程:《%s》", course.get("title")));
        fromCourseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        String.format(Const.MOBILE_WEB_COURSE, mTargetId)
                );
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
            }
        });
    }

    private IMMessageManager mIMMessageManager = new IMMessageManager(mContext) {

        public int updateMessage(MessageEntity messageEntity) {
            return 0;
        }

        public int updateMessageFieldByMsgNo(String msgNo, ContentValues cv) {
            return 0;
        }

        public int updateMessageField(int id, ContentValues cv) {
            return 0;
        }

        public int updateMessageFieldByUid(String uid, ContentValues cv) {
            return 0;
        }

        public MessageEntity getMessage(int id) {
            return mMessageEntityList.get(id);
        }

        public List<MessageEntity> getMessageListByConvNo(String convNo, int start, int limit) {
            return mMessageEntityList;
        }

        public MessageEntity getMessageByUID(String uid) {
            return null;
        }

        public IMUploadEntity getUploadEntity(String muid) {
            return null;
        }

        public long saveUploadEntity(String muid, String type, String source) {
            return 0;
        }

        public MessageEntity createMessage(MessageEntity messageEntity) {
            return null;
        }

        public long deleteByConvNo(String convNo) {
            return 0;
        }

        public int deleteById(int id) {
            return 0;
        }
    };
}
