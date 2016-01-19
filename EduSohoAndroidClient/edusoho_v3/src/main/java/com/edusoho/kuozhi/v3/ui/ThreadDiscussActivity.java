package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.ChatAdapter;
import com.edusoho.kuozhi.v3.adapter.ThreadDiscussAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.bal.push.BaseMsgEntity;
import com.edusoho.kuozhi.v3.model.bal.push.CourseThreadPostResult;
import com.edusoho.kuozhi.v3.model.bal.push.UpYunUploadResult;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.bal.thread.CourseThreadEntity;
import com.edusoho.kuozhi.v3.model.bal.thread.CourseThreadPostEntity;
import com.edusoho.kuozhi.v3.model.bal.thread.PostThreadResult;
import com.edusoho.kuozhi.v3.model.result.CloudResult;
import com.edusoho.kuozhi.v3.model.sys.AudioCacheEntity;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseChatActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.AudioCacheUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.CourseThreadDataSource;
import com.edusoho.kuozhi.v3.util.sql.CourseThreadPostDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static com.edusoho.kuozhi.v3.adapter.ThreadDiscussAdapter.ThreadDiscussEntity;

/**
 * Created by JesseHuang on 15/12/23.
 */
public class ThreadDiscussActivity extends BaseChatActivity implements ChatAdapter.ImageErrorClick {
    public static final String TAG = "ThreadDiscussActivity";
    public static final String ACTIVITY_TYPE = "activity_type";
    public static final String THREAD_ID = "thread_id";
    public static final String COURSE_ID = "course_id";
    public static final String LESSON_ID = "lesson_id";
    public static final String IMAGE_FORMAT = "<img alt=\"\" src=\"%s\" />";
    public static final String AUDIO_FORMAT = "%s";
    public static int CurrentThreadId = 0;

    /**
     * ask,answer
     */
    private String mActivityType;
    private String mRoleType;
    private String mCourseTitle;
    private int mToUserId;
    private int mThreadId;
    private int mCourseId;
    private int mLessonId;

