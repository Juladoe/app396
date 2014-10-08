package com.edusoho.kuozhi.ui.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.MaterialType;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.Question.Answer;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.TestpaperFullResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.course.CourseDetailsTabActivity;
import com.edusoho.kuozhi.ui.fragment.testpaper.TestpaperCardFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by howzhi on 14-8-31.
 */
public class TestpaperActivity extends CourseDetailsTabActivity
        implements MessageEngine.MessageCallback {

    public static final int CHANGE_ANSWER = 0001;

    private int mTestId;
    private int mLessonId;
    private MenuItem timeMenuItem;
    private HashMap<QuestionType, ArrayList<Answer>> answerMap;

    private HashMap<QuestionType, ArrayList<QuestionTypeSeq>> mQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app.registMsgSource(this);
        answerMap = new HashMap<QuestionType, ArrayList<Answer>>();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.testpaper_menu_card){
            showTestpaperCard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        switch (type) {
            case CHANGE_ANSWER:
                Bundle bundle = message.data;
                int index = bundle.getInt("index", 0);
                ArrayList<String> data = bundle.getStringArrayList("data");
                String qtStr = bundle.getString("QuestionType");
                QuestionType questionType = QuestionType.value(qtStr);
                changeAnswer(questionType, index, data);
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(CHANGE_ANSWER, source)
        };
        return messageTypes;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMenu == 0) {
            return false;
        }
        getMenuInflater().inflate(mMenu, menu);
        timeMenuItem = menu.findItem(R.id.testpaper_menu_time);
        return true;
    }

    private void changeAnswer(QuestionType questionType, int index, ArrayList<String> data)
    {
        ArrayList<Answer> answerList = answerMap.get(questionType);
        if (answerList == null) {
            return;
        }
        Answer answer = answerList.get(index);
        answer.data = data;
        answer.isAnswer = data == null || data.isEmpty() ? false : true;
    }

    private void showTestpaperCard()
    {
        TestpaperCardFragment fragment = new TestpaperCardFragment();
        fragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    protected void initView() {
        super.initView();
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

                for (QuestionType qt : mQuestions.keySet()) {
                    ArrayList<QuestionTypeSeq> seqs = mQuestions.get(qt);
                    ArrayList<Answer> answerList = new ArrayList<Answer>();

                    for (QuestionTypeSeq seq : seqs) {
                        if (seq.questionType == QuestionType.material) {
                            for (QuestionTypeSeq itemSeq : seq.items) {
                                answerList.add(new Answer());
                            }
                            continue;
                        }
                        answerList.add(new Answer());
                    }
                    answerMap.put(qt, answerList);
                }

                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
                timeMenuItem.setTitle(format.format(new Date(result.testpaper.limitedTime * 60 * 1000)));
                app.sendMessage(Const.TESTPAPER_REFRESH_DATA, null);
            }
        });
    }

    private void getLimitedTime(int limitedTime)
    {
        int hh = limitedTime / 3600;
        int mm = limitedTime % 3600 / 60;
        int ss = limitedTime % 60;

        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
    }

    public HashMap<QuestionType, ArrayList<Answer>> getAnswer()
    {
        return answerMap;
    }

    public HashMap<QuestionType, ArrayList<QuestionTypeSeq>> getAllQuestions()
    {
        return mQuestions;
    }

    public ArrayList<QuestionTypeSeq> getQuesions(QuestionType type)
    {
        if (mQuestions == null) {
            return null;
        }
        return mQuestions.get(type);
    }
}
