package com.edusoho.kuozhi.v3.ui.homework;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by Melomelon on 2015/10/13.
 */
public class HomeworkSummary extends ActionBarBaseActivity {
    public static final int HOME_HORK = 1;
    public static final int EXERCISE = 2;

    private String lessonTitle;
    private int type;

    private TextView tvLessonTitle;
    private TextView homeworkName;
    private TextView homeworkNameContent;
    private TextView homeworkInfo;
    private TextView homeworkInfoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        lessonTitle = bundle.getString("lesson");
        type = bundle.getInt("type");
        setContentView(R.layout.homework_summary_layout);
        setBackMode(BACK, type == HOME_HORK ? "作业" : "练习");
        initView();
    }

    public void initView() {

        tvLessonTitle = (TextView) findViewById(R.id.homework_belong_content);
        homeworkName = (TextView) findViewById(R.id.homeork_name);
        homeworkNameContent = (TextView) findViewById(R.id.homeork_name_content);
        homeworkInfo = (TextView) findViewById(R.id.homeork_info);
        homeworkInfoContent = (TextView) findViewById(R.id.homeork_info_content);
        if (type == HOME_HORK) {
            homeworkName.setText("作业名称");
            homeworkInfo.setText("作业说明");
        } else {
            homeworkName.setText("练习名称");
            homeworkInfo.setText("练习说明");
        }

        tvLessonTitle.setText(lessonTitle);
    }
}
