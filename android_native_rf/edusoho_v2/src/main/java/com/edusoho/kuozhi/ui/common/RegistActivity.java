package com.edusoho.kuozhi.ui.common;

import android.os.Bundle;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.BaseActivity;

public class RegistActivity extends BaseActivity {

    private AQuery aq;
    public static final int RESULT = 1001;
    public static final int REQUEST = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);
        initView();
    }

    private void initView() {
        setBackMode("注册用户", true, null);
        aq = new AQuery(this);
        regist();
    }

    private void regist()
    {

    }

}
