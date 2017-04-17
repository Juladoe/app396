package com.edusoho.kuozhi.clean.module.courseset.payments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.courseset.BaseFinishActivity;
import com.edusoho.kuozhi.clean.module.courseset.alipay.AlipayActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

/**
 * Created by DF on 2017/4/7.
 */

public class PaymentsActivity extends BaseFinishActivity implements View.OnClickListener, PaymentsContract.View {

    private static final String ORDER_INFO = "order_info";
    private static final String ORDER_PRICE = "order_price";
    private static final String COUPON_POSITION_IN_COUPONS = "position";

    private View mBack;
    private View mAlipay;
    private TextView mVirtualCoin;
    private TextView mDiscount;
    private TextView mBalance;
    private TextView mAvailableName;
    private View mPay;
    private Dialog mDialog;
    private LoadDialog mProcessDialog;
    private EditText mInputPw;

    private PaymentsContract.Presenter mPresenter;

    private OrderInfo mOrderInfo;
    private float mOrderPrice;
    private int mPosition;

    public static void launch(Context context, OrderInfo orderInfo, float price, int position) {
        Intent intent = new Intent(context, PaymentsActivity.class);
        intent.putExtra(ORDER_INFO, orderInfo);
        intent.putExtra(ORDER_PRICE, price);
        intent.putExtra(COUPON_POSITION_IN_COUPONS, position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_way);
        mOrderInfo = (OrderInfo) getIntent().getSerializableExtra(ORDER_INFO);
        mOrderPrice = getIntent().getFloatExtra(ORDER_PRICE, 0);
        mPosition = getIntent().getIntExtra(COUPON_POSITION_IN_COUPONS, -1);

        initView();
        initEvent();
        initShow();
    }

    private void initView() {
        mBack = findViewById(R.id.iv_back);
        mAlipay = findViewById(R.id.iv_alipay);
        mVirtualCoin = (TextView) findViewById(R.id.tv_virtual_coin);
        mDiscount = (TextView) findViewById(R.id.tv_discount);
        mBalance = (TextView) findViewById(R.id.tv_available_balance);
        mAvailableName = (TextView) findViewById(R.id.tv_available_name);
        mPay = findViewById(R.id.tv_pay);

        mPresenter = new PaymentsPresenter(this, mOrderInfo, mPosition);
        mPresenter.subscribe();
    }

    private void initEvent() {
        mBack.setOnClickListener(this);
        mAlipay.setOnClickListener(this);
        mAlipay.setSelected(true);
        mVirtualCoin.setOnClickListener(this);
        mPay.setOnClickListener(this);
    }

    private void initShow() {
        mVirtualCoin.setText(mOrderInfo.coinName.length() != 0 ? mOrderInfo.coinName : getString(R.string.virtual_coin_pay));
        mBalance.setText(String.format("%.2f", mOrderInfo.account.cash));
        mAvailableName.setText(String.format(getString(R.string.available_balance),
                mOrderInfo.coinName.length() != 0 ? mOrderInfo.coinName : getString(R.string.virtual_coin)));
        mDiscount.setText(String.format(getString(R.string.yuan), mOrderPrice));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.iv_alipay) {
            clickAlipay();
        } else if (id == R.id.tv_virtual_coin) {
            clickVirtual();
        } else if (id == R.id.tv_pay) {
            goPay();
        }
    }

    private void clickAlipay() {
        mAlipay.setSelected(true);
        mVirtualCoin.setSelected(false);
        mBalance.setText(String.format("%.2f", mOrderInfo.account.cash));
    }

    private void clickVirtual() {
        mAlipay.setSelected(false);
        mVirtualCoin.setSelected(true);
        if (mOrderPrice > mOrderInfo.account.cash) {
            mBalance.setText(R.string.insufficient_balance);
        }
    }

    private void goPay() {
        if (mAlipay.isSelected()) {
            showProcessDialog();
            mPresenter.createOrderAndPay(PaymentsPresenter.ALIPAY, null, -1);
        } else {
            if (mOrderPrice > mOrderInfo.account.cash) {
                CommonUtil.shortToast(this, getString(R.string.insufficient_balance));
                return;
            }
            showDialog();
        }
    }

    private void showDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(this, R.style.dialog_custom);
            mDialog.setContentView(R.layout.dialog_input_pay_pw);
            mDialog.setCanceledOnTouchOutside(true);
            Window window = mDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = getResources().getDisplayMetrics().widthPixels;
            window.setGravity(Gravity.BOTTOM);
            window.setAttributes(lp);
            mInputPw = (EditText) mDialog.findViewById(R.id.et_input_pw);
            mInputPw.setOnEditorActionListener(getOnEditorActionListener());
        }
        InputUtils.showKeyBoard(mInputPw, this);
        mDialog.show();
    }

    @NonNull
    private TextView.OnEditorActionListener getOnEditorActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String pw = mInputPw.getText().toString().trim();
                if (pw.length() < 6) {
                    CommonUtil.shortToast(PaymentsActivity.this, "密码长度有误");
                    return true;
                }
                showProcessDialog();
                mPresenter.createOrderAndPay(PaymentsPresenter.COIN, mInputPw.getText().toString().trim(), mOrderPrice);
                mDialog.dismiss();
                return true;
            }
        };
    }

    @Override
    public void goToAlipay(final String data) {
        AlipayActivity.launch(this, data);
    }

    protected void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
        }
        mProcessDialog.show();
    }

    protected void hideProcesDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    @Override
    public void showLoadDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcesDialog();
        }
    }

    @Override
    public void sendBroad(){
        Intent intent = new Intent();
        intent.setAction("Finish");
        sendBroadcast(intent);
        CourseProjectActivity.launch(this, mOrderInfo.targetId);
        finish();
    }
}
