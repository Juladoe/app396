package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.edusoho.kuozhi.R;

/**
 * Created by howzhi on 14-9-16.
 */
public class VideoLessonFragment extends BaseFragment {

    @Override
    public String getTitle() {
        return "视频课时";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.video_lesson_fragment_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }
}
