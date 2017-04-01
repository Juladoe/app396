package com.edusoho.kuozhi.clean.module.courseset.confirmorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseNoTitleActivity;

/**
 * Created by DF on 2017/3/25.
 */

public class ConfirmOrderActivity extends BaseNoTitleActivity {

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, ConfirmOrderActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
    }
}
