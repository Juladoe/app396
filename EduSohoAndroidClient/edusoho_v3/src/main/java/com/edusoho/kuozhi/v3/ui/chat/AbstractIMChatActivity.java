package com.edusoho.kuozhi.v3.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.ui.MessageListFragment;
import com.edusoho.kuozhi.imserver.ui.listener.MessageControllerListener;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.NotificationProvider;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.ChatSelectFragment;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by suju on 16/9/6.
 */
public abstract class AbstractIMChatActivity extends ActionBarBaseActivity {

    public static final int SEND_IMAGE = 1;
    public static final int SEND_CAMERA = 2;

    public static final String TAG = "ChatActivity";
    public static final String FROM_ID = "from_id";
    public static final String TARGET_TYPE = "targer_type";
    public static final String FROM_NAME = "from_name";
    public static final String CONV_NO = "conv_no";
    public static final String HEAD_IMAGE_URL = "head_image_url";

    protected int mTargetId;
    protected String mTargetName;
    protected String mTargetType;
    protected String mConversationNo;
    protected MessageListFragment mMessageListFragment;

    private MessageControllerListener.PhotoSelectCallback mPhotoSelectCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createView());
        initParams();
        setBackMode(BACK, TextUtils.isEmpty(mTargetName) ? "聊天" : mTargetName);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachMessageListFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mConversationNo != null) {
            getNotificationProvider().cancelNotification(mConversationNo.hashCode());
        }
    }

    protected NotificationProvider getNotificationProvider() {
        return FactoryManager.getInstance().create(NotificationProvider.class);
    }

    protected void attachMessageListFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mMessageListFragment = createFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MessageListFragment.CONV_NO, mConversationNo);
        bundle.putInt(MessageListFragment.TARGET_ID, mTargetId);
        bundle.putString(MessageListFragment.TARGET_TYPE, getTargetType());
        mMessageListFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.chat_content, mMessageListFragment, "im_container").commit();
    }

    protected MessageListFragment createFragment() {
        MessageListFragment messageListFragment = (MessageListFragment) Fragment.instantiate(mContext, MessageListFragment.class.getName());
        messageListFragment.setMessageControllerListener(getMessageControllerListener());
        return messageListFragment;
    }

    protected String getTargetType() {
        return TextUtils.isEmpty(mTargetType) ? Destination.USER : mTargetType;
    }

    protected void initParams() {
        Intent dataIntent = getIntent();
        mTargetId = dataIntent.getIntExtra(FROM_ID, 0);
        mConversationNo = dataIntent.getStringExtra(CONV_NO);
        mTargetType = dataIntent.getStringExtra(TARGET_TYPE);
        mTargetName = dataIntent.getStringExtra(FROM_NAME);
    }

    protected MessageControllerListener getMessageControllerListener() {
        return new MessageControllerListener() {
            @Override
            public void createConvNo(final ConvNoCreateCallback callback) {
                final LoadDialog loadDialog = LoadDialog.create(mActivity);
                loadDialog.show();
                createChatConvNo().then(new PromiseCallback<String>() {
                    @Override
                    public Promise invoke(String convNo) {
                        loadDialog.dismiss();
                        callback.onCreateConvNo(convNo);
                        return null;
                    }
                });
            }

            @Override
            public void createRole(String type, int rid, RoleUpdateCallback callback) {
                createTargetRole(type, rid, callback);
            }

            @Override
            public Map<String, String> getRequestHeaders() {
                HashMap map = new HashMap();
                map.put("Auth-Token", ApiTokenUtil.getApiToken(mContext));
                return map;
            }

            @Override
            public void onShowImage(int index, ArrayList<String> imageList) {
                Bundle bundle = new Bundle();
                bundle.putInt("index", index);
                bundle.putStringArrayList("imageList", imageList);
                CoreEngine.create(mContext).runNormalPluginWithBundle("ViewPagerActivity", mContext, bundle);
            }

            @Override
            public void onShowUser(Role role) {
                Bundle bundle = new Bundle();
                School school = getAppSettingProvider().getCurrentSchool();
                bundle.putString(Const.WEB_URL, String.format(
                        Const.MOBILE_APP_URL,
                        school.url + "/",
                        String.format(Const.USER_PROFILE, role.getRid()))
                );
                CoreEngine.create(mContext).runNormalPluginWithBundle("WebViewActivity", mContext, bundle);
            }

            @Override
            public void onShowWebPage(String url) {
                Bundle bundle = new Bundle();
                bundle.putString(Const.WEB_URL, url);
                CoreEngine.create(mContext).runNormalPluginWithBundle("WebViewActivity", mContext, bundle);
            }

            @Override
            public void selectPhoto(PhotoSelectCallback callback) {
                mPhotoSelectCallback = callback;
                openPictureFromLocal();
            }

            @Override
            public void takePhoto(PhotoSelectCallback callback) {
                mPhotoSelectCallback = callback;
                openPictureFromCamera();
            }

            @Override
            public void onShowActivity(Bundle bundle) {
                String activityName = bundle.getString("activityName");
                switch (activityName) {
                    case "ThreadDiscussActivity":
                        CoreEngine.create(mContext).runNormalPluginWithBundle("ThreadDiscussActivity", mContext, bundle);
                        break;
                    case "ChatSelectFragment":
                        try {
                            JSONObject data = new JSONObject(bundle.getString("data"));
                            final RedirectBody redirectBody = RedirectBody.createByJsonObj(data);
                            mActivity.app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    startIntent.putExtra(Const.ACTIONBAR_TITLE, "选择");
                                    startIntent.putExtra(ChatSelectFragment.BODY, redirectBody);
                                    startIntent.putExtra(FragmentPageActivity.FRAGMENT, "ChatSelectFragment");
                                }
                            });
                        } catch (JSONException e) {
                        }
                        break;
                }
            }
        };
    }

    protected abstract void createTargetRole(String type, int rid, MessageControllerListener.RoleUpdateCallback callback);

    protected abstract Promise createChatConvNo();

    protected View createView() {
        FrameLayout frameLayout = new FrameLayout(getBaseContext());
        frameLayout.setId(R.id.chat_content);

        return frameLayout;
    }

    /**
     * 从图库获取图片
     */
    protected void openPictureFromLocal() {
        Intent intent = new Intent(getBaseContext(), MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 5);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        startActivityForResult(intent, SEND_IMAGE);
    }

    protected void openPictureFromCamera() {
        Intent intent = new Intent(getBaseContext(), MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_TAKE_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, SEND_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SEND_IMAGE) {
            List<String> pathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (mPhotoSelectCallback != null) {
                mPhotoSelectCallback.onSelected(pathList);
            }
        }
    }
}
