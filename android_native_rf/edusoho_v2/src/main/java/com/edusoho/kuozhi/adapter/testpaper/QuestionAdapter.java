package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-29.
 */
public class QuestionAdapter extends QuestionViewPagerAdapter {

    public QuestionAdapter(
            Context context, ArrayList<QuestionTypeSeq> list)
    {
        super(context, list, 0);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        QuestionTypeSeq questionTypeSeq = mList.get(position);

        View view = switchQuestionWidget(questionTypeSeq, position + 1);
        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return view;
    }
}
