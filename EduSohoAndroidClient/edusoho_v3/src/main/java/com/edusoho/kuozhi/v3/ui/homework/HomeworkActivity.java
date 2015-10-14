package com.edusoho.kuozhi.v3.ui.homework;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.cache.request.model.Request;
import com.edusoho.kuozhi.v3.model.bal.Answer;
import com.edusoho.kuozhi.v3.model.bal.homework.HomeworkContentResult;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.test.PaperResult;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionTypeSeq;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.CourseDetailsTabActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Melomelon on 2015/10/14.
 */
public class HomeworkActivity extends CourseDetailsTabActivity {

    private int mHomeworkId;
    private int mLessonId;

    private HashMap<QuestionType, ArrayList<Answer>> answerMap;
    private HashMap<QuestionType, ArrayList<QuestionTypeSeq>> questionMap;

    public ArrayList<QuestionTypeSeq> getQuesions(QuestionType type) {
        if (questionMap == null) {
            return null;
        }
        return questionMap.get(type);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerMap = new HashMap<QuestionType, ArrayList<Answer>>();

        loadHomeworkContent();
    }

    @Override
    protected void initIntentData() {
        Bundle bundle = getIntent().getExtras();

        Intent data = getIntent();
        mTitle = data.getStringExtra(Const.ACTIONBAR_TITLE);
        mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
        titles = data.getStringArrayExtra(TITLES);
        fragmentArrayList = data.getStringArrayExtra(LISTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homework_menu, menu);
        return true;
    }

    public void loadHomeworkContent() {
        RequestUrl requestUrl = app.bindNewUrl(Const.HOMEWORK_CONTENT, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        stringBuffer.append("/" + mLessonId + "?_idType=lesson");
        requestUrl.url = stringBuffer.toString();

        ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                HomeworkContentResult ContentResult = parseJsonValue(response, new TypeToken<HomeworkContentResult>() {
//                });
            }
        }, null);
    }
}
