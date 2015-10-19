package com.edusoho.kuozhi.homework;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.view.EduSohoButton;

/**
 * Created by Melomelon on 2015/10/19.
 */
public class HomeworkAnswerCardActivity extends ActionBarBaseActivity {

    private String mTitle = "´ðÌâ¿¨";
    private LinearLayout mCardLayout;
    private EduSohoButton submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework_answer_card_layout);
        initView();
    }

    public void initView() {
        setBackMode(BACK, mTitle);
        submitBtn = (EduSohoButton) findViewById(R.id.homework_submit_btn);
        mCardLayout = (LinearLayout) findViewById(R.id.homework_answer_card_layout);

    }
}
