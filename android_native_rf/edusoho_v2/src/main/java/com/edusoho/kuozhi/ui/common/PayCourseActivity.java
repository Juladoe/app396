package com.edusoho.kuozhi.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

/**
 * Created by howzhi on 14-9-4.
 */
public class PayCourseActivity extends ActionBarBaseActivity {

    private TextView mPriceView;
    private TextView mTitleView;
    private TextView mCardNumber;
    private TextView mCardEndPrice;

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
        mCardEndPrice = (TextView) findViewById(R.id.pay_course_card_end_price);

        setViewData();
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
