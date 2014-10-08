package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;

/**
 * Created by howzhi on 14-9-29.
 */
public abstract class BaseQuestionWidget extends RelativeLayout implements IQuestionWidget{

    protected Context mContext;
    protected QuestionTypeSeq mQuestionSeq;
    protected int mIndex;

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

    protected abstract void invalidateData();

    @Override
    public void setData(QuestionTypeSeq questionSeq, int index) {
        mIndex = index;
        mQuestionSeq = questionSeq;
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
}
