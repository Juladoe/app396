package com.edusoho.kuozhi.ui.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Testpaper.TestpaperResultType;
import com.edusoho.kuozhi.ui.fragment.testpaper.TestpaperResultFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14-10-10.
 */
public class TestpaperParseActivity extends TestpaperBaseActivity {

    private int mTestpaperResultId;
    public static TestpaperParseActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @Override
    protected void initView() {
        super.initView();
        loadTestpaperResult();
    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        Intent intent = getIntent();
        if (intent != null) {
            mTestpaperResultId = intent.getIntExtra(TestpaperResultFragment.RESULT_ID, 0);
        }

        intent.putExtra(FRAGMENT_DATA, new Bundle());
    }

    private void loadTestpaperResult()
    {
        setProgressBarIndeterminateVisibility(true);
        RequestUrl requestUrl = mActivity.app.bindUrl(Const.TESTPAPER_RESULT, true);
        requestUrl.setParams(new String[]{
                "id",  mTestpaperResultId + ""
        });

        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
                TestpaperResultType result = parseJsonValue(
                        object, new TypeToken<TestpaperResultType>() {
                });

                Log.d(null, "parse->testpaper " + result);
                if (result == null) {
                    return;
                }

                mQuestions = result.items;
                mTestpaper = result.testpaper;
                mTestpaperResult = result.paperResult;
                mFavorites = result.favorites;

                app.sendMessage(Const.TESTPAPER_REFRESH_DATA, null);
            }
        });
    }

    public static TestpaperParseActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
