package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.edusoho.kuozhi.v3.view.EduSohoLoadingButton;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by DF on 2016/11/28.
 */

public class CompletePhoneActivity extends ActionBarBaseActivity {
    private EditText etAccount;
    private EduSohoLoadingButton btnNext;
    private ImageView ivBack;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setContentView(R.layout.activity_complete_phone);
        initView();
    }

    private void initView() {
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvInfo.setText(R.string.register_complete_info);
        etAccount = (EditText) findViewById(R.id.et_phone_num);
        btnNext = (EduSohoLoadingButton) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(nextClickListener);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(mBackClickListener);
    }

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CompletePhoneActivity.this.finish();
        }
    };

    private View.OnClickListener nextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String phoneNum = etAccount.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNum)) {
                CommonUtil.shortCenterToast(mContext,getString(R.string.reg_phone_hint));
            } else if (isPhone(phoneNum)) {
                RequestUrl requestUrl = app.bindUrl(Const.COMPLETE, false);
                HashMap<String, String> params = (HashMap<String, String>) requestUrl.getParams();
                params.put("mobile", String.valueOf(phoneNum));
                params.put("type", "sms_verify_mobile");
                mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MsgCode result = parseJsonValue(response, new TypeToken<MsgCode>() {
                            });
                            if (result != null && result.code == 200) {
                                startActivity(new Intent(CompletePhoneActivity.this,CompletePhoneConfActivity.class).
                                        putExtra("phoneNum",phoneNum));
                            } else {
                                CommonUtil.shortCenterToast(CompletePhoneActivity.this,getString(R.string.complete_info_text));
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "phone reg error");
                        }
                    }
                }, null);
            } else {
                CommonUtil.shortCenterToast(mContext, getString(R.string.register_phone_error));
            }
        }
    };

    /**
     * 判断是否为手机号
     */
    private boolean isPhone(String str) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }
}

