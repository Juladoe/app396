package com.edusoho.kuozhi.homework.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.homework.R;
import com.edusoho.kuozhi.homework.adapter.HomeworkQuestionAdapter;
import com.edusoho.kuozhi.homework.listener.IHomeworkQuestionResult;
import com.edusoho.kuozhi.homework.model.HomeWorkQuestion;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;

import java.util.List;

/**
 * Created by howzhi on 15/10/16.
 */
public class HomeWorkQuestionFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    protected int mHomeworkQuestionCount;
    protected TextView mQuestionIndexView;
    protected TextView mQuestionTitleView;
    protected ViewPager mHomeworkQuestionPager;
    private IHomeworkQuestionResult mQuestionResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.homework_question_layout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mQuestionResult = (IHomeworkQuestionResult) activity;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mQuestionIndexView = (TextView) view.findViewById(R.id.homework_index);
        mQuestionTitleView = (TextView) view.findViewById(R.id.homework_title);
        mHomeworkQuestionPager = (ViewPager) view.findViewById(R.id.homework_viewpaper);
        mHomeworkQuestionPager.setOnPageChangeListener(this);

        mQuestionTitleView.setText(getArguments().getString(Const.ACTIONBAR_TITLE));
        List<HomeWorkQuestion> questionList = mQuestionResult.getQuestionList();
        mHomeworkQuestionCount = questionList.size();
        HomeworkQuestionAdapter adapter = new HomeworkQuestionAdapter(mContext, questionList);
        mHomeworkQuestionPager.setAdapter(adapter);
        setHomeworkIndex(1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setHomeworkIndex(position + 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected void setHomeworkIndex(int position)
    {
        String text = String.format("%d/%d", position, mHomeworkQuestionCount);
        SpannableString spannableString = new SpannableString(text);
        int color = getResources().getColor(R.color.action_bar_bg);
        int length = getNumberLength(position);
        spannableString.setSpan(
                new ForegroundColorSpan(color), 0, length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(
                new RelativeSizeSpan(2.0f), 0, length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        mQuestionIndexView.setText(spannableString);
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
