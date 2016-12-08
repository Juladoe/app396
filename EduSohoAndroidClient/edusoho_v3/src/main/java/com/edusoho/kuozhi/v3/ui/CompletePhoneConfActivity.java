package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.register.MsgCode;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DF on 2016/11/28.
 */
public class CompletePhoneConfActivity extends ActionBarBaseActivity{

    private int mClockTime;
    private TextView tvShow;
    private EditText etPwd;
    private EditText etAuth;
    private TextView tvSend;
    private Timer mTimer;
    private SmsCodeHandler mSmsCodeHandler;
    private String num;
    private ImageView ivShowPwd;
    private TextView tvConfirm;
    private String mCookie = "";
    private ImageView ivBack;
    private TextView tvInfo;
    private ImageView ivClearPwd;
    private ImageView ivClearAuth;
    private TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setContentView(R.layout.activity_complete_phone_conf);
        initView();
    }

    private void initView() {
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvInfo.setText("完善信息");
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvSend = (TextView) findViewById(R.id.tv_send);
        tvSend.setOnClickListener(mSmsSendClickListener);
        etAuth = (EditText) findViewById(R.id.et_auth);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        ivShowPwd = (ImageView) findViewById(R.id.iv_show_pwd);
        ivShowPwd.setOnClickListener(nShowPwdClickListener);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(mConfirmRegClickListener);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(mBackClickListener);
        ivClearAuth = (ImageView) findViewById(R.id.iv_clear_auth);
        ivClearAuth.setOnClickListener(mClearContent);
        ivClearPwd = (ImageView) findViewById(R.id.iv_clear_pwd);
        ivClearPwd.setOnClickListener(mClearContent);
        tvTime = (TextView)  findViewById(R.id.tv_show_time);

        num = getIntent().getStringExtra("phoneNum");
        tvShow.setText(getString(R.string.phone_code_input_hint)+ num);

        initTextChange();

        InputUtils.showKeyBoard(etAuth,mContext);
        mSmsCodeHandler = new SmsCodeHandler(this);
        mSmsSendClickListener.onClick(tvSend);
        sendSms();
    }

    private void initTextChange() {
        InputUtils.addTextChangedListener(etAuth, new NormalCallback<Editable>() {
            @Override
            public void success(Editable editable) {
                if (etAuth.length() == 0) {
                    ivClearAuth.setVisibility(View.INVISIBLE);
                }else {
                    ivClearAuth.setVisibility(View.VISIBLE);
                }
                if (etAuth.length() == 0 || etPwd.length() == 0) {
                    tvConfirm.setAlpha(0.6f);
                } else {
                    tvConfirm.setAlpha(1.0f);
                }
            }
        });

        InputUtils.addTextChangedListener(etPwd, new NormalCallback<Editable>() {
            @Override
            public void success(Editable editable) {
                if (etPwd.length() == 0) {
                    ivClearPwd.setVisibility(View.INVISIBLE);
                }else {
                    ivClearPwd.setVisibility(View.VISIBLE);
                }
                if (etAuth.length() == 0 || etPwd.length() == 0) {
                    tvConfirm.setAlpha(0.6f);
                } else {
                    ivClearPwd.setVisibility(View.VISIBLE);
                    tvConfirm.setAlpha(1.0f);
                }
            }
        });
    }

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CompletePhoneConfActivity.this.finish();
        }
    };

    View.OnClickListener mClearContent = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_clear_auth) {
                etAuth.setText("");
            }else if (id == R.id.iv_clear_pwd){
                etPwd.setText("");
            }
        }
    };

    /**
     * 处理隐藏pwd
     */
    private boolean isShowPwd = true;

    View.OnClickListener nShowPwdClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (isShowPwd) {
                etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivShowPwd.setImageResource(R.drawable.pwd_show);
            }else{
                etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivShowPwd.setImageResource(R.drawable.pwd_unshow);
            }
            isShowPwd = !isShowPwd;
            etPwd.setSelection(etPwd.getText().toString().length());
        }
    };

    /**
     * 注册账号
     */
    View.OnClickListener mConfirmRegClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl url = app.bindUrl(Const.REGIST, false);
            HashMap<String, String> params = (HashMap<String, String>) url.getParams();
            params.put("registeredWay","android");

            params.put("phone", num);

            String strCode = etAuth.getText().toString().trim();
            if (TextUtils.isEmpty(strCode)) {
                CommonUtil.longToast(mContext, getString(R.string.reg_code_hint));
                return;
            } else {
                params.put("smsCode", strCode);
            }
            String strPass = etPwd.getText().toString();
            if (TextUtils.isEmpty(strPass)) {
                CommonUtil.longToast(mContext, getString(R.string.reg_password_hint));
                return;
            }
            params.put("password", strPass);


            HashMap<String, String> headers = url.getHeads();
            if (!TextUtils.isEmpty(mCookie)) {
                headers.put("Cookie", mCookie);
            }

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
                            tvConfirm.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    //绑定成功后直接进到网校
                                    CommonUtil.shortCenterToast(CompletePhoneConfActivity.this,"绑定成功");
                                    app.mEngine.runNormalPlugin("DefaultPageActivity", mContext, null, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    app.sendMessage(Const.LOGIN_SUCCESS, null);
                                }
                            }, 500);
                        } else {
                            if (!TextUtils.isEmpty(response)) {
                                CommonUtil.longToast(mContext, response);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + new String(error.networkResponse.data).toString());
                    CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                }
            });
        }
    };

    /**
     * 处理验证码
     */
    private void sendSms(){
        tvSend.setEnabled(false);
        mClockTime = 120;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = mSmsCodeHandler.obtainMessage();
                message.what = 0;
                mSmsCodeHandler.sendMessage(message);
            }
        }, 0, 1000);
    }

    View.OnClickListener mSmsSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl requestUrl = app.bindUrl(Const.SMS_SEND, false);
            HashMap<String, String> params = (HashMap<String, String>) requestUrl.getParams();
            params.put("phoneNumber", String.valueOf(num));
            mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        MsgCode result = parseJsonValue(response, new TypeToken<MsgCode>() {
                        });
                        if (result != null && result.code == 200) {
                            tvTime.setVisibility(View.VISIBLE);
                            tvSend.setEnabled(false);
                            mClockTime = 120;
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
        WeakReference<CompletePhoneConfActivity> mWeakReference;
        CompletePhoneConfActivity mActivity;

        public SmsCodeHandler(CompletePhoneConfActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            mActivity = mWeakReference.get();
            mActivity.tvSend.setText(mActivity.mClockTime + "S");
            mActivity.mClockTime--;
            if (mActivity.mClockTime < 0) {
                mActivity.mTimer.cancel();
                mActivity.mTimer = null;
                mActivity.tvSend.setText(mActivity.getResources().getString(R.string.reg_send_code));
                mActivity.tvSend.setEnabled(true);
                mActivity.tvTime.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
