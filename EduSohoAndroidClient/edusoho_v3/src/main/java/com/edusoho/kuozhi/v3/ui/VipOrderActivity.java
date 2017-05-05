package com.edusoho.kuozhi.v3.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.edusoho.kuozhi.v3.util.Validator;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tree on 2017/4/30.
 */

public class VipOrderActivity extends ActionBarBaseActivity {

    private ImageView mIvBack;
    private ImageView mIvClearPhone;
    private ImageView mIvClearCode;
    private TextView mTvGetCode;
    private TextView mTvSend;
    private TextView mTvTime;
    private Button mBtnSubmit;
    private EditText mEtPhoneNum;
    private EditText mEtPhoneCode;
    private LinearLayout mLlGetCodeAgain;

    private int mClockTime;
    private Timer mTimer;
    private CountDownHandler mCountDownHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_order);
        ActivityUtil.setStatusViewBackgroud(this, Color.BLACK);
        initView();
    }

    private void initView() {
        mLlGetCodeAgain = (LinearLayout) findViewById(R.id.ll_get_code);
        mTvSend = (TextView) findViewById(R.id.tv_send);
        mTvSend.setOnClickListener(mCodeClickListener);
        mTvTime = (TextView) findViewById(R.id.tv_show_time);
        mEtPhoneNum = (EditText) findViewById(R.id.et_phone_num);
        mEtPhoneCode = (EditText) findViewById(R.id.et_phone_code);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(mSubmitClickListener);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(mBackClickListener);
        mTvGetCode = (TextView) findViewById(R.id.tv_get_code);
        mTvGetCode.setOnClickListener(mCodeClickListener);
        mIvClearPhone = (ImageView) findViewById(R.id.iv_clear_phone);
        mIvClearPhone.setOnClickListener(mClearContentListener);
        mIvClearCode = (ImageView) findViewById(R.id.iv_clear_code);
        mIvClearCode.setOnClickListener(mClearContentListener);
        mCountDownHandler = new CountDownHandler(this);

        initTextChange();
        InputUtils.showKeyBoard(mEtPhoneNum, mContext);
    }

    private View.OnClickListener mCodeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mEtPhoneNum.length() == 0){
                return;
            }
            if("".equals(mEtPhoneNum.getText().toString().trim())){
                CommonUtil.longToast(mContext, getString(R.string.complete_phone_empty));
                return;
            }
            String phoneNum = mEtPhoneNum.getText().toString().trim();
            if(Validator.isPhone(phoneNum)){
                //进行网络请求得到验证码，成功后执行倒计时

                countDown();
            } else {
                CommonUtil.longToast(mContext, getString(R.string.register_error));
            }

        }
    };

    private void countDown(){
        mTvGetCode.setVisibility(View.GONE);
        mLlGetCodeAgain.setVisibility(View.VISIBLE);
        mTvTime.setVisibility(View.VISIBLE);
        mTvSend.setEnabled(false);
        mClockTime = 10;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = mCountDownHandler.obtainMessage();
                message.what = 0;
                mCountDownHandler.sendMessage(message);
            }
        }, 0, 1000);
    }

    private void initTextChange(){
        InputUtils.addTextChangedListener(mEtPhoneNum, new NormalCallback<Editable>() {
            @Override
            public void success(Editable obj) {
                if(mEtPhoneNum.length() == 0){
                    mIvClearPhone.setVisibility(View.INVISIBLE);
                } else {
                    mIvClearPhone.setVisibility(View.VISIBLE);
                }
            }
        });

        InputUtils.addTextChangedListener(mEtPhoneCode, new NormalCallback<Editable>() {
            @Override
            public void success(Editable obj) {
                if(mEtPhoneCode.length() == 0){
                    mIvClearCode.setVisibility(View.INVISIBLE);
                } else {
                    mIvClearCode.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private View.OnClickListener mClearContentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.iv_clear_phone){
                mEtPhoneNum.setText("");
            } else {
                mEtPhoneCode.setText("");
            }
        }
    };


    private View.OnClickListener mSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mEtPhoneCode.length() == 0){
                return;
            }
            if("".equals(mEtPhoneCode.getText().toString().trim())){
                CommonUtil.longToast(mContext, getString(R.string.complete_msgcode_empty));
                return;
            }
            String msgCode = mEtPhoneCode.getText().toString().trim();
            if(msgCode.equals("123456")){
                mActivity.app.mEngine.runNormalPlugin("VipPaySuccessActivity", mContext, null);
            } else {
                CommonUtil.longToast(mContext, getString(R.string.msgcode_error));
            }
        }
    };

    private View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VipOrderActivity.this.finish();
        }
    };


    public static class CountDownHandler extends Handler{
        WeakReference<VipOrderActivity> mWeakReference;
        VipOrderActivity mActivity;

        public CountDownHandler(VipOrderActivity activity){
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            mActivity = mWeakReference.get();
            mActivity.mTvSend.setText(mActivity.mClockTime + "s");
            mActivity.mTvSend.setTextColor(mActivity.getResources().getColor(R.color.primary_color));
            mActivity.mClockTime--;
            if(mActivity.mClockTime < 0){
                mActivity.mTimer.cancel();
                mActivity.mTimer = null;
                mActivity.mTvSend.setText(mActivity.getResources().getString(R.string.reg_send_code));
                mActivity.mTvSend.setEnabled(true);
                mActivity.mTvTime.setVisibility(View.GONE);
            }
        }
    }
}
