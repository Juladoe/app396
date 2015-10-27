package com.edusoho.kuozhi.homework;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.homework.model.ExerciseModel;
import com.edusoho.kuozhi.homework.model.ExerciseProvider;
import com.edusoho.kuozhi.homework.model.HomeWorkQuestion;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melomelon on 2015/10/26.
 */
public class ExerciseActivity extends HomeworkActivity {

    private static ExerciseActivity exerciseActivity;
    public static final String EXERCISE_ID = "exerciseId";

    protected int mExerciseId;
    protected int mCurrentQuesitonIndex;
    protected List<HomeWorkQuestion> mExerciseQuestionList;
    protected ExerciseProvider mExerciseProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mExerciseProvider = ModelProvider.initProvider(getBaseContext(), ExerciseProvider.class);
        super.onCreate(savedInstanceState);
        setBackMode(BACK, "练习");
    }

    @Override
    protected Intent initIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(
                    getBaseContext(),
                    HomeworkSummaryActivity.HOMEWORK.equals(mType) ? "获取作业数据错误" : "获取练习数据错误"
            );
            throw new RuntimeException("获取数据失败");
        }

        mType = intent.getStringExtra("type");
        mExerciseId = intent.getIntExtra(EXERCISE_ID, 0);
        return intent;
    }

    @Override
    protected void initView() {
        RequestUrl requestUrl = getRequestUrl();
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.show();
        mExerciseProvider.getExercise(requestUrl).success(new NormalCallback<ExerciseModel>() {
            @Override
            public void success(ExerciseModel exerciseModel) {
                coverQuestionList(exerciseModel);
                Bundle bundle = new Bundle();
                bundle.putString(Const.ACTIONBAR_TITLE, "练习题目");
                loadFragment(bundle);
                loadDialog.dismiss();
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                loadDialog.dismiss();
            }
        });
    }

    private void coverQuestionList(ExerciseModel exerciseModel) {
        mExerciseQuestionList = new ArrayList<HomeWorkQuestion>();
        for (HomeWorkQuestion question : exerciseModel.getItems()) {
            QuestionType type = QuestionType.value(question.getType());
            if (QuestionType.material == type) {
                List<HomeWorkQuestion> items = question.getItems();
                for (HomeWorkQuestion itemQuestion : items) {
                    itemQuestion.setParent(question);
                    mExerciseQuestionList.add(itemQuestion);
                }
                continue;
            }
            mExerciseQuestionList.add(question);
        }
    }

    @Override
    protected void loadFragment(Bundle bundle) {
        try {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            Fragment fragment = Fragment.instantiate(getBaseContext(), "com.edusoho.kuozhi.homework.ui.fragment.HomeWorkQuestionFragment");
            fragment.setArguments(bundle);
            fragmentTransaction.replace(android.R.id.content, fragment);
            fragmentTransaction.commit();
        } catch (Exception ex) {
            Log.d("HomeworkActivity", ex.toString());
        }
    }

    @Override
    protected RequestUrl getRequestUrl() {
        return app.bindNewUrl(String.format(Const.EXERCISE_CONTENT, mExerciseId), true);
    }

    @Override
    public List<HomeWorkQuestion> getQuestionList() {
        return mExerciseQuestionList;
    }
}
