package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.util.Map;


/**
 * Created by DF on 2016/11/28.
 */

public class CompletePhoneActivity extends ActionBarBaseActivity {
    private EditText etPhone;
    private TextView tvNext;
    private ImageView ivBack;
    private TextView tvInfo;
    private ImageView ivClearPhone;
    private RelativeLayout rl;
    private EditText etCode;
    private ImageView ivGraph;
    private TextView tvGraph;
    private Bundle bundle;
    private String verified;
    private ImageView ivClearCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setContentView(R.layout.activity_complete_phone);
        initView();
        initGraphContent();

    }

    private void initGraphContent() {
        rl = (RelativeLayout) findViewById(R.id.rl_graphic);
        etCode = (EditText) findViewById(R.id.et_graphic_code);
        ivGraph = (ImageView) findViewById(R.id.iv_graphic);
        tvGraph = (TextView) findViewById(R.id.tv_change);
        ivClearCode = (ImageView) findViewById(R.id.iv_clear_code);
        ivClearCode.setOnClickListener(mClearListener);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            String img_code = bundle.getString("img_code");
            verified = bundle.getString("verified_token");
            rl.setVisibility(View.VISIBLE);
            byte[] byteArray = Base64.decode(img_code, Base64.DEFAULT);
            ivGraph.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
        }
        tvGraph.setOnClickListener(mChangListener);
        InputUtils.addTextChangedListener(etCode, new NormalCallback<Editable>() {
            @Override
            public void success(Editable obj) {
                if (etCode.length() == 0) {
                    ivClearCode.setVisibility(View.INVISIBLE);
                }else {
                    ivClearCode.setVisibility(View.VISIBLE);
                }
                if (etCode.length() == 0 || etPhone.length() == 0) {
                    tvNext.setAlpha(0.6f);
                } else {
                    tvNext.setAlpha(1.0f);
                }
            }
        });
    }

    View.OnClickListener mChangListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl requestUrl = app.bindNewUrl(Const.COMPLETE, true);
            Map<String, String> params =  requestUrl.getParams();
            params.put("mobile", etPhone.getText().toString().trim());
            params.put("type", "sms_bind");
            mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    MsgCode result = parseJsonValue(response, new TypeToken<MsgCode>() {
                    });
                    byte[] byteArray = Base64.decode(result.img_code, Base64.DEFAULT);
                    ivGraph.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
                }
            },null);
        }
    };

    private void initView() {
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvInfo.setText(R.string.complete_info);
        etPhone = (EditText) findViewById(R.id.et_phone_num);
        tvNext = (TextView) findViewById(R.id.tv_next);
        tvNext.setOnClickListener(nextClickListener);
        ivClearPhone = (ImageView) findViewById(R.id.iv_clear_phone);
        ivClearPhone.setOnClickListener(mClearListener);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(mBackClickListener);

        InputUtils.showKeyBoard(etPhone, this);
        InputUtils.addTextChangedListener(etPhone, new NormalCallback<Editable>() {
            @Override
            public void success(Editable obj) {
                if (etPhone.length() == 0) {
                    ivClearPhone.setVisibility(View.INVISIBLE);
                }else {
                    ivClearPhone.setVisibility(View.VISIBLE);
                }
                if (bundle != null) {
                    if (etPhone.length() == 0 || etPhone.length() == 0) {
                        tvNext.setAlpha(0.6f);
                    } else {
                        tvNext.setAlpha(1.0f);
                    }
                }else {
                    if (etPhone.length() == 0) {
                        tvNext.setAlpha(0.6f);
                    }else {
                        tvNext.setAlpha(1.0f);
                    }
                }
            }
        });
    }


    View.OnClickListener mClearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.iv_clear_phone) {
                etPhone.setText("");
            }else if(v.getId() == R.id.iv_clear_code){
                etCode.setText("");
            }
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
            if (etPhone.length() == 0) {
                return;
            }
            if ("".equals(etPhone.getText().toString().trim())) {
                CommonUtil.longToast(mContext, getString(R.string.complete_phone_empty));
                return;
            }
            if (etCode.length() == 0 && bundle != null) {
                return;
            }
            if ("".equals(etCode.getText().toString().trim()) && bundle != null) {
                CommonUtil.longToast(mContext,getString(R.string.img_code_hint));
                return;
            }
            final String phoneNum = etPhone.getText().toString().trim();
            if (Validator.isPhone(phoneNum)) {
                if (bundle!=null) {
                    RequestUrl requestUrl = app.bindNewUrl(Const.COMPLETE, false);
                    Map<String, String> params = requestUrl.getParams();
                    params.put("mobile", phoneNum);
                    String img_code = etCode.getText().toString().trim();
                    params.put("img_code", img_code);
                    params.put("type", "sms_change_password");
                    params.put("verified_token",verified);
                    app.postUrl(requestUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            MsgCode result = parseJsonValue(response, new TypeToken<MsgCode>() {});
                            jumpNext(response, phoneNum, null);
                        }
                    },null);
                }else {
                    RequestUrl requestUrl = app.bindNewUrl(Const.COMPLETE, true);
                    Map<String, String> params = requestUrl.getParams();
                    params.put("mobile", phoneNum);
                    params.put("type", "sms_bind");
                    app.postUrl(requestUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                MsgCode result = parseJsonValue(response, new TypeToken<MsgCode>() {
                                });
                                if ("limited".equals(result.status)) {
                                    bundle = new Bundle();
                                    byte[] byteArray = Base64.decode(result.img_code, Base64.DEFAULT);
                                    ivGraph.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
                                    verified = result.verified_token;
                                    rl.setVisibility(View.VISIBLE);
                                }
                                jumpNext(response, phoneNum, null);
                            } catch (Exception e) {
                                Log.d(TAG, "phone reg error");
                            }
                        }
                    }, null);
                }
            } else {
                CommonUtil.shortCenterToast(mContext, getString(R.string.phone_error));
            }
        }
    };
    /**
     * 跳转界面
     */
    private void jumpNext(String response, String phoneNum,MsgCode result) {
        if (result != null) {
            verified = result.verified_token;
        }
        if (result != null && result.code == 200) {
            startActivity(new Intent(CompletePhoneActivity.this, CompletePhoneConfActivity.class).
                    putExtra("phoneNum", phoneNum).putExtra("verified_token", result.verified_token));
        } else {
            if (response.equals(getString(R.string.register_hint))) {
                CommonUtil.shortCenterToast(CompletePhoneActivity.this, getString(R.string.complete_info_registered));
            } else {
                CommonUtil.longToast(mContext, response);
            }
        }
    }
}

