package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.TestResult;
import com.edusoho.kuozhi.ui.lesson.TestpaperParseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EduSohoTextBtn;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

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
            case essay:
            case material:
            case determine:
            case fill:
                stem = String.format(
                        "%d, (%s) %s (%.2f分)",
                        mIndex,
                        mQuestion.type.title(),
                        mQuestion.stem,
                        mQuestion.score
                );
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

        final EduSohoTextBtn favoriteBtn = (EduSohoTextBtn) view.findViewById(R.id.question_favorite);
        ArrayList<Integer> favorites = testpaperParseActivity.getFavorites();
        if (favorites.contains(mQuestion.id)) {
            favoriteBtn.setTag(true);
            favoriteBtn.setIcon(R.string.font_favorited);
            favoriteBtn.setTextColor(getResources().getColor(R.color.course_favorited));
        }

        favoriteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFavorite;
                Object tag = favoriteBtn.getTag();
                if (tag == null) {
                    isFavorite = false;
                } else {
                    isFavorite = (Boolean) tag;
                }

                favoriteQuestion(mQuestion.id, mQuestionSeq.testId, !isFavorite, favoriteBtn);
            }
        });
    }

    private void favoriteQuestion(
            int questionId, int targetId, final boolean isFavorite, final EduSohoTextBtn btn)
    {
        final TestpaperParseActivity testpaperParseActivity = TestpaperParseActivity.getInstance();
        if (testpaperParseActivity == null) {
            return;
        }

        EdusohoApp app = testpaperParseActivity.app;
        RequestUrl requestUrl = app.bindUrl(Const.FAVORITE_QUESTION, true);
        requestUrl.setParams(new String[] {
                "targetType", "testpaper",
                "targetId", targetId + "",
                "id", questionId + "",
                "action", isFavorite ? "favorite" : "unFavorite"
        });

        btn.setEnabled(false);
        testpaperParseActivity.setProgressBarIndeterminateVisibility(true);
        testpaperParseActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                btn.setEnabled(true);
                testpaperParseActivity.setProgressBarIndeterminateVisibility(false);
                Boolean result = testpaperParseActivity.parseJsonValue(
                        object, new TypeToken<Boolean>(){});
                if (result == null) {
                    return;
                }
                Toast.makeText(
                        mContext,
                        isFavorite ? "收藏成功!" : "取消收藏成功!",
                        Toast.LENGTH_SHORT
                ).show();

                btn.setTag(isFavorite);
                if (isFavorite){
                    btn.setIcon(R.string.font_favorited);
                    btn.setTextColor(getResources().getColor(R.color.course_favorited));
                } else {
                    btn.setIcon(R.string.font_favoirte);
                    btn.setTextColor(getResources().getColor(R.color.system_normal_text));
                }
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

        AnalysisText.setText(
                TextUtils.isEmpty(mQuestion.analysis) ? "暂无解析" : Html.fromHtml(mQuestion.analysis));
        initFavoriteBtn(view);
    }
}
