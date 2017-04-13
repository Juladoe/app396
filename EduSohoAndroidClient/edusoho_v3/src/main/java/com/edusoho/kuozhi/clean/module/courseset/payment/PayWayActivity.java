package com.edusoho.kuozhi.clean.module.courseset.payment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.courseset.BaseFinishActivity;
import com.edusoho.kuozhi.clean.module.courseset.alipay.AlipayActivity;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by DF on 2017/4/7.
 */

public class PayWayActivity extends BaseFinishActivity implements View.OnClickListener, PayWayContract.View {

    public static final String STUDY_PLAN = "study_plan";

    private View mBack;
    private View mAlipay;
    private View mVirtualCoin;
    private TextView mDiscount;
    private View mPay;
    private Dialog mDialog;
    private LoadDialog mProcessDialog;
    private PayWayContract.Presenter mPresenter;
    private EditText mInputPw;
    private CourseProject mCourseStudyPlan;

    public static void newInstance(Context context, CourseProject courseStudyPlan) {
        Intent intent = new Intent(context, PayWayActivity.class);
        intent.putExtra(STUDY_PLAN, courseStudyPlan);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_way);
        mCourseStudyPlan = (CourseProject) getIntent().getSerializableExtra(STUDY_PLAN);

        initView();
        initEvent();
        initShwo();
    }

    private void initView() {
        mBack = findViewById(R.id.iv_back);
        mAlipay = findViewById(R.id.iv_alipay);
        mVirtualCoin = findViewById(R.id.tv_virtual_coin);
        mDiscount = (TextView) findViewById(R.id.tv_discount);
        mPay = findViewById(R.id.tv_pay);

        mPresenter = new PayWayPresenter(this, mCourseStudyPlan.id);
        mPresenter.subscribe();
    }

    private void initEvent() {
        mBack.setOnClickListener(this);
        mAlipay.setOnClickListener(this);
        mVirtualCoin.setOnClickListener(this);
        mPay.setOnClickListener(this);
    }

    private void initShwo() {
        mDiscount.setText(String.format(getString(R.string.yuan), mCourseStudyPlan.price));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.iv_alipay) {
            mAlipay.setSelected(true);
            mVirtualCoin.setSelected(false);
        } else if (id == R.id.tv_virtual_coin) {
            mAlipay.setSelected(false);
            mVirtualCoin.setSelected(true);
        } else if (id == R.id.tv_pay) {
            goPay();
        }
    }

    private void goPay() {
        if (!mAlipay.isSelected() && !mVirtualCoin.isSelected()) {
            Toast.makeText(this, "请选择支付方式", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAlipay.isSelected()) {
            goAlipay();
        } else {
            showDialog();
        }
    }

    private void goAlipay() {
        Map<String, String> map = new TreeMap<>();
        map.put("targetType", "course");
//        map.put("couponCode", "");
//        map.put("coinPayAmount", "");
//        map.put("payPassword", "");
        mPresenter.createOrderAndPay(map, "course", "alipay");
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

    @Override
    public void goToAlipay(final String data) {
        AlipayActivity.newInstance(this, data);
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
}
