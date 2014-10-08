package com.edusoho.kuozhi.ui.fragment.testpaper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.testpaper.TestpaperCardAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.Answer;
import com.edusoho.kuozhi.model.Review;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.fragment.ReviewInfoFragment;
import com.edusoho.kuozhi.ui.lesson.TestpaperActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoButton;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-9-19.
 */
public class TestpaperCardFragment extends DialogFragment {

    private LinearLayout mCardLayout;
    private TestpaperActivity mTestpaperActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.PopDialogTheme);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mTestpaperActivity = (TestpaperActivity) activity;
        Bundle bundle = getArguments();
        if (bundle != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.testpaper_card_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(mTestpaperActivity);
        mCardLayout = (LinearLayout) view.findViewById(R.id.testpaper_card_layout);

        HashMap<QuestionType, ArrayList<QuestionTypeSeq>> questionTypeArrayListHashMap =
                mTestpaperActivity.getAllQuestions();

        HashMap<QuestionType, ArrayList<Answer>> answerMap = mTestpaperActivity.getAnswer();
        for (QuestionType type : questionTypeArrayListHashMap.keySet()) {
            View cardView = layoutInflater.inflate(R.layout.testpaper_card_layout, null);

            TextView label = (TextView) cardView.findViewById(R.id.testpaper_card_label);
            GridView cardGridView = (GridView) cardView.findViewById(R.id.testpaper_card_gridview);

            ArrayList<QuestionTypeSeq> questionTypeSeqs = questionTypeArrayListHashMap.get(type);
            if (type == QuestionType.material) {
                questionTypeSeqs = getMaterialItems(questionTypeSeqs);
            }

            TestpaperCardAdapter adapter = new TestpaperCardAdapter(
                    mTestpaperActivity,
                    questionTypeSeqs,
                    answerMap.get(type),
                    R.layout.testpaper_card_gridview_item
            );

            cardGridView.setAdapter(adapter);
            label.setText(type.title());
            mCardLayout.addView(cardView);
        }
    }

    private ArrayList<QuestionTypeSeq> getMaterialItems(
            ArrayList<QuestionTypeSeq> questionTypeSeqs)
    {
        ArrayList<QuestionTypeSeq> list = new ArrayList<QuestionTypeSeq>();
        for (QuestionTypeSeq questionTypeSeq : questionTypeSeqs) {
            list.addAll(questionTypeSeq.items);
        }
        return list;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);

        lp.width = EdusohoApp.screenW;
        lp.height = (int)(EdusohoApp.screenH * 0.8f);

        dialogWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
