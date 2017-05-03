package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by tree on 2017/4/30.
 */

public class ConfirmPayActivity extends ActionBarBaseActivity {

    private Button mBtnConfirmPay;
    private ImageView mIvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pay);
        initView();
    }

    private void initView() {
        mBtnConfirmPay = (Button) findViewById(R.id.btn_confirm_pay);
        mBtnConfirmPay.setOnClickListener(mConfirmClickListener);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(mBackClickListener);
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
