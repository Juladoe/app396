package com.edusoho.kuozhi.v3.ui.base;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.edusoho.kuozhi.R;

/**
 * Created by DEL on 2016/11/24.
 */

public class BaseNoTitleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setSupportActionBar(null);
        super.onCreate(savedInstanceState);
    }

    protected void initView() {
        hideActionBar();
        View back = findViewById(R.id.back);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
