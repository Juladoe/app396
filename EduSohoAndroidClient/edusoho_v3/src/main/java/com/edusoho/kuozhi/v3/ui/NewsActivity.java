package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by tree on 2017/5/3.
 */

public class NewsActivity extends ActionBarBaseActivity {


    private Toolbar tbActionBar;
    private TextView tvTitle;
    private View viewTitleLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        tbActionBar = (Toolbar) findViewById(R.id.tb_action_bar);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        viewTitleLoading = findViewById(R.id.ll_title_loading);
        setSupportActionBar(tbActionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle.setText(R.string.title_news);
        setTitleLoading(true);
    }

    public void setTitleLoading(boolean isLoading) {
        if (isLoading) {
            tvTitle.setVisibility(View.GONE);
            viewTitleLoading.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            viewTitleLoading.setVisibility(View.GONE);
        }
    }
}
