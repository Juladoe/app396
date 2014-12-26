package com.edusoho.kuozhi.ui.QuestionNew;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionReplyFragment extends BaseFragment{

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.question_reply_fragment_layout);
    }
}
