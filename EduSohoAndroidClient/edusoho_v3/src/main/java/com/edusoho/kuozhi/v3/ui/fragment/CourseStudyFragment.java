package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.ThreadDiscussActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.PushUtil;

/**
 * Created by JesseHuang on 15/12/14.
 */
public class CourseStudyFragment extends BaseFragment {
    private TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_study_fragment);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    protected void initView(View view) {
        tv = (TextView) view.findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.mEngine.runNormalPlugin("ThreadDiscussActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        //ask
//                        startIntent.putExtra(ThreadDiscussActivity.COURSE_ID, 1);
//                        startIntent.putExtra(ThreadDiscussActivity.LESSON_ID, 0);
//                        startIntent.putExtra(ThreadDiscussActivity.ACTIVITY_TYPE, PushUtil.ThreadMsgType.THREAD);

                        startIntent.putExtra(ThreadDiscussActivity.COURSE_ID, 1);
                        startIntent.putExtra(ThreadDiscussActivity.LESSON_ID, 0);
                        startIntent.putExtra(ThreadDiscussActivity.THREAD_ID, 1);

                        startIntent.putExtra(ThreadDiscussActivity.ACTIVITY_TYPE, PushUtil.ThreadMsgType.THREAD_POST);
                    }
                });
            }
        });
    }

    private void initData() {
    }
}
