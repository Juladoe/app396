package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FillListAdapter extends QuestionViewPagerAdapter {

    public FillListAdapter(
            Context context, ArrayList<QuestionTypeSeq> list, int resource)
    {
        super(context, list, resource);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(mResouce, null);
        TextView stemView = (TextView) view.findViewById(R.id.question_stem);
        LinearLayout fillLayout = (LinearLayout) view.findViewById(R.id.question_fill_layout);

        QuestionTypeSeq questionTypeSeq = mList.get(position);
        Question question = questionTypeSeq.question;

        stemView.setText(
                String.format("%d, %s", position + 1, parseStem(questionTypeSeq.question.stem)));
        ArrayList<String> answers = questionTypeSeq.question.answer;
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

        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return view;
    }

    private StringBuffer parseStem(String stem)
    {
        Pattern stemPattern = Pattern.compile("\"(\\\\[\\\\[[^\\\\[\\\\]]+\\\\]\\\\])", Pattern.DOTALL);
        Matcher matcher = stemPattern.matcher(stem);
        StringBuffer stringBuilder = new StringBuffer();
        int count = 0;
        while (matcher.find()) {
            count ++;
            matcher.appendReplacement(stringBuilder, "(" + count + ")");
        }
        matcher.appendTail(stringBuilder);

        return stringBuilder;
    }

    private class ViewHolder
    {
        public TextView mQuestionSetm;
        public LinearLayout mFillLayout;
    }

}
