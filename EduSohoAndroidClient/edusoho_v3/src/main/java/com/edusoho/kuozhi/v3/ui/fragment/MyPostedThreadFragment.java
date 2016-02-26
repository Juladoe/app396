package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

/**
 * Created by melomelon on 16/2/26.
 */
public class MyPostedThreadFragment extends BaseFragment {

    public MyPostedThreadFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.my_posted_thread_fragment_layout);
    }
}
