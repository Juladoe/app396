package com.edusoho.kuozhi.v3.ui.homework;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by Melomelon on 2015/10/13.
 */
public class HomeworkSummary extends ActionBarBaseActivity{
    private static String mTag = "作业";
    private String lessonTitle;

    private TextView tvLessonTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.homework_summary_layout);
        setBackMode(BACK, mTag);
        initView();
    }

    public void initView(){

        tvLessonTitle = (TextView) findViewById(R.id.homework_belong_content);

        Intent intent  = getIntent();
        lessonTitle = intent.getStringExtra("lesson");
        tvLessonTitle.setText(lessonTitle);
    }
}
