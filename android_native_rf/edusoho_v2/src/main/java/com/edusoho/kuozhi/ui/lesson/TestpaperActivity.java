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
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.Question.Answer;
import com.edusoho.kuozhi.model.Testpaper.PaperResult;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.TestpaperFullResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.fragment.testpaper.TestpaperCardFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howzhi on 14-8-31.
 */
public class TestpaperActivity extends TestpaperBaseActivity
        implements MessageEngine.MessageCallback {

    public static final int CHANGE_ANSWER = 0001;
    public static final int PHOTO_CAMEAR = 0002;

    public static final int REDO = 0003;
    public static final int DO = 0004;
    public static final int SHOW_TEST = 0005;

    private int mTestId;
    private int mTestpaperResultId;
    private int mLessonId;
    private MenuItem timeMenuItem;
    private Timer mTimer;
    private int mLimitedTime;
    private boolean mIsRun;
    private boolean mIsTimeOver;
    private int mStopType;
    private int mDoType;

    private boolean isLoadTitleByNet;

    private static TestpaperActivity testpaperActivity;

    private static final int UPDATE_TIME     = 0001;
    private static final int SHOW_SUBMIT_DLG     = 0002;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TIME:
                    timeMenuItem.setTitle(getLimitedTime(mLimitedTime));
                    break;
                case SHOW_SUBMIT_DLG:
                    showTestpaperCard(true);
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
        mIsRun = true;
        answerMap = new HashMap<QuestionType, ArrayList<Answer>>();
    }

    @Override
    protected void initIntentData() {
        Intent data = getIntent();
        mTitle = data.getStringExtra(Const.ACTIONBAT_TITLE);
        mTestId = data.getIntExtra(Const.MEDIA_ID, 0);
        mTestpaperResultId = data.getIntExtra(Const.mTestpaperResultId, 0);
        mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
        mDoType = data.getIntExtra(Const.TESTPAPER_DO_TYPE, DO);
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
            showTestpaperCard(false);
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

    private void showTestpaperCard(boolean isTimeOver)
    {
        TestpaperCardFragment fragment = new TestpaperCardFragment();
        if (isTimeOver) {
            fragment.setNotCancel();
        }
        fragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    protected void initView() {
        Intent data = getIntent();
        if (data != null) {
            isLoadTitleByNet = data.getBooleanExtra("isLoadTitleByNet", false);
        }
        if (!isLoadTitleByNet) {
            super.initView();
        }
        if (isLoadTitleByNet) {
            initIntentData();
            setBackMode(BACK, mTitle);
        }
        loadTestpaper();
    }

    private void loadTestpaper()
    {
        setProgressBarIndeterminateVisibility(true);
        String baseUrl = "";
        if (mDoType == DO) {
            baseUrl = Const.TESTPAPER_FULL_INFO;
        } else if (mDoType == REDO) {
            baseUrl = Const.RE_DO_TESTPAPER_FULL_INFO;
        } else if (mDoType == SHOW_TEST) {
            baseUrl = Const.SHOW_TESTPAPER;
        }

        Log.d(null, "baseurl->" + baseUrl);
        RequestUrl requestUrl = app.bindUrl(baseUrl, true);
        requestUrl.setParams(new String[] {
                "id", mTestpaperResultId + "",
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
                if (isLoadTitleByNet) {
                    titles = getTestpaperQSeq();
                    fragmentArrayList = getTestpaperFragments();
                    initFragmentPaper();
                }
                mLimitedTime = result.testpaper.limitedTime * 60;
                if (mLimitedTime > 0) {
                    startTask();
                }
                app.sendMessage(Const.TESTPAPER_REFRESH_DATA, null);
            }
        });
    }

    private void startTask()
    {
        mStopType = 0;
        mTimer = new Timer();
        mTimer.schedule(getTask(), 0, 1000);
    }

    public void setType(int type)
    {
        mStopType = type;
    }

    private PopupDialog mLoadDlg;

    @Override
    protected void onResume() {
        super.onResume();
        if (mStopType == PHOTO_CAMEAR) {
            startTask();
            return;
        }
        if (!mIsRun && mLoadDlg == null){
            mLoadDlg = PopupDialog.createNormal(
                    mActivity, "暂停考试", "考试暂停中！");
            mLoadDlg.setOnDismissListener(new DialogInterface.OnDismissListener(){
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    startTask();
                    mLoadDlg = null;
                }
            });
            mLoadDlg.show();
        }
    }

    private void stopTask()
    {
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
                    mIsTimeOver = true;
                    mHandler.obtainMessage(SHOW_SUBMIT_DLG).sendToTarget();
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
                continue;
            }
            testpaperQuestions.put(qt, mQuestions.get(qt));
        }

        return testpaperQuestions;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mIsTimeOver) {
            stopTask();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsRun = false;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        testpaperActivity = null;
    }

    public int getUsedTime()
    {
        return mTestpaper.limitedTime * 60 - mLimitedTime;
    }

    public PaperResult getTestpaperResult()
    {
        return mTestpaperResult;
    }

    public static TestpaperActivity getInstance()
    {
        return testpaperActivity;
    }

    private String[] getTestpaperQSeq()
    {
        ArrayList<QuestionType> questionTypeSeqs = mTestpaper.metas.question_type_seq;
        String[] TESTPAPER_QUESTION_TYPE = new String[questionTypeSeqs.size()];
        for (int i=0; i < TESTPAPER_QUESTION_TYPE.length; i++) {
            TESTPAPER_QUESTION_TYPE[i] = questionTypeSeqs.get(i).title();
        }

        return TESTPAPER_QUESTION_TYPE;
    }

    private String[] getTestpaperFragments()
    {
        ArrayList<QuestionType> questionTypeSeqs = mTestpaper.metas.question_type_seq;
        String[] TESTPAPER_QUESTIONS = new String[questionTypeSeqs.size()];
        for (int i=0; i < TESTPAPER_QUESTIONS.length; i++) {
            switch (questionTypeSeqs.get(i)) {
                case choice:
                    TESTPAPER_QUESTIONS[i] = "ChoiceFragment";
                    break;
                case single_choice:
                    TESTPAPER_QUESTIONS[i] = "SingleChoiceFragment";
                    break;
                case essay:
                    TESTPAPER_QUESTIONS[i] = "EssayFragment";
                    break;
                case uncertain_choice:
                    TESTPAPER_QUESTIONS[i] = "UncertainChoiceFragment";
                    break;
                case fill:
                    TESTPAPER_QUESTIONS[i] = "FillFragment";
                    break;
                case determine:
                    TESTPAPER_QUESTIONS[i] = "DetermineFragment";
                    break;
                case material:
                    TESTPAPER_QUESTIONS[i] = "MaterialFragment";
                    break;
            }
        }

        return TESTPAPER_QUESTIONS;
    }
}
