package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.register.MsgCode;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoLoadingButton;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by DF on 2016/11/24.
 */

public class RegisterConfirmActivity extends ActionBarBaseActivity {

    private TextView tvShow;
    private EditText etPwd;
    private EditText etAuth;
    private Button btnSend;

    private int mClockTime;
    private Timer mTimer;
    private SmsCodeHandler mSmsCodeHandler;
    private String num;
    private Button btnShow;
    private EduSohoLoadingButton btnConfirm;
    private String mCookie = "";
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_confirm);
        hideActionBar();
        initView();
    }

    private void initView() {
        tvShow = (TextView) findViewById(R.id.tv_show);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(mSmsSendClickListener);
        etAuth = (EditText) findViewById(R.id.et_auth);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        btnShow = (Button) findViewById(R.id.btn_show);
        btnShow.setOnClickListener(nShowPwdClickListener);
        btnConfirm = (EduSohoLoadingButton) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(mConfirmRegClickListener);
        iv = (ImageView) findViewById(R.id.iv_back);   //后期抽取
        iv.setOnClickListener(mBackClickListener);

        num = getIntent().getStringExtra("num");
        tvShow.setText("验证码已经发送到:"+ num);

        mSmsCodeHandler = new SmsCodeHandler(this);

        mSmsSendClickListener.onClick(btnSend);

        firstReq();
    }

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RegisterConfirmActivity.this.finish();
        }
    };

    /**
     * 处理隐藏pwd
     */
    private boolean isShowPwd = true;

    View.OnClickListener nShowPwdClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isShowPwd) {
                etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnShow.setBackgroundResource(R.drawable.register_pwd_unshow);
            }else{
                etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnShow.setBackgroundResource(R.drawable.register_pwd_show);
            }
            isShowPwd = !isShowPwd;
        }
    };

    /**
     * 注册账号
     */
    View.OnClickListener mConfirmRegClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl url = app.bindUrl(Const.REGIST, false);
            HashMap<String, String> params = url.getParams();
            params.put("registeredWay","android");

            params.put("phone", num);

            String strCode = etAuth.getText().toString().trim();
            if (TextUtils.isEmpty(strCode)) {
                CommonUtil.longToast(mContext, "请输入验证码");
                return;
            } else {
                params.put("smsCode", strCode);
            }
            String strPass = etPwd.getText().toString();
            if (TextUtils.isEmpty(strPass)) {
                CommonUtil.longToast(mContext, "请输入密码");
                return;
            }
            params.put("password", strPass);


            HashMap<String, String> headers = url.getHeads();
            if (!TextUtils.isEmpty(mCookie)) {
                headers.put("Cookie", mCookie);
            }

            btnConfirm.setLoadingState();

            mActivity.ajaxPost(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("test", response);
                        UserResult userResult = mActivity.parseJsonValue(
                                response, new TypeToken<UserResult>() {
                                });
                        if (userResult != null && userResult.user != null) {
                            app.saveToken(userResult);
                            btnConfirm.setSuccessState();
                            btnConfirm.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    app.mEngine.runNormalPlugin("DefaultPageActivity", mContext, null, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    app.sendMessage(Const.LOGIN_SUCCESS, null);
                                    RegisterConfirmActivity.this.finish();
                                }
                            }, 500);
                        } else {
                            btnConfirm.setInitState();
                            if (!TextUtils.isEmpty(response)) {
                                CommonUtil.longToast(mContext, response);
                            }
                        }
                    } catch (Exception e) {
                        btnConfirm.setInitState();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + new String(error.networkResponse.data).toString());
                    btnConfirm.setInitState();
                    CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                }
            });
        }
    };

    /**
     * 处理验证码
     */
    private void firstReq(){
        btnSend.setEnabled(false);
        btnSend.setTextColor(mActivity.getResources().getColor(R.color.register_send));
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
        CommonUtil.longToast(mContext, "短信已发送");
    }

    View.OnClickListener mSmsSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl requestUrl = app.bindUrl(Const.SMS_SEND, false);

            HashMap<String, String> params = requestUrl.getParams();
            params.put("phoneNumber", String.valueOf(num));
            mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        MsgCode result = parseJsonValue(response, new TypeToken<MsgCode>() {
                        });
                        if (result != null && result.code == 200) {
                            btnSend.setEnabled(false);
                            btnSend.setTextColor(mActivity.getResources().getColor(R.color.register_send));
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
                            CommonUtil.longToast(mContext, result.msg);

                        } else {
                            CommonUtil.longToast(mContext, response);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "phone reg error");
                    }
                }
            }, null);
        }
    };

    public static class SmsCodeHandler extends Handler {
        WeakReference<RegisterConfirmActivity> mWeakReference;
        RegisterConfirmActivity mActivity;

        public SmsCodeHandler(RegisterConfirmActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            mActivity = mWeakReference.get();
            mActivity.btnSend.setText(mActivity.mClockTime + "S后重发");
            mActivity.mClockTime--;
            if (mActivity.mClockTime < 0) {
                mActivity.mTimer.cancel();
                mActivity.mTimer = null;
                mActivity.btnSend.setTextColor(mActivity.getResources().getColor(R.color.register_send_auth));
                mActivity.btnSend.setText(mActivity.getResources().getString(R.string.reg_send_code));
                mActivity.btnSend.setEnabled(true);
            }
        }
    }
}
