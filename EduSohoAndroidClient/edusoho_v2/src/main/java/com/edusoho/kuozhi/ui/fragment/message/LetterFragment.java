package com.edusoho.kuozhi.ui.fragment.message;

import android.os.Bundle;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;

/**
 * Created by Hby on 14/11/23.
 */
public class LetterFragment extends BaseFragment {
    @Override
    public String getTitle() {
        return "私信";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.letter_fragment_layout);
    }
}
