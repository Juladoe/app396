package com.edusoho.kuozhi.ui.common;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.TokenResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.fragment.MineFragment;
import com.edusoho.kuozhi.ui.fragment.SchoolRoomFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 15/6/8.
 */
public class V3RegisterActivity extends ActionBarBaseActivity {
    private EditText etPhone;
    private EditText etCode;
    private EditText etPhonePass;
    private Button btnPhoneReg;
    private Button btnSendCode;
    private EditText etMail;
    private EditText etMailPass;
    private Button btnMailReg;
    private String mCookie = "";

    private int mClockTime;
    private Timer mTimer;
    private SmsCodeHandler mSmsCodeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setBackMode(BACK, "注册");
        initView();
    }

    private void initView() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("手机注册").setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("邮箱注册").setContent(R.id.tab2));
        TabWidget tabWidget = tabHost.getTabWidget();

        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(AppUtil.px2sp(this, getResources().getDimension(R.dimen.medium_font_size)));
            tv.setTypeface(null, Typeface.NORMAL);
            tv.setTextColor(this.getResources().getColor(R.color.green_alpha));
            tabWidget.getChildAt(i).setBackgroundResource(R.drawable.register_tab_bg);
        }

        etPhone = (EditText) findViewById(R.id.et_phone);
        etCode = (EditText) findViewById(R.id.et_code);
        etPhonePass = (EditText) findViewById(R.id.et_phone_pass);
        btnSendCode = (Button) findViewById(R.id.btn_send_code);
        btnSendCode.setOnClickListener(mSmsSendClickListener);
        btnPhoneReg = (Button) findViewById(R.id.btn_phone_reg);
        btnPhoneReg.setOnClickListener(mPhoneRegClickListener);
        etMail = (EditText) findViewById(R.id.et_mail);
        etMailPass = (EditText) findViewById(R.id.et_mail_pass);
        btnMailReg = (Button) findViewById(R.id.btn_mail_reg);
        btnMailReg.setOnClickListener(mMailRegClickListener);
        mSmsCodeHandler = new SmsCodeHandler(this);
    }

    public static class SmsCodeHandler extends Handler {
        WeakReference<V3RegisterActivity> mWeakReference;
        V3RegisterActivity mActivity;

        public SmsCodeHandler(V3RegisterActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            mActivity = mWeakReference.get();
            mActivity.btnSendCode.setText(mActivity.mClockTime + "秒后重发");
            mActivity.mClockTime--;
            if (mActivity.mClockTime < 0) {
                mActivity.mTimer.cancel();
                mActivity.mTimer = null;
                mActivity.btnSendCode.setText(mActivity.getResources().getString(R.string.reg_send_code));
                mActivity.btnSendCode.setEnabled(true);
                mActivity.btnSendCode.setBackgroundResource(R.drawable.reg_code_press);
            }
        }
    }

    View.OnClickListener mSmsSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String phoneNumber = etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                ToastUtils.show(mContext, String.format("请输入%s", "手机号"));
                return;
            }


            RequestUrl requestUrl = app.bindUrl(Const.SMS_SEND, false);

            HashMap<String, String> params = requestUrl.getParams();
            params.put("phoneNumber", String.valueOf(phoneNumber));
            mActivity.ajaxPost(requestUrl, new ResultCallback() {
                @Override
                public void callback(String url, String object, AjaxStatus ajaxStatus) {
                    try {
                        mCookie = ajaxStatus.getHeader("Set-Cookie");
                        JSONObject jsonObject = new JSONObject(object);
                        if (jsonObject.getString("code").equals("200")) {
                            btnSendCode.setEnabled(false);
                            btnSendCode.setBackgroundResource(R.drawable.reg_code_disable);
                            mClockTime = 60;
                            mTimer = new Timer();
                            mTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Message message = mSmsCodeHandler.obtainMessage();
                                    message.what = 0;
                                    mSmsCodeHandler.sendMessage(message);

                                }
                            }, 0, 1000);
                            ToastUtils.show(mContext, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                    }
                }
            });

        }
    };

    View.OnClickListener mPhoneRegClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl url = app.bindUrl(Const.REGIST, false);
            HashMap<String, String> params = url.getParams();

            String strPhone = etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(strPhone)) {
                ToastUtils.show(mContext, String.format("请输入手机号"));
                return;
            }
            params.put("phone", strPhone);

            String strPass = etPhonePass.getText().toString();
            if (TextUtils.isEmpty(strPass)) {
                ToastUtils.show(mContext, "请输入密码");
                return;
            }
            params.put("password", strPass);

            String strCode = etCode.getText().toString().trim();
            if (TextUtils.isEmpty(strCode)) {
                ToastUtils.show(mContext, "请输入验证码");
                return;
            } else {
                params.put("smsCode", strCode);
            }

            HashMap<String, String> headers = url.getHeads();
            if (!TextUtils.isEmpty(mCookie)) {
                headers.put("Cookie", mCookie);
            }

            mActivity.ajaxPostByLoding(url, new ResultCallback() {
                @Override
                public void callback(String url, String object, AjaxStatus ajaxStatus) {
                    try {
                        TokenResult userResult = mActivity.parseJsonValue(
                                object, new TypeToken<TokenResult>() {
                                });
                        app.saveToken(userResult);
                        mActivity.finish();
                        app.sendMsgToTarget(MineFragment.REFRESH, null, MineFragment.class);
                        app.sendMsgToTarget(SchoolRoomFragment.REFRESH, null, SchoolRoomFragment.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    View.OnClickListener mMailRegClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl url = app.bindUrl(Const.REGIST, false);
            HashMap<String, String> params = url.getParams();

            String strMail = etMail.getText().toString().trim();
            if (TextUtils.isEmpty(strMail)) {
                ToastUtils.show(mContext, String.format("请输入邮箱地址"));
                return;
            }
            params.put("email", strMail);

            String strPass = etMailPass.getText().toString();
            if (TextUtils.isEmpty(strPass)) {
                ToastUtils.show(mContext, "请输入密码");
                return;
            }
            params.put("password", strPass);

            mActivity.ajaxPostByLoding(url, new ResultCallback() {
                @Override
                public void callback(String url, String object, AjaxStatus ajaxStatus) {
                    try {
                        TokenResult userResult = mActivity.parseJsonValue(
                                object, new TypeToken<TokenResult>() {
                                });
                        app.saveToken(userResult);
                        mActivity.finish();
                        app.sendMsgToTarget(MineFragment.REFRESH, null, MineFragment.class);
                        app.sendMsgToTarget(SchoolRoomFragment.REFRESH, null, SchoolRoomFragment.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}
