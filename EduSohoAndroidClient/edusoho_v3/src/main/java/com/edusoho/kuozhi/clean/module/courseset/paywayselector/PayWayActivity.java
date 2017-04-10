package com.edusoho.kuozhi.clean.module.courseset.paywayselector;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

/**
 * Created by DF on 2017/4/7.
 */

public class PayWayActivity extends AppCompatActivity implements View.OnClickListener,PayWayContract.View {

    public static final String PLANID = "plan_id";

    private View mBack;
    private View mZhiFuBao;
    private View mVirtualCoin;
    private TextView mDiscount;
    private TextView mOriginal;
    private View mPay;
    private Dialog mDialog;
    private LoadDialog mProcessDialog;
    private PayWayContract.Presenter mPresenter;
    private EditText mInputPw;
    private int mPlanId;

    public static void newInstance(Context context, int id) {
        Intent intent = new Intent(context, PayWayActivity.class);
        intent.putExtra(PLANID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_way);
        mPlanId = getIntent().getIntExtra(PLANID, 0);

        initView();
        initEvent();
        initShwo();
    }

    private void initView() {
        mBack = findViewById(R.id.iv_back);
        mZhiFuBao = findViewById(R.id.iv_zhifubao);
        mVirtualCoin = findViewById(R.id.tv_virtual_coin);
        mDiscount = (TextView) findViewById(R.id.tv_discount);
        mOriginal = (TextView) findViewById(R.id.tv_original);
        mPay = findViewById(R.id.tv_pay);

        mPresenter = new PayWayPresenter(this, mPlanId);
    }

    private void initEvent() {
        mBack.setOnClickListener(this);
        mZhiFuBao.setOnClickListener(this);
        mVirtualCoin.setOnClickListener(this);
        mPay.setOnClickListener(this);
    }

    private void initShwo() {
        mOriginal.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.iv_zhifubao) {
            mZhiFuBao.setSelected(true);
            mVirtualCoin.setSelected(false);
        } else if (id == R.id.tv_virtual_coin) {
            mZhiFuBao.setSelected(false);
            mVirtualCoin.setSelected(true);
        } else if (id == R.id.tv_pay) {
            goPay();
        }
    }

    private void goPay() {
        if (!mZhiFuBao.isSelected() && !mVirtualCoin.isSelected()) {
            Toast.makeText(this, "请选择支付方式", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mZhiFuBao.isSelected()) {

        } else {
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
            mInputPw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    showProcessDialog();
                    mDialog.dismiss();
                    return true;
                }
            });
        }
        InputUtils.showKeyBoard(mInputPw, this);
        mDialog.show();
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
}
