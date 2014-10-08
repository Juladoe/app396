package com.edusoho.kuozhi.ui.fragment.testpaper;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.plugin.photo.HackyViewPager;

/**
 * Created by howzhi on 14-9-24.
 */
public abstract class SelectQuestionFragment extends QuestionTypeBaseFragment
        implements ViewPager.OnPageChangeListener{

    protected int mQuestionCount;
    protected ViewPager mQuestionPager;

    @Override
    protected void initView(View view) {
        super.initView(view);
        mQuestionPager = (HackyViewPager) view.findViewById(R.id.question_pager);
        mQuestionPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setQuestionNumber(position + 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected void setQuestionNumber(int position)
    {
        String text = String.format("%d/%d", position, mQuestionCount);
        SpannableString spannableString = new SpannableString(text);
        int color = getResources().getColor(R.color.action_bar_bg);
        int length = getNumberLength(position);
        spannableString.setSpan(
                new ForegroundColorSpan(color), 0, length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(
                new RelativeSizeSpan(2.0f), 0, length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        mQuestionNumber.setText(spannableString);
    }

    private int getNumberLength(int number)
    {
        int length = 1;
        while (number >= 10) {
            length ++;
            number = number / 10;
        }

        return length;
    }
}