    private CourseThreadEntity mThreadModel;
    private List<CourseThreadPostEntity> mPosts;
    private CourseThreadDataSource mCourseThreadDataSource;
    private CourseThreadPostDataSource mCourseThreadPostDataSource;
    private ThreadDiscussAdapter mAdapter;
    private LoadDialog mLoadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();
        setBackMode(BACK, "描述你的问题");
        mLoadDialog = LoadDialog.create(mContext);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canDoRefresh = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                //int count = getList(mStart).size();
                return canDoRefresh;
            }
        });
        btnVoice.setOnClickListener(mAskClickListener);
        ivAddMedia.setOnClickListener(mAskClickListener);
        //mHandler.postDelayed(mNewFragment2UpdateItemBadgeRunnable, 500);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        mActivityType = intent.getStringExtra(ACTIVITY_TYPE);
        mCourseId = intent.getIntExtra(COURSE_ID, 0);
        mLessonId = intent.getIntExtra(LESSON_ID, 0);
        mThreadId = intent.getIntExtra(THREAD_ID, 0);
        CurrentThreadId = mThreadId;
        if (TextUtils.isEmpty(mRoleType)) {
            String[] roles = new String[app.loginUser.roles.length];
            for (int i = 0; i < app.loginUser.roles.length; i++) {
                roles[i] = app.loginUser.roles[i].toString();
            }
            if (CommonUtil.inArray(UserRole.ROLE_TEACHER.name(), roles)) {
                mRoleType = PushUtil.ChatUserType.TEACHER;
            } else {
                mRoleType = PushUtil.ChatUserType.FRIEND;
            }
        }
        mCourseThreadDataSource = new CourseThreadDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        mCourseThreadPostDataSource = new CourseThreadPostDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        if (PushUtil.ThreadMsgType.THREAD_POST.equals(mActivityType)) {
            if (mThreadId == 0) {
                CommonUtil.shortToast(mContext, "问题不存在");
                finish();
            } else {
                getLists(mThreadId, new NormalCallback<Boolean>() {
                    @Override
                    public void success(Boolean tag) {
                        if (tag) {
                            mAdapter = new ThreadDiscussAdapter(mPosts, mThreadModel, mContext);
                            mAdapter.setSendImageClickListener(ThreadDiscussActivity.this);
                            lvMessage.setAdapter(mAdapter);
                            mAudioDownloadReceiver.setAdapter(mAdapter);
                            lvMessage.post(mListViewSelectRunnable);
                        }
                    }
                });
            }
        } else {
            mAdapter = new ThreadDiscussAdapter(mContext);
            lvMessage.setAdapter(mAdapter);
            mAdapter.setSendImageClickListener(this);
            mAudioDownloadReceiver.setAdapter(mAdapter);
        }
        NotificationUtil.cancelById(mThreadId);
    }

    protected Runnable mListViewSelectRunnable = new Runnable() {
        @Override
        public void run() {
            lvMessage.setSelection(mAdapter.getCount());
            lvMessage.setOnScrollListener(mListViewScrollEvent);
        }
    };

    @Override
    public void sendMsg(final String content) {
        Log.d(TAG, content);
        if (mAdapter.getCount() == 0) {
            handleSendThread(content, PushUtil.ChatMsgType.TEXT);
        } else if (mAdapter.getCount() > 0) {
            final CourseThreadPostEntity postModel = createCoursePostThreadByCurrentUser(content, PushUtil.ChatMsgType.TEXT, PushUtil.MsgDeliveryType.UPLOADING);
            postModel.pid = (int) mCourseThreadPostDataSource.create(postModel);
            final ThreadDiscussEntity discussModel = convertThreadDiscuss(postModel);
            addItem2ListView(discussModel);
            handleSendPost(postModel, mSendMsgNormalCallback);
        }
    }

    @Override
    public void sendMsgAgain(final BaseMsgEntity model) {
        final CourseThreadPostEntity postModel = mCourseThreadPostDataSource.getPost(model.id);
        mAdapter.updateItemState(model.id, PushUtil.MsgDeliveryType.UPLOADING);
        handleSendPost(postModel, mSendMsgNormalCallback);
    }

    // region 多媒体资源上传

    public void uploadMedia(final File file, final String type, String strType) {
        if (file == null || !file.exists()) {
            CommonUtil.shortToast(mContext, String.format("%s不存在", strType));
            return;
        }
        try {
            final CourseThreadPostEntity postModel = createCoursePostThreadByCurrentUser(file.getPath(), type, PushUtil.MsgDeliveryType.UPLOADING);
            postModel.pid = (int) mCourseThreadPostDataSource.create(postModel);
            ThreadDiscussEntity discussModel = convertThreadDiscuss(postModel);
            addItem2ListView(discussModel);
            getUpYunUploadInfo(file, app.loginUser.id, new NormalCallback<UpYunUploadResult>() {
                @Override
                public void success(final UpYunUploadResult result) {
                    if (result != null) {
                        postModel.upyunMediaPutUrl = result.putUrl;
                        postModel.upyunMediaGetUrl = result.getUrl;
                        postModel.headers = result.getHeaders();
                        AudioCacheUtil.getInstance().create(postModel.content, postModel.upyunMediaGetUrl);
                        uploadUnYunMedia(file, postModel);
                        ThreadDiscussActivity.super.saveUploadResult(result.putUrl, result.getUrl, mThreadId);
                        postModel.delivery = PushUtil.MsgDeliveryType.SUCCESS;
                        mCourseThreadPostDataSource.update(postModel);
                        mAdapter.updateItemState(postModel.pid, PushUtil.MsgDeliveryType.SUCCESS);
                    } else {
                        postModel.delivery = PushUtil.MsgDeliveryType.FAILED;
                        mCourseThreadPostDataSource.update(postModel);
                        AudioCacheUtil.getInstance().create(postModel.content, postModel.upyunMediaGetUrl);
                        mAdapter.updateItemState(postModel.pid, PushUtil.MsgDeliveryType.FAILED);
                    }
                }
            });
            viewMediaLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void uploadUnYunMedia(final File file, final CourseThreadPostEntity model) {
        RequestUrl putUrl = new RequestUrl(model.upyunMediaPutUrl);
        putUrl.setHeads(model.headers);
        putUrl.setMuiltParams(new Object[]{"file", file});
        ajaxPostMultiUrl(putUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "success");
                handleSendPost(model, mSendMsgNormalCallback);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtil.longToast(mActivity, getString(R.string.request_fail_text));
                Log.d(TAG, "upload media res to upyun failed");
            }
        }, Request.Method.PUT);
    }

    @Override
    public void uploadMediaAgain(final File file, final BaseMsgEntity model, final String type, String strType) {
        try {
            final CourseThreadPostEntity postModel = mCourseThreadPostDataSource.getPost(model.id);
            getUpYunUploadInfo(file, app.loginUser.id, new NormalCallback<UpYunUploadResult>() {
                @Override
                public void success(final UpYunUploadResult result) {
                    if (result != null) {
                        postModel.upyunMediaPutUrl = result.putUrl;
                        postModel.upyunMediaGetUrl = result.getUrl;
                        postModel.headers = result.getHeaders();
                        uploadUnYunMedia(file, postModel);
                        ThreadDiscussActivity.super.saveUploadResult(result.putUrl, result.getUrl, mThreadId);
                        postModel.delivery = PushUtil.MsgDeliveryType.SUCCESS;
                        mCourseThreadPostDataSource.update(postModel);
                        mAdapter.updateItemState(postModel.pid, PushUtil.MsgDeliveryType.SUCCESS);
                    } else {
                        handleNetError("图片上传失败");
                        postModel.delivery = PushUtil.MsgDeliveryType.FAILED;
                        mCourseThreadPostDataSource.update(postModel);
                        mAdapter.updateItemState(postModel.pid, PushUtil.MsgDeliveryType.FAILED);
                    }
                }
            });
            viewMediaLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
    // endregion

    /**
     * 处理用户的提问
     *
     * @param content     内容
     * @param contentType 类型
     */
    private void handleSendThread(final String content, final String contentType) {
        if (!PushUtil.ChatMsgType.TEXT.equals(contentType)) {
            CommonUtil.shortToast(mContext, "描述的问题不能图片或语音");
            return;
        }
        RequestUrl requestUrl = app.bindNewUrl(Const.CREATE_THREAD, true);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("targetType", mLessonId == 0 ? "course" : "lesson");
        params.put("targetId", (mLessonId == 0 ? mCourseId : mLessonId) + "");
        params.put("title", content);
        params.put("content", content);
        ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("threadId")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        mThreadId = jsonObject.getInt("threadId");
                        CurrentThreadId = mThreadId;
                        CourseThreadEntity model = createCourseThreadByCurrentUser(content);
                        ThreadDiscussEntity discussModel = convertThreadDiscuss(model);
                        mCourseThreadDataSource.create(model);
                        addItem2ListView(discussModel);
                    } catch (Exception ex) {
                        Log.e(TAG, ex.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleNetError("问题提交失败");
            }
        });
    }

    /**
     * 处理用户的回复
     *
     * @param postModel      回复对象
     * @param normalCallback 回调,成功：写入本地DB，更新ListView，Push一条消息
     */
    private void handleSendPost(final CourseThreadPostEntity postModel, final NormalCallback<CourseThreadPostEntity> normalCallback) {
        RequestUrl requestUrl = app.bindUrl(Const.POST_THREAD, true);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("courseId", mCourseId + "");
        params.put("threadId", mThreadId + "");
        params.put("content", formatContent(postModel, postModel.type));
        ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                PostThreadResult postThreadResult = parseJsonValue(response, new TypeToken<PostThreadResult>() {
                });
                postModel.postId = postThreadResult.id;
                postModel.isElite = postThreadResult.isElite;
                postModel.createdTime = postThreadResult.createdTime;
                postModel.delivery = PushUtil.MsgDeliveryType.SUCCESS;
                normalCallback.success(postModel);
                mCourseThreadPostDataSource.update(postModel);
                mAdapter.updateItemState(postModel.pid, PushUtil.MsgDeliveryType.SUCCESS);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleNetError(getString(R.string.network_does_not_work));
                postModel.delivery = PushUtil.MsgDeliveryType.FAILED;
                mCourseThreadPostDataSource.update(postModel);
                mAdapter.updateItemState(postModel.pid, PushUtil.MsgDeliveryType.FAILED);
            }
        });
    }

    /**
     * 回调推送
     */
    private NormalCallback<CourseThreadPostEntity> mSendMsgNormalCallback = new NormalCallback<CourseThreadPostEntity>() {
        @Override
        public void success(final CourseThreadPostEntity postModel) {
            getToUserId(new NormalCallback<Integer>() {
                @Override
                public void success(Integer toUserId) {
                    WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
                    message.setTitle(mCourseTitle);
                    message.setContent(postModel.content);
                    V2CustomContent v2CustomContent = getV2CustomContent(postModel, postModel.type, getActivityState());
                    String v2CustomContentJson = gson.toJson(v2CustomContent);
                    message.setCustomContentJson(v2CustomContentJson);
                    message.isForeground = true;
                    RequestUrl requestUrl = app.bindPushUrl(Const.SEND);
                    HashMap<String, String> params = requestUrl.getParams();
                    params.put("title", message.title);
                    params.put("content", message.content);
                    params.put("custom", v2CustomContentJson);
                    mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            CloudResult result = parseJsonValue(response, new TypeToken<CloudResult>() {
                            });
                            if (result != null && result.getResult()) {
                                Log.d("sendMsg", "text success");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("sendMsg", "text failed");
                        }
                    });
                }
            });
        }
    };

    private void getLists(final int threadId, final NormalCallback<Boolean> normalCallback) {
        if (app.getNetIsConnect()) {
            mLoadDialog.show();
            RequestUrl requestUrl = app.bindUrl(Const.GET_THREAD, true);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("threadId", threadId + "");
            ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mThreadModel = mActivity.parseJsonValue(response, new TypeToken<CourseThreadEntity>() {
                    });
                    final int threadId = mThreadModel.id;
                    if (mCourseThreadDataSource.get(threadId) == null) {
                        mCourseThreadDataSource.create(mThreadModel);
                    }
                    RequestUrl requestUrl = app.bindUrl(Const.GET_THREAD_POST, true);
                    HashMap<String, String> params = requestUrl.getParams();
                    params.put("threadId", threadId + "");
                    ajaxPost(requestUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            CourseThreadPostResult result = mActivity.parseJsonValue(response, new TypeToken<CourseThreadPostResult>() {
                            });
                            if (result != null && result.data != null) {
                                mPosts = result.data;
                                Collections.reverse(mPosts);
                                filterPostThreads(mPosts);
                                mCourseThreadPostDataSource.deleteByThreadId(threadId);
                                mCourseThreadPostDataSource.create(mPosts);
                                normalCallback.success(true);
                                mLoadDialog.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mLoadDialog.dismiss();
                            handleNetError("问题详情获取失败");
                            finish();
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mLoadDialog.dismiss();
                    handleNetError("问题详情获取失败");
                    finish();
                }
            });
        } else {
            mThreadModel = mCourseThreadDataSource.get(mThreadId);
            mPosts = mCourseThreadPostDataSource.getPosts(mThreadId);
            Collections.reverse(mPosts);
            normalCallback.success(true);
        }
    }

    private void getToUserId(final NormalCallback<Integer> normalCallback) {
        if (mToUserId != 0) {
            normalCallback.success(mToUserId);
            return;
        }
        if (PushUtil.ChatUserType.FRIEND.equals(mRoleType)) {
            RequestUrl requestUrl = app.bindUrl(Const.COURSE, false);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("courseId", mCourseId + "");
            ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    CourseDetailsResult courseResult = parseJsonValue(response, new TypeToken<CourseDetailsResult>() {
                    });
                    if (courseResult != null && courseResult.course != null && courseResult.course.teachers != null) {
                        mToUserId = courseResult.course.teachers[0].id;
                        mCourseTitle = courseResult.course.title;
                        normalCallback.success(mToUserId);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        } else if (PushUtil.ChatUserType.TEACHER.equals(mRoleType)) {
            RequestUrl requestUrl = app.bindUrl(Const.GET_THREAD, true);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("threadId", mThreadId + "");
            ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    CourseThreadEntity model = parseJsonValue(response, new TypeToken<CourseThreadEntity>() {
                    });
                    if (model.user != null) {
                        mToUserId = model.user.id;
                        mCourseTitle = model.courseTitle;
                        //normalCallback.success(mToUserId);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    private String getActivityState() {
        if (mAdapter.getCount() == 0) {
            return PushUtil.ThreadMsgType.THREAD;
        } else {
            return PushUtil.ThreadMsgType.THREAD_POST;
        }
    }

    private void handleNetError(String msg) {
        CommonUtil.shortToast(mContext, msg);
    }

    private void addItem2ListView(ThreadDiscussEntity model) {
        etSend.setText("");
        etSend.requestFocus();
        mAdapter.addItem(model);
    }

    private String formatContent(CourseThreadPostEntity model, String type) {
        String content;
        switch (type) {
            case PushUtil.ChatMsgType.IMAGE:
                content = String.format(IMAGE_FORMAT, model.upyunMediaGetUrl);
                model.content = model.upyunMediaGetUrl;
                break;
            case PushUtil.ChatMsgType.AUDIO:
                content = String.format(AUDIO_FORMAT, model.upyunMediaGetUrl);
                model.content = model.upyunMediaGetUrl;
                break;
            default:
                content = model.content;
        }
        return content;
    }

    protected View.OnClickListener mAskClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.iv_show_media_layout) {
                //加号，显示多媒体框
                if (mAdapter == null || mAdapter.getCount() == 0) {
                    handleNetError("提问无法发送图片");
                    return;
                }
                if (viewMediaLayout.getVisibility() == View.GONE) {
                    viewMediaLayout.setVisibility(View.VISIBLE);
                    etSend.clearFocus();
                    ivAddMedia.requestFocus();
                } else {
                    viewMediaLayout.setVisibility(View.GONE);
                }
            } else if (v.getId() == R.id.btn_voice) {
                //语音
                if (mAdapter == null || mAdapter.getCount() == 0) {
                    handleNetError("提问无法发送语音");
                    return;
                }
                viewMediaLayout.setVisibility(View.GONE);
                btnKeyBoard.setVisibility(View.VISIBLE);
                btnVoice.setVisibility(View.GONE);
                viewMsgInput.setVisibility(View.GONE);
                viewPressToSpeak.setVisibility(View.VISIBLE);
            }
        }
    };

    private CourseThreadPostEntity createCoursePostThreadByCurrentUser(String content, String contentType, int deliveryState) {
        CourseThreadPostEntity model = new CourseThreadPostEntity();
        model.courseId = mCourseId;
        model.lessonId = mLessonId;
        model.threadId = mThreadId;
        model.user = new CourseThreadPostEntity.UserEntity();
        model.user.id = app.loginUser.id;
        model.user.nickname = app.loginUser.nickname;
        model.user.mediumAvatar = app.loginUser.mediumAvatar;
        model.content = content;
        model.type = contentType;
        model.delivery = deliveryState;
        model.createdTime = AppUtil.converMillisecond2TimeZone(System.currentTimeMillis());
        return model;
    }

    private CourseThreadEntity createCourseThreadByCurrentUser(String content) {
        CourseThreadEntity model = new CourseThreadEntity();
        model.id = mThreadId;
        model.courseId = mCourseId;
        model.lessonId = mLessonId;
        model.user = new CourseThreadEntity.UserEntity();
        model.user.id = app.loginUser.id;
        model.user.nickname = app.loginUser.nickname;
        model.user.mediumAvatar = app.loginUser.mediumAvatar;
        model.type = PushUtil.ChatMsgType.TEXT;
        model.title = content;
        model.content = content;
        model.createdTime = AppUtil.converMillisecond2TimeZone(System.currentTimeMillis());
        return model;
    }

    private void filterPostThreads(List<CourseThreadPostEntity> posts) {
        for (CourseThreadPostEntity post : posts) {
            if (post.content.contains("amr")) {
                post.type = PushUtil.ChatMsgType.AUDIO;
                AudioCacheEntity cache = AudioCacheUtil.getInstance().getAudioCacheByPath(post.content);
                if (cache != null && !TextUtils.isEmpty(cache.localPath)) {
                    post.content = cache.localPath;
                    post.delivery = PushUtil.MsgDeliveryType.SUCCESS;
                } else {
                    post.delivery = PushUtil.MsgDeliveryType.UPLOADING;
                }
            } else if (post.content.contains("<img")) {
                Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                Matcher m = p.matcher(post.content);
                while (m.find()) {
                    post.content = m.group(1);
                    post.type = PushUtil.ChatMsgType.IMAGE;
                    post.delivery = PushUtil.MsgDeliveryType.SUCCESS;
                    break;
                }
            } else {
                post.content = Html.fromHtml(post.content).toString();
                String lastStr = post.content.substring(post.content.length() - 2, post.content.length());
                if ("\n\n".equals(lastStr)) {
                    post.content = post.content.substring(0, post.content.length() - 2);
                }
                post.type = PushUtil.ChatMsgType.TEXT;
                post.delivery = PushUtil.MsgDeliveryType.SUCCESS;
            }
        }
    }

    // region convert entity

    private ThreadDiscussEntity convertThreadDiscuss(CourseThreadEntity courseThreadEntity) {
        //帖子PostId默认为0，默认发送成功
        return new ThreadDiscussEntity(
                0,
                courseThreadEntity.id,
                courseThreadEntity.courseId,
                courseThreadEntity.lessonId,
                courseThreadEntity.user.id,
                courseThreadEntity.user.nickname,
                courseThreadEntity.user.mediumAvatar,
                courseThreadEntity.content,
                courseThreadEntity.type,
                1,
                courseThreadEntity.createdTime);
    }

    private ThreadDiscussEntity convertThreadDiscuss(CourseThreadPostEntity courseThreadPostEntity) {
        return new ThreadDiscussEntity(
                courseThreadPostEntity.pid,
                courseThreadPostEntity.threadId,
                courseThreadPostEntity.courseId,
                courseThreadPostEntity.lessonId,
                courseThreadPostEntity.user.id,
                courseThreadPostEntity.user.nickname,
                courseThreadPostEntity.user.mediumAvatar,
                courseThreadPostEntity.content,
                courseThreadPostEntity.type,
                courseThreadPostEntity.delivery,
                courseThreadPostEntity.createdTime);
    }

    private V2CustomContent getV2CustomContent(CourseThreadPostEntity postModel, String msgType, String type) {
        V2CustomContent v2CustomContent = new V2CustomContent();
        v2CustomContent.setType(type);
        V2CustomContent.FromEntity fromEntity = new V2CustomContent.FromEntity();
        fromEntity.setId(app.loginUser.id);
        fromEntity.setImage(app.loginUser.mediumAvatar);
        fromEntity.setNickname(app.loginUser.nickname);
        fromEntity.setType(mRoleType);
        v2CustomContent.setFrom(fromEntity);
        V2CustomContent.ToEntity toEntity = new V2CustomContent.ToEntity();
        toEntity.setId(mToUserId);
        toEntity.setType(PushUtil.ChatUserType.USER);
        v2CustomContent.setTo(toEntity);
        V2CustomContent.BodyEntity bodyEntity = new V2CustomContent.BodyEntity();
        bodyEntity.setType(msgType);
        bodyEntity.setContent(postModel.content);
        bodyEntity.setPostId(postModel.postId);
        bodyEntity.setThreadId(mThreadId);
        bodyEntity.setCourseId(mCourseId);
        bodyEntity.setLessonId(mLessonId);
        v2CustomContent.setBody(bodyEntity);
        v2CustomContent.setV(Const.PUSH_VERSION);
        //v2CustomContent.setCreatedTime(mSendTime);
        return v2CustomContent;
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{new MessageType(Const.ADD_THREAD_POST, getClass().getSimpleName())};
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
        V2CustomContent v2CustomContent = parseJsonValue(wrapperMessage.getCustomContentJson(), new TypeToken<V2CustomContent>() {
        });
        switch (messageType.code) {
            case Const.ADD_THREAD_POST:
                if (CurrentThreadId == v2CustomContent.getBody().getThreadId()) {
                    CourseThreadPostEntity postModel = new CourseThreadPostEntity();
                    postModel.postId = v2CustomContent.getBody().getPostId();
                    postModel.threadId = v2CustomContent.getBody().getThreadId();
                    postModel.courseId = mCourseId;
                    postModel.lessonId = mLessonId;
                    postModel.content = wrapperMessage.getContent();
                    postModel.user.id = v2CustomContent.getFrom().getId();
                    postModel.user.nickname = v2CustomContent.getFrom().getNickname();
                    postModel.user.mediumAvatar = v2CustomContent.getFrom().getImage();
                    postModel.createdTime = AppUtil.converMillisecond2TimeZone(v2CustomContent.getCreatedTime());
                    postModel.delivery = 2;
                    postModel.type = v2CustomContent.getBody().getType();
                    postModel.pid = (int) mCourseThreadPostDataSource.create(postModel);
                    mAdapter.addItem(convertThreadDiscuss(postModel));
                }
                break;
        }
    }

    // endregion
}
