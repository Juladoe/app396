package com.edusoho.kuozhi.v3.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by DF on 2016/12/19.
 */

public class StudentListActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_student_list);

        initView();
    }

    private void initView() {
        TextView tvBack = (TextView) findViewById(R.id.tv_back);

    }
}
