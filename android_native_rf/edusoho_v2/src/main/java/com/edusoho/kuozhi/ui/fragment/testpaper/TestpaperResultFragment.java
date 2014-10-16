package com.edusoho.kuozhi.ui.fragment.testpaper;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.testpaper.TestpaperResultListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Testpaper.Accuracy;
import com.edusoho.kuozhi.model.Testpaper.PaperResult;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.Testpaper;
import com.edusoho.kuozhi.model.Testpaper.TestpaperResultType;
import com.edusoho.kuozhi.ui.course.CourseDetailsTabActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoButton;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-9-21.
 */
public class TestpaperResultFragment extends BaseFragment {

    private ListView mListView;
    private TextView mTotalView;
    private TextView mReviewView;
    private EdusohoButton mResultParseBtn;

    private int mTestpaperResultId;
    private Testpaper mTestpaper;
    private String mStatus;
    public static final String RESULT_ID = "testpaperResultId";

    @Override
    public String getTitle() {
        return "考试结果";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.testpaper_result_fragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTestpaperResultId = bundle.getInt(RESULT_ID, 0);
            mStatus = bundle.getString(Const.STATUS);
            mTitle = bundle.getString(Const.ACTIONBAT_TITLE);
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mResultParseBtn = (EdusohoButton) view.findViewById(R.id.testpaper_result_show);
        mListView = (ListView) view.findViewById(R.id.testpaper_result_listview);
        mTotalView = (TextView) view.findViewById(R.id.testpaper_result_total);
        mReviewView = (TextView) view.findViewById(R.id.testpaper_result_review);

        RequestUrl requestUrl = mActivity.app.bindUrl(Const.TESTPAPER_RESULT, true);
        requestUrl.setParams(new String[]{
                "id", mTestpaperResultId + ""
        });

        showProgress(true);
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                showProgress(false);
                TestpaperResultType testpaperResultType = mActivity.parseJsonValue(
                        object, new TypeToken<TestpaperResultType>(){});
                if (testpaperResultType == null) {
                    return;
                }

                mTestpaper = testpaperResultType.testpaper;
                HashMap<QuestionType, Accuracy> accuracy = testpaperResultType.accuracy;
                ArrayList<QuestionType> questionTypeSeqs = mTestpaper.metas.question_type_seq;
                TestpaperResultListAdapter adapter = new TestpaperResultListAdapter(
                        mContext, accuracy, questionTypeSeqs, R.layout.testpaper_result_item
                );

                mListView.setAdapter(adapter);
                final PaperResult paperResult = testpaperResultType.paperResult;
                if ("reviewing".equals(paperResult.status)) {
                    setTotalText(mTotalView, "待批阅");
                    mReviewView.setText(R.string.testpaper_reviewing);
                } else if ("finished".equals(paperResult.status)) {
                    setTotalText(mTotalView, paperResult.score + "");
                    mReviewView.setText(
                            TextUtils.isEmpty(paperResult.teacherSay) ? "没有评语" : "评语:" + paperResult.teacherSay);
                }

                mResultParseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Const.ACTIONBAT_TITLE, mTitle);
                        bundle.putInt(TestpaperResultFragment.RESULT_ID, mTestpaperResultId);
                        bundle.putInt(Const.LESSON_ID, 0);
                        bundle.putStringArray(CourseDetailsTabActivity.TITLES, getTestpaperQSeq());
                        bundle.putStringArray(CourseDetailsTabActivity.LISTS, getTestpaperFragments());
                        startAcitivityWithBundle("TestpaperParseActivity", bundle);
                    }
                });
            }
        });

    }

    private void setTotalText(TextView rightText, String text)
    {
        StringBuffer stringBuffer = new StringBuffer("总分:");
        int start = stringBuffer.length();
        stringBuffer.append(text);

        SpannableString spannableString = new SpannableString(stringBuffer);
        int color = mContext.getResources().getColor(R.color.testpaper_result_item_total);
        spannableString.setSpan(
                new ForegroundColorSpan(color),
                start,
                stringBuffer.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        );
        rightText.setText(spannableString);
    }

    private ArrayList<Accuracy> getAccuracys(
            HashMap<QuestionType, Accuracy> accuracys
    )
    {
        ArrayList<Accuracy> list = new ArrayList<Accuracy>();
        for (Accuracy accuracy : accuracys.values()) {
            list.add(accuracy);
        }

        return list;
    }

    private ArrayList<QuestionType> getQuestionTypes(
            HashMap<QuestionType, Accuracy> accuracy)
    {
        ArrayList<QuestionType> list = new ArrayList<QuestionType>();
        for (QuestionType type : accuracy.keySet()) {
            list.add(type);
        }

        return list;
    }

    private String[] getTestpaperQSeq()
    {
        ArrayList<QuestionType> questionTypeSeqs = mTestpaper.metas.question_type_seq;
        String[] TESTPAPER_QUESTION_TYPE = new String[questionTypeSeqs.size()];
        for (int i=0; i < TESTPAPER_QUESTION_TYPE.length; i++) {
            TESTPAPER_QUESTION_TYPE[i] = questionTypeSeqs.get(i).title();
        }

        return TESTPAPER_QUESTION_TYPE;
    }

    private String[] getTestpaperFragments()
    {
        ArrayList<QuestionType> questionTypeSeqs = mTestpaper.metas.question_type_seq;
        String[] TESTPAPER_QUESTIONS = new String[questionTypeSeqs.size()];
        for (int i=0; i < TESTPAPER_QUESTIONS.length; i++) {
            switch (questionTypeSeqs.get(i)) {
                case choice:
                    TESTPAPER_QUESTIONS[i] = "ChoiceFragment";
                    break;
                case single_choice:
                    TESTPAPER_QUESTIONS[i] = "SingleChoiceFragment";
                    break;
                case essay:
                    TESTPAPER_QUESTIONS[i] = "EssayFragment";
                    break;
                case uncertain_choice:
                    TESTPAPER_QUESTIONS[i] = "UncertainChoiceFragment";
                    break;
                case fill:
                    TESTPAPER_QUESTIONS[i] = "FillFragment";
                    break;
                case determine:
                    TESTPAPER_QUESTIONS[i] = "DetermineFragment";
                    break;
                case material:
                    TESTPAPER_QUESTIONS[i] = "MaterialFragment";
                    break;
            }
        }

        return TESTPAPER_QUESTIONS;
    }

}
