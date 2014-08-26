package com.edusoho.kuozhi.ui.common;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

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
