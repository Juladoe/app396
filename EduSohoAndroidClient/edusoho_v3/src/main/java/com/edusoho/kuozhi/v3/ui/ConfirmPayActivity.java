package com.edusoho.kuozhi.v3.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.ActivityUtil;

/**
 * Created by tree on 2017/4/30.
 */

public class ConfirmPayActivity extends ActionBarBaseActivity {

    private Button mBtnConfirmPay;
    private CheckBox mMobilePay;
    private ImageView mIvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pay);
        ActivityUtil.setStatusViewBackgroud(this, Color.BLACK);
        initView();
    }

    private void initView() {
        mBtnConfirmPay = (Button) findViewById(R.id.btn_confirm_pay);
        mBtnConfirmPay.setOnClickListener(mConfirmClickListener);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(mBackClickListener);
        mMobilePay = (CheckBox) findViewById(R.id.cb_mobile_pay);
        mMobilePay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mBtnConfirmPay.setAlpha(1f);
                    mBtnConfirmPay.setClickable(true);
                } else {
                    mBtnConfirmPay.setAlpha(0.6f);
                    mBtnConfirmPay.setClickable(false);
                }
            }
        });

    }

    private View.OnClickListener mConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.app.mEngine.runNormalPlugin("VipOrderActivity", mContext, null);
        }
    };

    private View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ConfirmPayActivity.this.finish();
        }
    };
}
