package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.MaterialQuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-29.
 */
public class MaterialQuestionAdapter extends QuestionViewPagerAdapter {


    public MaterialQuestionAdapter(
            Context context, ArrayList<MaterialQuestionTypeSeq> list)
    {
        super(context, null, 0);
        mList = new ArrayList<QuestionTypeSeq>();
        for(MaterialQuestionTypeSeq materialQuestionTypeSeq : list) {
            mList.add(materialQuestionTypeSeq);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MaterialQuestionTypeSeq questionTypeSeq = (MaterialQuestionTypeSeq) mList.get(position);

        View materialView = LayoutInflater.from(mContext).inflate(
                R.layout.material_item_layout, null);
        TextView stemText = (TextView) materialView.findViewById(R.id.question_stem);
        ViewGroup viewContent = (ViewGroup) materialView.findViewById(R.id.question_content);

        View view = switchQuestionWidget(questionTypeSeq, position + 1);
        stemText.setText(getQuestionStem(questionTypeSeq.parent.question, position + 1, stemText));
        viewContent.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        container.addView(materialView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return materialView;
    }
}
