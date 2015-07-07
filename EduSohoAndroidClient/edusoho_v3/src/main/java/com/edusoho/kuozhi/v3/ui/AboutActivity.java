package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by JesseHuang on 15/5/18.
 */
public class AboutActivity extends ActionBarBaseActivity {
    private TextView tvAboutSchool;
    private TextView tvFeedback;
    private TextView tvCheckUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        setBackMode(BACK, "关于");
        initView();
    }

    private void initView() {
        tvAboutSchool = (TextView) findViewById(R.id.tv_about_school);
        tvAboutSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.mEngine.runNormalPlugin("AboutSchool", mActivity, null);
            }
        });

        tvFeedback = (TextView) findViewById(R.id.tv_feedback);
        tvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.mEngine.runNormalPlugin("SuggestionActivity", mActivity, null);
            }
        });

        tvCheckUpdate = (TextView) findViewById(R.id.tv_check_update);
    }
}
