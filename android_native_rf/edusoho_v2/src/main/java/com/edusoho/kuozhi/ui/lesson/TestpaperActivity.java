package com.edusoho.kuozhi.ui.lesson;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.edusoho.kuozhi.model.Testpaper.PaperResult;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.Testpaper;
import com.edusoho.kuozhi.model.Testpaper.TestpaperFullResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.course.CourseDetailsTabActivity;
import com.edusoho.kuozhi.ui.fragment.testpaper.TestpaperCardFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howzhi on 14-8-31.
 */
public class TestpaperActivity extends CourseDetailsTabActivity
        implements MessageEngine.MessageCallback {

    public static final int CHANGE_ANSWER = 0001;

    private int mTestId;
    private int mLessonId;
    private MenuItem timeMenuItem;
    private Timer mTimer;
    private int mLimitedTime;
    private boolean mIsRun;
    private Testpaper mTestpaper;
    private PaperResult mTestpaperResult;
    private HashMap<QuestionType, ArrayList<Answer>> answerMap;

    private HashMap<QuestionType, ArrayList<QuestionTypeSeq>> mQuestions;

    private static TestpaperActivity testpaperActivity;

    private static final int UPDATE_TIME     = 0001;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TIME:
                    timeMenuItem.setTitle(getLimitedTime(mLimitedTime));
                    break;
            }
        }
    };

    public ArrayList<Answer> getAnswerByQT(QuestionType questionType)
    {
        return answerMap.get(questionType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app.registMsgSource(this);
        testpaperActivity = this;
        answerMap = new HashMap<QuestionType, ArrayList<Answer>>();
    }

    @Override
    protected void initIntentData() {
        Intent data = getIntent();
        mTitle = data.getStringExtra(Const.ACTIONBAT_TITLE);
        mTestId = data.getIntExtra(Const.MEDIA_ID, 0);
        mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
        titles = data.getStringArrayExtra(TITLES);
        fragmentArrayList = data.getStringArrayExtra(LISTS);
        mMenu = R.menu.testpaper_menu;

        data.putExtra(FRAGMENT_DATA, new Bundle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.testpaper_menu_card){
            if (mQuestions == null) {
                return true;
            }
            showTestpaperCard();
            return true;
        } else if (id == R.id.testpaper_menu_time) {
            if (mIsRun) {
                stopTask();
            } else {
                startTask();
            }
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
                mTestpaper = result.testpaper;
                mTestpaperResult = result.testpaperResult;

                for (QuestionType qt : mQuestions.keySet()) {
                    ArrayList<QuestionTypeSeq> seqs = mQuestions.get(qt);
                    ArrayList<Answer> answerList = new ArrayList<Answer>();
                    for (QuestionTypeSeq seq : seqs) {
                        if (seq == null) {
                            continue;
                        }
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

                mLimitedTime = result.testpaper.limitedTime * 60;
                startTask();
                app.sendMessage(Const.TESTPAPER_REFRESH_DATA, null);
            }
        });
    }

    private void startTask()
    {
        mTimer = new Timer();
        mTimer.schedule(getTask(), 0, 1000);
    }

    private void stopTask()
    {
        PopupDialog dialog = PopupDialog.createNormal(
                mActivity, "暂停考试", "考试暂停中！");
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                startTask();
            }
        });
        dialog.show();
        mIsRun = false;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private TimerTask getTask()
    {
        return new TimerTask(){
            @Override
            public void run() {
                mIsRun = true;
                if (mLimitedTime == 0) {
                    mTimer.cancel();
                    return;
                }
                mLimitedTime --;
                mHandler.obtainMessage(UPDATE_TIME).sendToTarget();
            }
        };
    }

    private String getLimitedTime(int limitedTime)
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

        return strTemp;
    }

    public HashMap<QuestionType, ArrayList<Answer>> getAnswer()
    {
        return answerMap;
    }

    public HashMap<QuestionType, ArrayList<QuestionTypeSeq>> getAllQuestions()
    {
        return mQuestions;
    }

    public HashMap<QuestionType, ArrayList<QuestionTypeSeq>> getTestpaperQuestions()
    {
        HashMap<QuestionType, ArrayList<QuestionTypeSeq>> testpaperQuestions
                = new HashMap<QuestionType, ArrayList<QuestionTypeSeq>>();

        for (QuestionType qt : mQuestions.keySet()) {
            if (qt == QuestionType.material) {
                ArrayList<QuestionTypeSeq> questionTypeSeqs = mQuestions.get(qt);
                ArrayList<QuestionTypeSeq> materialItems = new ArrayList<QuestionTypeSeq>();
                for (QuestionTypeSeq seq : questionTypeSeqs) {
                    materialItems.addAll(seq.items);
                }
                testpaperQuestions.put(qt, materialItems);
            }
            testpaperQuestions.put(qt, mQuestions.get(qt));
        }

        return testpaperQuestions;
    }

    public ArrayList<QuestionTypeSeq> getQuesions(QuestionType type)
    {
        if (mQuestions == null) {
            return null;
        }
        return mQuestions.get(type);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTask();
        testpaperActivity = null;
    }

    public int getUsedTime()
    {
        return mTestpaper.limitedTime - mLimitedTime;
    }

    public PaperResult getTestpaperResult()
    {
        return mTestpaperResult;
    }

    public static TestpaperActivity getInstance()
    {
        return testpaperActivity;
    }
}
