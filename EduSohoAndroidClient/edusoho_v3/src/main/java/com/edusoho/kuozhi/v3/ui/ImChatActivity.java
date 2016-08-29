package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.provider.UserProvider;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by suju on 16/8/26.
 */
public class ImChatActivity extends ActionBarBaseActivity {

    public static final String TAG = "ChatActivity";
    public static final String FROM_ID = "from_id";
    public static final String FROM_NAME = "from_name";
    public static final String CONV_NO = "conv_no";
    public static final String HEAD_IMAGE_URL = "head_image_url";

    protected int mTargetId;
    protected String mTargetName;
    protected String mConversationNo;
    protected MessageListFragment mMessageListFragment;

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

    private void attachMessageListFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mMessageListFragment = (MessageListFragment) Fragment.instantiate(mContext, MessageListFragment.class.getName());

        mMessageListFragment.setMessageControllerListener(getMessageControllerListener());
        Bundle bundle = new Bundle();
        bundle.putString(MessageListFragment.CONV_NO, mConversationNo);
        bundle.putInt(MessageListFragment.TARGET_ID, mTargetId);
        bundle.putString(MessageListFragment.TARGET_TYPE, getTargetType());
        mMessageListFragment.setArguments(bundle);
        fragmentTransaction.add(android.R.id.content, mMessageListFragment,  "im_container").commit();
    }

    protected String getTargetType() {
        return Destination.USER;
    }

    private void initParams() {
        Intent dataIntent = getIntent();
        mTargetId = dataIntent.getIntExtra(FROM_ID, 0);
        mConversationNo = dataIntent.getStringExtra(CONV_NO);
        mTargetName = dataIntent.getStringExtra(FROM_NAME);
    }

    private MessageControllerListener getMessageControllerListener() {
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
            public void createRole(RoleUpdateCallback callback) {
                createTargetRole(callback);
            }

            @Override
            public Map<String, String> getRequestHeaders() {
                HashMap map = new HashMap();
                map.put("Auth-Token", app.apiToken);
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
        };
    }

    protected void createTargetRole(final MessageControllerListener.RoleUpdateCallback callback) {
        new UserProvider(mContext).getUserInfo(mTargetId)
                .success(new NormalCallback<User>() {
                    @Override
                    public void success(User user) {
                        Role role = new Role();
                        role.setRid(user.id);
                        role.setAvatar(user.mediumAvatar);
                        role.setType(Destination.USER);
                        role.setNickname(user.nickname);
                        callback.onCreateRole(role);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.user_profile) {
            Bundle bundle = new Bundle();
            School school = getAppSettingProvider().getCurrentSchool();
            bundle.putString(Const.WEB_URL, String.format(Const.MOBILE_APP_URL, school.url + "/", String.format(Const.USER_PROFILE, mTargetId)));
            CoreEngine.create(mContext).runNormalPluginWithBundle("WebViewActivity", mContext, bundle);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected Promise createChatConvNo() {
        final Promise promise = new Promise();
        User currentUser = getAppSettingProvider().getCurrentUser();
        if (currentUser == null || currentUser.id == 0) {
            ToastUtils.show(getBaseContext(), "用户未登录");
            promise.resolve(null);
            return promise;
        }

        new UserProvider(mContext).createConvNo(new int[]{currentUser.id, mTargetId})
                .success(new NormalCallback<LinkedHashMap>() {
                    @Override
                    public void success(LinkedHashMap linkedHashMap) {
                        String no = null;
                        if (linkedHashMap != null || linkedHashMap.containsKey("no")) {
                            no = linkedHashMap.get("no").toString();
                        }
                        promise.resolve(no);
                    }
                });

        return promise;
    }

    private View createView() {
        FrameLayout frameLayout = new FrameLayout(getBaseContext());
        frameLayout.setId(android.R.id.content);

        return frameLayout;
    }
}
