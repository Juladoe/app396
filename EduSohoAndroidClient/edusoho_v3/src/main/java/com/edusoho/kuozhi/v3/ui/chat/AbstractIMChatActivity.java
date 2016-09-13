package com.edusoho.kuozhi.v3.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.ui.MessageListFragment;
import com.edusoho.kuozhi.imserver.ui.listener.MessageControllerListener;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.NotificationProvider;
import com.edusoho.kuozhi.v3.factory.UtilFactory;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
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
public abstract class AbstractIMChatActivity extends AppCompatActivity {

    public static final int SEND_IMAGE = 1;
    public static final int SEND_CAMERA = 2;

    public static final String BACK = "返回";
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
    protected Context mContext;
    protected TextView mTitleTextView;
    protected View mTitleLayoutView;

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mActionBar = getSupportActionBar();
        mContext = getBaseContext();
        setContentView(createView());
        initParams();
        setBackMode(BACK, TextUtils.isEmpty(mTargetName) ? "聊天" : mTargetName);
        attachMessageListFragment();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mConversationNo != null) {
            getNotificationProvider().cancelNotification(mConversationNo.hashCode());
        }
    }

    public void setBackMode(String backTitle, String title) {
        mTitleLayoutView = getLayoutInflater().inflate(R.layout.actionbar_custom_title, null);
        mTitleTextView = (TextView) mTitleLayoutView.findViewById(R.id.tv_action_bar_title);
        mTitleTextView.setText(title);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        mActionBar.setCustomView(mTitleLayoutView, layoutParams);

        if (backTitle != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected NotificationProvider getNotificationProvider() {
        return FactoryManager.getInstance().create(NotificationProvider.class);
    }

    protected void attachMessageListFragment() {
        Log.d(TAG, "attachMessageListFragment");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("im_container");
        if (fragment != null) {
            mMessageListFragment = (MessageListFragment) fragment;
            mMessageListFragment.setMessageControllerListener(getMessageControllerListener());
            //fragmentTransaction.show(fragment);
        } else {
            mMessageListFragment = createFragment();
            mMessageListFragment.setMessageControllerListener(getMessageControllerListener());
            Bundle bundle = new Bundle();
            bundle.putString(MessageListFragment.CONV_NO, mConversationNo);
            bundle.putInt(MessageListFragment.TARGET_ID, mTargetId);
            bundle.putString(MessageListFragment.TARGET_TYPE, getTargetType());
            mMessageListFragment.setArguments(bundle);
            fragmentTransaction.add(R.id.chat_content, mMessageListFragment, "im_container");
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected MessageListFragment createFragment() {
        MessageListFragment messageListFragment = (MessageListFragment) Fragment.instantiate(mContext, MessageListFragment.class.getName());
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
                final LoadDialog loadDialog = LoadDialog.create(AbstractIMChatActivity.this);
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
                openPictureFromLocal();
            }

            @Override
            public void takePhoto(PhotoSelectCallback callback) {
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
                            CoreEngine.create(mContext).runNormalPlugin("FragmentPageActivity", mContext, new PluginRunCallback() {
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SEND_IMAGE) {
            ArrayList<String> pathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            int size = getSupportFragmentManager().getFragments().size();
            data.removeExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            data.putStringArrayListExtra("ImageList", pathList);
            for (int i = 0; i < size; i++) {
                getSupportFragmentManager().getFragments().get(i).onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }

    protected UtilFactory getUtilFactory() {
        return FactoryManager.getInstance().create(UtilFactory.class);
    }
}
