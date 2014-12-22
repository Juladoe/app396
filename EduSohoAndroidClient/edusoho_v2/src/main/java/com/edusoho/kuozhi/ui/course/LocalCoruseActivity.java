package com.edusoho.kuozhi.ui.course;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by howzhi on 14/12/15.
*/
public class LocalCoruseActivity extends CourseDetailsTabActivity {

    @Override
    protected void initIntentData() {
        titles = new String[] { "正在下载", "已下载" };
        fragmentArrayList = new String[] { "LessonDownloadedFragment", "LessonDownloadedFragment"};

        Intent data = getIntent();
        data.putExtra(FRAGMENT_DATA, new Bundle());

        setBackMode(BACK, "已下载课程");
    }

    @Override
    protected void initView() {
        super.initView();
    }
}
