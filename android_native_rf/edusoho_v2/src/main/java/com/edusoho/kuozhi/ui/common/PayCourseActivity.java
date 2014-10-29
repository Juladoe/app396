package com.edusoho.kuozhi.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.PayStatus;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.model.course.CourseCode;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14-9-4.
 */
public class PayCourseActivity extends ActionBarBaseActivity
        implements MessageEngine.MessageCallback{

    private TextView mPriceView;
    private TextView mTitleView;
    private TextView mCardNumber;
    private TextView mCardEndPrice;
    private View mCodeCheckBtn;
    private View mPayBtn;
    private EditText mCodeView;

    private String mTitle;
    private int mCourseId;
    private double mPrice;

    public static final int PAY_SUCCESS = 001;
    public static final int PAY_EXIT = 002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_course);
        app.registMsgSource(this);
        initView();
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        switch (type) {
            case PAY_SUCCESS:
                Log.d(null, "pay->success");
                longToast("支付完成");
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                setResult(CourseDetailsActivity.PAY_COURSE_SUCCESS);
                finish();
                break;
            case PAY_EXIT:
                Log.d(null, "pay->exit");
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(PAY_SUCCESS, source),
                new MessageType(PAY_EXIT, source)
        };
        return messageTypes;
    }

    private void initView()
    {
        Intent data = getIntent();
        mTitle = data.getStringExtra("title");
        mCourseId = data.getIntExtra("courseId", 0);
        mPrice = data.getDoubleExtra("price", 0.0);

        setBackMode(BACK, "购买课程");

        mPayBtn = findViewById(R.id.pay_course_pay_btn);
        mTitleView = (TextView) findViewById(R.id.pay_course_title);
        mPriceView = (TextView) findViewById(R.id.pay_course_price);
        mCardNumber = (TextView) findViewById(R.id.pay_course_card_number);
        mCodeCheckBtn = findViewById(R.id.pay_course_checkcard_btn);
        mCodeView = (EditText) findViewById(R.id.pay_course_card_edt);
        mCardEndPrice = (TextView) findViewById(R.id.pay_course_card_end_price);

        setViewData();

        mCodeCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mCodeView.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    longToast("请输入优惠码！");
                    return;
                }
                checkCode(code);
            }
        });

        mPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LoadDialog loadDialog = LoadDialog.create(mActivity);
                loadDialog.show();

                RequestUrl url = app.bindUrl(Const.PAYCOURSE, true);
                url.setParams(new String[]{
                        "payment", "alipay",
                        "courseId", mCourseId + ""
                });
                ajaxPost(url, new ResultCallback(){
                    @Override
                    public void callback(String url, String object, AjaxStatus ajaxStatus) {
                        loadDialog.dismiss();
                        final PayStatus payStatus = parseJsonValue(
                                object, new TypeToken<PayStatus>() {
                        });

                        if (payStatus == null) {
                            longToast("购买课程失败！！");
                            return;
                        }
                        app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AlipayFragment");
                                startIntent.putExtra(Const.ACTIONBAT_TITLE, "支付课程-" + mTitle);
                                startIntent.putExtra("payurl", payStatus.payUrl);
                            }
                        });
                    }
                });

            }
        });
    }

    private void checkCode(String code)
    {
        RequestUrl requestUrl = app.bindUrl(Const.COURSE_CODE, false);
        requestUrl.setParams(new String[] {
                Const.COURSE_ID,  mCourseId + "",
                "type", "course",
                "code", code
        });
        setProgressBarIndeterminateVisibility(true);
        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
                CourseCode result = parseJsonValue(
                        object, new TypeToken<CourseCode>(){});
                if (result != null) {
                    if (result.useable == CourseCode.Code.yes) {
                        setColorText(
                                mCardNumber,
                                "优惠:",
                                result.decreaseAmount + "元",
                                getResources().getColor(R.color.pay_course_end_price)
                        );
                        double newPrice = mPrice - result.decreaseAmount > 0
                                ? mPrice - result.decreaseAmount : 0;
                        mPrice = newPrice;
                        setColorText(
                                mCardEndPrice,
                                "优惠后价格:",
                                newPrice + "元",
                                getResources().getColor(R.color.pay_course_end_price)
                        );
                        mCodeCheckBtn.setEnabled(false);
                        longToast("优惠:" + result.decreaseAmount);
                    } else {
                        longToast(result.message);
                    }
                } else {
                    longToast("验证错误！");
                }
            }
        });
    }

    private void setViewData()
    {
        setColorText(mPriceView, "价格:", mPrice + "元", getResources().getColor(R.color.pay_course_old_price));
        setColorText(mCardEndPrice, "优惠后价格:", mPrice + "元", getResources().getColor(R.color.pay_course_end_price));
        setColorText(mCardNumber, "优惠:", "0.00元", getResources().getColor(R.color.pay_course_end_price));
        mTitleView.setText("课程名称：  " + mTitle);
    }

    private void setColorText(TextView view, String base, String text, int color)
    {
        StringBuilder oldText = new StringBuilder(base);
        int start = oldText.length();
        oldText.append(text);
        Spannable spannable = new SpannableString(oldText);
        spannable.setSpan(
                new ForegroundColorSpan(color), start, oldText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        view.setText(spannable);
    }
}
