package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;

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

    @Override
    protected void invalidateData() {
        fillLayout = (LinearLayout) this.findViewById(R.id.question_fill_layout);
        stemView = (TextView) this.findViewById(R.id.question_stem);

        Question mQuestion = mQuestionSeq.question;
        stemView.setText(
                String.format("%d, %s", mIndex + 1, parseStem(mQuestion.stem)));
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
            fillLayout.addView(
                    editText, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
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
