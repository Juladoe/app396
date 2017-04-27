package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.shard.ThirdPartyLogin;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.NotificationProvider;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.provider.IMServiceProvider;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by tree on 2017/4/25.
 */

public class MineFragment1 extends BaseFragment {

    private View rlUserInfo;
    private View rlMyVip;
    private View rlDynamic;
    private View rlMsgNotify;
    private View rlAbout;
    private View rlFeedBack;
    private CheckBox cbOfflineType;
    private TextView tvName;
    private TextView tvUserType;
    private CircleImageView ivAvatar;
    private Button btnLogout;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_mine1);
    }

    @Override
    protected void initView(View view) {
        rlUserInfo = view.findViewById(R.id.rl_user_info);
        rlUserInfo.setOnClickListener(getUserViewClickListener());
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvUserType = (TextView) view.findViewById(R.id.tv_avatar_type);
        ivAvatar = (CircleImageView) view.findViewById(R.id.iv_avatar);
        initUserInfo();

        rlMyVip = view.findViewById(R.id.rl_my_vip);
        rlMyVip.setOnClickListener(vipClickListener);
        rlDynamic = view.findViewById(R.id.rl_dynamic);
        rlDynamic.setOnClickListener(dynamicClickListener);
        rlMsgNotify = view.findViewById(R.id.rl_msg_notify);
        rlMsgNotify.setOnClickListener(msgClickListener);
        rlAbout = view.findViewById(R.id.rl_about);
        rlAbout.setOnClickListener(aboutClickListener);
        cbOfflineType = (CheckBox) view.findViewById(R.id.cb_offline_type);
        cbOfflineType.setOnClickListener(setOfflineTypeListener);
        rlFeedBack = view.findViewById(R.id.rl_feedback);
        rlFeedBack.setOnClickListener(feedbackClickListener);
        btnLogout = (Button) view.findViewById(R.id.mine_logout_btn);
        btnLogout.setOnClickListener(logoutClickLister);

        cbOfflineType.setChecked(app.config.offlineType == 1);

    }

    private void initUserInfo(){
        if(app.loginUser != null){
            tvName.setText(app.loginUser.nickname);
            tvUserType.setText(app.loginUser.userRole2String());
            ImageLoader.getInstance().displayImage(app.loginUser.getMediumAvatar(), ivAvatar, app.mAvatarOptions);
        }
    }

    private View.OnClickListener getUserViewClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "i_userInformationPortal");
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.MY_INFO);
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
            }
        };
    }

    private View.OnClickListener vipClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(mContext, "i_my_vip");
            mActivity.app.mEngine.runNormalPlugin("MyVipActivity", mActivity ,null);
        }
    };

    private View.OnClickListener dynamicClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener msgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(mContext, "i_mySetting_newMessageNotification");
            mActivity.app.mEngine.runNormalPlugin("MsgReminderActivity", mActivity, null);
        }
    };

    private View.OnClickListener aboutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(mContext, "i_mySetting_about");
            mActivity.app.mEngine.runNormalPlugin("AboutActivity", mActivity, null);
        }
    };

    private View.OnClickListener setOfflineTypeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (cbOfflineType.isChecked()) {
                MobclickAgent.onEvent(mContext, "i_mySetting_4gCachESwitch_on");
            } else {
                MobclickAgent.onEvent(mContext, "i_mySetting_4gCacheSwitch_off");
            }
            app.config.offlineType = cbOfflineType.isChecked() ? 1 : 0;
            app.saveConfig();
        }
    };

    private View.OnClickListener feedbackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            app.mEngine.runNormalPlugin("SuggestionActivity", mActivity, null);
        }
    };

    private View.OnClickListener logoutClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(mContext, "i_my_Setting_logout");
            if (TextUtils.isEmpty(app.loginUser.thirdParty)) {
                RequestUrl requestUrl = app.bindUrl(Const.LOGOUT, true);
                mActivity.ajaxPostWithLoading(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Const.BIND_USER_ID, app.loginUser.id + "");

                        new IMServiceProvider(mContext).unBindServer();
                        getAppSettingProvider().setUser(null);
                        app.removeToken();
                        btnLogout.setVisibility(View.INVISIBLE);
                        app.sendMessage(Const.LOGOUT_SUCCESS, null);
                        app.sendMsgToTarget(Const.SWITCH_TAB, null, DefaultPageActivity.class);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }, "");
                Bundle bundle = new Bundle();
                bundle.putString(Const.BIND_USER_ID, app.loginUser.id + "");
            } else {
                new IMServiceProvider(mContext).unBindServer();
                getAppSettingProvider().setUser(null);
                ThirdPartyLogin.getInstance(mContext).loginOut(app.loginUser.thirdParty);
                app.removeToken();
                btnLogout.setVisibility(View.INVISIBLE);
                app.sendMessage(Const.LOGOUT_SUCCESS, null);
                app.sendMsgToTarget(Const.SWITCH_TAB, null, DefaultPageActivity.class);
            }
            getNotificationProvider().cancelAllNotification();
            M3U8DownService service = M3U8DownService.getService();
            if (service != null) {
                service.cancelAllDownloadTask();
            }
        }
    };

    protected NotificationProvider getNotificationProvider() {
        return FactoryManager.getInstance().create(NotificationProvider.class);
    }



    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{new MessageType(Const.LOGIN_SUCCESS), new MessageType(Const.THIRD_PARTY_LOGIN_SUCCESS)};
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if(messageType.type.equals(Const.LOGIN_SUCCESS) || messageType.type.equals(Const.THIRD_PARTY_LOGIN_SUCCESS)){
            initUserInfo();
        }
    }
}
