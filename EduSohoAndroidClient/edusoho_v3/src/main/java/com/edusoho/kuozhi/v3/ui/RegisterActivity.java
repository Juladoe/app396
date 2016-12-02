package com.edusoho.kuozhi.v3.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.register.MsgCode;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DF on 2016/11/28.
 */

public class RegisterActivity extends ActionBarBaseActivity {
    private EditText etAccount;
    private Button btnNext;
    private ImageView ivBack;
    private TextView tvInfo;
    private ImageView ivClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        hideActionBar();
        initView();
    }

    private void initView() {
        tvInfo = (TextView) findViewById(R.id.tv_info);
        etAccount = (EditText) findViewById(R.id.et_phone_num);
        etAccount.addTextChangedListener(mTextChangeListener);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(nextClickListener);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(mBackClickListener);
        ivClear = (ImageView) findViewById(R.id.iv_clear_num);
        ivClear.setOnClickListener(mClearClickListener);
    }

    TextWatcher mTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                btnNext.setEnabled(true);
            }else {
                btnNext.setEnabled(false);
            }
        }
    };

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RegisterActivity.this.finish();
        }
    };

    View.OnClickListener mClearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            etAccount.setText("");
        }
    };

    private View.OnClickListener nextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String st = etAccount.getText().toString().trim();
            if (isPhone(st)) {
                RequestUrl requestUrl = app.bindUrl(Const.SMS_SEND, false);
                HashMap<String, String> params = requestUrl.getParams();
                params.put("phoneNumber", String.valueOf(st));
                mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MsgCode result = parseJsonValue(response, new TypeToken<MsgCode>() {
                            });
                            if (result != null && result.code == 200) {
                                Intent registerIntent = new Intent(mContext,RegisterConfirmActivity.class);
                                registerIntent.putExtra("num", st);
                                startActivity(registerIntent);
                            } else {
                                if (response.equals("该手机号码已被其他用户绑定")) {
                                    showDialog();
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "phone reg error");
                        }
                    }
                }, null);
            } else {
                CommonUtil.longToast(mContext, "你输入的手机号格式有误");
            }
            app.mEngine.runNormalPlugin("DefaultPageActivity", mContext, null, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            app.sendMessage(Const.LOGIN_SUCCESS, null);
            RegisterActivity.this.finish();
        }
    };

    /**
     * 弹窗提示已注册
     */
    private void showDialog() {
        final Dialog dialog = new Dialog(this, R.style.RegisterDialogStyle);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_register, null);
        dialog.setContentView(view);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                CommonUtil.longToast(mContext,"请更换手机号码");
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                RegisterActivity.this.finish();
            }
        });
        dialog.show();
    }

    /**
     * 判断是否为手机号
     */
    private boolean isPhone(String str) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
