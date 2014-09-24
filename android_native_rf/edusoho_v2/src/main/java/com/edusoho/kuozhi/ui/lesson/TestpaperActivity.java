package com.edusoho.kuozhi.ui.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.TestpaperFullResult;
import com.edusoho.kuozhi.ui.course.CourseDetailsTabActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-8-31.
 */
public class TestpaperActivity extends CourseDetailsTabActivity {

    private int mTestId;
    private int mLessonId;

    private HashMap<QuestionType, ArrayList<QuestionTypeSeq>> mQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initIntentData() {
        Intent data = getIntent();
        mTitle = data.getStringExtra(Const.ACTIONBAT_TITLE);
        mTestId = data.getIntExtra(Const.MEDIA_ID, 0);
        mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
        titles = Const.TESTPAPER_QUESTION_TYPE;
        fragmentArrayList = Const.TESTPAPER_QUESTIONS;
        mMenu = R.menu.testpaper_menu;

        data.putExtra(FRAGMENT_DATA, new Bundle());
    }

    @Override
    protected void initView() {
        super.initView();
        mFragmentPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        loadTestpaper();
    }

    private void loadTestpaper()
    {
        setProgressBarIndeterminateVisibility(true);
        RequestUrl requestUrl = app.bindUrl(Const.TESTPAPER_FULL_INFO, true);
        requestUrl.setParams(new String[] {
                "testId", mTestId + "",
                "targetType", "lesson",
                "targetId", mLessonId + ""
        });

        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
                TestpaperFullResult result = parseJsonValue(
                        object, new TypeToken<TestpaperFullResult>() {
                });

                if (result == null) {
                    return;
                }

                mQuestions = result.items;
                app.sendMessage(Const.TESTPAPER_REFRESH_DATA, null);
            }
        });
    }

    public ArrayList<QuestionTypeSeq> getQuesions(QuestionType type)
    {
        if (mQuestions == null) {
            return null;
        }
        return mQuestions.get(type);
    }
}
