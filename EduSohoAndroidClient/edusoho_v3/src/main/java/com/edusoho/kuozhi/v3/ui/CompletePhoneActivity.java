package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.register.MsgCode;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.edusoho.kuozhi.v3.util.Validator;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;


/**
 * Created by DF on 2016/11/28.
 */

public class CompletePhoneActivity extends ActionBarBaseActivity {
    private EditText etAccount;
    private TextView tvNext;
    private ImageView ivBack;
    private TextView tvInfo;
    private ImageView ivClearPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setContentView(R.layout.activity_complete_phone);
        initView();
    }

    private void initView() {
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvInfo.setText(R.string.complete_info);
        etAccount = (EditText) findViewById(R.id.et_phone_num);
        tvNext = (TextView) findViewById(R.id.tv_next);
        tvNext.setOnClickListener(nextClickListener);
        ivClearPhone = (ImageView) findViewById(R.id.iv_clear_phone);
        ivClearPhone.setOnClickListener(mClearListener);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(mBackClickListener);

        InputUtils.showKeyBoard(etAccount,this);
        InputUtils.addTextChangedListener(etAccount, new NormalCallback<Editable>() {
            @Override
            public void success(Editable obj) {
                if (etAccount.length() > 0) {
                    tvNext.setAlpha(1f);
                    ivClearPhone.setVisibility(View.VISIBLE);
                }else {
                    tvNext.setAlpha(0.6f);
                    ivClearPhone.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    View.OnClickListener mClearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            etAccount.setText("");
        }
    };

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CompletePhoneActivity.this.finish();
        }
    };

    private View.OnClickListener nextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (etAccount.length() == 0) {
                return;
            }
            if ("".equals(etAccount.getText().toString().trim())) {
                CommonUtil.longToast(mContext, getString(R.string.complete_phone_empty));
                return;
            }
            final String phoneNum = etAccount.getText().toString().trim();
            if (Validator.isPhone(phoneNum)) {
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
                                if (response.equals(getString(R.string.register_hint))) {
                                    CommonUtil.shortCenterToast(CompletePhoneActivity.this,getString(R.string.complete_info_registered));
                                }else{
                                    CommonUtil.longToast(mContext, response);
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "phone reg error");
                        }
                    }
                }, null);
            } else {
                CommonUtil.shortCenterToast(mContext, getString(R.string.phone_error));
            }
        }
    };

}

