package com.edusoho.kuozhi;

import android.os.Bundle;
import com.crashlytics.android.Crashlytics;
import com.edusoho.kuozhi.ui.StartActivity;

public class KuozhiActivity extends StartActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
    }
}
