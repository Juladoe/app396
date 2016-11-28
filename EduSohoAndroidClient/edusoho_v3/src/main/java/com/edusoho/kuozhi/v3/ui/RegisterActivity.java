package com.edusoho.kuozhi.v3.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.register.MsgCode;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoLoadingButton;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DF on 2016/11/28.
 */

public class RegisterActivity extends ActionBarBaseActivity {
    private EditText etAccount;
    private EduSohoLoadingButton btnNext;
    private ImageView ivBack;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        setContentView(R.layout.activity_register);
        setBackMode(BACK, "");
        hideActionBar();
        initView();
    }

    private void initView() {
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvInfo.setText("完善信息");
        etAccount = (EditText) findViewById(R.id.et_phone_num);
        btnNext = (EduSohoLoadingButton) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(nextClickListener);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(mBackClickListener);
    }

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RegisterActivity.this.finish();
        }
    };

    private View.OnClickListener nextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String st = etAccount.getText().toString().trim();
            if (TextUtils.isEmpty(st)) {
                CommonUtil.longToast(mContext, "请输入手机号");
            } else if (isPhone(st)) {
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
                app.mEngine.runNormalPlugin("LoginActivity", mContext, null, Intent.FLAG_ACTIVITY_CLEAR_TOP);

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
}
