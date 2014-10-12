package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.TestResult;
import com.edusoho.kuozhi.ui.lesson.TestpaperParseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.view.EduSohoTextBtn;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-29.
 */
public abstract class BaseQuestionWidget extends RelativeLayout implements IQuestionWidget{

    protected Context mContext;
    protected QuestionTypeSeq mQuestionSeq;
    protected Question mQuestion;
    protected ViewStub mAnalysisVS;

    protected int mIndex;

    public static final String[] CHOICE_ANSWER = {
            "A", "B", "C",
            "D", "E", "F",
            "G", "H", "I",
            "J", "K", "L"
    };

    public static final String[] DETERMINE_ANSWER = { "错误", "正确" };

    public BaseQuestionWidget(Context context) {
        super(context);
        mContext = context;
        initView(null);
    }

    public BaseQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    protected abstract void initView(android.util.AttributeSet attrs);

    protected void invalidateData(){
    }

    @Override
    public void setData(QuestionTypeSeq questionSeq, int index) {
        mIndex = index;
        mQuestionSeq = questionSeq;
        mQuestion = mQuestionSeq.question;
    }

    /**
     * 获取题干
     */
    protected Spanned getQuestionStem()
    {
        String stem = "";
        Question mQuestion = mQuestionSeq.question;
        switch (mQuestion.type) {
            case choice:
            case uncertain_choice:
            case single_choice:
                stem = String.format("%d, (%s) %s %s", mIndex, mQuestion.type.title(), mQuestion.stem, "( )");
                break;
            case essay:
                stem = mIndex + mQuestion.type.title() + mQuestion.stem;
                break;
            case material:
                stem = mIndex + mQuestion.type.title() + mQuestion.stem;
                break;
            case determine:
                stem = mIndex + ", " + mQuestion.stem;
                break;
            case fill:
                stem = mIndex + mQuestion.type.title() + mQuestion.stem;
        }

        return Html.fromHtml(stem);
    }

    protected void enable(ViewGroup viewGroup, boolean isEnable)
    {
        int count = viewGroup.getChildCount();
        for (int i=0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(isEnable);
        }
    }


    protected String listToStr(ArrayList<String> arrayList)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (String answer : arrayList) {
            if (TextUtils.isEmpty(answer)) {
                continue;
            }
            stringBuilder.append(getAnswerByType(mQuestion.type, answer));
            stringBuilder.append(",");
        }
        int length = stringBuilder.length();
        if (length > 0) {
            stringBuilder.delete(length - 1, length);
        }
        return stringBuilder.toString();
    }

    protected String getAnswerByType(QuestionType qt, String answer)
    {
        switch (qt) {
            case choice:
            case single_choice:
            case uncertain_choice:
                return parseAnswer(answer);
            case determine:
                return parseDetermineAnswer(answer);
            case essay:
            case fill:
                return answer;
        }

        return  "";
    }

    private String parseDetermineAnswer(String answer)
    {
        int index = 0;
        try {
            index = Integer.parseInt(answer);
        } catch (Exception e) {
            index = 0;
        }

        return DETERMINE_ANSWER[index];
    }

    private String parseAnswer(String answer)
    {
        int index = 0;
        try {
            index = Integer.parseInt(answer);
        } catch (Exception e) {
            index = 0;
        }
        return CHOICE_ANSWER[index];
    }

    protected void initFavoriteBtn(View view)
    {
        TestpaperParseActivity testpaperParseActivity = TestpaperParseActivity.getInstance();
        if (testpaperParseActivity == null) {
            return;
        }

        EduSohoTextBtn favoriteBtn = (EduSohoTextBtn) view.findViewById(R.id.question_favorite);
        ArrayList<Integer> favorites = testpaperParseActivity.getFavorites();
        if (favorites.contains(mQuestion.id)) {
            favoriteBtn.setTag(true);
            favoriteBtn.setIcon(R.string.font_favorited);
            favoriteBtn.setTextColor(getResources().getColor(R.color.course_favorited));
        }

        favoriteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    protected void initResultAnalysis(View view)
    {
        TextView myAnswerText = (TextView) view.findViewById(R.id.question_my_anwer);
        TextView myRightText = (TextView) view.findViewById(R.id.question_right_anwer);
        TextView AnalysisText = (TextView) view.findViewById(R.id.question_analysis);

        TestResult testResult = mQuestion.testResult;
        String myAnswer = null;
        if ("noAnswer".equals(testResult.status)) {
            myAnswer = "未答题";
        } else {
            myAnswer = listToStr(testResult.answer);
        }

        int rightColor = mContext.getResources().getColor(R.color.testpaper_result_right);
        SpannableString rightText = AppUtil.getColorTextAfter(
                "正确答案:", listToStr(mQuestion.answer), rightColor);
        myAnswerText.setText("你的答案:" + myAnswer);
        myRightText.setText(rightText);

        AnalysisText.setText(Html.fromHtml(mQuestion.analysis));
        initFavoriteBtn(view);
    }
}
