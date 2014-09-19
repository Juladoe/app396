package com.edusoho.kuozhi.ui.question;

import android.os.Bundle;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

public class QuestionReplyActivity extends ActionBarBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_reply_layout);
        setBackMode(BACK, "回复");
    }


}
