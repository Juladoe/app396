package com.edusoho.kuozhi.ui.fragment.testpaper;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.lesson.TestpaperActivity;
import com.edusoho.kuozhi.ui.lesson.TestpaperBaseActivity;
import com.edusoho.kuozhi.util.Const;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-24.
 */
public abstract class QuestionTypeBaseFragment extends BaseFragment {

    protected TextView mQuestionType;
    protected TextView mQuestionNumber;
    protected TestpaperBaseActivity mTestpaperActivity;

    protected int mCurrentIndex;

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mTestpaperActivity = (TestpaperBaseActivity) activity;
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[] {
                new MessageType(Const.TESTPAPER_REFRESH_DATA)
        };
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (Const.TESTPAPER_REFRESH_DATA.equals(messageType.type)) {
            refreshViewData();
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mQuestionNumber = (TextView) view.findViewById(R.id.question_number);
        mQuestionType = (TextView) view.findViewById(R.id.question_type);

        mCurrentIndex = 1;
    }

    protected abstract void refreshViewData();

    public ArrayList<QuestionTypeSeq> getQuestion(QuestionType type)
    {
        return mTestpaperActivity.getQuesions(type);
    }
}
