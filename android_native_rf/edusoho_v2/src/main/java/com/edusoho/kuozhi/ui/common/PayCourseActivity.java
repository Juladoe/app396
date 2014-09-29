package com.edusoho.kuozhi.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.course.CourseCode;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by howzhi on 14-9-4.
 */
public class PayCourseActivity extends ActionBarBaseActivity {

    private TextView mPriceView;
    private TextView mTitleView;
    private TextView mCardNumber;
    private TextView mCardEndPrice;
    private View mCodeCheckBtn;
    private EditText mCodeView;

    private String mTitle;
    private String mCourseId;
    private double mPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_course);
        initView();
    }

    private void initView()
    {
        Intent data = getIntent();
        mTitle = data.getStringExtra("title");
        mCourseId = data.getStringExtra("courseId");
        mPrice = data.getDoubleExtra("price", 0.0);

        setBackMode(BACK, "购买课程");

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
    }

    private void checkCode(String code)
    {
        RequestUrl requestUrl = app.bindUrl(Const.COURSE_CODE, false);
        requestUrl.setParams(new String[] {
                Const.COURSE_ID,  mCourseId,
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
                                result.decreaseAmount + "元",
                                getResources().getColor(R.color.pay_course_end_price)
                        );
                        double newPrice = mPrice - result.decreaseAmount > 0
                                ? mPrice - result.decreaseAmount : 0;
                        setColorText(
                                mCardEndPrice,
                                newPrice + "元",
                                getResources().getColor(R.color.pay_course_end_price)
                        );
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
        setColorText(mPriceView, mPrice + "元", getResources().getColor(R.color.pay_course_old_price));
        setColorText(mCardEndPrice, mPrice + "元", getResources().getColor(R.color.pay_course_end_price));
        setColorText(mCardNumber, "0.00元", getResources().getColor(R.color.pay_course_end_price));
        mTitleView.setText("课程名称：  " + mTitle);
    }

    private void setColorText(TextView view, String text, int color)
    {
        StringBuilder oldText = new StringBuilder(view.getText());
        int start = oldText.length();
        oldText.append(text);
        Spannable spannable = new SpannableString(oldText);
        spannable.setSpan(
                new ForegroundColorSpan(color), start, oldText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        view.setText(spannable);
    }
}
