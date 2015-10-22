package com.edusoho.kuozhi.homework;

import android.content.Intent;

import com.edusoho.kuozhi.homework.HomeworkActivity;
import com.edusoho.kuozhi.homework.model.HomeworkProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.Const;

/**
 * Created by howzhi on 15/10/21.
 */
public class HomeWorkParseActivity extends HomeworkActivity {

    public static final String HOMEWORK_RESULTID = "homeworkResultId";
    private int mHomeWorkResultId;

    @Override
    protected Intent initIntentData() {
        Intent intent = super.initIntentData();
        mHomeWorkResultId = intent.getIntExtra(HOMEWORK_RESULTID, 0);
        return intent;
    }

    @Override
    protected RequestUrl getRequestUrl() {
        return app.bindNewUrl(String.format(Const.HOMEWORK_CONTENT_RESULT, mHomeWorkResultId), true);
    }
}
