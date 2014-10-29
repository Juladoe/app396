package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.MaterialQuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.TestResult;
import com.edusoho.kuozhi.ui.lesson.TestpaperActivity;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by howzhi on 14-9-29.
 */
public class FillQuestionWidget extends BaseQuestionWidget {

    protected LinearLayout fillLayout;
    protected TextView stemView;

    public FillQuestionWidget(Context context) {
        super(context);
    }

    public FillQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private TextWatcher onTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int index, int i2, int i3) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", mIndex - 1);
            if (mQuestionSeq instanceof MaterialQuestionTypeSeq) {
                bundle.putString("QuestionType", QuestionType.material.name());
            } else {
                bundle.putString("QuestionType", mQuestionSeq.question.type.name());
            }
            ArrayList<String> data = new ArrayList<String>();
            int count = fillLayout.getChildCount();
            for (int i=0; i < count; i++) {
                EditText editText = (EditText) fillLayout.getChildAt(i);
                data.add(editText.getText().toString());
            }

            bundle.putStringArrayList("data", data);
            EdusohoApp.app.sendMsgToTarget(
                    TestpaperActivity.CHANGE_ANSWER, bundle, TestpaperActivity.class);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void invalidateData() {
        super.invalidateData();
        fillLayout = (LinearLayout) this.findViewById(R.id.question_fill_layout);
        stemView = (TextView) this.findViewById(R.id.question_stem);

        Question mQuestion = mQuestionSeq.question;
        String stem = String.format("%d, %s", mIndex, parseStem(mQuestion.stem));
        stemView.setText(
                Html.fromHtml(stem)
                );
        ArrayList<String> answers = mQuestion.answer;
        Resources resources = mContext.getResources();
        fillLayout.removeAllViews();
        int size = answers.size();
        for (int i=1; i <= size; i++) {
            EditText editText = new EditText(mContext);
            editText.setSingleLine();
            editText.setPadding(10, 5, 5, 5);
            editText.setHint("答案" + i);
            editText.setTextColor(resources.getColor(R.color.question_fill_text));
            editText.setHintTextColor(resources.getColor(R.color.question_fill_hit));
            editText.setBackgroundDrawable(resources.getDrawable(R.drawable.login_edt_bg_sel));
            editText.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.question_fill));
            editText.addTextChangedListener(onTextChangedListener);
            fillLayout.addView(
                    editText, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        if (mQuestion.testResult != null) {
            enable(fillLayout, false);
            mAnalysisVS = (ViewStub) this.findViewById(R.id.quetion_choice_analysis);
            mAnalysisVS.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub viewStub, View view) {
                    initResultAnalysis(view);
                }
            });
            mAnalysisVS.inflate();
        }
    }

    @Override
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

        myAnswerText.setText("你的答案:\n" + myAnswer);

        int rightColor = mContext.getResources().getColor(R.color.testpaper_result_right);
        SpannableString rightText = AppUtil.getColorTextAfter(
                "正确答案:\n", listToStr(mQuestion.answer), rightColor);
        myRightText.setText(rightText);

        AnalysisText.setText(
                TextUtils.isEmpty(mQuestion.analysis) ? "暂无解析" : Html.fromHtml(mQuestion.analysis));
        initFavoriteBtn(view);
    }

    @Override
    protected String listToStr(ArrayList<String> arrayList)
    {
        int index = 1;
        StringBuilder stringBuilder = new StringBuilder();
        for (String answer : arrayList) {
            if (TextUtils.isEmpty(answer)) {
                continue;
            }
            stringBuilder.append(String.format("答案(%d):", index++));
            stringBuilder.append(answer);
            stringBuilder.append("\n");
        }
        int length = stringBuilder.length();
        if (length > 0) {
            stringBuilder.delete(length - 1, length);
        }
        return stringBuilder.toString();
    }

    private StringBuffer parseStem(String stem)
    {
        Pattern stemPattern = Pattern.compile("(\\[\\[[^\\[\\]]+\\]\\])", Pattern.DOTALL);
        Matcher matcher = stemPattern.matcher(stem);
        StringBuffer stringBuilder = new StringBuffer();
        int count = 0;
        while (matcher.find()) {
            Log.d(null, "find-->" + matcher);
            count ++;
            matcher.appendReplacement(stringBuilder, "(" + count + ")");
        }
        matcher.appendTail(stringBuilder);

        return stringBuilder;
    }
    @Override
    protected void initView(AttributeSet attrs) {
    }

    @Override
     public void setData(QuestionTypeSeq questionSeq, int index) {
        super.setData(questionSeq, index);
        invalidateData();
    }
}
